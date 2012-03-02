// TODO: Tweak server code to fit our needs (right now is pretty much copy &
//       paste from Microsoft example).

#define WIN32_LEAN_AND_MEAN

#include <windows.h>
#include <winsock2.h>
#include <ws2tcpip.h>
#include <tchar.h>
#include <stdlib.h>
#include <stdio.h>

// Link with ws2_32.lib
#pragma comment(lib, "Ws2_32.lib")

extern BOOL bServerRunning;
extern int port;
extern WSADATA wsaData;

extern void WINAPI processStream(const char *szStream);

DWORD WINAPI serverThread(LPVOID lpParam)
{
	int iResult = 0;

	SOCKET RecvSocket;
	sockaddr_in RecvAddr;

	char RecvBuf[1024];
	int BufLen = 1024;

	sockaddr_in SenderAddr;
	int SenderAddrSize = sizeof (SenderAddr);

	//-----------------------------------------------
	// Create a receiver socket to receive datagrams
	RecvSocket = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);
	if (RecvSocket == INVALID_SOCKET) {
		wprintf(L"socket failed with error %d\n", WSAGetLastError());
		return 1;
	}
	//-----------------------------------------------
	// Bind the socket to any address and the specified port.
	RecvAddr.sin_family = AF_INET;
	RecvAddr.sin_port = htons((USHORT)port);
	RecvAddr.sin_addr.s_addr = htonl(INADDR_ANY);

	iResult = bind(RecvSocket, (SOCKADDR *) & RecvAddr, sizeof (RecvAddr));
	if (iResult != 0) {
		wprintf(L"bind failed with error %d\n", WSAGetLastError());
		return 1;
	}

	u_long nonblocking = 1;
	ioctlsocket(RecvSocket, FIONBIO, &nonblocking);

	for (;;) {
		//-----------------------------------------------
		// Call the recvfrom function to receive datagrams
		// on the bound socket.
		ZeroMemory(RecvBuf, BufLen);
		iResult = recvfrom(RecvSocket,
						   RecvBuf, BufLen, 0, (SOCKADDR *) & SenderAddr, &SenderAddrSize);
		if (iResult != SOCKET_ERROR)
			processStream(RecvBuf);
		if (!bServerRunning)
			break;
		Sleep(1);
	}
 
	//-----------------------------------------------
	// Close the socket when finished receiving datagrams
	wprintf(L"Finished receiving. Closing socket.\n");
	iResult = closesocket(RecvSocket);
	if (iResult == SOCKET_ERROR) {
		wprintf(L"closesocket failed with error %d\n", WSAGetLastError());
		return 1;
	}

	return 0;
}
