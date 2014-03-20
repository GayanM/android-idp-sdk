package org.wso2.mobile.idp.sdk;

import java.io.*;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;



import org.apache.commons.codec.binary.Base64;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;


import android.content.Context;

import android.util.Log;



public class ServerUtilities {

    public static String sendData(){
        return "Helllo";
    }
	
	public static Map<String, String> postData(Context context, String url, Map<String, String> params) {
	    // Create a new HttpClient and Post Header
		Map<String, String> response_params = new HashMap<String, String>();
	    HttpClient httpclient = getCertifiedHttpClient(context);
		Log.d(IDPSDKConstants.INFO,"Posting '" + params.toString() + "' to " + url);
		StringBuilder bodyBuilder = new StringBuilder();
		Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, String> param = iterator.next();
			bodyBuilder.append(param.getKey()).append('=')
					.append(param.getValue());
			if (iterator.hasNext()) {
				bodyBuilder.append('&');
			}
		}
		
		String body = bodyBuilder.toString();
		Log.d(IDPSDKConstants.INFO, "Posting '" + body + "' to " + url);
		
		byte[] postData = body.getBytes();	
		
		HttpPost httppost = new HttpPost(url);

		httppost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        httppost.setHeader("Accept", "*/*");
        httppost.setHeader("User-Agent","Mozilla/5.0 ( compatible ), Android");

        ClientCredentials clientCredentials = ClientCredentials.getInstance();
		
		String authorizationString = "Basic "+new String(Base64.encodeBase64((clientCredentials.getClientID() + ":" + clientCredentials.getClientSecret()).getBytes())); //this line is diffe
		httppost.setHeader("Authorization", authorizationString);
		
	    try {
	        httppost.setEntity(new ByteArrayEntity(postData));
	        HttpResponse response = httpclient.execute(httppost);
	        Log.d("RESPONSE2",response.getStatusLine().getStatusCode()+"");
	        response_params.put("response",getResponseBody(response));
			response_params.put("status", String.valueOf(response.getStatusLine().getStatusCode()));
			Log.d("RESPONSE",response_params.get("response"));
			return response_params;
	    } catch (ClientProtocolException e) {
	    	e.printStackTrace();
	    	Log.e("RESPONSE3",e.toString());
	    	return null;
	    } catch (IOException e) {
	    	e.printStackTrace();
	    	Log.e("RESPONSE4",e.toString());
	    	return null;
	    }
	} 
	public static HttpClient getCertifiedHttpClient(Context context) {
	     try {
	    	/* KeyStore localTrustStore = KeyStore.getInstance("BKS");
	    	// InputStream in = context.getResources().openRawResource(R.raw.emm_truststore);
             File file = new File("emm_truststore.bks");
             InputStream in = new FileInputStream(file);
	    	 localTrustStore.load(in, IDPSDKConstants.TRUSTSTORE_PASSWORD.toCharArray());

	    	 SchemeRegistry schemeRegistry = new SchemeRegistry();
	    	 schemeRegistry.register(new Scheme("http", PlainSocketFactory
	    	                 .getSocketFactory(), 80));
	    	 SSLSocketFactory sslSocketFactory = new SSLSocketFactory(localTrustStore);
	    	 sslSocketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	    	 schemeRegistry.register(new Scheme("https", sslSocketFactory, 443));
	    	 HttpParams params = new BasicHttpParams();
	    	 ClientConnectionManager cm = 
	    	     new ThreadSafeClientConnManager(params, schemeRegistry);

	    	 HttpClient client = new DefaultHttpClient(cm, params);*/
	    	 HttpClient client = new DefaultHttpClient();
	    	 return client;
	     } catch (Exception e) {
	    	 Log.e("ERROR",e.toString());
	         return null;
	     }
	}
	public static String getResponseBody(HttpResponse response) {

	    String response_text = null;
	    HttpEntity entity = null;
	    try {
	        entity = response.getEntity();
	        response_text = _getResponseBody(entity);
	    } catch (ParseException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        if (entity != null) {
	            try {
	                entity.consumeContent();
	            } catch (IOException e1) {
	            }
	        }
	    }
	    return response_text;
	}
	public static String _getResponseBody(final HttpEntity entity) throws IOException, ParseException {

	    if (entity == null) {
	        throw new IllegalArgumentException("HTTP entity may not be null");
	    }

	    InputStream instream = entity.getContent();

	    if (instream == null) {
	        return "";
	    }

	    if (entity.getContentLength() > Integer.MAX_VALUE) {
	        throw new IllegalArgumentException(

	        "HTTP entity too large to be buffered in memory");
	    }

	    String charset = getContentCharSet(entity);

	    if (charset == null) {

	        charset = HTTP.DEFAULT_CONTENT_CHARSET;

	    }

	    Reader reader = new InputStreamReader(instream, charset);

	    StringBuilder buffer = new StringBuilder();

	    try {

	        char[] tmp = new char[1024];

	        int l;

	        while ((l = reader.read(tmp)) != -1) {

	            buffer.append(tmp, 0, l);

	        }

	    } finally {

	        reader.close();

	    }

	    return buffer.toString();

	}

	public static String getContentCharSet(final HttpEntity entity) throws ParseException {

	    if (entity == null) {
	        throw new IllegalArgumentException("HTTP entity may not be null");
	    }

	    String charset = null;

	    if (entity.getContentType() != null) {

	        HeaderElement values[] = entity.getContentType().getElements();

	        if (values.length > 0) {

	            NameValuePair param = values[0].getParameterByName("charset");

	            if (param != null) {

	                charset = param.getValue();

	            }

	        }

	    }

	    return charset;

	}

}
