package org.wso2.mobile.idp.sdk;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;


/**
 * Created with IntelliJ IDEA.
 * User: gayan
 * Date: 3/14/14
 * Time: 7:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class WebViewBridge extends Activity {

    public Intent getLoginIntenttte() {
        Log.v("RECIEVED", "RECIVED");
        final Intent loginIntent = new Intent("android.intent.action.MAIN");
        loginIntent.setComponent(ComponentName.unflattenFromString(IDPSDKConstants.idpProxyPackage + "/" + IDPSDKConstants.idpProxyActivity));
        ClientCredentials clientCredentials = ClientCredentials.getInstance();
        loginIntent.putExtra("client_id", clientCredentials.getClientID());
        loginIntent.putExtra("redirect_url", clientCredentials.getRedirectURL());
        Log.v("RECIEVED3", "RECIVED3");
        return loginIntent;
        //startActivityForResult(loginIntent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v("RECIEVED2", "RECIVED2");
        super.onActivityResult(requestCode, resultCode, data);
        /*String code = data.getStringExtra("code");
        Log.d("Test Data",code);
        AccessTokenHandler accessTokenHandler = new AccessTokenHandler();
        accessTokenHandler.obtainAccessToken(code);*/

    }

}
