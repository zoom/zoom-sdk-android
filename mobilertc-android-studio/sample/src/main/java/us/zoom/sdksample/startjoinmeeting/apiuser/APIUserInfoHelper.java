package us.zoom.sdksample.startjoinmeeting.apiuser;

import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;

public class APIUserInfoHelper implements APIUserConstants {

    private final static String TAG = "ZoomSDKExample";
    /*
    * Create JWT Access token
    *
    * Header:
    *
    * { "alg": "HS256", "typ": "JWT" }
    * Payload
    *
    * { "iss": "API_KEY", "exp": expired timestamp }
    * AccessToken Format :
    * base64UrlEncode(header) + "." + base64UrlEncode(payload) +"." +
    * HMACSHA256( base64UrlEncode(header) + "." + base64UrlEncode(payload), api_secret)
     */

    private static APIUserInfo curAPIUserInfo;

    /**
     * Create JWT Access token
     */
    public static String createJWTAccessToken() {

        long time=System.currentTimeMillis()/1000  + EXPIRED_TIME;

        String header = "{\"alg\": \"HS256\", \"typ\": \"JWT\"}";
        String payload = "{\"iss\": \"" + API_KEY + "\"" + ", \"exp\": " + String.valueOf(time) + "}";
        try {
            String headerBase64Str = Base64.encodeToString(header.getBytes("utf-8"), Base64.NO_WRAP| Base64.NO_PADDING | Base64.URL_SAFE);
            String payloadBase64Str = Base64.encodeToString(payload.getBytes("utf-8"), Base64.NO_WRAP| Base64.NO_PADDING | Base64.URL_SAFE);
            final Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(API_SECRET.getBytes(), "HmacSHA256");
            mac.init(secretKeySpec);

            byte[] digest = mac.doFinal((headerBase64Str + "." + payloadBase64Str).getBytes());

            return headerBase64Str + "." + payloadBase64Str + "." + Base64.encodeToString(digest, Base64.NO_WRAP| Base64.NO_PADDING | Base64.URL_SAFE);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get zoom token for the user
     * @param userId the user's id
     */
    public static String getZoomToken(String userId) {
        String jwtAccessToken = createJWTAccessToken();

        if(jwtAccessToken == null || jwtAccessToken.isEmpty())
            return null;

        // Create connection
        try {
            URL zoomTokenEndpoint = new URL("https://api.zoom.us/v2/users/" + userId + "/token?type=token&access_token=" + jwtAccessToken);
            HttpsURLConnection connection = (HttpsURLConnection) zoomTokenEndpoint.openConnection();
            //connection.setRequestProperty("Content-Type", " application/json");
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream responseBody = connection.getInputStream();
                InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                BufferedReader streamReader = new BufferedReader(responseBodyReader);
                StringBuilder responseStrBuilder = new StringBuilder();

                //get JSON String
                String inputStr;
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);

                connection.disconnect();
                JSONObject jsonObject = new JSONObject(responseStrBuilder.toString());
                return jsonObject.getString("token");
            } else {
                Log.d(TAG, "error in connection");
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get zoom acess token for the user
     * @param userId the user's id
     */
    public static String getZoomAccessToken(String userId) {
        String jwtAccessToken = createJWTAccessToken();

        if(jwtAccessToken == null || jwtAccessToken.isEmpty())
            return null;
        // Create connection
        try {
            URL zoomTokenEndpoint = new URL("https://api.zoom.us/v2/users/" + userId + "/token?type=zak&access_token=" + jwtAccessToken);
            HttpsURLConnection connection = (HttpsURLConnection) zoomTokenEndpoint.openConnection();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream responseBody = connection.getInputStream();
                InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                BufferedReader streamReader = new BufferedReader(responseBodyReader);
                StringBuilder responseStrBuilder = new StringBuilder();

                //get JSON String
                String inputStr;
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);

                connection.disconnect();
                JSONObject jsonObject = new JSONObject(responseStrBuilder.toString());
                return jsonObject.getString("token");
            } else {
                Log.d(TAG, "error in connection");
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveAPIUserInfo(APIUserInfo userInfo) {
        curAPIUserInfo = userInfo;
    }

    public static APIUserInfo getAPIUserInfo() {
        return curAPIUserInfo;
    }
}
