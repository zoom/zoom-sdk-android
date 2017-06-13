# CHANGELOG

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


