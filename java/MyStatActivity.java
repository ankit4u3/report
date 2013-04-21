package in.electromedica.in.treasurehunt;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class MyStatActivity extends Activity {
	int myrank = 0;
	String response;
	TextView tvnick, tvpoints, tvperc, tvrank;
	public WindowManager.LayoutParams lp;
	ProgressDialog pd;
	// String response;\\
	String build;
	String qNum, question, answer, points, latitude, longitute, hint;
	int count = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		lp = getWindow().getAttributes();

		checkBrightness();
		// overridePendingTransition(android.R.anim.fade_in,
		// android.R.anim.fade_out);
		setContentView(R.layout.mystat_layout);
		tvnick = (TextView) findViewById(R.id.textNick);
		tvpoints = (TextView) findViewById(R.id.textPoints);
		tvperc = (TextView) findViewById(R.id.textPerc);
		tvrank = (TextView) findViewById(R.id.textRank);
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		tvnick.setText("User Nick: " + prefs.getString("nick", "null"));
		tvpoints.setText("Coins collected: " + prefs.getInt("points", 0));
		int len = prefs.getInt("count", 0);
		for (int i = 1; i <= len; i++) {
			int stat = prefs.getInt("q" + i, 0);
			if (stat == 1) {
				count++;
			}
		}
		tvperc.setText("Tasks completed: " + count + " out of " + len
				+ " Tasks");
		// setGlobalRank();

		GetGlobalRankTask task = new GetGlobalRankTask();
		pd = ProgressDialog.show(this, "", "Fetching global rank");
		pd.setCancelable(true);
		task.execute();
	}

	public int setGlobalRank() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		String mynick = prefs.getString("nick", "dummy");
		try {
			HttpClient httpclient = new DefaultHttpClient();
			String getURL = MyConsts.NETWORK_FOLDER + "getrank.php" + "?nick="
					+ mynick;
			HttpGet get = new HttpGet(getURL);
			HttpResponse httpResponse = httpclient.execute(get);

			HttpEntity resEntityGet = httpResponse.getEntity();
			if (resEntityGet != null) {
				response = EntityUtils.toString(resEntityGet);
				Log.i("GET RESPONSE", response);
				// Log.d(
			}
			StringTokenizer tmptoken = new StringTokenizer(response, ":");

			myrank = Integer.parseInt(tmptoken.nextToken());
		} catch (Exception e) {
			Toast.makeText(this,
					"Unable to fetch global rank: " + e.getMessage(),
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		return myrank;

	}

	public int getDataFromJSON() {
		try {

			HttpClient client = new DefaultHttpClient();
			String getURL = MyConsts.NETWORK_FOLDER + "getallusers.php";
			// String getURL = "http://flashracebits.webatu.com/questions.json";

			HttpGet get = new HttpGet(getURL);
			HttpResponse responseGet = client.execute(get);
			HttpEntity resEntityGet = responseGet.getEntity();

			if (resEntityGet != null) {
				InputStream instream = resEntityGet.getContent();
				BufferedReader str = new BufferedReader(new InputStreamReader(
						instream));

				String ans = new String("");
				build = new String("");
				while ((ans = str.readLine()) != null) {
					build = build + ans;
					// Log.d(
				}

				JSONObject jobj = new JSONObject(build);
				JSONArray arr = jobj.getJSONArray("users");
				String arrlen = Integer.toString(arr.length());
				// Log.d(
				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(getBaseContext());
				String mynick = prefs.getString("nick", "dummy");
				for (int i = 0; i < arr.length(); i++) {
					JSONObject qs = arr.getJSONObject(i);
					qNum = qs.getString("nick");
					question = qs.getString("points");
					if (qNum.equals(mynick)) {
						int myrank = Integer.valueOf(question);
						count = myrank;

						return myrank;

					}

				}

			}
		} catch (Exception e) {

		} finally {

		}
		return count;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		finish();
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			Toast.makeText(this, "Can be done through Home page only.",
					Toast.LENGTH_SHORT).show();
			return true;
		case R.id.outdoorvis:
			if (MainActivity.isBright) {
				lp.screenBrightness = MainActivity.initBright;
				getWindow().setAttributes(lp);
				MainActivity.isBright = false;
			} else {
				lp.screenBrightness = 1;
				getWindow().setAttributes(lp);
				MainActivity.isBright = true;
			}

			return true;
		}
		return false;
	}

	public void checkBrightness() {
		if (!MainActivity.isBright) {
			lp.screenBrightness = MainActivity.initBright;
			getWindow().setAttributes(lp);
			MainActivity.isBright = false;
		} else {
			lp.screenBrightness = 1;
			getWindow().setAttributes(lp);
			MainActivity.isBright = true;
		}

	}

	@Override
	protected void onResume() {
		checkBrightness();
		super.onResume();
	}

	private class GetGlobalRankTask extends AsyncTask<String, Void, Integer> {
		@Override
		protected Integer doInBackground(String... params) {
			int response = getDataFromJSON();// setGlobalRank();
			return response;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// postLeaderData(result);
			tvrank.setText("Global Rank : " + result);
			if (pd.isShowing())
				pd.dismiss();

		}
	}
}
