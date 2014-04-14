package org.wso2.mobile.idp.proxy;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


/**
 * Encapsulate IDP proxy application information inside proxy
 */
public class IdentityProxyActivity extends Activity {
    private static String TAG = "IdentityProxyActivity";
    private static String clientID = null;
    private static String clientSecret = null;

    static void setClientCrdentials(String clientId, String client_Secret) {
        clientID = clientId;
        clientSecret = client_Secret;
    }

    public void sendResponse() {
        Log.d("sendResponse", "sendResponse");
        Intent intent = new Intent();
       /* intent.putExtra("status",status);
        intent.putExtra("message",message);*/
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        IdentityProxy.getInstance().setIdentityProxyActivity(this);
        Log.d(TAG, "starting IdentityProxyActivity Activity");
        final Intent loginIntent = new Intent("android.intent.action.MAIN");
        loginIntent.setComponent(ComponentName.unflattenFromString(IDPConstants.idpProxyPackage + "/" + IDPConstants.idpProxyActivity));
        IdentityProxy clientCredentials = IdentityProxy.getInstance();
        Log.d(TAG, clientID);
        loginIntent.putExtra("client_id", clientID);
        startActivityForResult(loginIntent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "starting IdentityProxyActivity onActivityResult");
        if (data != null) {
            String code = data.getStringExtra("code");
            String accessTokenURL = data.getStringExtra("access_token_url");
            Log.v(TAG, code);
            IdentityProxy.getInstance().setAccessTokenURL(accessTokenURL);
            super.onActivityResult(requestCode, resultCode, data);
            try {
                AccessTokenHandler accessTokenHandler = new AccessTokenHandler(getApplicationContext(), clientID, clientSecret);
                accessTokenHandler.obtainAccessToken(code);
            } catch (Exception e) {
                Log.d("ERROR", e.toString());
            }
        }

    }
}
