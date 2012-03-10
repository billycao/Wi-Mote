#define WIN32_LEAN_AND_MEAN

#include <windows.h>
#include <winsock2.h>
#include <ws2tcpip.h>

extern BOOL bServerRunning;
extern int port;
extern WSADATA wsaData;

extern void WINAPI processStream(const char *szStream);

DWORD WINAPI serverThread(LPVOID lpParam)
{
	SOCKET recvSocket = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);
	if (recvSocket == INVALID_SOCKET)
		return 1;

	sockaddr_in recvAddr;
	ZeroMemory(&recvAddr, sizeof(recvAddr));
	recvAddr.sin_family = AF_INET;
	recvAddr.sin_port = htons((USHORT)port);
	recvAddr.sin_addr.s_addr = htonl(INADDR_ANY);

	if (bind(recvSocket, (SOCKADDR *)&recvAddr, sizeof(recvAddr)) != 0)
		return 1;

	u_long nonblocking = 1;
	ioctlsocket(recvSocket, FIONBIO, &nonblocking);

	for (;;) {
		char recvBuf[1024];
		sockaddr_in senderAddr;
		int senderAddrSize = sizeof(senderAddr);
		ZeroMemory(recvBuf, sizeof(recvBuf));
		ZeroMemory(&senderAddr, senderAddrSize);
		if (recvfrom(recvSocket, recvBuf, sizeof(recvBuf), 0, (SOCKADDR *)&senderAddr, &senderAddrSize) != SOCKET_ERROR)
			processStream(recvBuf);
		if (!bServerRunning)
			break;
		Sleep(1);
	}

	if (closesocket(recvSocket) == SOCKET_ERROR)
		return 1;

	return 0;
}
