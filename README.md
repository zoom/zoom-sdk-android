# Zoom Android Software Development Kit (SDK)

> Encounter with issues? The answer might be waiting for you at [Frequently Asked Questions](https://github.com/zoom/zoom-sdk-android#frequently-asked-questions-faq) sections.

> Our brand new [Zoom Developer Community Forum](https://devforum.zoom.us/) is now online!!! Check it out! We are here to help! :D

Zoom SDK makes it easy to integrate Zoom with your Android applications, and boosts up your applications with the power of Zoom.

* **Easy to use**: Our SDK is built to be easy to use. Just import the libraries, call a few functions, and we will take care all video conferencing related stuffs for you.
* **Localizable**: Our SDK naturally supports 7 major languages, and you can add more to grow your applications internationally.
* **Custom Meeting UI**: If you want to add your own decorations to your Zoom meeting rooms, try our Custom UI feature to make your meeting room special.

## Disclaimer

**Please be aware that all hard-coded variables and constants shown in the documentation and in the demo, such as Zoom Token, Zoom Access, Token, etc., are ONLY FOR DEMO AND TESTING PURPOSES. We STRONGLY DISCOURAGE the way of HARDCODING any Zoom Credentials (username, password, API Keys & secrets, SDK keys & secrets, etc.) or any Personal Identifiable Information (PII) inside your application. WE DON’T MAKE ANY COMMITMENTS ABOUT ANY LOSS CAUSED BY HARD-CODING CREDENTIALS OR SENSITIVE INFORMATION INSIDE YOUR APP WHEN DEVELOPING WITH OUR SDK**.

## Getting Started

The following instructions will get you a copy of the project up and running on your local machine for development and testing purposes.
* For detailed instructions, please refer to our documentation website: [[https://marketplace.zoom.us/docs/sdk/android](https://marketplace.zoom.us/docs/sdk/android)];
* If you need support or assistance, please visit our [Zoom Developer Community Forum](https://devforum.zoom.us/);

### Prerequisites

Before you try out our SDK, you would need the following to get started:

* **A Zoom Account**: If you do not have one, you can sign up at [https://zoom.us/signup](https://zoom.us/signup).
  * Once you have your Zoom Account, sign up for a 60-days free trial at [https://marketplace.zoom.us/](https://marketplace.zoom.us/)
* **An Android device**:
  * OS: Android 4.0 (API Level 14) or later. The latest version (v4.1.34082.1024) supports API level up to 27.
  * CPU: armeabi-v7a or above.


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
├── customuidemo
├── example
├── example2
├── gradle
├── gradlew
├── gradlew.bat
├── mobilertc
└── settings.gradle
```

We provide 3 different examples for you:
 * customuidemo: An Android app that has custom video conferencing UI. Please ensure your APP_KEY and APP_SECRET supports Custom UI feature.
 * example: An Android app that has all basic features for both login users and API users.
 * example2: An Android app that shows how to join meeting without any login credentials.


## Running the examples

Connect your Android device to your computer and simply press "Run" on selected example, the example will run on your device.


## Documentation

Please visit [[https://marketplace.zoom.us/docs/sdk/android](https://marketplace.zoom.us/docs/sdk/android)] for details of each features and functions.

## Versioning

For the versions available, see the [tags on this repository](https://github.com/zoom/zoom-sdk-android/tags).

## Change log

Please refer to our [CHANGELOG](https://github.com/zoom/zoom-sdk-android/blob/master/CHANGELOG.md) for all changes.

## Frequently Asked Questions (FAQ)

* :one: `Emulator is not working`:
  * Our SDK currently only supports emulators with ABI **arm series(armeabi-v7a, arm64-v8a)**. Emulators with **x86 ABI** are not supported at this point.
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
