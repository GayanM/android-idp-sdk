package org.wso2.mobile.idp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

/**
 * Entry point of IDP proxy application
 * 
 *
 */

public class WebViewActivity extends Activity {
	private static final String TAG = "WebViewActivity";
	private WebView webView;
	private String clientID = null;
	private String selfLogin = null;
	private OauthEndPoints oauthEndPoints = null;
	public static int imageFileSize = 0;
	private FileInputStream fis;

	/* 
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	@SuppressLint("SetJavaScriptEnabled")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);
		oauthEndPoints = OauthEndPoints.getInstance();
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		String host = sharedPreferences.getString("edittext_preference_host", "10.100.5.3");//read host from configurations
		Log.d("Host", host);
		String port = sharedPreferences.getString("edittext_preference_port", "9763");//read port from configurations 
		Log.d("Port", port);
		oauthEndPoints.setEndPointURLs(host, port);//set port & host	
		
		clientID = getIntent().getStringExtra("client_id");//get client ID from consumer application 
		selfLogin = getIntent().getStringExtra("self_login");//self login will be null if you start the login process from consumer application, self_login will have String value if start login process from IDP proxy application itself
		
		if (clientID != null && selfLogin == null) {//login process has been started from consumer application, then show profile picture and load web view 
			Log.d(TAG, "Recived Client ID, from Third Party Application");
			Log.d(TAG, clientID);
			showImage();
			loadWebView();
		} else if (clientID == null && selfLogin != null) {//MainActivity launch WebView activity after doing configurations like host, port and profile picture at this point self_login has some String value and client ID is null.   
			Log.d(OauthCostants.INFO, "Recived Client ID, Redirect URL from IDP Proxy Application");
			clientID = OauthCostants.CLIENT_ID;//set client ID of IDP proxy application
			showImage();
			loadWebView();
		} else if(clientID == null && selfLogin == null) {//first time launch of IDP proxy application so both client ID and selfLogin are null.
			Intent entry = new Intent(this, MainActivity.class);
			startActivity(entry);//launch MainActivity to do configurations 
			finish();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/* Configuration can be launched even at this point if user want to change host and port
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		switch (item.getItemId()) {
			case R.id.action_settings:
				Log.v("Menu Clicked", "Menu Setting Clicked");
				Intent intent = new Intent(this, Configuration.class);
				startActivity(intent);
				break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	private class LoginWebViewClient extends WebViewClient {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.webkit.WebViewClient#shouldOverrideUrlLoading(android.webkit
		 * .WebView, java.lang.String)
		 */
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.d("Redirect URL", url);
			String code = Uri.parse(url).getQueryParameter("code");
			if (url.contains(OauthCostants.CALL_BACK_URL) && code != null) {
				
				//Log.d("Obtained Authorization Code", code);
				if (selfLogin != null) {
					Log.d("selfLogin", selfLogin);
					selfLogin = null;
					finish();//finish the process if IDP proxy application start the login process
				} else {
					Intent intent = new Intent();
					intent.putExtra("code", code);
					intent.putExtra("authorize_url", oauthEndPoints.getAuthorizeURL());
					intent.putExtra("access_token_url", oauthEndPoints.getAccessTokenURL());
					setResult(RESULT_OK, intent);//send authorization code and access token url to consumer application 
					finish();
				}
			}
			return false;
		}
		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
			handler.proceed(); // Ignore SSL certificate errors
		}
	}

	/**
	 * display profile picture
	 */
	void showImage() {
		FileInputStream readFile;
		try {
			readFile = openFileInput("profile_pic.png");
			byte[] fileInputArray = new byte[MainActivity.imageFileSize];
			readFile.read(fileInputArray);
			readFile.close();
			Bitmap myBitmap =
			                  BitmapFactory.decodeByteArray(fileInputArray, 0,
			                                                fileInputArray.length);

			ImageView iv = (ImageView) findViewById(R.id.imageView1);
			iv.setImageBitmap(myBitmap);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/*
	 *load web pages for given URL 
	 */
	@SuppressLint("SetJavaScriptEnabled")
	void loadWebView() {
		String authrizeRequest =
		                         oauthEndPoints.getAuthorizeURL() +
		                                 "?response_type=code&client_id=" + clientID +
		                                 "&redirect_uri=" + OauthCostants.CALL_BACK_URL + "&scope=openid";
		webView = (WebView) findViewById(R.id.webView1);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl(authrizeRequest);
		webView.setWebViewClient(new LoginWebViewClient());
	}
	

}