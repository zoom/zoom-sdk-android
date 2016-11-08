Simple introductions

1. example project depends on lib project zoom-common-lib and zoom-sdk.
2. Change constants values defined in src/us/zoom/sdkexample/Constants.java to your values.
3. Build example project.
4. Run the example app on a real device(Emulator is not supported):
   1) Enter a scheduled meeting number and click "Start Meeting" to start this meeting;
   2) Enter a running meeting number and click "Join Meeting" to join this meeting;
   3) Just click "Start Instant Meeting" to start a instant meeting.
   
   
How to build the example project?
1. Use Eclipse(with ADT plug-in installed)
   1) Import 3 projects as Android project
   2) Build example project
2. Use ant
   1) Define ANDROID_SDK_HOME to your Android SDK root path as an system environment variable;
   2) Open command line tool
      > cd example
      > ant debug
