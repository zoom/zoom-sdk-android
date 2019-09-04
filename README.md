# Zoom Android Software Development Kit (SDK)
<div align="center">
<img src="https://s3.amazonaws.com/user-content.stoplight.io/8987/1541013063688" width="400px" max-height="400px" style="margin:auto;"/>
</div>

## Latest SDK Notifications
1. :red_circle: Non-AndroidX Version (EOL: 01/01/2020)
**Per Google's suggestions and guidance, we have upgraded our regular Android SDK to support AndroidX. We understand that upgrading an existing Android project/product to AndroidX would need some time, and we heard your feedback. We hereby offer an Android SDK version that does not require AndroidX, you may find it (android-mobilertc_support_xxx.zip
) here: [https://github.com/zoom/zoom-sdk-android/releases](https://github.com/zoom/zoom-sdk-android/releases) The end-of-life date of offering and supporting this version will be 01/01/2020.**

**Please use this version if you are not able to upgrade your project to AndroidX at the moment.**

**Please plan to upgrade your Android project to AndroidX before 01/01/2020.**

2. **Android 64-bit support is now available in the release [v4.4.55130.0712](https://github.com/zoom/zoom-sdk-android/releases/tag/v4.4.55130.0712).**, please don't forget to upgrade your Android project to be a AndroidX project.
3. **Variable Name Changes**: Since [v4.3.1.47200.0322](https://github.com/zoom/zoom-sdk-android/releases/tag/v4.3.1.47200.0322), we have renamed the term "APP" to "SDK" in our demo to avoid confusion between the term "API" and "APP".
4. Please be aware that some of our interfaces are deprecated in the latest release, please check out our [CHANGELOG](https://github.com/zoom/zoom-sdk-android/blob/master/CHANGELOG.md) for more detail
5. Encounter with issues? The answer might be waiting for you at [Frequently Asked Questions](https://marketplace.zoom.us/docs/sdk/native-sdks/android/get-help/faq#frequently-asked-questions) sections.
6. Our brand new [Zoom Developer Community Forum](https://devforum.zoom.us/) is now online!!! Check it out! We are here to help! :D

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
  * OS: Android 4.0 (API Level 14) or later.
  * CPU: armeabi-v7a or above / x86 ABI.


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

## Versioning

For the versions available, see the [tags on this repository](https://github.com/zoom/zoom-sdk-android/tags).

## Change log

Please refer to our [CHANGELOG](https://github.com/zoom/zoom-sdk-android/blob/master/CHANGELOG.md) for all changes.

## Frequently Asked Questions (FAQ)

* :one: `Emulator is not working`:
  * SDK releases before **v4.3.53571.0118** only supports emulators with ABI **arm series(armeabi-v7a, arm64-v8a)**. Starting from **v4.3.53571.0118**, both **arm series(armeabi-v7a, arm64-v8a)** and **x86 ABI** are supported.
* :two: `java.lang.NoClassDefFoundError: Failed resolution of: Landroidx/fragment/app/FragmentActivity`:
  * The Zoom SDK has adapted AndroidX since the release [v4.4.55130.0712](https://github.com/zoom/zoom-sdk-android/releases/tag/v4.4.55130.0712), if you have not migrated your project to be a AndroidX project, you will receive the above error message. Upgrading your Android project to be a AndroidX project will fix this issue.
* Not finding what you want? We are here to help! Please visit our [Zoom Developer Community Forum](https://devforum.zoom.us/) for further assistance.

## Support

For any issues regarding our SDK, please visit our new Community Support Forum at https://devforum.zoom.us/.

## License

Please refer to [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* :star: If you like our SDK, please give us a "Star". Your support is what keeps us moving forward and delivering happiness to you! Thanks a million! :smiley:
* If you need any support or assistance, we are here to help you: [Zoom Developer Community Forum](https://devforum.zoom.us/);

---
Copyright ©2019 Zoom Video Communications, Inc. All rights reserved.
