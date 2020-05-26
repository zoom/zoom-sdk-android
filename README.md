# Zoom Android Software Development Kit (SDK)

<div align="center">
<img src="https://s3.amazonaws.com/user-content.stoplight.io/8987/1541013063688" width="400px" max-height="400px" style="margin:auto;"/>
</div>

## Table of Contents
- [:rotating_light: Announcement :rotating_light:](#rotatinglight-announcement-rotatinglight)
- [Latest SDK Notifications](#latest-sdk-notifications)
- [Full Documentation && Community Support](#full-documentation-community-support)
- [What is Zoom Android SDK?](#what-is-zoom-android-sdk)
- [Disclaimer](#disclaimer)
- [Getting Started](#getting-started)
	- [Prerequisites](#prerequisites)
	- [Installing](#installing)
- [Running the examples](#running-the-examples)
- [Documentation](#documentation)
- [Navigating SDK sample files](#navigating-sdk-sample-files)
- [SDK Reference](#sdk-reference)
- [Versioning](#versioning)
- [Change log](#change-log)
- [Frequently Asked Questions (FAQ)](#frequently-asked-questions-faq)
- [Support](#support)
- [License](#license)
- [Acknowledgments](#acknowledgments)


## :rotating_light: Announcement :rotating_light:
 To align with Zoom’s [recent announcement](https://blog.zoom.us/wordpress/2020/04/22/zoom-hits-milestone-on-90-day-security-plan-releases-zoom-5-0/) pertaining to our security initiative, Zoom Client SDKs have added **AES 256-bit GCM encryption** support, which provides more protection for meeting data and greater resistance to tampering. **The system-wide account enablement of AES 256-bit GCM encryption will take place on June 01.** You are **strongly recommended** to start the required upgrade to this latest version 4.6.21666.0429 at your earliest convenience. Please note that any Client SDK versions below 4.6.21666.0429 will **no longer be operational** from June 01.

 > If you would like to test the latest SDK with AES 256-bit GCM encryption meeting before 05/30, you may:
 > 1. Download the latest version of Zoom client: https://zoom.us/download
 > 2. Visit https://zoom.us/testgcm and launch a GCM enabled meeting with your Zoom client, you will see a Green Shield icon that indicates the GCM encryption is enabled
 > 3. Use SDK to join this meeting


## Latest SDK Notifications
1. :red_circle: Non-AndroidX Version (EOL: 01/01/2020)
**Per Google's suggestions and guidance, we have upgraded our regular Android SDK to support AndroidX. We used to offer a non-AndroidX version to help you migrate to the AndroidX project. However, the previous non-AndroidX version has reached its End-of-Life (01/01/20). Please plan to upgrade your Android project to AndroidX to use the latest Android SDK**

## Full Documentation && Community Support
You can find the full Zoom Android SDK documentation and the community support forum here:
<div align="center">
   <a target="_blank" href="https://marketplace.zoom.us/docs/sdk/native-sdks/android" style="text-decoration:none">
   <img src="https://s3-us-west-1.amazonaws.com/sdk.zoom.us/Doc-button.png" width="350px" max-height="350px" style="margin:1vh 1vw;"/>
   </a>
   <a target="_blank" href="https://devforum.zoom.us/c/mobile-sdk" style="text-decoration:none">
   <img src="https://s3-us-west-1.amazonaws.com/sdk.zoom.us/Forum-button.png" width="350px" max-height="350px" style="margin:1vh 1vw;"/>
   </a>
</div>

## What is Zoom Android SDK?

Zoom SDK makes it easy to integrate Zoom with your Android applications, and boosts up your applications with the power of Zoom.

* **Easy to use**: Our SDK is built to be easy to use. Just import the libraries, call a few functions, and we will take care all video conferencing related stuffs for you.
* **Localizable**: Our SDK naturally supports 7 major languages, and you can add more to grow your applications internationally.
* **Custom Meeting UI**: If you want to add your own decorations to your Zoom meeting rooms, try our Custom UI feature to make your meeting room special.

## Disclaimer

**Please be aware that all hard-coded variables and constants shown in the documentation and in the demo, such as Zoom Token, Zoom Access, Token, etc., are ONLY FOR DEMO AND TESTING PURPOSES. We STRONGLY DISCOURAGE the way of HARDCODING any Zoom Credentials (username, password, API Keys & secrets, SDK keys & secrets, etc.) or any Personal Identifiable Information (PII) inside your application. WE DON’T MAKE ANY COMMITMENTS ABOUT ANY LOSS CAUSED BY HARD-CODING CREDENTIALS OR SENSITIVE INFORMATION INSIDE YOUR APP WHEN DEVELOPING WITH OUR SDK**.

## Getting Started

The following instructions will get you a copy of the project up and running on your local machine for development and testing purposes.
* For detailed instructions, please refer to our documentation website: [https://marketplace.zoom.us/docs/sdk/native-sdks/android](https://marketplace.zoom.us/docs/sdk/native-sdks/android);
* If you need support or assistance, please visit our [Zoom Developer Community Forum](https://devforum.zoom.us/);

### Prerequisites

Before you try out our SDK, you would need the following to get started:

* **A Zoom Account**: If you do not have one, you can sign up at [https://zoom.us/signup](https://zoom.us/signup).
  * Once you have your Zoom Account, sign up for a 60-days free trial at [https://marketplace.zoom.us/](https://marketplace.zoom.us/)
* **An Android device**:
  * OS: Android 5.0 (API Level 21) or later.
  * CPU: armeabi-v7a, x86, armeabi, arm64-v8a, x86_64
* **Android Project**:
  * Support **AndroidX**
* **Gradle settings**:
  * **compileSdkVersion**: 29+
  * **buildToolsVersion**: 29+
  * **minSdkVersion**: 21
  * **Required dependencies**
  ```
  implementation 'androidx.multidex:multidex:2.0.0'
  implementation 'androidx.recyclerview:recyclerview:1.0.0'
  implementation 'androidx.appcompat:appcompat:1.0.0'
  implementation 'androidx.constraintlayout:constraintlayout:1.1.3'
  implementation 'com.google.android.material:material:1.0.0-rc01'
  ```


### Installing

Clone or download a copy of our SDK files from GitHub. After you unzipped the file, you should have the following folders:

```
.
├── CHANGELOG.md
├── LICENSE.md
├── README.md
├── docs
├── [mobilertc-android-studio] <- Libraries and examples are inside.
├── proguard.cfg
└── version.txt
```
Launch your **Android Studio**, navigate to the "mobilerte-android-studio" folder, and open it.

Once the project is fully loaded, you will see the following structure:

```
.
├── build.gradle
├── commonlib
├── example2
├── gradle
├── gradlew
├── gradlew.bat
├── mobilertc
├── sample
└── settings.gradle
```

We provide 2 different examples for you:
 * sample: An Android app that has all basic features for both login users and non-login users.
 * example2: An Android app that shows how to join meeting without any login credentials.

In `sample` app, we provide detailed examples of how to implement each feature, which could be very helpful when you are integrating Zoom SDK into your app.
Here are the categories of the code sample that we offer
```
.
├── initsdk
├── inmeetingfunction
│   ├── customizedmeetingui
│   │   ├── audio
│   │   ├── other
│   │   ├── remotecontrol
│   │   ├── share
│   │   ├── user
│   │   ├── video
│   │   └── view
│   │       ├── adapter
│   │       └── share
│   └── zoommeetingui
├── otherfeatures
│   └── scheduleforloginuser
├── startjoinmeeting
│   ├── apiuser
│   ├── emailloginuser
│   ├── joinmeetingonly
│   └── ssologinuser
└── ui
```


## Running the examples

Connect your Android device to your computer and simply press "Run" on selected example, the example will run on your device.


## Documentation

Please visit [[https://marketplace.zoom.us/docs/sdk/native-sdks/android](https://marketplace.zoom.us/docs/sdk/native-sdks/android)] for details of each features and functions.

## Navigating SDK sample files

The following table provides the link to the implementation of each features in our demo app:

| UI mode            | Feature                                                      | Corresponding sample files                                   |
| ------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| Essential features | SDK Initialization & Authentication                          | * [InitAuthSDKHelper.java](https://github.com/zoom/zoom-sdk-android/blob/master/mobilertc-android-studio/sample/src/main/java/us/zoom/sdksample/initsdk/InitAuthSDKHelper.java) <br />* [InitAuthSDKCallback.java](https://github.com/zoom/zoom-sdk-android/blob/master/mobilertc-android-studio/sample/src/main/java/us/zoom/sdksample/initsdk/InitAuthSDKCallback.java) |
|                    | Authenticate with Zoom REST API and start a meeting as API user | * [ApiUserStartMeetingHelper.java](https://github.com/zoom/zoom-sdk-android/blob/master/mobilertc-android-studio/sample/src/main/java/us/zoom/sdksample/startjoinmeeting/apiuser/ApiUserStartMeetingHelper.java) <br />* [APIUserInfoHelper.java](https://github.com/zoom/zoom-sdk-android/blob/master/mobilertc-android-studio/sample/src/main/java/us/zoom/sdksample/startjoinmeeting/apiuser/APIUserInfoHelper.java) <br />* [APIUserInfo.java](https://github.com/zoom/zoom-sdk-android/blob/master/mobilertc-android-studio/sample/src/main/java/us/zoom/sdksample/startjoinmeeting/apiuser/APIUserInfo.java) <br />* [APIUserConstants.java](https://github.com/zoom/zoom-sdk-android/blob/master/mobilertc-android-studio/sample/src/main/java/us/zoom/sdksample/startjoinmeeting/apiuser/APIUserConstants.java) |
|                    | Login with email & password                                  | * [EmailUserLoginHelper.java](https://github.com/zoom/zoom-sdk-android/blob/master/mobilertc-android-studio/sample/src/main/java/us/zoom/sdksample/startjoinmeeting/emailloginuser/EmailUserLoginHelper.java) <br />* [UserLoginCallback.java](https://github.com/zoom/zoom-sdk-android/blob/master/mobilertc-android-studio/sample/src/main/java/us/zoom/sdksample/startjoinmeeting/UserLoginCallback.java) |
|                    | Login with SSO token                                         | * [SSOlUserLoginHelper.java](https://github.com/zoom/zoom-sdk-android/blob/master/mobilertc-android-studio/sample/src/main/java/us/zoom/sdksample/startjoinmeeting/ssologinuser/SSOlUserLoginHelper.java) <br />* [UserLoginCallback.java](https://github.com/zoom/zoom-sdk-android/blob/master/mobilertc-android-studio/sample/src/main/java/us/zoom/sdksample/startjoinmeeting/UserLoginCallback.java) |
|                    | Start an instant meeting(For Logged-in user)                 | * [EmailLoginUserStartMeetingHelper.java](https://github.com/zoom/zoom-sdk-android/blob/master/mobilertc-android-studio/sample/src/main/java/us/zoom/sdksample/startjoinmeeting/emailloginuser/EmailLoginUserStartMeetingHelper.java) <br />* [SSOLoginUserStartMeetingHelper.java](https://github.com/zoom/zoom-sdk-android/blob/master/mobilertc-android-studio/sample/src/main/java/us/zoom/sdksample/startjoinmeeting/ssologinuser/SSOLoginUserStartMeetingHelper.java) |
|                    | Join a meeting                                               | * [JoinMeetingHelper.java](https://github.com/zoom/zoom-sdk-android/blob/master/mobilertc-android-studio/sample/src/main/java/us/zoom/sdksample/startjoinmeeting/joinmeetingonly/JoinMeetingHelper.java) |
|                    | Schedule a meeting (For logged-in user)                      | * [ScheduleMeetingExampleActivity.java](https://github.com/zoom/zoom-sdk-android/blob/master/mobilertc-android-studio/sample/src/main/java/us/zoom/sdksample/otherfeatures/scheduleforloginuser/ScheduleMeetingExampleActivity.java) <br />* [PreMeetingExampleActivity.java](https://github.com/zoom/zoom-sdk-android/blob/master/mobilertc-android-studio/sample/src/main/java/us/zoom/sdksample/otherfeatures/scheduleforloginuser/PreMeetingExampleActivity.java) |
|                    | Settings                                                     | * [MeetingSettingActivity.java](https://github.com/zoom/zoom-sdk-android/blob/master/mobilertc-android-studio/sample/src/main/java/us/zoom/sdksample/ui/MeetingSettingActivity.java) <br />* [ZoomMeetingUISettingHelper.java](https://github.com/zoom/zoom-sdk-android/blob/master/mobilertc-android-studio/sample/src/main/java/us/zoom/sdksample/inmeetingfunction/zoommeetingui/ZoomMeetingUISettingHelper.java) |
|                    | Invitation                                                   | * [MyInviteActivity.java](https://github.com/zoom/zoom-sdk-android/blob/master/mobilertc-android-studio/sample/src/main/java/us/zoom/sdksample/otherfeatures/MyInviteActivity.java) <br />* [MyInviteContentGenerator.java](https://github.com/zoom/zoom-sdk-android/blob/master/mobilertc-android-studio/sample/src/main/java/us/zoom/sdksample/otherfeatures/MyInviteContentGenerator.java) |
| Custom UI          | Basic UI management                                          | * [MyMeetingActivity.java](https://github.com/zoom/zoom-sdk-android/blob/master/mobilertc-android-studio/sample/src/main/java/us/zoom/sdksample/inmeetingfunction/customizedmeetingui/MyMeetingActivity.java) <br />* [SimpleInMeetingListener.java](https://github.com/zoom/zoom-sdk-android/blob/master/mobilertc-android-studio/sample/src/main/java/us/zoom/sdksample/inmeetingfunction/customizedmeetingui/SimpleInMeetingListener.java) |
|                    | Video/Share View                                             | * [MeetingWindowHelper.java](https://github.com/zoom/zoom-sdk-android/blob/master/mobilertc-android-studio/sample/src/main/java/us/zoom/sdksample/inmeetingfunction/customizedmeetingui/view/MeetingWindowHelper.java) <br />* [MeetingOptionBar.java](https://github.com/zoom/zoom-sdk-android/blob/master/mobilertc-android-studio/sample/src/main/java/us/zoom/sdksample/inmeetingfunction/customizedmeetingui/view/MeetingOptionBar.java) <br />* [VideoListLayout.java](https://github.com/zoom/zoom-sdk-android/blob/master/mobilertc-android-studio/sample/src/main/java/us/zoom/sdksample/inmeetingfunction/customizedmeetingui/view/VideoListLayout.java) <br />* [AttenderVideoAdapter.java](https://github.com/zoom/zoom-sdk-android/blob/master/mobilertc-android-studio/sample/src/main/java/us/zoom/sdksample/inmeetingfunction/customizedmeetingui/view/adapter/AttenderVideoAdapter.java) <br />* [ToolbarDragView.java](https://github.com/zoom/zoom-sdk-android/blob/master/mobilertc-android-studio/sample/src/main/java/us/zoom/sdksample/inmeetingfunction/customizedmeetingui/view/share/ToolbarDragView.java) <br />* [RCMouseView.java](https://github.com/zoom/zoom-sdk-android/blob/master/mobilertc-android-studio/sample/src/main/java/us/zoom/sdksample/inmeetingfunction/customizedmeetingui/view/share/RCMouseView.java) <br />* [RCFloatView.java](https://github.com/zoom/zoom-sdk-android/blob/master/mobilertc-android-studio/sample/src/main/java/us/zoom/sdksample/inmeetingfunction/customizedmeetingui/view/share/RCFloatView.java) <br />* [CustomShareView.java](https://github.com/zoom/zoom-sdk-android/blob/master/mobilertc-android-studio/sample/src/main/java/us/zoom/sdksample/inmeetingfunction/customizedmeetingui/view/share/CustomShareView.java) <br />* [AnnotateToolbar.java](https://github.com/zoom/zoom-sdk-android/blob/master/mobilertc-android-studio/sample/src/main/java/us/zoom/sdksample/inmeetingfunction/customizedmeetingui/view/share/AnnotateToolbar.java) |
|                    | Basic callback                                               | * [MeetingCommonCallback.java](https://github.com/zoom/zoom-sdk-android/blob/master/mobilertc-android-studio/sample/src/main/java/us/zoom/sdksample/inmeetingfunction/customizedmeetingui/other/MeetingCommonCallback.java) |
|                    | Audio                                                        | * [MeetingAudioHelper.java](https://github.com/zoom/zoom-sdk-android/blob/master/mobilertc-android-studio/sample/src/main/java/us/zoom/sdksample/inmeetingfunction/customizedmeetingui/audio/MeetingAudioHelper.java) <br />* [MeetingAudioCallback.java](https://github.com/zoom/zoom-sdk-android/blob/master/mobilertc-android-studio/sample/src/main/java/us/zoom/sdksample/inmeetingfunction/customizedmeetingui/audio/MeetingAudioCallback.java) |
|                    | Video                                                        | * [MeetingVideoHelper.java](https://github.com/zoom/zoom-sdk-android/blob/master/mobilertc-android-studio/sample/src/main/java/us/zoom/sdksample/inmeetingfunction/customizedmeetingui/video/MeetingVideoHelper.java) <br />* [MeetingVideoCallback.java](https://github.com/zoom/zoom-sdk-android/blob/master/mobilertc-android-studio/sample/src/main/java/us/zoom/sdksample/inmeetingfunction/customizedmeetingui/video/MeetingVideoCallback.java) |
|                    | User                                                         | * [MeetingUserCallback.java](https://github.com/zoom/zoom-sdk-android/blob/master/mobilertc-android-studio/sample/src/main/java/us/zoom/sdksample/inmeetingfunction/customizedmeetingui/user/MeetingUserCallback.java) |
|                    | Share                                                        | * [MeetingShareHelper.java](https://github.com/zoom/zoom-sdk-android/blob/master/mobilertc-android-studio/sample/src/main/java/us/zoom/sdksample/inmeetingfunction/customizedmeetingui/share/MeetingShareHelper.java) <br />* [MeetingShareCallback.java](https://github.com/zoom/zoom-sdk-android/blob/master/mobilertc-android-studio/sample/src/main/java/us/zoom/sdksample/inmeetingfunction/customizedmeetingui/share/MeetingShareCallback.java) |
|                    | Remote Control                                               | * [MeetingRemoteControlHelper.java](https://github.com/zoom/zoom-sdk-android/blob/master/mobilertc-android-studio/sample/src/main/java/us/zoom/sdksample/inmeetingfunction/customizedmeetingui/remotecontrol/MeetingRemoteControlHelper.java) |

## SDK Reference

You may find the SDK interface reference at [https://marketplace.zoom.us/docs/sdk/native-sdks/android/sdk-reference](https://marketplace.zoom.us/docs/sdk/native-sdks/android/sdk-reference).
If you would like to get a local copy of the SDK reference, you may [download it here](https://github.com/zoom/zoom-sdk-android/tree/master/docs).

## Versioning

For the versions available, see the [tags on this repository](https://github.com/zoom/zoom-sdk-android/tags).

## Change log

Please refer to our [CHANGELOG](https://github.com/zoom/zoom-sdk-android/blob/master/CHANGELOG.md) for all changes.

## Frequently Asked Questions (FAQ)

* :one: `Emulator is not working`:
  * SDK releases before **v4.3.53571.0118** only supports emulators with ABI **arm series(armeabi-v7a, arm64-v8a)**. Starting from **v4.3.53571.0118**, both **arm series(armeabi-v7a, arm64-v8a)** and **x86 ABI** are supported.
* :two: `java.lang.NoClassDefFoundError: Failed resolution of: Landroidx/fragment/app/FragmentActivity`:
  * The Zoom SDK has adapted AndroidX since the release v4.4.55130.0712, if you have not migrated your project to be a AndroidX project, you will receive the above error message. Upgrading your Android project to be a AndroidX project will fix this issue.
* Not finding what you want? We are here to help! Please visit our [Zoom Developer Community Forum](https://devforum.zoom.us/) for further assistance.

## Support

For any issues regarding our SDK, please visit our new Community Support Forum at https://devforum.zoom.us/.

## License

Please refer to [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* :star: If you like our SDK, please give us a "Star". Your support is what keeps us moving forward and delivering happiness to you! Thanks a million! :smiley:
* If you need any support or assistance, we are here to help you: [Zoom Developer Community Forum](https://devforum.zoom.us/);

---
Copyright ©2020 Zoom Video Communications, Inc. All rights reserved.
