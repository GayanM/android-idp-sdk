package org.wso2.mobile.idp.proxy;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
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
	public final static boolean initiated = false;
    private static String TAG = "IdentityProxy";
    private Token token;
    private static IdentityProxy identityProxy = new IdentityProxy();
    private static Context context;
    private String clientID;
    private String clientSecret;
    private String accessTokenURL;
    private APIAccessCallBack apiAccessCallBack;
	int requestCode = 0;
    public int getRequestCode() {
		return requestCode;
	}

	public void setRequestCode(int requestCode) {
		this.requestCode = requestCode;
	}

	private IdentityProxy() {

    }

    public String getAccessTokenURL() {
        return accessTokenURL;
    }

    public void setAccessTokenURL(String accessTokenURL) {
        this.accessTokenURL = accessTokenURL;
    }

    public void receiveAccessToken(String status, String message, Token token) {
    	if(token!= null){
    		Log.d(TAG, token.getAccessToken());
	        Log.d(TAG, token.getRefreshToken());   
    	} 
    	this.token = token;
        apiAccessCallBack.onAPIAccessRecive(status);
    }

    public void receiveNewAccessToken(String status, String message, Token token) {
        this.token = token;
    }

    public static synchronized IdentityProxy getInstance() {
        return identityProxy;
    }
    public void init(String clientID, String clientSecret){
    	this.clientID = clientID;
    	this.clientSecret = clientSecret;		
    }
    public void init(String clientID, String clientSecret,String username, String password,String tokenEndPoint, APIAccessCallBack apiAccessCallBack, Context contextt) {
        this.clientID = clientID;
        this.clientSecret = clientSecret;
        this.apiAccessCallBack = apiAccessCallBack;
        context = contextt;
        SharedPreferences mainPref = context.getSharedPreferences("com.mdm",Context.MODE_PRIVATE);
        Editor editor = mainPref.edit();
        editor.putString("client_id",clientID);
        editor.putString("client_secret",clientSecret);
        editor.commit();
        AccessTokenHandler accessTokenHandler = new AccessTokenHandler(clientID, clientSecret, username, password, tokenEndPoint, this);
        accessTokenHandler.obtainAccessToken();
    }

    public Token getToken(Context contextt) throws Exception, InterruptedException, ExecutionException, TimeoutException {
    	context = contextt;
        SharedPreferences mainPref = context.getSharedPreferences("com.mdm",Context.MODE_PRIVATE);
        String refreshToken = mainPref.getString("refresh_token","Default").toString();
        
        if(token == null){
        	if(refreshToken == null){
                return null;
        	}else if(refreshToken != null){
        		this.clientID = mainPref.getString("client_id","Default").toString();
        		this.clientSecret = mainPref.getString("client_secret","Default").toString();
        		token = new Token();
        		token.setRefreshToken(refreshToken);
        	}
        }

        boolean decision = dateComparison(token.getDate());
        if (decision) {
            return token;
        }
        RefreshTokenHandler refreshTokenHandler = new RefreshTokenHandler(context, clientID, clientSecret, token);
        refreshTokenHandler.obtainNewAccessToken();
        return token;
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
    
    public Context getContext(){
    	return context;
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
