# ztxfdty
a fudandtiyu exercise information crawler Android app by ztx

## Why make it

There is no application on Android that can directly get exercise information. So we have to visit the fdty-website. But, the website is using the old UIS to authenticate student, and studentcode and password on the old UIS page cannot be remembered. So this is a very troublesome thing. Why cann't I make an app for Android, and I just need to simply click the app icon to get my exercise information?

## What it is

Its core is a crawler. The app remeber the studentcode and password, and use them to login fdty-website in backstage.

## Security

Because I'm a noob, I don't know how to protect the app data. The app is using SharedPreferences file to keep data, and MODE_PRIVATE was setted. So, If your phone is safe and does not installed any strange app, it's safe enough. You can check the source code in this repository. I didn't upload the whole AS project, because it's too big. If you still don't trust, you can remake an app and then use it. **For safety sake, get the app by the link below!**



## Requirement

Android version >= 4

## Download

[https://github.com/zhangtianxiang/ztxfdty/raw/master/app/release/app-release.apk](https://github.com/zhangtianxiang/ztxfdty/raw/master/app/release/app-release.apk)

## Caution

Please do not use this app if you can't guarantee your phone's security.
Please do not enter your account password on someone else's mobile phone.
Please assess the risk yourself.
The author does not collect any user information, and the author is not responsible for the loss caused by the app.

## Known Issue

Abnormal exit:

- When http://www.fdty.fudan.edu.cn cannot be accessed.
- When your exercise record is empty.

## LICEN
MIT
