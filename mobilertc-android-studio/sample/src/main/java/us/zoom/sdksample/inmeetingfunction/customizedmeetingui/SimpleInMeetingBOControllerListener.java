package us.zoom.sdksample.inmeetingfunction.customizedmeetingui;

import us.zoom.sdk.IBOAdmin;
import us.zoom.sdk.IBOAssistant;
import us.zoom.sdk.IBOAttendee;
import us.zoom.sdk.IBOCreator;
import us.zoom.sdk.IBOData;
import us.zoom.sdk.InMeetingBOControllerListener;

public abstract class SimpleInMeetingBOControllerListener implements InMeetingBOControllerListener {
    @Override
    public void onHasCreatorRightsNotification(IBOCreator iboCreator) {

    }

    @Override
    public void onHasAdminRightsNotification(IBOAdmin iboAdmin) {

    }

    @Override
    public void onHasAssistantRightsNotification(IBOAssistant iboAssistant) {

    }

    @Override
    public void onHasAttendeeRightsNotification(IBOAttendee iboAttendee) {

    }

    @Override
    public void onHasDataHelperRightsNotification(IBOData iboData) {

    }

    @Override
    public void onLostCreatorRightsNotification() {

    }

    @Override
    public void onLostAdminRightsNotification() {

    }

    @Override
    public void onLostAssistantRightsNotification() {

    }

    @Override
    public void onLostAttendeeRightsNotification() {

    }

    @Override
    public void onLostDataHelperRightsNotification() {

    }

    @Override
    public void onNewBroadcastMessageReceived(String message) {

    }
}
