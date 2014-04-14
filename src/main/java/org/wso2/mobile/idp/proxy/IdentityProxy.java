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
    private FrontEndCallBack frontEndCallBack;
    private IdentityProxyActivity identityProxyActivity;
    private Context context;
    private String clientID;
    private String clientSecret;

    private IdentityProxy() {

    }

    public void receiveAccessToken(String status, String message, Token token) {
        Log.d(TAG, token.getAccessToken());
        Log.d(TAG, token.getIdToken());
        Log.d(TAG, token.getRefreshToken());
        this.token = token;
        identityProxyActivity.sendResponse();
    }

    public void receiveNewAccessToken(String status, String message, Token token) {
        this.token = token;
    }

    public void setIdentityProxyActivity(IdentityProxyActivity identityProxyActivity) {
        this.identityProxyActivity = identityProxyActivity;
    }

    public static IdentityProxy getInstance() {
        return identityProxy;
    }

    public void init(String clientID, String clientSecret, Context context) {
        IdentityProxyActivity.setClientCrdentials(clientID, clientSecret);
        this.clientID = clientID;
        this.clientSecret = clientSecret;
        this.context = context;
    }

    public Token getToken() throws Exception, InterruptedException, ExecutionException, TimeoutException {
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