package in.electromedica.in.treasurehunt;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class LocationMarker extends Activity {
	GestureDetector gestureDetector;
	int REQUEST_CODE = 1;
	QuestionDataSource datasource;
	Question q;
	int pos;
	String nick;
	String response;
	LocationManager locationManager;
	Location pointLocation;
	LocationListener listen;
	ProgressDialog pd;
	String answer_md5;
	EditText et;
	ListView lvoption;
	ArrayAdapter adap;
	AlertDialog.Builder builder;
	Dialog d;
	EditText alat;
	EditText alon;
	public WindowManager.LayoutParams lp;

	// public AmbilWarnaDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_marker);
		// Parse.initialize(this, "0vU0f1YM4Bmt1QOn2JKkeWBe7N36Hu4nmZayJyzX",
		// "nOCQcbmsEQBuMFU9fFTxDDk6w0TWQwan4NQB2kiQ");

		Button gps = (Button) findViewById(R.id.button1);
		Button save = (Button) findViewById(R.id.button2);
		final EditText aqnum = (EditText) findViewById(R.id.AQNUM);
		final EditText aanswer = (EditText) findViewById(R.id.AANSWER);
		final EditText apoints = (EditText) findViewById(R.id.APOINTS);
		alat = (EditText) findViewById(R.id.ALAT);
		alon = (EditText) findViewById(R.id.ALON);
		final EditText ahint = (EditText) findViewById(R.id.AHINT);

		save.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				send2WebSyncContactsMain(aqnum.getText().toString(), aanswer
						.getText().toString(), apoints.getText().toString(),
						alat.getText().toString(), alon.getText().toString(),
						ahint.getText().toString());
				// send2WebSyncContactsMain("1", "2", "2", "3", "4", "4");

				if (alat.getText().toString().equals(null)) {
					return;

				}
				if (alon.getText().toString().equals(null)) {
					return;

				}
				double lat = Double.valueOf(alat.getText().toString());

				double lon = Double.valueOf(alon.getText().toString());

				Intent i = new Intent(LocationMarker.this, ShowTheMap.class);
				ShowTheMap.putLatLong(lat, lon);

				startActivity(i);
			}
		});

		gps.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					// TODO: give hints for user. load hint status from shared
					// prefs
					locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
					listen = new MyLocationListener();
					locationManager.requestLocationUpdates(
							LocationManager.GPS_PROVIDER,
							MyConsts.MINIMUM_TIME_BETWEEN_UPDATE,
							MyConsts.MINIMUM_DISTANCECHANGE_FOR_UPDATE, listen);
				} catch (Exception e) {
					Log.i("proximity", e.getMessage());
					e.printStackTrace();
				}
			}
		});

	}

	protected void send2WebSyncContactsMain(final String question,
			final String answer, final String points, final String lat,
			final String lon, final String hint) {
		Thread t = new Thread() {

			@Override
			public void run() {
				Looper.prepare(); // For Preparing Message Pool for the
									// child
									// Thread
				HttpClient client = new DefaultHttpClient();
				HttpConnectionParams.setConnectionTimeout(client.getParams(),
						10000); // Timeout Limit
				HttpResponse response;

				try {

					// ParseObject testObject = new ParseObject("questions");
					// testObject.put("question", question);
					// testObject.put("answer", answer);
					// testObject.put("lat", lat);
					// testObject.put("long", lon);
					// testObject.put("hint", hint);
					// testObject.put("point", points);
					// testObject.saveInBackground();

				} catch (Exception e) {
					e.printStackTrace();
					// Log.d(
				}

				Looper.loop(); // Loop in the message queue
			}
		};

		t.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.location_marker, menu);
		return true;
	}

	public class MyLocationListener implements LocationListener {
		public void onLocationChanged(Location location) {
			if (location != null) {
				alat.setText(String.valueOf(location.getLatitude()));
				alon.setText(String.valueOf(location.getLongitude()));
			}

		}

		public void onProviderDisabled(String arg0) {
			// TODO Auto-generated method stub
			if (arg0 != null) {
				locationManager.removeUpdates(this);
			}

		}

		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}
	}

}
