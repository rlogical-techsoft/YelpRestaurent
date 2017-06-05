package com.yelprestaurant;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.Business;
import com.yelp.fusion.client.models.SearchResponse;
import com.yelprestaurant.utils.AppUtils;
import com.yelprestaurant.utils.CustomProgressDialog;
import com.yelprestaurant.utils.TypefaceUtils;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

public class MainActivity extends AppCompatActivity {

    // Term Tag to retrieve Near By Restaurants only from Yelp
    private final String SEARCH_TERM = "restaurants";

    private String LATITUDE, LONGITUDE;

    private GoogleMap mMap;

    private Marker curLocMarker;

    private Context context;

    private int padding = 20; // offset from edges of the map in pixels
    private LatLngBounds.Builder builder;

    private Typeface assistantBold;
    private Typeface assistantSemibold;
    private Typeface assistantRegular;

    private LatLng curLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Near By Restaurant");

        context = this;

        // Typeface Initialization
        assistantBold = TypefaceUtils.getAssistantBoldFont(this);
        assistantSemibold = TypefaceUtils.getAssistantSemiBoldFont(this);
        assistantRegular = TypefaceUtils.getAssistantRegularFont(this);

        builder = new LatLngBounds.Builder();

        /**
         * Self permission check. If user allowed to access location permission, then app will search for the device current location.
         */

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            startService(new Intent(context, ServiceGPSTracker.class));


        // Map Initialization
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                initMap();
            }
        });

        /**
         * Calling Yelp Api to get Near By Restaurants
         */
        if (AppUtils.isInternetAvailable(this))
            new AsyncSearchRestaurants().execute();
        else
            AppUtils.showMessage(this, getString(R.string.txt_msg_no_internet_available));
    }

    @Override
    public void onResume() {
        super.onResume();
        // Register mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(context).registerReceiver(mMessageReceiver, new IntentFilter("update-location"));
    }

    // handler for received Intents for the "update-location" event. It will update users current location whenever location change detected in device gps.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            LATITUDE = intent.getStringExtra("latitude");
            LONGITUDE = intent.getStringExtra("longitude");

            // For Testing Purpose Only
            //LATITUDE = "37.775647";
            //LONGITUDE = "-122.4338062";

            // Add current device location on map marker with pink pin
            curLocation = new LatLng(Double.parseDouble(LATITUDE), Double.parseDouble(LONGITUDE));

            updateCurrentLocation();
        }
    };

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    /**
     * Common properties used for the Map initialization
     */

    public void initMap() {
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.setBuildingsEnabled(false);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setIndoorEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);

        // Infowindow click
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intFullDetail = new Intent(context, RestaurantMoreInfo.class);
                intFullDetail.putExtra("restaurant_detail", marker.getSnippet());
                startActivity(intFullDetail);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        // Display Infowindow when click on restaurant map marker
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(final Marker marker) {

                //Infowindow Custom View Binding

                if (marker.getSnippet() == null)
                    return null;

                final Business selMarBusiness = new Gson().fromJson(marker.getSnippet(), Business.class);

                View vMarker = getLayoutInflater().inflate(R.layout.item_marker_info_window, null, false);

                int width = (int) (AppUtils.getDeviceWidth(context) / 1.2);

                LinearLayout layout_info_top = (LinearLayout) vMarker.findViewById(R.id.layout_info_top);
                layout_info_top.setLayoutParams(new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT));

                TextView txt_restaurant_name = (TextView) vMarker.findViewById(R.id.txt_restaurant_name);
                txt_restaurant_name.setText(selMarBusiness.getName());
                txt_restaurant_name.setTypeface(assistantBold);

                TextView txt_restaurant_rating = (TextView) vMarker.findViewById(R.id.txt_restaurant_rating);
                txt_restaurant_rating.setText("Rating: " + String.valueOf(selMarBusiness.getRating()));
                txt_restaurant_name.setTypeface(assistantSemibold);

                String addresss = "";

                for (String addr : selMarBusiness.getLocation().getDisplayAddress())
                    addresss += addr;

                TextView txt_restaurant_address = (TextView) vMarker.findViewById(R.id.txt_restaurant_address);
                txt_restaurant_address.setText(addresss);
                txt_restaurant_address.setTypeface(assistantRegular);

                return vMarker;
            }
        });
    }

    /**
     * Add current device location on map marker with pink pin
     */

    public void updateCurrentLocation() {

        if (curLocMarker == null) {
            curLocMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_current))
                    .position(curLocation).title("You are here!!"));
        } else
            curLocMarker.setPosition(curLocation);
    }

    /**
     * Get Near By restaurants from Yelp
     */

    private class AsyncSearchRestaurants extends AsyncTask<String, Void, SearchResponse> {

        private CustomProgressDialog dialog;

        private AsyncSearchRestaurants() {
            dialog = new CustomProgressDialog(MainActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        protected SearchResponse doInBackground(String... param) {

            try {

                YelpFusionApiFactory apiFactory = new YelpFusionApiFactory();

                YelpFusionApi yelpFusionApi = apiFactory.createAPI(getString(R.string.txt_client_id), getString(R.string.txt_client_secret));

                Map<String, String> params = new HashMap<>();
                params.put("term", SEARCH_TERM);
                params.put("latitude", LATITUDE);
                params.put("longitude", LONGITUDE);

                Call<SearchResponse> call = yelpFusionApi.getBusinessSearch(params);

                return call.execute().body();

            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(SearchResponse result) {
            super.onPostExecute(result);

            try {

                updateCurrentLocation();

                if (result != null) {

                    if (result.getBusinesses().size() > 0) {

                        /**
                         * Block will execute if any near by restaurant found.
                         */

                        for (Business business : result.getBusinesses()) {

                            LatLng latlng = new LatLng(business.getCoordinates().getLatitude(), business.getCoordinates().getLongitude());

                            mMap.addMarker(
                                    new MarkerOptions().position(latlng).snippet(new Gson().toJson(business))
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_restaurant))
                                            .title(business.getName()));

                            builder.include(latlng);
                        }

                        LatLngBounds bounds = builder.build();
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                        mMap.animateCamera(cu);

                    } else {

                        /**
                         * Block will execute if there are not any near by restaurant found.
                         */

                        AppUtils.showMessage(MainActivity.this, "There are no any near by restaurant found.");

                        builder.include(curLocation);

                        LatLngBounds bounds = builder.build();
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                        mMap.animateCamera(cu);
                    }

                } else {

                    /**
                     * Block will execute if there are not any near by restaurant found or any issue with location tracking
                     */

                    AppUtils.showMessage(MainActivity.this, "There are no any near by restaurant found.");

                    builder.include(curLocation);

                    LatLngBounds bounds = builder.build();
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    mMap.animateCamera(cu);

                }
            } catch (Exception e) {
                AppUtils.showMessage(MainActivity.this, getString(R.string.txt_msg_please_try_again));
                e.printStackTrace();
            }

            dialog.dismiss();
        }
    }
}