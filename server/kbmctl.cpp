#include <dinput.h>
#include <malloc.h>
#include <stdlib.h>
#include <string.h>
#include <windows.h>
#include <vector>
using namespace std;

static const char *szDelim = " ";

static void WINAPI mouseClick(DWORD dwAction);
static void WINAPI moveMouse(const char *szCoords, BOOL bAbsolute);
static void WINAPI turnMouseWheel(const char *szAmount, DWORD dwDirection);
static void WINAPI processKeyStream(const char *szKeyStream, BOOL bKeyUp);

// The processStream() function takes in a string received from the client
// (the Android device) and parses it. This string should be in one of the
// following formats:
//
//     KEY_DOWN key-stream
//     KEY_UP key-stream
//
//         Description: Press or release key(s). key-stream is a stream of
//                      space-delimited keys to be pressed or released
//                      simultaneously.
//
//     MOUSE_DELTA x y
//
//         Description: Set mouse position based on change in coordinates. There
//                      are no specific units for (x, y), so optimal values are
//                      best determined by testing and trial-and-error.
//
//     MOUSE_ABS x y
//
//         Description: Set mouse position based on absolute coordinates.
//                      x range: [0, 65535]
//                      y range: [0, 65535]
//
//     MOUSE_LEFT_DOWN
//     MOUSE_LEFT_UP
//     MOUSE_RIGHT_DOWN
//     MOUSE_RIGHT_UP
//     MOUSE_MIDDLE_DOWN
//     MOUSE_MIDDLE_UP
//
//         Description: Click or release mouse buttons.
//
//     MOUSE_SCROLL amount
//     MOUSE_HSCROLL amount
//
//         Description: Use the mouse wheel to scroll vertically or horizontally.
//                      amount is the number of notches to turn the mouse wheel.
//                      amount > 0: Wheel rotated AWAY from user
//                                  (scroll up / scroll right / zoom in).
//                      amount < 0: Wheel rotated TOWARD user
//                                  (scroll down / scroll left / zoom out).
//
//     MOUSE_ZOOM amount
//
//         Description: Use the mouse wheel to zoom.
//                      amount =  1: Zoom in.
//                      amount = -1: Zoom out.

void WINAPI processStream(const char *szStream)
{
	if (!szStream)
		return;

	// Duplicate szStream into a separate, temporary buffer for strtok_s.
	char *szStreamDup = _strdup(szStream);
	if (!szStreamDup)
		return;

	// Find the first token in the stream, delimited by ' '. The first token
	// specifies is one of the actions listed above.
	char *szNext = NULL;
	char *szAction = strtok_s(szStreamDup, szDelim, &szNext);
	if (!szAction) {
		free(szStreamDup);
		return;
	}

	// The rest of the string are the parameters for the action.
	char *szParams = (char *)szStream + strlen(szAction);

	if (!strcmp(szAction, "KEY_DOWN"))
		processKeyStream(szParams, FALSE);
	else if (!strcmp(szAction, "KEY_UP"))
		processKeyStream(szParams, TRUE);
	else if (!strcmp(szAction, "MOUSE_DELTA"))
		moveMouse(szParams, FALSE);
	else if (!strcmp(szAction, "MOUSE_ABS"))
		moveMouse(szParams, TRUE);
	else if (!strcmp(szAction, "MOUSE_LEFT_DOWN"))
		mouseClick(MOUSEEVENTF_LEFTDOWN);
	else if (!strcmp(szAction, "MOUSE_LEFT_UP"))
		mouseClick(MOUSEEVENTF_LEFTUP);
	else if (!strcmp(szAction, "MOUSE_RIGHT_DOWN"))
		mouseClick(MOUSEEVENTF_RIGHTDOWN);
	else if (!strcmp(szAction, "MOUSE_RIGHT_UP"))
		mouseClick(MOUSEEVENTF_RIGHTUP);
	else if (!strcmp(szAction, "MOUSE_MIDDLE_DOWN"))
		mouseClick(MOUSEEVENTF_MIDDLEDOWN);
	else if (!strcmp(szAction, "MOUSE_MIDDLE_UP"))
		mouseClick(MOUSEEVENTF_MIDDLEUP);
	else if (!strcmp(szAction, "MOUSE_HSCROLL"))
		turnMouseWheel(szParams, MOUSEEVENTF_HWHEEL);
	else if (!strcmp(szAction, "MOUSE_SCROLL"))
		turnMouseWheel(szParams, MOUSEEVENTF_WHEEL);
	else if (!strcmp(szAction, "MOUSE_ZOOM")) {
		processKeyStream("CTRL", FALSE);
		turnMouseWheel(szParams, MOUSEEVENTF_WHEEL);
		processKeyStream("CTRL", TRUE);
	}

	free(szStreamDup);
}

