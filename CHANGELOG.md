# CHANGELOG

## Note

### :red_circle: Non-AndroidX Version (EOL: 01/01/2020)
**Per Google's suggestions and guidance, we have upgraded our regular Android SDK to support AndroidX. We used to offer a non-AndroidX version to help you migrate to the AndroidX project. However, the previous non-AndroidX version has reached its End-of-Life (01/01/20). Please plan to upgrade your Android project to AndroidX to use the latest Android SDK**

## 2020-04-29 @ v4.6.21666.0429

## Added:
* Added support for AES 256-bit GCM encryption.
  * **Please plan to upgrade your SDK accordingly. See the announcement in [README](https://github.com/zoom/zoom-sdk-android) for more information**
* Added a new interface to hide Reaction emoji
  * `hideReactionsOnMeetingUI()`
* Added a new interface to allow more customization of the invite action
  * You could show your own invitation menu and handle the onClick event for the menu options

## Changed & Fixed:
* Upgraded OpenSSL to version 1.1.1e
* Fixed an issue that sometimes user might not be able to use vanityId to join a meeting

## Deprecated
* Deprecated the interface to get user's email: `InMeetingUserInfo().getEmail()`

## 2020-04-04 @ v4.6.15801.0403

## Added:
* Add new interfaces for customizing [breakout room](https://support.zoom.us/hc/en-us/articles/206476093-Getting-Started-with-Breakout-Rooms), new interfaces can be found in the following classes:
 * `InMeetingBOController`
 * `IBOCreator`
 * `IBOAssistant`
 * `IBOAdmin`
 * `IBOMeeting`
 * `IBOData`
 * `BOCtrlUserStatus`
* Add new interfaces and options for schedule meeting
* Add a new interface to allow customizing meeting notification
 * In `MeetingSettingsHelper`
   * `setCustomizedNotificationData(CustomizedNotificationData data,InMeetingNotificationHandle handle)`
* Add a new interface for "Lower all hands"
 * In `InMeetingService`
   * `lowerAllHands()`
* Add new interfaces to reclaim host
 * In `InMeetingService`
   * `canReclaimHost()`
   * `reclaimHost()`
* Add new interfaces to control views under gallery view in Zoom UI
 * In `ZoomUIService`
   * `switchToNextPage()`
   * `switchToPreviousPage()`
   * `switchToActiveSpeaker()`
   * `switchToDriveScene()`
   * `switchToVideoWall()`
* Add an interface to allow webinar participants to pre-enter the registration information, and skip the pop-up
 * In `ZoomUIService`
   * `prePopulateWebinarRegistrationInfo(String email, String username)`
* Add new interfaces and callbacks to configure the chat privilege of the meeting/webinar attendees and to get notified on the corresponding events.
 * In `InMeetingChatController`
   * `allowAttendeeChat(MobileRTCWebinarChatPriviledge priviledge)`
   * `changeAttendeeChatPriviledge(MobileRTCMeetingChatPriviledge priviledge)`
 * In `InMeetingServiceListener`
   * `onSinkAttendeeChatPriviledgeChanged(int privilege)`
   * `onSinkAllowAttendeeChatNotification(int privilege)`

## Changed & Fixed:
* Fixed an issue that the interface `setNoUserJoinOrLeaveTipEnabled` results in unexpected behavior
* Fixed an issue that the SDK is not working on non-standard Android device
* Fixed an issue that the interface `setVideoOnWhenMyShare` in `MeetingSettingsHelper` is not working properly


## 2020-02-10 @ v4.6.15086.0209

## Added:
* Add new features in Zoom default UI
  * Allow Call-In Attendees to unmute in the webinar
  * Closed captioning in breakout sessions
  * Support for multiple pages on the whiteboard
  * Audio setting for auto-select based on network
  * Switch between video and content sharing
  * Zoom in/out on their camera
  * Annotation enhancements
  * Reduced volume for entering/exiting chime
  * Rename meeting hosted with personal meeting ID
  * Push notification for contact requests
  * Rename webinar attendees
  * Send a message to participants in a waiting room
  * Settings to disable meeting notifications
  * Merge participant's video and Audio
  * Hide non-video participants
  * Meeting reactions
  * View other participant's audio status
* Add support for the Korean language.
* Add a new callback for the event when the active video user has changed.
  * `onMeetingActiveVideo`

## Changed & Fixed:
* Enhanced security and upgraded OpenSSL to 1.0.2u.
* Fixed an issue that pictures from device storage cannot be shared.

## 2019-12-16 @ v4.4.57218.1211

## Added:
* Add new interfaces for SDK initialization with JWT token.
  * `ZoomSDKInitParams.jwtToken`
* Add new interfaces for the Q&A feature in the webinar.
  * `ZoomSDK.getInstance().getInMeetingService().getInMeetingQAController()`
* Add a new interface to get the attendeeID while in the meeting.
  * `ZoomSDK.getInstance().getInMeetingService().getParticipantId()`
* Add a new interface to show/hide the "My Connected Time".
  * `ZoomSDK.getInstance().getMeetingSettingsHelper().enableShowMyMeetingElapseTime(boolean show)`
* Add a new interface for users to get the meeting password while in the meeting.
  * `ZoomSDK.getInstance().getInMeetingService().getMeetingPassword()`
* Add a callback to remind the user that the free meeting will be ended in 10 minutes.
  * `InMeetingServiceListener onFreeMeetingReminder(boolean isOrignalHost, boolean canUpgrade, boolean isFirstGift)`
* Add a new callback for the event when the host asks to unmute the attendee's video.
  * `InMeetingServiceListener onHostAskStartVideo(long userId)`

## Changed & Fixed:
* Fixed an issue that unable to share any files while using SDK with Android Q.
* Fixed an issue that the meeting restarts for a few times after pressing the end meeting button.
* Fixed an issue that the app crashes when sharing a PDF file and changing pages.
* Fixed an issue that the shared view is not responding while annotating.
* Fixed an issue that the host is unable to mute the attendee's audio.

## 2019-11-04 @ v4.4.56624.1028

## Added
*    Add a new interface to hide the 'chat' button for zoom default UI
*    Add a new interface to enable send video when my share start
*    Add a new interface to hide the popup dialog when the host ask the attendee to unmute themselves
*    Add a new interface for SDK initialization
*    Add a new interface to hide the Q&A button and the POLL button
*    Add a new interface to save shared bitmap
*    Add a new interface to change the priority of the ongoing meeting notification

## Changed & Fixed
*    Modified a behavior that separating the main session user event from the event in the breakout session
*    Updated the ProGuard rule.

## Deprecated
*    `initialize(Context context, String appKey, String appSecret, ZoomSDKInitializeListener listener)`
*    `initialize(Context context, String appKey, String appSecret, boolean autoRetryVerifyApp, ZoomSDKInitializeListener listener)`
*    `initialize(Context context, String appKey, String appSecret, String domain, boolean autoRetryVerifyApp, ZoomSDKInitializeListener listener)`
*    `initialize(Context context, String appKey, String appSecret, String domain, boolean autoRetryVerifyApp, ZoomSDKInitializeListener listener, boolean enableLog)`
*    `initialize(Context context, String appKey, String appSecret, String domain, boolean autoRetryVerifyApp, ZoomSDKInitializeListener listener, boolean enableLog, int logSize)`
*    `initialize(Context context, String appKey, String appSecret, String domain, ZoomSDKInitializeListener listener)`
*    `initialize(Context context, String appKey, String appSecret, String domain, ZoomSDKInitializeListener listener, boolean enableLog)`
*    `initialize(Context context, String appKey, String appSecret, String domain, ZoomSDKInitializeListener listener, boolean enableLog, int logSize)`

## 2019-09-04 @ v4.4.55968.0904

## Added
* **Add an SDK version that does not require AndroidX (End-of-Life: 01/01/2020)**
* Add support for Android Q (Android 10)
* Add a new interface to hide the "Chat" button in Zoom UI
* `MeetingSettingsHelper:disableChatUI(boolean disable)`
* Add a new interface to keep the video on when sharing content with others
* `MeetingSettingsHelper:setVideoOnWhenMyShare(boolean videoOnWhenMyShare)`
* Add a new interface to hide the popup dialog when the host requests to unmute an attendee
* `MeetingOptions.no_unmute_confirm_dialog`
* Add a new interface for SDK initialization
* `ZoomSDK:initialize(Context context, ZoomSDKInitializeListener listener, ZoomSDKInitParams params)`

## Changed & Fixed
* Fixed a compatibility issue with 64-bit `arm64-v8a` ABI.
* Fixed an issue that the video is turned off by default when starting a meeting with ZAK
* Fixed an issue that the system notification remains after the SDK app is closed from Recent Apps
* Fixed an issue that the participant cannot annotate when the host is sharing
* Fixed an issue that the PDF sharing is not working properly with 64-bit ABIs
* Separate the main session user events from the breakout session
* Updated the ProGuard rule

## Deprecating

We are going to deprecate the following interfaces in the near future. Please plan to use the latest interface accordingly.

* `initialize(Context context, String appKey, String appSecret, ZoomSDKInitializeListener listener)`
* `initialize(Context context, String appKey, String appSecret, boolean autoRetryVerifyApp, ZoomSDKInitializeListener listener)`
* `initialize(Context context, String appKey, String appSecret, String domain, boolean autoRetryVerifyApp, ZoomSDKInitializeListener listener)`
* `initialize(Context context, String appKey, String appSecret, String domain, boolean autoRetryVerifyApp, ZoomSDKInitializeListener listener, boolean enableLog)`
* `initialize(Context context, String appKey, String appSecret, String domain, boolean autoRetryVerifyApp, ZoomSDKInitializeListener listener, boolean enableLog, int logSize)`
* `initialize(Context context, String appKey, String appSecret, String domain, ZoomSDKInitializeListener listener)`
* `initialize(Context context, String appKey, String appSecret, String domain, ZoomSDKInitializeListener listener, boolean enableLog)`
* `initialize(Context context, String appKey, String appSecret, String domain, ZoomSDKInitializeListener listener, boolean enableLog, int logSize)`

## 2019-07-15 @ v4.4.55130.0712

**Added**

* **Add arm64-v8a, x86_64 library and provide Android 64-bit support**
* Introduce new Zoom meeting UI
* Add a new interface to distinguish H.323 user and telephone user
	* `InMeetingUserInfo.isH323User`
	* `InMeetingUserInfo.isPureCallInUser`
* Add a new interface to hide/show “Webinar need register” dialog
	* `MeetingOptions.no_webinar_register_dialog`
* Add a new interface to query video quality
	* `VideoStatus.videoQuality`
* Add a new parameter to the SDK initialization method to customize the log size, the range is from 1MB to 50MB per log file, and by default is 5MB. The maximum number of the log file is 5.
	* `ZoomSDK initialize(Context context, String appKey, String appSecret, String domain, ZoomSDKInitializeListener listener, boolean enableLog,int logSize)`
* Add a new callback to listen to the events when the end button is clicked
	* `MeetingActivity onClickEndButton()`
* Add a new callback to listen to the changes in the user’s network quality
	* `InMeetingServiceListener onUserNetworkQualityChanged(long userId)`


**Changed & Fixed**

* Fixed an issue that sometimes the app crashes when trying to end a meeting while the Bluetooth is connected
* Fixed an issue that the top/bottom bar in the Zoom UI is hidden unexpected
* Fixed an issue that the app occasionally crashes when trying to start live streaming
* Fixed an issue that the setting of muting the attendee’s audio is not working

## 2019-03-25 @ v4.3.1.47200.0322

**Added**

* Introduce new Zoom meeting UI
* Add new interfaces to control “disable viewer’s annotate” on the share sender site
* Add new interfaces to customize the sub-tab pages in H323 invite page and customize all tab pages in the invite dialog
* Add support to customize invite dialog

**Changed & Fixed**

* Fix an issue that the avatar will stretch when switching video on/off
* Fix an issue that the SDK is not properly translating county translations in ‘Call My Phone’ screen
* Fix an issue that the poll view does not show up and does not update during the webinar
* Enhanced security

## 2019-01-23 @ v4.3.0.53571.0118

**Added**
* Support for the x86 emulator.
*	New functional documentation.
*	A new refactorized demo project that provides clear instructions on how to implement major features.
*	New callback method for the case when active video changed in the meeting.
*	New logging feature that stores logs with the maximum size of 5MB.
*	A new method to join/start meeting directly via url, such as zoommtg://zoom.us/join?action=....
*	Support to select dial-in country while scheduling a meeting.

**Changed & Fixed**
*	Join audio improvements
*	Some issues that cause crashes

**Deprecated**
*	MeetingService.joinMeeting(Context, String, String)
*	MeetingService.joinMeeting(Context, String, String, MeetingOptions)
*	MeetingService.joinMeeting(Context, String, String, String)
*	MeetingService.joinMeeting(Context, String, String, String, MeetingOptions)
*	ZoomSDK.setBoxAppKeyPair(Context, String, String)
*	ZoomSDK.setDropBoxAppKeyPair(Context, String, String)
*	ZoomSDK.setGoogleDriveInfo(Context, String, String)
*	ZoomSDK.setOneDriveClientId(Context, String)
*	MeetingService.startInstantMeeting(Context, String, String, int, String)
*	MeetingService.startInstantMeeting(Context, String, String, int, String, MeetingOptions)
*	MeetingService.startMeeting(Context, String)
*	MeetingService.startMeeting(Context, String, MeetingOptions)
*	MeetingService.startMeeting(Context, String, String, int, String, String)
*	MeetingService.startMeeting(Context, String, String, int, String, String, MeetingOptions)

## 2018-10-24 @ v4.1.34082.1024
1. Added support for Android API Level 27;
2. Added support to schedule meeting for a specified user;
3. Added support to third party audio;
4. Added support to specified a domain;
5. Added support to only allow signed in user to join the meeting;
6. Enhanced Custom Meeting UI feature;
7. Enhanced meeting scheduling feature;


## 2018-09-11 @ v4.1.32128.0910

1. Schedule Meeting Feature Enhancement

2. Bug fixes

## 2018-08-20 @ v4.1.30378.0817

1. Custom Meeting UI (support basic meeting function, except for Webinar and Breakout Session)

2. Single process to replace previous two processes implement - meeting will not run in a separate process (no meeting process any more)

3. Remove protobuf library dependency from SDK

4. Support Android 8.0

5. Change MeetingServiceListener callback from onMeetingEvent to onMeetingStatusChanged and enchance MeetingStatus

6. SDK Meeting Interface refactor, InMeetingService category cluster for specific Service Module:
InMeetingAudioController for Audio service;
InMeetingVideoController for Video service;
InMeetingChatController for Chat service;
InMeetingShareController for Share service;

7. Support In-meeting Chat related common feature

8. Support In-meeting Share related common feature

9. Support Annotate and Remote Control for custom Meeting UI

10. Bug fixes


## 2018-07-26 @ v4.1.28807.0726

The start meeting logic for API users has changed. Please read below before upgrading to this version.

**Added**

1.New start/join meeting interfaces

We added a new parameter, zoom access token (ZAK), which can be retrieved through REST API:
https://zoom.github.io/api/#retrieve-a-users-permissions

Old API User start meeting logic:
```
StartMeetingOptions opts = new StartMeetingOptions();
//opts.no_driving_mode = true;
//……………
int ret = meetingService.startMeeting(this, USER_ID, ZOOM_TOKEN,
USER _TYPE, meetingNo, DISPLAY_NAME, opts);
```
New API User start meeting logic:
```
StartMeetingParamsWithoutLogin params = new StartMeetingParamsWithoutLogin();
params.userId = USER_ID;
params.zoomToken = ZOOM_TOKEN;
params.userType = STYPE;;
params.displayName = DISPLAY_NAME;
params.zoomAccessToken = ZOOM_ACCESS_TOKEN;
params.meetingNo = meetingNo;

StartMeetingOptions opts = new StartMeetingOptions();
//opts.no_driving_mode = true;
//……………
int ret = meetingService.startMeetingWithParams(this, params, opts);
```
2.Add interface to let host assign&revoke Cohost

3.Add an interface which set Host Key to claim Host

4.Support join/start meeting with vanity ID

5.Add interface to start 3rd live stream

6.Default not auto retry to auth SDK after auth failed

7.Bugs fix

## 2017-05-28 @ v4.1.25388.0528

**Added**

1.Update Android SDK target version to 25(Android7.0)

2.Add in meeting chat message callback event;

3.New interface to set meeting large share video layout (side by side) when view other’s sharing;

4.Interface for getting current meeting room device list and inviting room system interfaces refactor;

5.Support to config DSCP for audio video session;

6.Support set domain start with http:// or https://;

## 2017-10-30 @ v4.0.11726.1030

**Added**
	1. No longer support zoom mobilertc version for ADT(eclipse)

	2. Support SSO login

	3. Add interfaces to auto connect device audio / always , mute my microphone / always turn off my video when joining
	meeting

	4. Add interfaces to check if meeting support inviting by phone and room system

	5. Add interfaces to mute and unmute my audio&video , mute and unmute all participants'audio

	6. Add interfaces to disable video gallery view

	7. Add interfaces to hide user enter/leave popup

	8. PreMeeting functions error code refactor

	9. Bugs fix:
		System statusbar will overlap zoom watermark if the android device support immersive mode
		If meeting activity has been destroyed, meeting process can not exit correctly after main process stopped
		If there is no internet connection, a NullPointerException is thrown from inside the Zoom SDK shortly after
		scheduleMeeting was called
		Fix zoom mobilertc meeting no_disconnect_audio option does not work issue

## 2017-06-19 @ v4.0.36490.0619

**Added**

Add interfaces to get a list of participants’ profile and status in meeting
	the name of the interfaces are InMeetingServiceListener and InMeetingService
	APIs provided in the interface are:

	1. void	onLowOrRaiseHandStatusChanged(long userId, boolean isRaiseHand)
	   Sink the event of Low Or Raise Hand Status changed

	2. void	onMeetingHostChanged(long userId)
	   Sink the event of meeting host changed

	3. void	onMeetingUserJoin(long userId)
	   Sink the event of meeting new user join

	4. void	onMeetingUserLeave(long userId)
	   Sink the event of meeting user leave

	5. void	onMeetingUserUpdated(long userId)
	   Sink the event of meeting user info updated

	6. void	onSpotlightVideoChanged(boolean on)
	   Sink the event of meeting host changed

	7. void	onUserAudioStatusChanged(long userId)
	   Sink the event of user's audio status changed

	8. void	onUserAudioTypeChanged(long userId)
	   Sink the event of user's audio type changed

	9. void	onUserVideoStatusChanged(long userId)
	   Sink the event of user's video status changed

	10. long activeShareUserID()
	    This method is used to get active share user id in the meeting.

	11. long activeVideoUserID()
	    This method is used to get active video user id in the meeting.

	12. void addListener(InMeetingServiceListener listener)
	    Register a listener for in-meeting event

	13. boolean changeName(java.lang.String inputName, long userId)
	    This method is used to change user's display name in meeting.

	14. InMeetingUserList getInMeetingUserList()
 		Note: Don't call this interface frequently.

	15. long getMyUserID()
	    This method is used to get my user id in the meeting.

	16. InMeetingUserInfo getMyUserInfo()
	    This method is used to get my user info in the meeting.

	17. InMeetingUserInfo getUserInfoById(long userId)
        This method is used to get user info by certain userId

	18. boolean isHostUser(long userId)
	    This method is used to check the user is host or not.

	19. boolean isMeetingLocked()
	    This method is used to tell whether the meeting is locked by host or not.

	20. boolean isMuteOnEntryOn()
	    This method is used to check whether MuteOnEntry is on in the meeting.

	21. boolean isMyself(long userId)
	    This method is used to check the user is myself or not.

	22. boolean isPlayChimeOn()
	    This method is used to check PlayChime or not while user join/leave meeting.

	23. boolean isSameUser(long user1, long user2)
	    This method is used to judge the same user.

	24. boolean isShareLocked()
        This method is used to tell whether the screen share is locked by host or not.

	25. boolean isUserVideoSpotLighted(long userId)
	    This method is used to check the user's video spotlighted or not.

	26. boolean lowerHand(long userId)
	    This method is used to lower user's hand.

	27. boolean makeHost(long userId)
	    This method is used to assign host role to another user in the meeting.

	28. boolean raiseMyHand()
	    This method is used to raise my hand.

	29. void removeListener(InMeetingServiceListener listener)
	    Unregister a listener

	30. boolean removeUser(long userId)
	    This method is used to remove a user in the meeting.

	31. boolean setMuteOnEntry(boolean on)
	    This method is used to set whether MuteOnEntry is on in the meeting.

	32. boolean setPlayChimeOnOff(boolean on)
	    This method is used to set PlayChime or not while user join/leave meeting.

	33. boolean spotLightVideo(boolean on, long userId)
        This method is used to spotlight the user's video or not.

## 2017-03-13 @ v4.0.26490.0313

**Added**

Add interfaces to call room device directly:
 -Public interface InviteRoomSystemHelper

Field:
 -Static int ROOMDEVICE_H323
 -Static int ROOMDEVICE_SIP

Method detail:
1. void addEventListener(InviteRoomSystemListener listener)
Usage: Register a invite room system listener
Parameter:listener - the listener instance

2. void removeEventListener(InviteRoomSystemListener listener)
Usage: Unregister a invite room system listener
Parameter: listener - the listener instance

3. java.lang.String[] getH323Address()
Usage: Get h323 address for current meeting
Returns: If the function succeeds, the return value is not null.

4. boolean sendMeetingPairingCode(long meetingId,  java.lang.String pairingCode)
Usage: Send Meeting pairing code
Parameters: meetingId – meeting to pairing, pairingCode – Code for pairing
Returns: success or failure

5. boolean callOutRoomSystem(java.lang.String address,  int deviceType)
Usage: Call out a room system
Parameters: address - ip address / e.164 number, deviceType - ROOMDEVICE_H323/ROOMDEVICE_SIP
Returns: success or failure

6. boolean cancelCallOutRoomSystem()
Usage: Cancel a room system call out.
Returns: success or failure

## 2017-01-18 @ v4.0.21754.0118

**Added**

1. Support to join Webinar with Panelist member;

2. Add option to show/hide thumbnail video while viewing/starting share in meeting;

3. Add option to hide “Leave Meeting” item in host side;

4. Add watermark in MobileRTC
