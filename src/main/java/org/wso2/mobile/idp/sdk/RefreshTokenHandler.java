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
public class RefreshTokenHandler extends Activity {
    private Context context;
    private String refreshToken = null;
    private String accessToken = null;
    private String idToken = null;
    private Activity activity;
    private Tokens tokens = null;

    public RefreshTokenHandler(Context context,Activity activity){
        this.activity = activity;
        this.context = context;
        tokens = Tokens.getTokensInstance();
    }

    public void obtainNewAccessToken(){
        new NetworkCallTask(activity).execute();
    }

    private class NetworkCallTask extends AsyncTask<String, Void, String> {
        final Activity activity;
        public NetworkCallTask(Activity activity){
            this.activity = activity;
        }
        @Override
        protected String doInBackground(String... params) {
            String response = "";
            Map<String, String> request_params = new HashMap<String, String>();
            request_params.put("grant_type", "refresh_token");
            request_params.put("refresh_token", tokens.getRefreshToken());
            Map<String, String> response_params = ServerUtilities.postData(context,TokenEndPoints.getInstance().getAccessTokenURL(), request_params);
            response = response_params.get("response");
            Log.d("RESPONSE",response);
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject mainObject = new JSONObject(result);
                refreshToken = mainObject.getString("refresh_token");
                accessToken =mainObject.getString("access_token");

                Log.d("Refresh Token",refreshToken);
                Log.d("Access Token",accessToken);
                Tokens tokens = Tokens.getTokensInstance();
                tokens.setAccessToken(accessToken);
                tokens.setRefreshToken(refreshToken);
                activity.closeContextMenu();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
