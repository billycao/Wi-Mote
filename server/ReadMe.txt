New updates:

- A PC might have multiple IP addresses (e.g. one for Wi-Fi, one for Ethernet).
  Unfortunately, some of these IP addresses might be useless (e.g. due to
  virtual network adapters - thanks Saqib for the test). For simplicity, only
  the first IP address found will now be displayed, which means we are assuming
  that the first IP address found is the main IP address of the PC.

- New PIN feature. The server and Android client must now agree on a common PIN
  in order to function. This is to prevent unauthorized control of a PC. If
  there are at least 10 failed attempts to control the PC due to mismatched
  PIN, the server will automatically stop to curb brute force attacks.

  If the user is running the server for the first time, a random PIN will be
  generated. Once the PIN is stored in WiMote.ini along with the port number,
  the server will always use the value from this file.

  The PIN must be in the range 0-65536.

  On the Android side, the WiMoteUtil class has been updated to transmit PIN.
  However, the PIN is currently hardcoded to be 1337.
  TODO: Add Android UI preferences for PIN.

-------------------------------------------------------------------------------

In Visual Studio 2010:

1. File -> New -> Project.
2. In the left list, select Visual C++.
3. In the adjacent list, select Win32 Application. (NOT Console Application!)
4. Specify name and location of project, and click OK.
5. Click Next.
6. Select Empty Project and click Finish.
7. Add files to project.