static void WINAPI mouseClick(DWORD dwAction)
{
	INPUT input;
	ZeroMemory(&input, sizeof(input));
	input.type = INPUT_MOUSE;
	input.mi.dwFlags = dwAction;
	INPUT inputArray[] = {input};
	SendInput(_countof(inputArray), inputArray, sizeof(INPUT));
}

static void WINAPI moveMouse(const char *szCoords, BOOL bAbsolute)
{
	if (!szCoords)
		return;

	// Duplicate szCoords into a separate, temporary buffer for strtok_s.
	char *szCoordsDup = _strdup(szCoords);
	if (!szCoordsDup)
		return;

	INPUT input;
	ZeroMemory(&input, sizeof(input));

	// Find x.
	char *szNext = NULL;
	char *szCoord = strtok_s(szCoordsDup, szDelim, &szNext);
	if (!szCoord) {
		free(szCoordsDup);
		return;
	}
	input.mi.dx = atoi(szCoord);

	// Find y.
	szCoord = strtok_s(NULL, szDelim, &szNext);
	if (!szCoord) {
		free(szCoordsDup);
		return;
	}
	input.mi.dy = atoi(szCoord);

	free(szCoordsDup);

	input.type = INPUT_MOUSE;
	input.mi.dwFlags = MOUSEEVENTF_MOVE | (bAbsolute ? (MOUSEEVENTF_ABSOLUTE | MOUSEEVENTF_VIRTUALDESK) : 0);
	INPUT inputArray[] = {input};
	SendInput(_countof(inputArray), inputArray, sizeof(INPUT));
}

static void WINAPI turnMouseWheel(const char *szAmount, DWORD dwDirection)
{
	if (!szAmount)
		return;

	INPUT input;
	ZeroMemory(&input, sizeof(input));
	input.type = INPUT_MOUSE;
	input.mi.mouseData = atoi(szAmount) * WHEEL_DELTA;
	input.mi.dwFlags = dwDirection;
	INPUT inputArray[] = {input};
	SendInput(_countof(inputArray), inputArray, sizeof(INPUT));
}

