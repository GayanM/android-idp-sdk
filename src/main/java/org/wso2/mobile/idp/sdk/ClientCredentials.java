package org.wso2.mobile.idp.sdk;

/**
 * Created with IntelliJ IDEA.
 * User: gayan
 * Date: 3/14/14
 * Time: 8:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClientCredentials {

    private String transportProtocol = "http://";
    private String clientID ;
    private String clientSecret;
    private String redirectURL;
    private String authorizeURL;
    private String accessTokenURL;


    public void setAuthorizeURL(String authorizeURL) {
        this.authorizeURL = authorizeURL;
    }

    public void setAccessTokenURL(String accessTokenURL) {
        this.accessTokenURL = accessTokenURL;
    }

    public String getAuthorizeURL() {
        return authorizeURL;
    }

    public String getAccessTokenURL() {
        return accessTokenURL;
    }

    private static ClientCredentials clientCredentials;

    private  ClientCredentials(){

    }

    public static ClientCredentials getInstance(){
        if(clientCredentials == null){
            clientCredentials = new ClientCredentials();
        }
        return clientCredentials;
    }

    public String getRedirectURL() {
        return redirectURL;
    }

    public void setRedirectURL(String redirectURL) {
        clientCredentials.redirectURL = redirectURL;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        clientCredentials.clientID = clientID;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        clientCredentials.clientSecret = clientSecret;
    }
}
