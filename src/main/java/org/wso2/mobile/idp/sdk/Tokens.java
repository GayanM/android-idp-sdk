package org.wso2.mobile.idp.sdk;

/**
 * Created with IntelliJ IDEA.
 * User: gayan
 * Date: 3/15/14
 * Time: 9:54 AM
 * To change this template use File | Settings | File Templates.
 */
public final class Tokens {
    private String accessToken = null;
    private String refreshToken = null;
    private String idToken = null;
    private static Tokens tokensInstance = null;


    private Tokens(){

    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public static Tokens getTokensInstance(){
        if(tokensInstance == null){
            tokensInstance = new Tokens();
        }
        return tokensInstance;
    }
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }



}
