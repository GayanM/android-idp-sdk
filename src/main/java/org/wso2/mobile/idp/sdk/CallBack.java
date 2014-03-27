package org.wso2.mobile.idp.sdk;

/**
 * Created with IntelliJ IDEA.
 * User: gayan
 * Date: 3/25/14
 * Time: 8:40 AM
 * To change this template use File | Settings | File Templates.
 */
public interface CallBack {
    void receiveAccessToken(String response, String status, String message);

    void receiveNewAccessToken(String response, String status, String message);
}
