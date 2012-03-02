#define WIN32_LEAN_AND_MEAN

#include <Windows.h>
#include <winsock2.h>
#include <ws2tcpip.h>
#include <malloc.h>
#include <stdlib.h>
#include <tchar.h>
#include "resource.h"

#pragma comment(linker, "\"/manifestdependency:type='win32' name='Microsoft.Windows.Common-Controls' version='6.0.0.0' processorArchitecture='*' publicKeyToken='6595b64144ccf1df' language='*'\"")

#define IS_PORT_VALID(x) x >= 0 && x <= 65535

static const int defaultPort = 27015;
static const int portStringLen = 64;
static const LPCTSTR lpszIniFile = TEXT(".\\WiMote.ini");
static const LPCTSTR lpszIniKey = TEXT("Port");
static const LPCTSTR lpszIniSection = TEXT("WiMote");
static const LPCTSTR lpszStart = TEXT("&Start Server");
static const LPCTSTR lpszStop = TEXT("&Stop Server");

static DWORD dwThreadId = 0;
static HANDLE hThread = NULL;
static HINSTANCE hInst = NULL;

BOOL bServerRunning = FALSE;
int port = defaultPort;
WSADATA wsaData;

extern DWORD WINAPI serverThread(LPVOID lpParam);

static BOOL WINAPI getSavePort(HWND hDlg)
{
	TCHAR tszPort[portStringLen + 1];
	ZeroMemory(tszPort, sizeof(tszPort));
	if (GetDlgItemText(hDlg, IDC_PORT, tszPort, portStringLen) != 0) {
		int p = _ttoi(tszPort);
		if (IS_PORT_VALID(p)) {
			port = p;
			if (WritePrivateProfileString(lpszIniSection, lpszIniKey, tszPort, lpszIniFile))
				return TRUE;
			else {
				MessageBox(hDlg, TEXT("Error saving port to INI file."), TEXT("Wi-Mote Error"), MB_OK | MB_ICONSTOP);
				return FALSE;
			}
		}
		else {
			MessageBox(hDlg, TEXT("Port range must be 0-65535."), TEXT("Wi-Mote Error"), MB_OK | MB_ICONSTOP);
			return FALSE;
		}
	}
	else {
		MessageBox(hDlg, TEXT("Error getting port from user input."), TEXT("Wi-Mote Error"), MB_OK | MB_ICONSTOP);
		return FALSE;
	}
}

static INT_PTR CALLBACK aboutDlgProc(HWND hDlg, UINT uMsg, WPARAM wParam, LPARAM lParam)
{
	switch (uMsg) {
		case WM_INITDIALOG:
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
			{
				char name[1024];
				if (gethostname(name, sizeof(name)) == 0) {
					struct addrinfo *result = NULL;
					if (getaddrinfo(name, NULL, NULL, &result) == 0) {
						size_t count = 0;
						for (struct addrinfo *ptr = result; ptr != NULL; ptr = ptr->ai_next)
							count++;
						size_t len = count * 64;
						char *szIpAddr = (char *)calloc(len + 1, sizeof(char));
						if (szIpAddr) {
							ZeroMemory(szIpAddr, len + 1);
							for (struct addrinfo *ptr = result; ptr != NULL; ptr = ptr->ai_next) {
								switch (ptr->ai_family) {
									case AF_INET: {
										struct sockaddr_in *sockaddr_ipv4 = (struct sockaddr_in *)ptr->ai_addr;
										if (sockaddr_ipv4) {
											if (strlen(szIpAddr) > 0)
												strcat_s(szIpAddr, len, ", ");
											strcat_s(szIpAddr, len, inet_ntoa(sockaddr_ipv4->sin_addr));
										}
										break;
									}
									/*case AF_INET6: {
										LPSOCKADDR sockaddr_ip = (LPSOCKADDR)ptr->ai_addr;
										DWORD ipbufferlength = 46;
										char ipstringbuffer[46];
										if (WSAAddressToStringA(sockaddr_ip, (DWORD)ptr->ai_addrlen, NULL, ipstringbuffer, &ipbufferlength) == 0) {
											if (strlen(szIpAddr) > 0)
												strcat_s(szIpAddr, len, ", ");
											strcat_s(szIpAddr, len, ipstringbuffer);
										}
										break;
									}*/
									default:
										break;
								}
							}
							if (strlen(szIpAddr) > 0)
								SetDlgItemTextA(hDlg, IDC_IPADDR, szIpAddr);
							free(szIpAddr);
						}
						freeaddrinfo(result);
					}
				}
			}
			int p = (int)GetPrivateProfileInt(lpszIniSection, lpszIniKey, defaultPort, lpszIniFile);
			if (IS_PORT_VALID(p))
				port = p;
			else
				port = defaultPort;
			TCHAR tszPort[portStringLen + 1];
			ZeroMemory(tszPort, sizeof(tszPort));
			if (_itot_s(port, tszPort, portStringLen, 10) == 0)
				SetDlgItemText(hDlg, IDC_PORT, tszPort);
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
						if (getSavePort(hDlg)) {
							bServerRunning = TRUE;
							hThread = CreateThread(NULL, 0, serverThread, NULL, 0, &dwThreadId);
							if (hThread) {
								SetDlgItemText(hDlg, IDC_STARTSTOP, lpszStop);
								SendDlgItemMessage(hDlg, IDC_BANNER, STM_SETIMAGE, (WPARAM)IMAGE_BITMAP, (LPARAM)LoadImage(hInst, MAKEINTRESOURCE(IDB_WIMOTESTARTED), IMAGE_BITMAP, 0, 0, LR_SHARED));
								ShowWindow(GetDlgItem(hDlg, IDC_ABOUT), SW_HIDE);
								ShowWindow(GetDlgItem(hDlg, IDC_ABOUT), SW_SHOW);
							}
							else
								bServerRunning = FALSE;
						}
					}
					else {
						bServerRunning = FALSE;
						CloseHandle(hThread);
						hThread = NULL;
						SetDlgItemText(hDlg, IDC_STARTSTOP, lpszStart);
						SendDlgItemMessage(hDlg, IDC_BANNER, STM_SETIMAGE, (WPARAM)IMAGE_BITMAP, (LPARAM)LoadImage(hInst, MAKEINTRESOURCE(IDB_WIMOTE), IMAGE_BITMAP, 0, 0, LR_SHARED));
						ShowWindow(GetDlgItem(hDlg, IDC_ABOUT), SW_HIDE);
						ShowWindow(GetDlgItem(hDlg, IDC_ABOUT), SW_SHOW);
					}
					break;
				case IDCANCEL:
					bServerRunning = FALSE;
					CloseHandle(hThread);
					getSavePort(hDlg);
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

int CALLBACK WinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance, LPSTR lpCmdLine, int nCmdShow)
{
	hInst = hInstance;

	if (WSAStartup(MAKEWORD(2, 2), &wsaData) != NO_ERROR) {
		MessageBox(NULL, TEXT("WinSock initialzation error!"), TEXT("WiMote Error"), MB_OK | MB_ICONSTOP);
		return 1;
	}

	DialogBox(hInstance, MAKEINTRESOURCE(IDD_MAIN), NULL, (DLGPROC)dlgProc);

	WSACleanup();

	return 0;
}
