package org.wso2.mobile.idp.proxy;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * After receiving authorization code client application can use this class to obtain access token
 */
public class AccessTokenHandler extends Activity {
    private static final String TAG = "AccessTokenHandler";
    private Context context;
    private String idToken = null;
    private CallBack callBack;
    private String clientSecret = null;
    private String clientID = null;

    public AccessTokenHandler(Context context, String clientID, String clientSecret) {
        this.callBack = callBack;
        this.context = context;
        this.clientID = clientID;
        this.clientSecret = clientSecret;
    }

    public void obtainAccessToken(String code) {
        new NetworkCallTask().execute(code);
    }

    /**
     * AsyncTask to contact authorization server and get access token, refresh token as a result
     */
    private class NetworkCallTask extends AsyncTask<String, Void, String> {
        private String responseCode = null;

        public NetworkCallTask() {

        }

        @Override
        protected String doInBackground(String... params) {
            String response = "";
            String code = params[0];
            Log.d(TAG, code);
            Map<String, String> request_params = new HashMap<String, String>();
            request_params.put("grant_type", "authorization_code");
            request_params.put("code", code);
            request_params.put("redirect_uri", IDPConstants.CALL_BACK_URL);
            request_params.put("scope", "openid");
            Map<String, String> response_params = ServerUtilities.postData(context, IdentityProxy.getInstance().getAccessTokenURL(), request_params, clientID, clientSecret);
            response = response_params.get("response");
            responseCode = response_params.get("status");
            Log.d(TAG, response);
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            String refreshToken = null;
            String accessToken = null;
            try {
                JSONObject response = new JSONObject(result);
                Token token = new Token();
                IdentityProxy identityProxy = IdentityProxy.getInstance();

                if (responseCode != null && responseCode.equals("200")) {

                    refreshToken = response.getString("refresh_token");
                    accessToken = response.getString("access_token");
                    idToken = response.getString("id_token");
                    idToken = new String(Base64.decodeBase64(idToken.getBytes()));
                    Log.d(TAG, refreshToken);
                    Log.d(TAG, accessToken);

                    token.setRefreshToken(refreshToken);
                    token.setIdToken(idToken);
                    token.setAccessToken(accessToken);
                    token.setDate();
                    identityProxy.receiveAccessToken(responseCode, "success", token);

                } else if (responseCode != null && responseCode.equals("400")) {

                    JSONObject mainObject = new JSONObject(result);
                    String error = mainObject.getString("error");
                    String errorDescription = mainObject.getString("error_description");
                    Log.d(TAG, error);
                    Log.d(TAG, errorDescription);
                    identityProxy.receiveAccessToken(responseCode, errorDescription, token);
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
