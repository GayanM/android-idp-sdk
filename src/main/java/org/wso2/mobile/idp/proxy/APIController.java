package org.wso2.mobile.idp.proxy;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.json.JSONException;
import org.json.JSONObject;
import android.os.AsyncTask;


import android.content.Context;
import android.util.Log;

public class APIController {
	private static String TAG = "APIController";
	public  void invokeAPI(APIUtilities apiUtilities){
		new NetworkCallTask(IdentityProxy.getInstance().getApiCallBack()).execute(apiUtilities);
	}
	 private class NetworkCallTask extends AsyncTask<APIUtilities, Void, Map<String,String>> {
		 	APICallBack apiCallBack;
	        public NetworkCallTask(APICallBack apiCallBack) {
	        	this.apiCallBack = apiCallBack;
	        }

	        @Override
	        protected Map<String,String> doInBackground(APIUtilities... params) {
	            APIUtilities apiUtilities = params[0];
	            Token token;
                try {
	                token = IdentityProxy.getInstance().getToken();
		    		String accessToken = token.getAccessToken();
		    		Map<String, String> headers = new HashMap<String, String>();
		    		headers.put("Authorization","Bearer "+accessToken);
		            Map<String, String> response_params = ServerUtilitiesTemp.postData( apiUtilities,headers);
		            return response_params;
                } catch (InterruptedException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
                } catch (ExecutionException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
                } catch (TimeoutException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
                } catch (Exception e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
                }
                return null;
	        }

	        @Override
	        protected void onPostExecute(Map<String,String> result) {
	        	apiCallBack.onReceiveAPIResult(result);
	        }
	    }
}
