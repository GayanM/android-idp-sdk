package org.wso2.mobile.idp.sdk;

import java.util.HashMap;
import java.util.Map;

import android.os.AsyncTask;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ComponentName;
import org.apache.commons.codec.binary.Base64;

import javax.activity.ActivityCompletedException;

/**
 * Created with IntelliJ IDEA.
 * User: gayan
 * Date: 3/14/14
 * Time: 8:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class AccessTokenHandler extends Activity {
    private Context context;
    private String refreshToken = null;
    private String accessToken = null;
    private String idToken = null;
    private CallBack callBack;

    public AccessTokenHandler(Context context, CallBack callBack) {
        this.callBack = callBack;
        this.context = context;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void obtainAccessToken(String code) {
        new NetworkCallTask(callBack).execute(code);
    }

    private class NetworkCallTask extends AsyncTask<String, Void, String> {
        final CallBack callBack;
        private String responseCode = null;

        public NetworkCallTask(CallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        protected String doInBackground(String... params) {
            String response = "";
            String code = params[0];
            Log.d("Code", code);
            Map<String, String> request_params = new HashMap<String, String>();
            request_params.put("grant_type", "authorization_code");
            request_params.put("code", code);
            request_params.put("redirect_uri", ClientCredentials.getInstance().getRedirectURL());
            request_params.put("scope", "openid");
            Map<String, String> response_params = ServerUtilities.postData(context, TokenEndPoints.getInstance().getAccessTokenURL(), request_params);
            response = response_params.get("response");
            responseCode = response_params.get("status");
            Log.d("RESPONSE", response);
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject response = new JSONObject(result);
                if(responseCode!=null&&responseCode.equals("200")){

                        refreshToken = response.getString("refresh_token");
                        accessToken = response.getString("access_token");
                        idToken = response.getString("id_token");
                        idToken = new String(Base64.decodeBase64(idToken.getBytes()));
                        Log.d("Refresh Token", refreshToken);
                        Log.d("Access Token", accessToken);
                        Tokens tokens = Tokens.getTokensInstance();
                        tokens.setRefreshToken(refreshToken);
                        tokens.setIdToken(idToken);
                        callBack.receiveAccessToken(accessToken,responseCode,"success");

                }else if(responseCode!=null&&responseCode.equals("400")){

                        JSONObject mainObject = new JSONObject(result);
                        String error = mainObject.getString("error");
                        String errorDescription = mainObject.getString("error_description");
                        Log.d("error", error);
                        Log.d("error_description", errorDescription);
                        callBack.receiveAccessToken(error,responseCode,errorDescription);
                }
            } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            }
        }
    }
}
