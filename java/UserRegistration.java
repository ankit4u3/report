package in.electromedica.in.treasurehunt;

import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class UserRegistration extends Activity {
	String response;
	EditText et_nick, et_name, et_email, et_contact, et_password;
	ProgressDialog pd;
	boolean chkFlag = false;
	String teamcolor;
	String gameid;
	ImageButton btnblack;
	ImageButton btnblue;
	ImageButton btnred;
	ImageButton btnyellow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_registration);
		et_nick = (EditText) findViewById(R.id.et_nick);
		// et_name = (EditText) findViewById(R.id.et_name);
		// et_email = (EditText) findViewById(R.id.et_email);
		// et_contact = (EditText) findViewById(R.id.et_contact);
		// et_password = (EditText) findViewById(R.id.et_password);
		final Button register = (Button) findViewById(R.id.btn_register);
		btnblack = (ImageButton) findViewById(R.id.bblack);
		btnblue = (ImageButton) findViewById(R.id.bblue);
		btnred = (ImageButton) findViewById(R.id.red);
		btnyellow = (ImageButton) findViewById(R.id.yellow);

		btnblack.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				storeteamcolor("black", "1");
			}
		});

		btnblue.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				storeteamcolor("blue", "1");
			}
		});

		btnred.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				storeteamcolor("red", "1");
			}
		});

		btnyellow.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				storeteamcolor("yellow", "1");
			}
		});

		register.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (checkDetails()) {
					pd = ProgressDialog.show(UserRegistration.this, "",
							"Registering");
					// storeteamcolor("red", "1");
					// RegisterUser task = new RegisterUser();
					// task.execute();

					storeDetails();
					Toast.makeText(getApplicationContext(),
							"Registered successfully!", Toast.LENGTH_SHORT)
							.show();
					Intent i = new Intent(getApplicationContext(),
							GameSelect.class);
					startActivity(i);
					pd.dismiss();
					finish();

				}
			}
		});
	}

	// to check correctness of input and network avaiability
	public boolean checkDetails() {
		String nick = et_nick.getText().toString().trim();
		// String password = et_password.getText().toString().trim();
		// String name = et_name.getText().toString().trim();
		if (nick.length() != 0 && isNetworkAvailable()) {
			return true;
		} else {
			Toast.makeText(this, "Invalid nickname", Toast.LENGTH_SHORT).show();
			return false;
		}
	}

	public boolean checkSpaces(String s) {
		for (int i = 0; i < s.length(); i++) {
			Character c = s.charAt(i);
			if (!Character.isLetterOrDigit(c)) {

				return false;
			}

		}
		return true;
	}

	// checks network availability
	public boolean isNetworkAvailable() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}

	// stores data on web server
	public boolean serverStore() {
		try {
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("nick", et_nick.getText()
					.toString()));
			nameValuePairs.add(new BasicNameValuePair("name", "test"));// et_name.getText()
																		// .toString()));
			nameValuePairs.add(new BasicNameValuePair("password", "pswd"));// et_password
																			// .getText().toString()));
			nameValuePairs.add(new BasicNameValuePair("email", "email"));// et_email
																			// .getText().toString()));
			nameValuePairs.add(new BasicNameValuePair("contact", "cont"));// et_contact
																			// .getText().toString()));
			HttpClient httpclient = new DefaultHttpClient();
			String postURL = MyConsts.NETWORK_FOLDER + "adduser.php";
			HttpPost httppost = new HttpPost(postURL);
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse httpResponse = httpclient.execute(httppost);
			HttpEntity resEntityGet = httpResponse.getEntity();
			if (resEntityGet != null) {
				// do something with the response
				response = EntityUtils.toString(resEntityGet);

				Log.i("GET RESPONSE", response);
				// // Log.d(
				// char responseCharZero = response.charAt(0);
				// if (responseCharZero == '1') {
				return true;
				// } else {
				// return false;
				// }
			}
		} catch (Exception e) {
			Toast.makeText(this, "some problem:" + e.getMessage(),
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		return false;

	}

	// store details in local preferences if successfully stored in web server
	public void storeDetails() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("nick", et_nick.getText().toString());
		// editor.putString("name", et_name.getText().toString());
		// editor.putString("password", et_password.getText().toString());
		// editor.putString("email", et_email.getText().toString());
		// editor.putString("contact", et_contact.getText().toString());
		editor.putInt("points", 0);
		editor.putInt("useCount", 1);
		editor.commit();

	}

	public void storeteamcolor(String color, String gameid) {
		SharedPreferences prefscolor = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		SharedPreferences.Editor editor = prefscolor.edit();
		editor.putString("color", color);
		editor.putString("gameid", gameid);
		editor.commit();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
		overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
	}

	private class RegisterUser extends AsyncTask<String, Void, Boolean> {
		@Override
		protected Boolean doInBackground(String... params) {
			boolean response = serverStore();
			return response;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result == true) {
				storeDetails();
				Toast.makeText(getApplicationContext(),
						"Registered successfully!", Toast.LENGTH_SHORT).show();
				Intent i = new Intent(getApplicationContext(),
						MainActivity.class);
				startActivity(i);
				pd.dismiss();
				finish();
			} else {
				pd.dismiss();
				Toast.makeText(getApplicationContext(),
						"Duplicate nick/email exists on network",
						Toast.LENGTH_SHORT).show();
			}
		}
	}
}
