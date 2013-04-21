package in.electromedica.in.treasurehunt;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.graphics.drawable.Drawable;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class ShowTheMap extends MapActivity {

	private static double lat;
	private static double lon;
	private int latE6;
	private int lonE6;
	private MapController mapControl;
	private GeoPoint gp;
	private MapView mapView;

	private Button overlayButton, accessButton;
	private Button routeButton;
	private List<Overlay> mapOverlays;
	private Drawable drawable1, drawable2;
	private MyItemizedOverlay itemizedOverlay1, itemizedOverlay2;
	private boolean foodIsDisplayed = false;
	private ArrayList<Address> multipleMapAddresses;// = ArrayList<Address>();
	// Define an array containing the food overlay items
	QuestionDataSource datasource;
	Question q;
	int pos;
	String nick;
	String response;
	private final OverlayItem[] foodItem = {
			new OverlayItem(new GeoPoint(35952967, -83929158), "Food Title 1",
					"Food snippet 1"),
			new OverlayItem(new GeoPoint(35953000, -83928000), "Food Title 2",
					"Food snippet 2"),
			new OverlayItem(new GeoPoint(35955000, -83929158), "Food Title 3",
					"Food snippet 3") };

	// Define an array containing the access overlay items

	private OverlayItem[] accessItem;// = {
	// new OverlayItem(new GeoPoint(35953700, -83926158),
	// "Access Title 1", "Access snippet 1"),
	// new OverlayItem(new GeoPoint(35954000, -83928200),
	// "Access Title 2", "Access snippet 2"),
	// new OverlayItem(new GeoPoint(35955000, -83927558),
	// "Access Title 3", "Access snippet 3"),
	// new OverlayItem(new GeoPoint(35954000, -83927158),
	// "Access Title 4", "Access snippet 4") };

	String TAG = "GPStest";
	// Set up the array of GeoPoints defining the route
	int numberRoutePoints;
	GeoPoint routePoints[]; // Dimension will be set in class RouteLoader below
	int routeGrade[]; // Index for slope of route from point i to point i+1
	RouteSegmentOverlay route; // This will hold the route segments
	boolean routeIsDisplayed = false;
	private ArrayList<Long> values;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); // Suppress title bar for
														// more space
		setContentView(R.layout.showthemap);
		multipleMapAddresses = new ArrayList<Address>();
		datasource = new QuestionDataSource(this);
		datasource.open();
		values = datasource.getAllIds();

		// Add map controller with zoom controls
		mapView = (MapView) findViewById(R.id.mv);
		mapView.setSatellite(false);
		mapView.setTraffic(false);
		mapView.setBuiltInZoomControls(true); // Set android:clickable=true in
												// main.xml
		int maxZoom = mapView.getMaxZoomLevel();
		int initZoom = maxZoom - 2;
		mapControl = mapView.getController();
		mapControl.setZoom(initZoom);
		// Convert lat/long in degrees into integers in microdegrees
		latE6 = (int) (lat * 1e6);
		lonE6 = (int) (lon * 1e6);
		gp = new GeoPoint(latE6, lonE6);
		mapControl.animateTo(gp);
		mapOverlays = mapView.getOverlays();
		drawable1 = this.getResources().getDrawable(R.drawable.chest);
		itemizedOverlay1 = new MyItemizedOverlay(drawable1,
				getApplicationContext());
		itemizedOverlay1.addOverlay(new OverlayItem(gp, "You'r Possition is ",
				"Click & Save"));
		mapOverlays.add(itemizedOverlay1);

		for (int ptr = 0; ptr < values.size(); ptr++) {
			Question q = datasource.getQsAtId(ptr);
			Log.d("map", q.getQuestion());//

			Double l;
			Double n;
			int lc, nc;
			l = Double.valueOf(q.getLatitude());
			n = Double.valueOf(q.getLongitute());
			lc = (int) (l * 1e6);
			nc = (int) (n * 1e6);

			itemizedOverlay1.addOverlay(new OverlayItem(new GeoPoint(lc, nc), q
					.getQuestion(), "THis is Sparta"));
			mapControl.animateTo(new GeoPoint(lc, nc));

		}

		mapView.postInvalidate();
		// Button to control food overlay
		overlayButton = (Button) findViewById(R.id.doOverlay);
		overlayButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// setOverlay1();
			}
		});

		// Button to control access overlay
		accessButton = (Button) findViewById(R.id.doAccess);
		accessButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// setOverlay2();
			}
		});

		// Button to control route overlay
		routeButton = (Button) findViewById(R.id.doRoute);
		routeButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!routeIsDisplayed) {
					routeIsDisplayed = true;
					loadRouteData();
				} else {
					if (route != null)
						route.setRouteView(false);
					route = null; // To prevent multiple route instances if key
									// toggled rapidly (see line 235)
					routeIsDisplayed = false;
					mapView.postInvalidate();
				}
			}
		});

	}

	/*
	 * Methods to set map overlays. In this case we will place a small overlay
	 * image at a specified location. Place the marker image as a png file in
	 * res > drawable-* . For example, the reference to
	 * R.drawable.knifefork_small below is to an image file called
	 * knifefork_small.png in the project folder res > drawable-hdpi. Can only
	 * use lower case letters a-z, numbers 0-9, ., and _ in these image file
	 * names. In this example the single overlay item is specified by drawable
	 * and the location of the overlay item is specified by overlayitem.
	 */

	// Display food location overlay. If not already displayed, clicking button
	// displays all
	// food overlays. If already displayed successive clicks remove items one by
	// one. This
	// illustrates ability to change individual overlay items dynamically at
	// runtime.

	public void setOverlay1() {
		int foodLength = foodItem.length;
		// Create itemizedOverlay2 if it doesn't exist and display all three
		// items
		if (!foodIsDisplayed) {
			mapOverlays = mapView.getOverlays();
			drawable1 = this.getResources().getDrawable(
					R.drawable.knifefork_small);
			itemizedOverlay1 = new MyItemizedOverlay(drawable1,
					getApplicationContext());
			// Display all three items at once

			for (int i = 0; i < foodLength; i++) {

				itemizedOverlay1.addOverlay(foodItem[i]);
				// itemizedOverlay1.addOverlay(overlay)
			}
			mapOverlays.add(itemizedOverlay1);
			foodIsDisplayed = !foodIsDisplayed;

			// Remove each item successively with button clicks
		} else {
			itemizedOverlay1.removeItem(itemizedOverlay1.size() - 1);
			if ((itemizedOverlay1.size() < 1))
				foodIsDisplayed = false;
		}
		// Added symbols will be displayed when map is redrawn so force redraw
		// now
		mapView.postInvalidate();
	}

	// Display accessibility overlay. If not already displayed, successive
	// button clicks display each of
	// the three icons successively, then the next removes them all. This
	// illustrates the ability to
	// change individual overlay items dynamically at runtime.

	public void setOverlay2() {
		int accessLength = accessItem.length;
		// Create itemizedOverlay2 if it doesn't exist
		if (itemizedOverlay2 == null) {
			mapOverlays = mapView.getOverlays();
			drawable2 = this.getResources().getDrawable(
					R.drawable.accessibility);
			itemizedOverlay2 = new MyItemizedOverlay(drawable2,
					getApplicationContext());
		}
		// Add items with each click
		if (itemizedOverlay2.size() < accessLength) {
			itemizedOverlay2.addOverlay(accessItem[itemizedOverlay2.size()]);
			mapOverlays.add(itemizedOverlay2);
			// Remove all items with one click
		} else {
			for (int i = 0; i < accessLength; i++) {
				itemizedOverlay2.removeItem(accessLength - 1 - i);
			}
		}
		// Added symbols will be displayed when map is redrawn so force redraw
		// now
		mapView.postInvalidate();
	}

	// Method to insert latitude and longitude in degrees
	public static void putLatLong(double latitude, double longitude) {
		lat = latitude;
		lon = longitude;
	}

	// This sets the s key on the phone to toggle between satellite and map view
	// and the t key to toggle between traffic and no traffic view (traffic view
	// relevant only in urban areas where it is reported).

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent e) {
		if (keyCode == KeyEvent.KEYCODE_S) {
			mapView.setSatellite(!mapView.isSatellite());
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_T) {
			mapView.setTraffic(!mapView.isTraffic());
			mapControl.animateTo(gp); // To ensure change displays immediately
		}
		return (super.onKeyDown(keyCode, e));
	}

	// Required method since class extends MapActivity
	@Override
	protected boolean isRouteDisplayed() {
		return false; // Don't display a route
	}

	// Method to read route data from server as XML

	public void loadRouteData() {
		try {
			String url = "http://eagle.phys.utk.edu/reubendb/UTRoute.php";
			String data = "?lat1=35952967&lon1=-83929158&lat2=35956567&lon2=-83925450";
			// RouteLoader RL = new RouteLoader();
			// RL.execute(new URL(url+data));
			new RouteLoader().execute(new URL(url + data));
		} catch (MalformedURLException e) {
			Log.i("NETWORK", "Failed to generate valid URL");
		}
	}

	// Overlay a route. This method is only executed after loadRouteData()
	// completes
	// on background thread.

	public void overlayRoute() {
		if (route != null)
			return; // To prevent multiple route instances if key toggled
					// rapidly (see also line 116)
		// Set up the overlay controller
		route = new RouteSegmentOverlay(routePoints, routeGrade); // My class
																	// defining
																	// route
																	// overlay
		mapOverlays = mapView.getOverlays();
		mapOverlays.add(route);

		// Added symbols will be displayed when map is redrawn so force redraw
		// now
		mapView.postInvalidate();
	}

	/*
	 * Class to implement single task on background thread without having to
	 * manage the threads directly. Launch with
	 * "new RouteLoader().execute(new URL(urlString)". Must be launched from the
	 * UI thread and may only be invoked once. Adapted from example in Ch. 10 of
	 * Android Wireless Application Development. Use this to do data load from
	 * network on separate thread from main user interface to prevent locking
	 * main UI if there is network delay.
	 */

	private class RouteLoader extends AsyncTask<URL, String, String> {

		@Override
		protected String doInBackground(URL... params) {
			// This pattern takes more than one param but we'll just use the
			// first
			try {
				URL text = params[0];

				XmlPullParserFactory parserCreator;

				parserCreator = XmlPullParserFactory.newInstance();

				XmlPullParser parser = parserCreator.newPullParser();

				parser.setInput(text.openStream(), null);

				publishProgress("Parsing XML...");

				int parserEvent = parser.getEventType();
				int pointCounter = -1;
				int wptCounter = -1;
				int totalWaypoints = -1;
				int lat = -1;
				int lon = -1;
				String wptDescription = "";
				int grade = -1;

				// Parse the XML returned on the network
				while (parserEvent != XmlPullParser.END_DOCUMENT) {
					switch (parserEvent) {
					case XmlPullParser.START_TAG:
						String tag = parser.getName();
						if (tag.compareTo("number") == 0) {
							numberRoutePoints = Integer.parseInt(parser
									.getAttributeValue(null, "numpoints"));
							totalWaypoints = Integer.parseInt(parser
									.getAttributeValue(null, "numwpts"));
							routePoints = new GeoPoint[numberRoutePoints];
							routeGrade = new int[numberRoutePoints];
							Log.i(TAG, "   Total points = " + numberRoutePoints
									+ " Total waypoints = " + totalWaypoints);
						}
						if (tag.compareTo("trkpt") == 0) {
							pointCounter++;
							lat = Integer.parseInt(parser.getAttributeValue(
									null, "lat"));
							lon = Integer.parseInt(parser.getAttributeValue(
									null, "lon"));
							grade = Integer.parseInt(parser.getAttributeValue(
									null, "grade"));
							routePoints[pointCounter] = new GeoPoint(lat, lon);
							routeGrade[pointCounter] = grade;
							Log.i(TAG, "   trackpoint=" + pointCounter
									+ " latitude=" + lat + " longitude=" + lon
									+ " grade=" + grade);
						} else if (tag.compareTo("wpt") == 0) {
							wptCounter++;
							lat = Integer.parseInt(parser.getAttributeValue(
									null, "lat"));
							lon = Integer.parseInt(parser.getAttributeValue(
									null, "lon"));
							wptDescription = parser.getAttributeValue(null,
									"description");
							Log.i(TAG, "   waypoint=" + wptCounter
									+ " latitude=" + lat + " longitude=" + lon
									+ " " + wptDescription);
						}
						break;
					}

					parserEvent = parser.next();
				}

			} catch (Exception e) {
				Log.i("RouteLoader", "Failed in parsing XML", e);
				return "Finished with failure.";
			}

			return "Done...";
		}

		@Override
		protected void onCancelled() {
			Log.i("RouteLoader", "GetRoute task Cancelled");
		}

		// Now that route data are loaded, execute the method to overlay the
		// route on the map
		@Override
		protected void onPostExecute(String result) {
			Log.i(TAG, "Route data transfer complete");
			overlayRoute();
		}

		@Override
		protected void onPreExecute() {
			Log.i(TAG, "Ready to load URL");
		}

		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
		}

	}

}
