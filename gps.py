private final LocationListener listener = new LocationListener() {

    @Override
    public void onLocationChanged(Location location) {
        // A new location update is received.  Do something useful with it.  In this case,
        // we're sending the update to a handler which then updates the UI with the new
        // location.
        Message.obtain(mHandler,
                UPDATE_LATLNG,
                location.getLatitude() + ", " +
                location.getLongitude()).sendToTarget();

            ...
        }
    ...
};

mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
        10000,          // 10-second interval.
        10,             // 10 meters.
        listener);
