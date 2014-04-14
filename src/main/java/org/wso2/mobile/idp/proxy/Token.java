package org.wso2.mobile.idp.proxy;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Persists refresh token to obtain new access token and id token to retrieve login user claims
 */
public final class Token {
    private String refreshToken = null;
    private String idToken = null;
    private String accessToken = null;
    private Date receivedDate = null;

    public Date getDate() {
        return receivedDate;
    }

    public void setDate() {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();
        String strDate = dateFormat.format(date);
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        try {
            receivedDate = format.parse(strDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String id_Token) {
        idToken = id_Token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
