#define WIN32_LEAN_AND_MEAN

#include <malloc.h>
#include <stdlib.h>
#include <tchar.h>
#include <time.h>
#include <windows.h>
#include <winsock2.h>
#include <ws2tcpip.h>
#include "resource.h"

#pragma comment(lib, "Ws2_32.lib")
#pragma comment(linker, "\"/manifestdependency:type='win32' name='Microsoft.Windows.Common-Controls' version='6.0.0.0' processorArchitecture='*' publicKeyToken='6595b64144ccf1df' language='*'\"")

#define IS_SHORT(x) (x >= 0 && x <= 65535)

static const int nameLen = 1024;
static const int portSafeThreshold = 10000;
static const int shortStringLen = 16;
static LPCTSTR lpszIniFile = TEXT(".\\WiMote.ini");
static LPCTSTR lpszIniKeyPin = TEXT("PIN");
static LPCTSTR lpszIniKeyPort = TEXT("Port");
static LPCTSTR lpszIniSection = TEXT("WiMote");
static LPCTSTR lpszStart = TEXT("&Start Server");
static LPCTSTR lpszStop = TEXT("&Stop Server");

static DWORD dwThreadId = 0;
static HANDLE hThread = NULL;
static HINSTANCE hInst = NULL;

BOOL bServerRunning = FALSE;
HWND hDlgMain = NULL;
int pin = 0;
int port = 27015;
WSADATA wsaData;

extern DWORD WINAPI serverThread(LPVOID lpParam);

static int WINAPI getSaveSettings(HWND hDlg)
{
	TCHAR tszBuffer[shortStringLen + 1];

	ZeroMemory(tszBuffer, shortStringLen + 1);
	if (GetDlgItemText(hDlg, IDC_PORT, tszBuffer, shortStringLen) != 0) {
		int i = _ttoi(tszBuffer);
		if (IS_SHORT(i)) {
			port = i;
			if (port <= portSafeThreshold)
				MessageBox(hDlg, TEXT("You have chosen a port number below 10000, which is not recommended for security reasons."), TEXT("Wi-Mote Warning"), MB_OK | MB_ICONEXCLAMATION);
			if (!WritePrivateProfileString(lpszIniSection, lpszIniKeyPort, tszBuffer, lpszIniFile))
				return -1;
		}
		else
			return 0;
	}
	else
		return -1;

	ZeroMemory(tszBuffer, shortStringLen + 1);
	if (GetDlgItemText(hDlg, IDC_PIN, tszBuffer, shortStringLen) != 0) {
		int i = _ttoi(tszBuffer);
		if (IS_SHORT(i)) {
			pin = i;
			if (!WritePrivateProfileString(lpszIniSection, lpszIniKeyPin, tszBuffer, lpszIniFile))
				return -1;
		}
		else
			return 0;
	}
	else
		return -1;

	return 1;
}

static INT_PTR CALLBACK aboutDlgProc(HWND hDlg, UINT uMsg, WPARAM wParam, LPARAM lParam)
{
	switch (uMsg) {
		case WM_INITDIALOG:
			AnimateWindow(hDlg, 500, AW_CENTER);
			return TRUE;
		case WM_COMMAND:
			switch (LOWORD(wParam)) {
				case IDCANCEL:
					EndDialog(hDlg, TRUE);
					break;
				default:
					break;
			}
			return 0;
		default:
			break;
	}
	return FALSE;
}

