package org.wso2.mobile.idp.sdk;

/**
 * Created with IntelliJ IDEA.
 * User: gayan
 * Date: 3/23/14
 * Time: 7:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class TokenEndPoints {

    private String authorizeURL;
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
}
