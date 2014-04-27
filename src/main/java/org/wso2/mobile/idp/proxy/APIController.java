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
	static Context context;
	public  void invokeAPI(APIUtilities apiUtilities, APICallBack apiCallBack){
		new NetworkCallTask(apiCallBack).execute(apiUtilities);
	}
	 private class NetworkCallTask extends AsyncTask<APIUtilities, Void, String> {
		 	String response;
		 	String responseCode;
		 	APICallBack apiCallBack;
	        public NetworkCallTask(APICallBack apiCallBack) {
	        	this.apiCallBack = apiCallBack;
	        }

	        @Override
	        protected String doInBackground(APIUtilities... params) {
	            String response = "";
	            APIUtilities apiUtilities = params[0];
	            Token token;
                try {
	                token = IdentityProxy.getInstance().getToken();
		    		String accessToken = token.getAccessToken();
		    		Map<String, String> headers = new HashMap<String, String>();
		    		headers.put("Authorization","Bearer "+accessToken);
		            Map<String, String> response_params = ServerUtilitiesTemp.postData( apiUtilities,headers);
		            response = response_params.get("response");
		            responseCode = response_params.get("status");
		            Log.d(TAG,responseCode);
		            return response;
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
	        protected void onPostExecute(String result) {
	        	apiCallBack.onReceiveAPIResult(result);
	        }
	    }
}
