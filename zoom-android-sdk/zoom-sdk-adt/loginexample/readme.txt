Simple introductions

1. loginexample project depends on lib project zoom-common-lib and zoom-sdk.
2. Change constants values defined in src/us/zoom/loginexample/Constants.java to your values.
3. Build loginexample project.
4. Run the loginexample app on a real device(Emulator is not supported):
   
   
How to build the loginexample project?
1. Use Eclipse(with ADT plug-in installed)
   1) Import 3 projects as Android project
   2) Build loginexample project
2. Use ant
   1) Define ANDROID_SDK_HOME to your Android SDK root path as an system environment variable;
   2) Open command line tool
      > cd loginexample
      > ant debug
