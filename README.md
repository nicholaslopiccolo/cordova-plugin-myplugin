## cordova-plugin-myplugin
cordova plugin for android application.

# install
cordova plugin add https://github.com/nicholaslopiccolo/cordova-plugin-myplugin.git

# How to test new code before commit
cordova plugin add --link ../cordova-plugin-myplugin/

# To add
to the android manifest
```
<uses-permission android:name="android.permission.SEND_SMS" />
```
# Debug a plugin
Listen all the Log from a specific tag:
Java code example:
```
import android.util.Log;
Log.i("tag","this is a log.");
```

From terminal:
```
adb -d logcat tag:I *:S
```

# continuo
Un buon plugin da cui prendere spunto: https://github.com/katzer/cordova-plugin-local-notifications/blob/master/plugin.xml



