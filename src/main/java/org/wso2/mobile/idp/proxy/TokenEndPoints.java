package org.wso2.mobile.idp.proxy;

/**
 * Authorization server endpoints
 */
public class TokenEndPoints {

    private String accessTokenURL;
    private static TokenEndPoints tokenEndPoints = null;

    private TokenEndPoints() {

    }

    public static TokenEndPoints getInstance() {
        if (tokenEndPoints == null) {
            tokenEndPoints = new TokenEndPoints();
        }
        return tokenEndPoints;
    }

    public void setAccessTokenURL(String accessTokenURL) {
        this.accessTokenURL = accessTokenURL;
    }

    public String getAccessTokenURL() {
        return accessTokenURL;
    }
}
