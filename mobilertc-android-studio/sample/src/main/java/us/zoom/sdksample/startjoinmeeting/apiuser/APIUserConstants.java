package us.zoom.sdksample.startjoinmeeting.apiuser;

public interface APIUserConstants {

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

    // TODO Change it to your web API Key
    public final static String API_KEY = "Your web Rest Api key";

    // TODO Change it to your web API Secret
    public final static String API_SECRET = "Your web Rest Api secret";

	// TODO change it to your user ID, do not need for login user
	public final static String USER_ID = "Your user id";

    // TODO change it to your Zoom access token expired time
	public final static long EXPIRED_TIME= 3600 * 24 * 7; //A week

}
