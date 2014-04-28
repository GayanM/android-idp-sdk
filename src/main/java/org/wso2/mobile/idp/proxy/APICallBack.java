package org.wso2.mobile.idp.proxy;

import java.util.Map;

public interface APICallBack {
	public void onAPIAccessRecive();
	public void onReceiveAPIResult(Map <String,String> result);
}
