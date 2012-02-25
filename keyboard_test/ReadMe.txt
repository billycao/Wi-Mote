How to set up project in Eclipse:

1. File -> New Android Project.
2. Project Name: keyboard_test
3. Create project from existing source.
4. Specify location of keyboard_test folder.
5. Package Name: keyboard.test.

-------------------------------------------------------------------------------

About new virtual keyboard:

*** NEW!!! *** SUPPORTS NKRO (N-KEY ROLLOVER), MEANING YOU CAN PRESS AS MANY
VIRTUAL KEYS AS YOU WANT SIMULTANEOUSLY (LIMITED ONLY BY YOUR SCREEN'S TOUCH
SENSOR). Obviously requires a multitouch screen. Most multitouch screens can
handle at most two points, which means you can press at most 2 keys at a time.
But our program should be future-proof.

The new virtual keyboard includes most of the keys from the main part of the
typical PC keyboard. There is no longer the need to rely on the built-in
Android virtual keyboard. However, the user still has the choice to use the
Android virtual keyboard by long-pressing the MENU button. The user can also
opt to use the physical keyboard if the phone has one.

Differences from the built-in Android virtual keyboard:

- Our virtual keyboard is implemented as Android UI buttons. The Android
  virtual keyboard, on the other hand, is a full-blown soft keyboard IME. While
  we could have also implemented our keyboard as a soft keyboard, that would be
  overkill since we are not intending to use our keyboard with other apps.

- Our virtual keyboard allows the user to hold down a key for an extended
  period of time, which makes it possible to play games (e.g. WASD keys). This
  is not possible on the Android virtual keyboard. Only a physical keyboard
  would allow this.

"Mod" button toggles the behavior of the modifier keys (SHIFT, CTRL, WIN, ALT).
By default "Mod" is turned on, which means the modifier keys act normally, i.e.
they cannot be pressed on their own; rather, they must modify another key
(e.g. CTRL+A). If "Mod" is turned off, then the modifier keys can be pressed
and even held down like any other key, which is useful for special applications
such as games (e.g. if you want to press CTRL to crouch).

"LC" and "RC" are for mouse left click and right click respectively. At the
moment, they are not implemented yet. This is intended for use with
accelerometer mouse while in keyboard mode.