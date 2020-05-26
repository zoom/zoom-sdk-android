package us.zoom.sdksample.otherfeatures;

import android.content.Context;
import com.zipow.videobox.util.InviteContentGenerator;


public class MyInviteContentGenerator implements InviteContentGenerator {

	@Override
	public String genEmailTopic(Context context, long meetingId, String meetingUrl,
			String myName, String password, String rawPassword) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String genEmailContent(Context context, long meetingId,
			String meetingUrl, String myName, String password, String rawPassword) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String genSmsContent(Context context, long meetingId,
			String meetingUrl, String myName, String password, String rawPassword) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String genCopyUrlText(Context context, long meetingId,
			String meetingUrl, String myName, String password, String rawPassword) {
		// TODO Auto-generated method stub
		return null;
	}

}
