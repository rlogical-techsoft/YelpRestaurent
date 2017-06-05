package com.yelprestaurant;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;

public class ServiceGPSTracker extends Service implements LocationListener {

    private Context mContext;

    Location curLocation;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 500; // 500 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

    // Handler to call for new location at every 1 Minute
    Handler handler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;

        locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

        handler.post(runnable);
    }

    /**
     * Thread will execute at every 1 minute
     */

    Runnable runnable = new Runnable() {

        @Override
        public void run() {

            getLocation();

            /**
             *  Send Local broadcast receiver to indicate location change
             */

            if (curLocation != null) {
                Intent intent = new Intent("update-location");
                intent.putExtra("latitude", String.valueOf(curLocation.getLatitude()));
                intent.putExtra("longitude", String.valueOf(curLocation.getLongitude()));
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            }

            handler.postDelayed(runnable, MIN_TIME_BW_UPDATES);
        }
    };

    /**
     * This method will check for the current location.
     *
     * @return
     */

    public Location getLocation() {

        try {

            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (isNetworkEnabled) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null)
                        curLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            }

            if (isGPSEnabled) {

                if (curLocation == null) {

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        if (locationManager != null)
                            curLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return curLocation;
    }

    @Override
    public void onLocationChanged(Location location) {

        curLocation = location;

        /**
         *  Send Local broadcast receiver to indicate location change
         */

        Intent intent = new Intent("update-location");
        intent.putExtra("latitude", String.valueOf(curLocation.getLatitude()));
        intent.putExtra("longitude", String.valueOf(curLocation.getLongitude()));
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
}