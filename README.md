# File cloud upload V2 plug-in

# 1. Overview
This plug-in uploads images directly from RICOH THETA to Google Drive server.

# 2. Terms of Service

> You agree to comply with all applicable export and import laws and regulations applicable to the jurisdiction in which the Software was obtained and in which it is used. Without limiting the foregoing, in connection with use of the Software, you shall not export or re-export the Software into any U.S. embargoed countries (currently including, but necessarily limited to, Crimea – Region of Ukraine, Cuba, Iran, North Korea, Sudan and Syria) or to anyone on the U.S. Treasury Department’s list of Specially Designated Nationals or the U.S. Department of Commerce Denied Person’s List or Entity List.  By using the Software, you represent and warrant that you are not located in any such country or on any such list.  You also agree that you will not use the Software for any purposes prohibited by any applicable laws, including, without limitation, the development, design, manufacture or production of missiles, nuclear, chemical or biological weapons.

By using the File cloud upload V2 plug-in, you are agreeing to the above and the license terms, [LICENSE.txt](LICENSE.txt).

Copyright &copy; 2019 Ricoh Company, Ltd.

# 3. Development Environment

* RICOH THETA V and RICOH THETA Z1
* Firmware version 2.50.1 (V), 1.03.5 (Z1)

> How to update your RICOH THETA firmware:
> * [THETA V](https://support.theta360.com/en/manual/v/content/update/update_01.html)
> * [THETA Z1](https://support.theta360.com/en/manual/z1/content/update/update_01.html)


The source code here is not exactly same as the one on [PLUG-IN store](https://pluginstore.theta360.com/plugins/com.theta360.clouduploadv2/), because Google client id and secret key is for private.
At least, **it is needed to set your client id and secret in ["api.properties"](/app/src/main/assets/api.properties)** to work fine after build.
See ["Setting up OAuth 2.0"](https://support.google.com/cloud/answer/6158849) in detail.

# 4. Install
Android Studio install apk after build automatically. Or use the following command after build.

```
adb install -r app-debug.apk
```

Or install from [PLUG-IN store](https://pluginstore.theta360.com/plugins/com.theta360.clouduploadv2/) by using RICOH THETA app for Windows or macOS.

### Give permissions for this plug-in.

Using desktop viewing app as Vysor, open Settings app and turns on the permissions at "Apps" > "File Cloud Upload V2" > "Permissions"

# 5. How to Use
The plugin setting (login to Google) is needed only once at the first time to upload.
After the setting, photos in THETA can be uploaded by pressing shutter button after launching this plug-in.


1. Turn on the THETA.
2. Open RICOH THETA app on your Win/Mac.
3. Set this plug-in as an active plugin from "File" > "Plug-in management..."
4. Connect THETA to Wireless-LAN by client mode.

   For example, let's assume that there are a THETA, a macOS machine and an iPhone on the same wireless LAN.

5. Set active plug-in
   1. Open the THETA mobile app on an iOS / Android smartphone.
   2. Tap "Settings" at right bottom corner.
   3. Confirm "Connection" is "Wi-Fi" or "Wi-Fi+Bluetooth".
   4. Tap "Camera settings".
   5. Tap "Plug-in".
   6. Select "File cloud upload V2".

6. Check IP address of the camera
   1. Back to the Camera settings
   2. Check IP address of THETA on smartphone app. 
   If you use macOS type "dns-sd -q THETAYL01234567.local" in Terminal. Here "THETAYL01234567" is an example, please change to your serial number.
   
7. Launch plug-in
   
   There are three ways to launch "File cloud upload V2".
   * Press Mode button till LED2 turns white  
   * On the browser, open the URL "http://*ip-address*/plugin" for THETA V, or  "http://*ip-address*/plugin?id=*plugin-order*" for THETA Z1.  
   * Launch plug-in from the smartphone app (RICOH THETA)

8. Open Web UI of plug-in

    Open the URL "http://*ip-address*:8888/" on the browser.  
    Here, *ip-address* is an example. Change it to your THETA's IP address.  

9. Authenticate your Google Account

   1. Press "Unregistered" at the right of "Google Drive".
   1. Copy the code displayed in the web page and press "Login".
   1. New window will be opened. It is Google's site. Please following the message of the page. The code which is copied above will be needed to enter.
   1. After login to Google, back to the original web page.
   1. Press "Done" at the right of the title "Google Drive".
   
10. Start upload
   
    Press shutter button of the camera
     or
    press "Start uploading" button on the Web UI.

11. (option) Stop upload

    During upload, Camera and LIVE LEDs are blinking. Progress is shown on the Web UI.  
    To stop upload,
    press shutter button
     or
     press "Stop uploading" button on the Web UI.
	 
12. Finish plug-in
	 
    Press Mode button of the camera till LED2 turns blue
     or
    press power button of the camera to sleep.

# 6. History
* ver.1.0.0 (2019/05/21): Initial version.

---

## Trademark Information

The names of products and services described in this document are trademarks or registered trademarks of each company.

* Android, Nexus, Google Chrome, Google Play, Google Play logo, Google Maps, Google+, Gmail, Google Drive, Google Cloud Print and YouTube are trademarks of Google Inc.
* Apple, Apple logo, Macintosh, Mac, Mac OS, OS X, AppleTalk, Apple TV, App Store, AirPrint, Bonjour, iPhone, iPad, iPad mini, iPad Air, iPod, iPod mini, iPod classic, iPod touch, iWork, Safari, the App Store logo, the AirPrint logo, Retina and iPad Pro are trademarks of Apple Inc., registered in the United States and other countries. The App Store is a service mark of Apple Inc.
* Microsoft, Windows, Windows Vista, Windows Live, Windows Media, Windows Server System, Windows Server, Excel, PowerPoint, Photosynth, SQL Server, Internet Explorer, Azure, Active Directory, OneDrive, Outlook, Wingdings, Hyper-V, Visual Basic, Visual C ++, Surface, SharePoint Server, Microsoft Edge, Active Directory, BitLocker, .NET Framework and Skype are registered trademarks or trademarks of Microsoft Corporation in the United States and other countries. The name of Skype, the trademarks and logos associated with it, and the "S" logo are trademarks of Skype or its affiliates.
* Wi-Fi™, Wi-Fi Certified Miracast, Wi-Fi Certified logo, Wi-Fi Direct, Wi-Fi Protected Setup, WPA, WPA 2 and Miracast are trademarks of the Wi-Fi Alliance.
* The official name of Windows is Microsoft Windows Operating System.
* All other trademarks belong to their respective owners.
