package org.wso2.mobile.idp.proxy;

import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;

public class SDKActivity extends Activity {

    private static String TAG = "IdentityProxyActivity";
    private String clientID = null;
    private String clientSecret = null;


	public void init(String clientID, String clientSecret, FrontEndCallBack frontEndCallBack){
        this.clientID = clientID;
        this.clientSecret = clientSecret;
		IdentityProxy identityProxy = IdentityProxy.getInstance();
        identityProxy.init(clientID,clientSecret,getApplicationContext(),frontEndCallBack);
        Log.d(TAG, "starting IDP Proxy App");
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
