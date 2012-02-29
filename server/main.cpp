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

extern void WINAPI processStream(const char *szStream);

int main()
{
	int iResult = 0;

	WSADATA wsaData;

	SOCKET RecvSocket;
	sockaddr_in RecvAddr;

	unsigned short Port = 27015;

	char RecvBuf[1024];
	int BufLen = 1024;

	sockaddr_in SenderAddr;
	int SenderAddrSize = sizeof (SenderAddr);

	//-----------------------------------------------
	// Initialize Winsock
	iResult = WSAStartup(MAKEWORD(2, 2), &wsaData);
	if (iResult != NO_ERROR) {
		wprintf(L"WSAStartup failed with error %d\n", iResult);
		return 1;
	}
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
	RecvAddr.sin_port = htons(Port);
	RecvAddr.sin_addr.s_addr = htonl(INADDR_ANY);

	iResult = bind(RecvSocket, (SOCKADDR *) & RecvAddr, sizeof (RecvAddr));
	if (iResult != 0) {
		wprintf(L"bind failed with error %d\n", WSAGetLastError());
		return 1;
	}
	for (;;) {
		//-----------------------------------------------
		// Call the recvfrom function to receive datagrams
		// on the bound socket.
		ZeroMemory(RecvBuf, BufLen);
		iResult = recvfrom(RecvSocket,
						   RecvBuf, BufLen, 0, (SOCKADDR *) & SenderAddr, &SenderAddrSize);
		if (iResult == SOCKET_ERROR) {
			wprintf(L"recvfrom failed with error %d\n", WSAGetLastError());
		}
		//printf("Received data: %s\n", RecvBuf);

		processStream(RecvBuf);
	}
 
	//-----------------------------------------------
	// Close the socket when finished receiving datagrams
	wprintf(L"Finished receiving. Closing socket.\n");
	iResult = closesocket(RecvSocket);
	if (iResult == SOCKET_ERROR) {
		wprintf(L"closesocket failed with error %d\n", WSAGetLastError());
		return 1;
	}

	//-----------------------------------------------
	// Clean up and exit.
	wprintf(L"Exiting.\n");
	WSACleanup();
	return 0;
}