static INT_PTR CALLBACK dlgProc(HWND hDlg, UINT uMsg, WPARAM wParam, LPARAM lParam)
{
	switch (uMsg) {
		case WM_INITDIALOG:
		{
			hDlgMain = hDlg;
			char name[nameLen + 1];
			ZeroMemory(name, nameLen + 1);
			if (gethostname(name, nameLen) == 0) {
				struct addrinfo *result = NULL;
				char *szIpAddr = NULL;
				if (getaddrinfo(name, NULL, NULL, &result) == 0 && result) {
					for (struct addrinfo *ptr = result; ptr != NULL; ptr = ptr->ai_next) {
						if (ptr->ai_family == AF_INET) {
							struct sockaddr_in *sockaddr_ipv4 = (struct sockaddr_in *)ptr->ai_addr;
							if (sockaddr_ipv4) {
								szIpAddr = inet_ntoa(sockaddr_ipv4->sin_addr);
								break;
							}
						}
					}
					if (szIpAddr)
						SetDlgItemTextA(hDlg, IDC_IPADDR, szIpAddr);
					freeaddrinfo(result);
				}
			}

			srand((unsigned int)time(NULL));
			do {
				pin = rand();
			} while (!(pin >= 10000 && pin <= 65535));

			int i = 0;
			TCHAR tszBuffer[shortStringLen + 1];

			i = (int)GetPrivateProfileInt(lpszIniSection, lpszIniKeyPort, port, lpszIniFile);
			if (IS_SHORT(i))
				port = i;
			ZeroMemory(tszBuffer, shortStringLen + 1);
			if (_itot_s(port, tszBuffer, shortStringLen, 10) == 0)
				SetDlgItemText(hDlg, IDC_PORT, tszBuffer);

			i = (int)GetPrivateProfileInt(lpszIniSection, lpszIniKeyPin, pin, lpszIniFile);
			if (IS_SHORT(i))
				pin = i;
			ZeroMemory(tszBuffer, shortStringLen + 1);
			if (_itot_s(pin, tszBuffer, shortStringLen, 10) == 0)
				SetDlgItemText(hDlg, IDC_PIN, tszBuffer);

			SetDlgItemText(hDlg, IDC_STARTSTOP, lpszStart);

			return TRUE;
		}
		case WM_COMMAND:
			switch (LOWORD(wParam)) {
				case IDC_ABOUT:
					DialogBox(hInst, MAKEINTRESOURCE(IDD_ABOUT), hDlg, (DLGPROC)aboutDlgProc);
					break;
				case IDC_STARTSTOP:
					if (!bServerRunning) {
						int ret = getSaveSettings(hDlg);
						if (ret == 1) {
							bServerRunning = TRUE;
							hThread = CreateThread(NULL, 0, serverThread, NULL, 0, &dwThreadId);
							if (hThread) {
								SetDlgItemText(hDlg, IDC_STARTSTOP, lpszStop);
								SendDlgItemMessage(hDlg, IDC_BANNER, STM_SETIMAGE, (WPARAM)IMAGE_BITMAP, (LPARAM)LoadImage(hInst, MAKEINTRESOURCE(IDB_WIMOTESTARTED), IMAGE_BITMAP, 0, 0, LR_SHARED));
								ShowWindow(GetDlgItem(hDlg, IDC_ABOUT), SW_HIDE);
								ShowWindow(GetDlgItem(hDlg, IDC_ABOUT), SW_SHOW);
								EnableWindow(GetDlgItem(hDlg, IDC_IPADDR), FALSE);
								EnableWindow(GetDlgItem(hDlg, IDC_PORT), FALSE);
								EnableWindow(GetDlgItem(hDlg, IDC_PIN), FALSE);
							}
							else
								bServerRunning = FALSE;
						}
						else if (ret == 0)
							MessageBox(hDlg, TEXT("Invalid inputs! Port/PIN must be in the range 0-65535."), TEXT("Wi-Mote Error"), MB_OK | MB_ICONSTOP);
						else
							MessageBox(hDlg, TEXT("An error has occurred while saving user settings."), TEXT("Wi-Mote Error"), MB_OK | MB_ICONSTOP);
					}
					else {
						bServerRunning = FALSE;
						CloseHandle(hThread);
						hThread = NULL;
						SetDlgItemText(hDlg, IDC_STARTSTOP, lpszStart);
						SendDlgItemMessage(hDlg, IDC_BANNER, STM_SETIMAGE, (WPARAM)IMAGE_BITMAP, (LPARAM)LoadImage(hInst, MAKEINTRESOURCE(IDB_WIMOTE), IMAGE_BITMAP, 0, 0, LR_SHARED));
						ShowWindow(GetDlgItem(hDlg, IDC_ABOUT), SW_HIDE);
						ShowWindow(GetDlgItem(hDlg, IDC_ABOUT), SW_SHOW);
						EnableWindow(GetDlgItem(hDlg, IDC_IPADDR), TRUE);
						EnableWindow(GetDlgItem(hDlg, IDC_PORT), TRUE);
						EnableWindow(GetDlgItem(hDlg, IDC_PIN), TRUE);
					}
					break;
				case IDCANCEL:
				{
					bServerRunning = FALSE;
					CloseHandle(hThread);
					int ret = getSaveSettings(hDlg);
					if (ret == 1)
						EndDialog(hDlg, TRUE);
					else if (ret == 0)
						MessageBox(hDlg, TEXT("Invalid inputs! Port/PIN be in the range 0-65535."), TEXT("Wi-Mote Error"), MB_OK | MB_ICONSTOP);
					else {
						MessageBox(hDlg, TEXT("An error has occurred while saving user settings."), TEXT("Wi-Mote Error"), MB_OK | MB_ICONSTOP);
						EndDialog(hDlg, TRUE);
					}
					break;
				}
				default:
					break;
			}
			return 0;
		default:
			break;
	}
	return FALSE;
}

int CALLBACK WinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance, LPSTR lpCmdLine, int nCmdShow)
{
	hInst = hInstance;

	if (WSAStartup(MAKEWORD(2, 2), &wsaData) != NO_ERROR) {
		MessageBox(NULL, TEXT("WinSock initialization error!"), TEXT("Wi-Mote Error"), MB_OK | MB_ICONSTOP);
		return 1;
	}

	DialogBox(hInstance, MAKEINTRESOURCE(IDD_MAIN), NULL, (DLGPROC)dlgProc);

	WSACleanup();

	return 0;
}
