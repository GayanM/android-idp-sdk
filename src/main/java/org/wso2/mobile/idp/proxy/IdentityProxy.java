package org.wso2.mobile.idp.proxy;

import android.content.Context;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * client application specific data
 */
public class IdentityProxy implements CallBack {
    private static String TAG = "IdentityProxy";
    private Token token;
    private static IdentityProxy identityProxy = new IdentityProxy();
    private Context context;
    private String clientID;
    private String clientSecret;
    private String accessTokenURL;
    private APIAccessCallBack apiAccessCallBack;


	private IdentityProxy() {

    }

    public String getAccessTokenURL() {
        return accessTokenURL;
    }

    public void setAccessTokenURL(String accessTokenURL) {
        this.accessTokenURL = accessTokenURL;
    }

    public void receiveAccessToken(String status, String message, Token token) {
        Log.d(TAG, token.getAccessToken());
      //  Log.d(TAG, token.getIdToken());
        Log.d(TAG, token.getRefreshToken());
        this.token = token;
        apiAccessCallBack.onAPIAccessRecive(status);
    }

    public void receiveNewAccessToken(String status, String message, Token token) {
        this.token = token;
    }

    public static synchronized IdentityProxy getInstance() {
        return identityProxy;
    }
    
    public void init(String clientID, String clientSecret,String username, String password,String tokenEndPoint, APIAccessCallBack apiAccessCallBack) {
        this.clientID = clientID;
        this.clientSecret = clientSecret;
        this.apiAccessCallBack = apiAccessCallBack;
        AccessTokenHandler accessTokenHandler = new AccessTokenHandler(clientID, clientSecret, username, password, tokenEndPoint, this);
        accessTokenHandler.obtainAccessToken();
    }

    public Token getToken() throws Exception, InterruptedException, ExecutionException, TimeoutException {
        if(token == null){
            return null;
        }
        boolean decision = dateComparison(token.getDate());
        if (decision) {
            return token;
        }
        RefreshTokenHandler refreshTokenHandler = new RefreshTokenHandler(context, clientID, clientSecret, token);
        refreshTokenHandler.obtainNewAccessToken();
        return token;
    }

    public boolean dateComparison(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date currentDate = new Date();
        String strDate = dateFormat.format(currentDate);
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        try {
            currentDate = format.parse(strDate);
        } catch (ParseException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        long diff = (currentDate.getTime() - date.getTime());
        long diffSeconds = diff / 1000 % 60;
        if (diffSeconds < 3000) {
            return true;
        }
        return false;
    }
}
