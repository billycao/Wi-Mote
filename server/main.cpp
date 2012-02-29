#include <Windows.h>
#include <tchar.h>
#include "resource.h"

#pragma comment(linker, "\"/manifestdependency:type='win32' name='Microsoft.Windows.Common-Controls' version='6.0.0.0' processorArchitecture='*' publicKeyToken='6595b64144ccf1df' language='*'\"")

static const LPCTSTR lpszStart = TEXT("&Start Server");
static const LPCTSTR lpszStop = TEXT("&Stop Server");

static HINSTANCE hInst = NULL;
static HANDLE hThread = NULL;
static DWORD dwThreadId = 0;
BOOL bServerRunning = FALSE;

extern DWORD WINAPI serverThread(LPVOID lpParam);

INT_PTR CALLBACK dlgProc(HWND hDlg, UINT uMsg, WPARAM wParam, LPARAM lParam)
{
	switch (uMsg) {
		case WM_INITDIALOG:
			SetDlgItemText(hDlg, IDC_STARTSTOP, lpszStart);
			SetDlgItemText(hDlg, IDC_IPADDR, TEXT("TODO!!"));
			SetDlgItemText(hDlg, IDC_PORT, TEXT("TODO!!"));
			return TRUE;
		case WM_COMMAND:
			switch (LOWORD(wParam)) {
				case IDC_ABOUT:
					MessageBox(hDlg, TEXT("WiMote\t...Because!\n\n(C) 2012. All rights reserved.\n\nBilly Cao - GitHub, Master Control Activity\nJames Hung - Keyboard, Server\nSaqib Mohammed - Accelerometer Mouse\nSayan Samanta - Tablet, Trackpad"), TEXT("About WiMote"), MB_OK);
					break;
				case IDC_STARTSTOP:
					if (!bServerRunning) {
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

	DialogBox(hInstance, MAKEINTRESOURCE(IDD_MAIN), NULL, (DLGPROC)dlgProc);

	return 0;
}
