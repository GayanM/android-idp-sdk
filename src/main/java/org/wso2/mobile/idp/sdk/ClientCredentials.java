package org.wso2.mobile.idp.sdk;

/**
 * Created with IntelliJ IDEA.
 * User: gayan
 * Date: 3/14/14
 * Time: 8:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClientCredentials {
    private String redirectURL;
    private String clientID = null;
    private String clientSecret = null;

    private static ClientCredentials clientCredentials;

    private  ClientCredentials(){
    }

    public static ClientCredentials getInstance(String clientID, String clientSecret, String redirectURL){
        if(clientCredentials == null){
            clientCredentials = new ClientCredentials();
            clientCredentials.setClientID(clientID);
            clientCredentials.setClientSecret(clientSecret);
            clientCredentials.setRedirectURL(redirectURL);
        }
        return clientCredentials;
    }

    public static ClientCredentials getInstance(){
        return clientCredentials;
    }

    public String getClientID() {
        return clientID;
    }

    private void setClientID(String clientID) {
        clientCredentials.clientID = clientID;
    }

    public String getClientSecret() {
        return clientSecret;
    }
    private void setClientSecret(String clientSecret) {
        clientCredentials.clientSecret = clientSecret;
    }
    public String getRedirectURL() {
        return redirectURL;
    }

    private void setRedirectURL(String redirectURL) {
        this.redirectURL = redirectURL;
    }
}
