package org.wso2.mobile.idp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import org.wso2.mobile.idp.filebrowser.FileDialog;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * This activity will be fired to user to insert his profile image. The idea of inserting a profile image is avoid phishing attacks
 * 
 *
 */
public class MainActivity extends Activity{
	private static final String TAG = "MainActivity";
	public static int imageFileSize = 0;
	private FileInputStream fis;
	/* Initialize two buttons, one for go forward to web view and other button for upload profile picture  
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button loginButton = (Button) findViewById(R.id.btnLogin);//login button to go to web view
		loginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(MainActivity.this, WebViewActivity.class);
				i.putExtra("self_login", "self_login");
				startActivity(i);
				finish();
			}

		});
		Button imageUploadButton = (Button) findViewById(R.id.btnUploadImage);//button to upload profile picture
		imageUploadButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(getBaseContext(), FileDialog.class);
				intent.putExtra(FileDialog.START_PATH, "/sdcard");
				intent.putExtra(FileDialog.CAN_SELECT_DIR, true);
				intent.putExtra(FileDialog.FORMAT_FILTER, new String[] { "jpg" });
				try {
					startActivityForResult(intent, 23);//launch FileDialog activity to select a image from SD card 
				} catch (NullPointerException e) {
					Log.d(TAG, e.toString());
				}
			}
		});
	}
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	/* launch configuration activity to set hostname and port
	 * (non-Javadoc)
	 * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_settings:
				Log.d(TAG, "Menu Setting Clicked");
				Intent intent = new Intent(this, Configuration.class);
				startActivity(intent);
				break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	/* Save captured image from SD card to application private data 
	 * (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 23) {
			Log.d(TAG, "Saving...");
			try {
				String filePath = data.getStringExtra(FileDialog.RESULT_PATH);
				File file = new File(filePath);
				Log.v("File Selected", filePath);
				fis = new FileInputStream(file);
				imageFileSize = (int) file.length();
				byte[] fileArray = new byte[imageFileSize];
				fis.read(fileArray);
				FileOutputStream outputStream =
						openFileOutput("profile_pic.png",
						               Context.MODE_PRIVATE);
				outputStream.write(fileArray);
				outputStream.flush();
				outputStream.close();
				super.onActivityResult(requestCode, resultCode, data);
			} catch (FileNotFoundException e) {
				Log.d(TAG, e.toString());
			} catch (Exception e) {
				Log.d(TAG, e.toString());
			}
		}
	}
	
}
