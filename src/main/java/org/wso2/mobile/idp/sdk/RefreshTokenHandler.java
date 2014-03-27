package org.wso2.mobile.idp.sdk;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: gayan
 * Date: 3/14/14
 * Time: 8:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class RefreshTokenHandler extends Activity {
    private Context context;
    private String idToken = null;
    private CallBack callBack;
    private Tokens tokens = null;

    public RefreshTokenHandler(Context context, CallBack callBack) {
        this.callBack = callBack;
        this.context = context;
        tokens = Tokens.getTokensInstance();
    }

    public void obtainNewAccessToken() {
        new NetworkCallTask(callBack).execute();
    }

    private class NetworkCallTask extends AsyncTask<String, Void, String> {
        CallBack callBack;
        private String responseCode = null;

        public NetworkCallTask(CallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        protected String doInBackground(String... params) {
            String response = "";
            Map<String, String> request_params = new HashMap<String, String>();
            request_params.put("grant_type", "refresh_token");
            request_params.put("refresh_token", tokens.getRefreshToken());
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
                if (responseCode != null && responseCode.equals("200")) {
                    refreshToken = response.getString("refresh_token");
                    accessToken = response.getString("access_token");
                    Log.d("Refresh Token", refreshToken);
                    Log.d("Access Token", accessToken);
                    Tokens tokens = Tokens.getTokensInstance();
                    tokens.setRefreshToken(refreshToken);
                    callBack.receiveNewAccessToken(accessToken, responseCode, "success");

                } else if (responseCode != null && responseCode.equals("400")) {
                    JSONObject mainObject = new JSONObject(result);
                    String error = mainObject.getString("error");
                    String errorDescription = mainObject.getString("error_description");
                    Log.d("error", error);
                    Log.d("error_description", errorDescription);
                    callBack.receiveAccessToken(error, responseCode, errorDescription);
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