static void WINAPI processKeyStream(const char *szKeyStream, BOOL bKeyUp)
{
	if (!szKeyStream)
		return;

	// Duplicate szStream into a separate, temporary buffer for strtok_s.
	char *szKeyStreamDup = _strdup(szKeyStream);
	if (!szKeyStreamDup)
		return;

	// Find the first key.
	char *szNext = NULL;
	char *szKey = strtok_s(szKeyStreamDup, szDelim, &szNext);

	// Vector of keys to be pressed simultaneously.
	vector<KEYBDINPUT> keys;

	// Process the keys.
	while (szKey) {
		KEYBDINPUT ki;
		ZeroMemory(&ki, sizeof(ki));

		if (bKeyUp)
			ki.dwFlags |= KEYEVENTF_KEYUP;

		if (!strcmp(szKey, "MOUSE_LEFT_CLICK")) {
			mouseClick(bKeyUp ? MOUSEEVENTF_LEFTUP : MOUSEEVENTF_LEFTDOWN);
			goto next;
		}
		if (!strcmp(szKey, "MOUSE_RIGHT_CLICK")) {
			mouseClick(bKeyUp ? MOUSEEVENTF_RIGHTUP : MOUSEEVENTF_RIGHTDOWN);
			goto next;
		}
		if (!strcmp(szKey, "MOUSE_MIDDLE_CLICK")) {
			mouseClick(bKeyUp ? MOUSEEVENTF_MIDDLEUP : MOUSEEVENTF_MIDDLEDOWN);
			goto next;
		}

		if (!strcmp(szKey, "BACKSPACE")) {
			ki.wScan = (WORD)DIKEYBOARD_BACK;
			ki.wVk = VK_BACK;
		}
		if (!strcmp(szKey, "TAB")) {
			ki.wScan = (WORD)DIKEYBOARD_TAB;
			ki.wVk = VK_TAB;
		}
		if (!strcmp(szKey, "ENTER")) {
			ki.wScan = (WORD)DIKEYBOARD_RETURN;
			ki.wVk = VK_RETURN;
		}
		if (!strcmp(szKey, "SHIFT")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;		// Can act as modifier key.
			ki.wVk = VK_SHIFT;
		}
		if (!strcmp(szKey, "CTRL")) {
			ki.wScan = (WORD)DIKEYBOARD_LCONTROL;	// Can act as modifier key.
			ki.wVk = VK_CONTROL;
		}
		if (!strcmp(szKey, "ALT")) {
			ki.wScan = (WORD)DIKEYBOARD_LMENU;		// Can act as modifier key.
			ki.wVk = VK_MENU;
		}
		if (!strcmp(szKey, "PAUSE")) {
			ki.wScan = (WORD)DIKEYBOARD_PAUSE;
			ki.wVk = VK_PAUSE;
		}
		if (!strcmp(szKey, "CAPS")) {
			ki.wScan = (WORD)DIKEYBOARD_CAPITAL;
			ki.wVk = VK_CAPITAL;
		}
		if (!strcmp(szKey, "ESC")) {
			ki.wScan = (WORD)DIKEYBOARD_ESCAPE;
			ki.wVk = VK_ESCAPE;
		}
		if (!strcmp(szKey, "SPACE")) {
			ki.wScan = (WORD)DIKEYBOARD_SPACE;
			ki.wVk = VK_SPACE;
		}
		if (!strcmp(szKey, "PGUP")) {
			ki.wScan = (WORD)DIKEYBOARD_PRIOR;
			ki.wVk = VK_PRIOR;
		}
		if (!strcmp(szKey, "PGDN")) {
			ki.wScan = (WORD)DIKEYBOARD_NEXT;
			ki.wVk = VK_NEXT;
		}
		if (!strcmp(szKey, "END")) {
			ki.wScan = (WORD)DIKEYBOARD_END;
			ki.wVk = VK_END;
		}
		if (!strcmp(szKey, "HOME")) {
			ki.wScan = (WORD)DIKEYBOARD_HOME;
			ki.wVk = VK_HOME;
		}
		if (!strcmp(szKey, "LEFT")) {
			ki.wScan = (WORD)DIKEYBOARD_LEFT;
			ki.wVk = VK_LEFT;
		}
		if (!strcmp(szKey, "UP")) {
			ki.wScan = (WORD)DIKEYBOARD_UP;
			ki.wVk = VK_UP;
		}
		if (!strcmp(szKey, "RIGHT")) {
			ki.wScan = (WORD)DIKEYBOARD_RIGHT;
			ki.wVk = VK_RIGHT;
		}
		if (!strcmp(szKey, "DOWN")) {
			ki.wScan = (WORD)DIKEYBOARD_DOWN;
			ki.wVk = VK_DOWN;
		}
		if (!strcmp(szKey, "PRTSCR")) {
			// No DirectInput equivalent for PRINT SCREEN!
			ki.wVk = VK_SNAPSHOT;
		}
		if (!strcmp(szKey, "INS")) {
			ki.wScan = (WORD)DIKEYBOARD_INSERT;
			ki.wVk = VK_INSERT;
		}
		if (!strcmp(szKey, "DEL")) {
			ki.wScan = (WORD)DIKEYBOARD_DELETE;
			ki.wVk = VK_DELETE;
		}
		if (!strcmp(szKey, "WIN")) {
			ki.wScan = (WORD)DIKEYBOARD_LWIN;		// Can act as modifier key.
			ki.wVk = VK_LWIN;
		}
		if (!strcmp(szKey, "CMENU")) {
			ki.wScan = (WORD)DIKEYBOARD_APPS;
			ki.wVk = VK_APPS;
		}
		if (!strcmp(szKey, "F1")) {
			ki.wScan = (WORD)DIKEYBOARD_F1;
			ki.wVk = VK_F1;
		}
		if (!strcmp(szKey, "F2")) {
			ki.wScan = (WORD)DIKEYBOARD_F2;
			ki.wVk = VK_F2;
		}
		if (!strcmp(szKey, "F3")) {
			ki.wScan = (WORD)DIKEYBOARD_F3;
			ki.wVk = VK_F3;
		}
		if (!strcmp(szKey, "F4")) {
			ki.wScan = (WORD)DIKEYBOARD_F4;
			ki.wVk = VK_F4;
		}
		if (!strcmp(szKey, "F5")) {
			ki.wScan = (WORD)DIKEYBOARD_F5;
			ki.wVk = VK_F5;
		}
		if (!strcmp(szKey, "F6")) {
			ki.wScan = (WORD)DIKEYBOARD_F6;
			ki.wVk = VK_F6;
		}
		if (!strcmp(szKey, "F7")) {
			ki.wScan = (WORD)DIKEYBOARD_F7;
			ki.wVk = VK_F7;
		}
		if (!strcmp(szKey, "F8")) {
			ki.wScan = (WORD)DIKEYBOARD_F8;
			ki.wVk = VK_F8;
		}
		if (!strcmp(szKey, "F9")) {
			ki.wScan = (WORD)DIKEYBOARD_F9;
			ki.wVk = VK_F9;
		}
		if (!strcmp(szKey, "F10")) {
			ki.wScan = (WORD)DIKEYBOARD_F10;
			ki.wVk = VK_F10;
		}
		if (!strcmp(szKey, "F11")) {
			ki.wScan = (WORD)DIKEYBOARD_F11;
			ki.wVk = VK_F11;
		}
		if (!strcmp(szKey, "F12")) {
			ki.wScan = (WORD)DIKEYBOARD_F12;
			ki.wVk = VK_F12;
		}
		if (!strcmp(szKey, "VOL_MUTE")) {
			ki.wScan = (WORD)DIKEYBOARD_MUTE;
			ki.wVk = VK_VOLUME_MUTE;
		}
		if (!strcmp(szKey, "VOL_DOWN")) {
			ki.wScan = (WORD)DIKEYBOARD_VOLUMEDOWN;
			ki.wVk = VK_VOLUME_DOWN;
		}
		if (!strcmp(szKey, "VOL_UP")) {
			ki.wScan = (WORD)DIKEYBOARD_VOLUMEUP;
			ki.wVk = VK_VOLUME_UP;
		}

		// ;: key
		if (!strcmp(szKey, ";")) {
			ki.wScan = (WORD)DIKEYBOARD_SEMICOLON;
			ki.wVk = VK_OEM_1;
		}
		if (!strcmp(szKey, ":")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;
			ki.wVk = VK_SHIFT;
			keys.push_back(ki);

			ki.wScan = (WORD)DIKEYBOARD_SEMICOLON;
			ki.wVk = VK_OEM_1;
		}

		// =+ key
		if (!strcmp(szKey, "=")) {
			ki.wScan = (WORD)DIKEYBOARD_EQUALS;
			ki.wVk = VK_OEM_PLUS;
		}
		if (!strcmp(szKey, "+")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;
			ki.wVk = VK_SHIFT;
			keys.push_back(ki);

			ki.wScan = (WORD)DIKEYBOARD_EQUALS;
			ki.wVk = VK_OEM_PLUS;
		}

		// ,< key
		if (!strcmp(szKey, ",")) {
			ki.wScan = (WORD)DIKEYBOARD_COMMA;
			ki.wVk = VK_OEM_COMMA;
		}
		if (!strcmp(szKey, "<")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;
			ki.wVk = VK_SHIFT;
			keys.push_back(ki);

			ki.wScan = (WORD)DIKEYBOARD_COMMA;
			ki.wVk = VK_OEM_COMMA;
		}

		// -_ key
		if (!strcmp(szKey, "-")) {
			ki.wScan = (WORD)DIKEYBOARD_MINUS;
			ki.wVk = VK_OEM_MINUS;
		}
		if (!strcmp(szKey, "_")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;
			ki.wVk = VK_SHIFT;
			keys.push_back(ki);

			ki.wScan = (WORD)DIKEYBOARD_MINUS;
			ki.wVk = VK_OEM_MINUS;
		}

		// .> key
		if (!strcmp(szKey, ".")) {
			ki.wScan = (WORD)DIKEYBOARD_PERIOD;
			ki.wVk = VK_OEM_PERIOD;
		}
		if (!strcmp(szKey, ">")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;
			ki.wVk = VK_SHIFT;
			keys.push_back(ki);

			ki.wScan = (WORD)DIKEYBOARD_PERIOD;
			ki.wVk = VK_OEM_PERIOD;
		}

		// /? key
		if (!strcmp(szKey, "/")) {
			ki.wScan = (WORD)DIKEYBOARD_SLASH;
			ki.wVk = VK_OEM_2;
		}
		if (!strcmp(szKey, "?")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;
			ki.wVk = VK_SHIFT;
			keys.push_back(ki);

			ki.wScan = (WORD)DIKEYBOARD_SLASH;
			ki.wVk = VK_OEM_2;
		}

		// `~ key
		if (!strcmp(szKey, "`")) {
			ki.wScan = (WORD)DIKEYBOARD_GRAVE;
			ki.wVk = VK_OEM_3;
		}
		if (!strcmp(szKey, "~")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;
			ki.wVk = VK_SHIFT;
			keys.push_back(ki);

			ki.wScan = (WORD)DIKEYBOARD_GRAVE;
			ki.wVk = VK_OEM_3;
		}

		// [{ key
		if (!strcmp(szKey, "[")) {
			ki.wScan = (WORD)DIKEYBOARD_LBRACKET;
			ki.wVk = VK_OEM_4;
		}
		if (!strcmp(szKey, "{")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;
			ki.wVk = VK_SHIFT;
			keys.push_back(ki);

			ki.wScan = (WORD)DIKEYBOARD_LBRACKET;
			ki.wVk = VK_OEM_4;
		}

		// \| key
		if (!strcmp(szKey, "\\")) {
			ki.wScan = (WORD)DIKEYBOARD_BACKSLASH;
			ki.wVk = VK_OEM_5;
		}
		if (!strcmp(szKey, "|")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;
			ki.wVk = VK_SHIFT;
			keys.push_back(ki);

			ki.wScan = (WORD)DIKEYBOARD_BACKSLASH;
			ki.wVk = VK_OEM_5;
		}

		// ]} key
		if (!strcmp(szKey, "]")) {
			ki.wScan = (WORD)DIKEYBOARD_RBRACKET;
			ki.wVk = VK_OEM_6;
		}
		if (!strcmp(szKey, "}")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;
			ki.wVk = VK_SHIFT;
			keys.push_back(ki);

			ki.wScan = (WORD)DIKEYBOARD_RBRACKET;
			ki.wVk = VK_OEM_6;
		}

		// '" key
		if (!strcmp(szKey, "'")) {
			ki.wScan = (WORD)DIKEYBOARD_APOSTROPHE;
			ki.wVk = VK_OEM_7;
		}
		if (!strcmp(szKey, "\"")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;
			ki.wVk = VK_SHIFT;
			keys.push_back(ki);

			ki.wScan = (WORD)DIKEYBOARD_APOSTROPHE;
			ki.wVk = VK_OEM_7;
		}

		// 0-9 key (unshifted)
		if (!strcmp(szKey, "1")) {
			ki.wScan = (WORD)DIKEYBOARD_1;
			ki.wVk = '1';
		}
		if (!strcmp(szKey, "2")) {
			ki.wScan = (WORD)DIKEYBOARD_2;
			ki.wVk = '2';
		}
		if (!strcmp(szKey, "3")) {
			ki.wScan = (WORD)DIKEYBOARD_3;
			ki.wVk = '3';
		}
		if (!strcmp(szKey, "4")) {
			ki.wScan = (WORD)DIKEYBOARD_4;
			ki.wVk = '4';
		}
		if (!strcmp(szKey, "5")) {
			ki.wScan = (WORD)DIKEYBOARD_5;
			ki.wVk = '5';
		}
		if (!strcmp(szKey, "6")) {
			ki.wScan = (WORD)DIKEYBOARD_6;
			ki.wVk = '6';
		}
		if (!strcmp(szKey, "7")) {
			ki.wScan = (WORD)DIKEYBOARD_7;
			ki.wVk = '7';
		}
		if (!strcmp(szKey, "8")) {
			ki.wScan = (WORD)DIKEYBOARD_8;
			ki.wVk = '8';
		}
		if (!strcmp(szKey, "9")) {
			ki.wScan = (WORD)DIKEYBOARD_9;
			ki.wVk = '9';
		}
		if (!strcmp(szKey, "0")) {
			ki.wScan = (WORD)DIKEYBOARD_0;
			ki.wVk = '0';
		}

		// 0-9 key (shifted)
		if (!strcmp(szKey, "!")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;
			ki.wVk = VK_SHIFT;
			keys.push_back(ki);

			ki.wScan = (WORD)DIKEYBOARD_0;
			ki.wVk = '1';
		}
		if (!strcmp(szKey, "@")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;
			ki.wVk = VK_SHIFT;
			keys.push_back(ki);

			ki.wScan = (WORD)DIKEYBOARD_1;
			ki.wVk = '2';
		}
		if (!strcmp(szKey, "#")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;
			ki.wVk = VK_SHIFT;
			keys.push_back(ki);

			ki.wScan = (WORD)DIKEYBOARD_2;
			ki.wVk = '3';
		}
		if (!strcmp(szKey, "$")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;
			ki.wVk = VK_SHIFT;
			keys.push_back(ki);

			ki.wScan = (WORD)DIKEYBOARD_3;
			ki.wVk = '4';
		}
		if (!strcmp(szKey, "%")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;
			ki.wVk = VK_SHIFT;
			keys.push_back(ki);

			ki.wScan = (WORD)DIKEYBOARD_4;
			ki.wVk = '5';
		}
		if (!strcmp(szKey, "^")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;
			ki.wVk = VK_SHIFT;
			keys.push_back(ki);

			ki.wScan = (WORD)DIKEYBOARD_5;
			ki.wVk = '6';
		}
		if (!strcmp(szKey, "&")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;
			ki.wVk = VK_SHIFT;
			keys.push_back(ki);

			ki.wScan = (WORD)DIKEYBOARD_6;
			ki.wVk = '7';
		}
		if (!strcmp(szKey, "*")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;
			ki.wVk = VK_SHIFT;
			keys.push_back(ki);

			ki.wScan = (WORD)DIKEYBOARD_7;
			ki.wVk = '8';
		}
		if (!strcmp(szKey, "(")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;
			ki.wVk = VK_SHIFT;
			keys.push_back(ki);

			ki.wScan = (WORD)DIKEYBOARD_8;
			ki.wVk = '9';
		}
		if (!strcmp(szKey, ")")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;
			ki.wVk = VK_SHIFT;
			keys.push_back(ki);

			ki.wScan = (WORD)DIKEYBOARD_9;
			ki.wVk = '0';
		}

		// A-Z (unshifted)
		if (!strcmp(szKey, "a")) {
			ki.wScan = (WORD)DIKEYBOARD_A;
			ki.wVk = 'A';
		}
		if (!strcmp(szKey, "b")) {
			ki.wScan = (WORD)DIKEYBOARD_B;
			ki.wVk = 'B';
		}
		if (!strcmp(szKey, "c")) {
			ki.wScan = (WORD)DIKEYBOARD_C;
			ki.wVk = 'C';
		}
		if (!strcmp(szKey, "d")) {
			ki.wScan = (WORD)DIKEYBOARD_D;
			ki.wVk = 'D';
		}
		if (!strcmp(szKey, "e")) {
			ki.wScan = (WORD)DIKEYBOARD_E;
			ki.wVk = 'E';
		}
		if (!strcmp(szKey, "f")) {
			ki.wScan = (WORD)DIKEYBOARD_F;
			ki.wVk = 'F';
		}
		if (!strcmp(szKey, "g")) {
			ki.wScan = (WORD)DIKEYBOARD_G;
			ki.wVk = 'G';
		}
		if (!strcmp(szKey, "h")) {
			ki.wScan = (WORD)DIKEYBOARD_H;
			ki.wVk = 'H';
		}
		if (!strcmp(szKey, "i")) {
			ki.wScan = (WORD)DIKEYBOARD_I;
			ki.wVk = 'I';
		}
		if (!strcmp(szKey, "j")) {
			ki.wScan = (WORD)DIKEYBOARD_J;
			ki.wVk = 'J';
		}
		if (!strcmp(szKey, "k")) {
			ki.wScan = (WORD)DIKEYBOARD_K;
			ki.wVk = 'K';
		}
		if (!strcmp(szKey, "l")) {
			ki.wScan = (WORD)DIKEYBOARD_L;
			ki.wVk = 'L';
		}
		if (!strcmp(szKey, "m")) {
			ki.wScan = (WORD)DIKEYBOARD_M;
			ki.wVk = 'M';
		}
		if (!strcmp(szKey, "n")) {
			ki.wScan = (WORD)DIKEYBOARD_N;
			ki.wVk = 'N';
		}
		if (!strcmp(szKey, "o")) {
			ki.wScan = (WORD)DIKEYBOARD_O;
			ki.wVk = 'O';
		}
		if (!strcmp(szKey, "p")) {
			ki.wScan = (WORD)DIKEYBOARD_P;
			ki.wVk = 'P';
		}
		if (!strcmp(szKey, "q")) {
			ki.wScan = (WORD)DIKEYBOARD_Q;
			ki.wVk = 'Q';
		}
		if (!strcmp(szKey, "r")) {
			ki.wScan = (WORD)DIKEYBOARD_R;
			ki.wVk = 'R';
		}
		if (!strcmp(szKey, "s")) {
			ki.wScan = (WORD)DIKEYBOARD_S;
			ki.wVk = 'S';
		}
		if (!strcmp(szKey, "t")) {
			ki.wScan = (WORD)DIKEYBOARD_T;
			ki.wVk = 'T';
		}
		if (!strcmp(szKey, "u")) {
			ki.wScan = (WORD)DIKEYBOARD_U;
			ki.wVk = 'U';
		}
		if (!strcmp(szKey, "v")) {
			ki.wScan = (WORD)DIKEYBOARD_V;
			ki.wVk = 'V';
		}
		if (!strcmp(szKey, "w")) {
			ki.wScan = (WORD)DIKEYBOARD_W;
			ki.wVk = 'W';
		}
		if (!strcmp(szKey, "x")) {
			ki.wScan = (WORD)DIKEYBOARD_X;
			ki.wVk = 'X';
		}
		if (!strcmp(szKey, "y")) {
			ki.wScan = (WORD)DIKEYBOARD_Y;
			ki.wVk = 'Y';
		}
		if (!strcmp(szKey, "z")) {
			ki.wScan = (WORD)DIKEYBOARD_Z;
			ki.wVk = 'Z';
		}

		// A-Z (shifted)
		if (!strcmp(szKey, "A")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;
			ki.wVk = VK_SHIFT;
			keys.push_back(ki);

			ki.wScan = (WORD)DIKEYBOARD_A;
			ki.wVk = 'A';
		}
		if (!strcmp(szKey, "B")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;
			ki.wVk = VK_SHIFT;
			keys.push_back(ki);

			ki.wScan = (WORD)DIKEYBOARD_B;
			ki.wVk = 'B';
		}
		if (!strcmp(szKey, "C")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;
			ki.wVk = VK_SHIFT;
			keys.push_back(ki);

			ki.wScan = (WORD)DIKEYBOARD_C;
			ki.wVk = 'C';
		}
		if (!strcmp(szKey, "D")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;
			ki.wVk = VK_SHIFT;
			keys.push_back(ki);

			ki.wScan = (WORD)DIKEYBOARD_D;
			ki.wVk = 'D';
		}
		if (!strcmp(szKey, "E")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;
			ki.wVk = VK_SHIFT;
			keys.push_back(ki);

			ki.wScan = (WORD)DIKEYBOARD_E;
			ki.wVk = 'E';
		}
		if (!strcmp(szKey, "F")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;
			ki.wVk = VK_SHIFT;
			keys.push_back(ki);

			ki.wScan = (WORD)DIKEYBOARD_F;
			ki.wVk = 'F';
		}
		if (!strcmp(szKey, "G")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;
			ki.wVk = VK_SHIFT;
			keys.push_back(ki);

			ki.wScan = (WORD)DIKEYBOARD_G;
			ki.wVk = 'G';
		}
		if (!strcmp(szKey, "H")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;
			ki.wVk = VK_SHIFT;
			keys.push_back(ki);

			ki.wScan = (WORD)DIKEYBOARD_H;
			ki.wVk = 'H';
		}
		if (!strcmp(szKey, "I")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;
			ki.wVk = VK_SHIFT;
			keys.push_back(ki);

			ki.wScan = (WORD)DIKEYBOARD_I;
			ki.wVk = 'I';
		}
		if (!strcmp(szKey, "J")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;
			ki.wVk = VK_SHIFT;
			keys.push_back(ki);

			ki.wScan = (WORD)DIKEYBOARD_J;
			ki.wVk = 'J';
		}
		if (!strcmp(szKey, "K")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;
			ki.wVk = VK_SHIFT;
			keys.push_back(ki);

			ki.wScan = (WORD)DIKEYBOARD_K;
			ki.wVk = 'K';
		}
		if (!strcmp(szKey, "L")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;
			ki.wVk = VK_SHIFT;
			keys.push_back(ki);

			ki.wScan = (WORD)DIKEYBOARD_L;
			ki.wVk = 'L';
		}
		if (!strcmp(szKey, "M")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;
			ki.wVk = VK_SHIFT;
			keys.push_back(ki);

			ki.wScan = (WORD)DIKEYBOARD_M;
			ki.wVk = 'M';
		}
		if (!strcmp(szKey, "N")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;
			ki.wVk = VK_SHIFT;
			keys.push_back(ki);

			ki.wScan = (WORD)DIKEYBOARD_N;
			ki.wVk = 'N';
		}
		if (!strcmp(szKey, "O")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;
			ki.wVk = VK_SHIFT;
			keys.push_back(ki);

			ki.wScan = (WORD)DIKEYBOARD_O;
			ki.wVk = 'O';
		}
		if (!strcmp(szKey, "P")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;
			ki.wVk = VK_SHIFT;
			keys.push_back(ki);

			ki.wScan = (WORD)DIKEYBOARD_P;
			ki.wVk = 'P';
		}
		if (!strcmp(szKey, "Q")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;
			ki.wVk = VK_SHIFT;
			keys.push_back(ki);

			ki.wScan = (WORD)DIKEYBOARD_Q;
			ki.wVk = 'Q';
		}
		if (!strcmp(szKey, "R")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;
			ki.wVk = VK_SHIFT;
			keys.push_back(ki);

			ki.wScan = (WORD)DIKEYBOARD_R;
			ki.wVk = 'R';
		}
		if (!strcmp(szKey, "S")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;
			ki.wVk = VK_SHIFT;
			keys.push_back(ki);

			ki.wScan = (WORD)DIKEYBOARD_S;
			ki.wVk = 'S';
		}
		if (!strcmp(szKey, "T")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;
			ki.wVk = VK_SHIFT;
			keys.push_back(ki);

			ki.wScan = (WORD)DIKEYBOARD_T;
			ki.wVk = 'T';
		}
		if (!strcmp(szKey, "U")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;
			ki.wVk = VK_SHIFT;
			keys.push_back(ki);

			ki.wScan = (WORD)DIKEYBOARD_U;
			ki.wVk = 'U';
		}
		if (!strcmp(szKey, "V")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;
			ki.wVk = VK_SHIFT;
			keys.push_back(ki);

			ki.wScan = (WORD)DIKEYBOARD_V;
			ki.wVk = 'V';
		}
		if (!strcmp(szKey, "W")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;
			ki.wVk = VK_SHIFT;
			keys.push_back(ki);

			ki.wScan = (WORD)DIKEYBOARD_W;
			ki.wVk = 'W';
		}
		if (!strcmp(szKey, "X")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;
			ki.wVk = VK_SHIFT;
			keys.push_back(ki);

			ki.wScan = (WORD)DIKEYBOARD_X;
			ki.wVk = 'X';
		}
		if (!strcmp(szKey, "Y")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;
			ki.wVk = VK_SHIFT;
			keys.push_back(ki);

			ki.wScan = (WORD)DIKEYBOARD_Y;
			ki.wVk = 'Y';
		}
		if (!strcmp(szKey, "Z")) {
			ki.wScan = (WORD)DIKEYBOARD_LSHIFT;
			ki.wVk = VK_SHIFT;
			keys.push_back(ki);

			ki.wScan = (WORD)DIKEYBOARD_Z;
			ki.wVk = 'Z';
		}

		keys.push_back(ki);

next:
		// Find the next key.
		szKey = strtok_s(NULL, szDelim, &szNext);
	}

	// Actually press/release the keys.
	for (size_t i = 0; i < keys.size(); i++) {
		INPUT input;
		ZeroMemory(&input, sizeof(input));
		input.type = INPUT_KEYBOARD;
		memcpy(&input.ki, &keys[i], sizeof(input.ki));

		INPUT inputArray[] = {input};
		SendInput(_countof(inputArray), inputArray, sizeof(INPUT));
	}
}
