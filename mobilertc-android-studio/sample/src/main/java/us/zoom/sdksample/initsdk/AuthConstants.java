package us.zoom.sdksample.initsdk;

public interface AuthConstants {

	// TODO Change this to your web domain
	public final static String WEB_DOMAIN = "zoom.us";

	// TODO Change this to your APP Key
	public final static String SDK_KEY = "";

	// TODO Change this to your APP Secret
	public final static String SDK_SECRET = "";

	/**
	 * We recommend that you generate a jwttoken on your own server instead of hardcoding it in your code.
	 * We hardcode it here just for demonstration purposes.
	 *
	 * You can generate a jwttoken at https://jwt.io/
	 * with this payload:
	 * {
	 *     "appKey": "string", // app key
	 *     "iat": long, // access token issue timestamp
	 *     "exp": long, // access token expire time
	 *     "tokenExp": long // token expire time
	 * }
	 */
	public final static String SDK_JWTTOKEN = YOUR JWTTOKEN;

}
