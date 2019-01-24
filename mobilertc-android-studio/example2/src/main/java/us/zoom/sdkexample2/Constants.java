package us.zoom.sdkexample2;

public interface Constants {

    /**
	*    ========== Disclaimer ==========
	*
	*    Please be aware that all hard-coded variables and constants 
	*    shown in the documentation and in the demo, such as Zoom Token, 
	*    Zoom Access, Token, etc., are ONLY FOR DEMO AND TESTING PURPOSES.
	*    We STRONGLY DISCOURAGE the way of HARDCODING any Zoom Credentials
	*    (username, password, API Keys & secrets, SDK keys & secrets, etc.)
	*    or any Personal Identifiable Information (PII) inside your application. 
	*    WE DON’T MAKE ANY COMMITMENTS ABOUT ANY LOSS CAUSED BY HARD-CODING CREDENTIALS
	*    OR SENSITIVE INFORMATION INSIDE YOUR APP WHEN DEVELOPING WITH OUR SDK.
	*
	*/

	// TODO Change it to your web domain
    String WEB_DOMAIN = "zoom.us";

	// TODO Change it to your APP Key
    String APP_KEY = "Your APP Key";
	
	// TODO Change it to your APP Secret
    String APP_SECRET = "Your APP Secret";

	// TODO change it to your user ID
    String USER_ID = "Your user ID from REST API";
	
	// TODO change it to your token
    String ZOOM_TOKEN = "Your token from REST API";
	
	// TODO Change it to your exist meeting ID to start meeting
    String MEETING_ID = null;

}
