# CHANGELOG

## 2017-07-26

The start meeting logic for API users has changed. Please read below before upgrading to this version.

### Added

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

## 2017-05-28

### Added

1.Update Android SDK target version to 25(Android7.0)

2.Add in meeting chat message callback event;

3.New interface to set meeting large share video layout (side by side) when view other’s sharing;

4.Interface for getting current meeting room device list and inviting room system interfaces refactor;

5.Support to config DSCP for audio video session;

6.Support set domain start with http:// or https://;

## 2017-10-30

### Added
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
		
## 2017-06-19

### Added

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

## 2017-03-13

### Added

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

## 2017-01-18

### Added

1. Support to join Webinar with Panelist member;

2. Add option to show/hide thumbnail video while viewing/starting share in meeting;

3. Add option to hide “Leave Meeting” item in host side;

4. Add watermark in MobileRTC


