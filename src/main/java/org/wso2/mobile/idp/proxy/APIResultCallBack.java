package org.wso2.mobile.idp.proxy;

import java.util.Map;

public interface APIResultCallBack {

	public void onReceiveAPIResult(Map <String,String> result, int requestCode);
}
