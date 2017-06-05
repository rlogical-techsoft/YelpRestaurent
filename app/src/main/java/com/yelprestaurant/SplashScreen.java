package com.yelprestaurant;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.TextView;

import com.yelprestaurant.utils.AppUtils;
import com.yelprestaurant.utils.TypefaceUtils;

import java.util.ArrayList;
import java.util.List;

public class SplashScreen extends AppCompatActivity {

    private final int PERMISSIONS = 2403;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_screen);

        context = this;

        TextView txt_find_restaurant = (TextView) findViewById(R.id.txt_find_restaurant);
        txt_find_restaurant.setTypeface(TypefaceUtils.getExo2RegularFont(this));

        /**
         *  Checking for the GPS enabled or not. If device has GPS disabled then it will just prompt user about that.
         */

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!isGPSEnabled)
            AppUtils.showMessage(this, "Seems your GPS is not enabled. Please enable it.");


        /**
         *  Runtime permission check. App will prompt user to enable location permissions tp get device current location.
         */
        List<String> permissions = new ArrayList<>();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        if (permissions.size() > 0)
            ActivityCompat.requestPermissions(this, permissions.toArray(new String[permissions.size()]), PERMISSIONS);
        else {

            // If permission is already granted then app will redirect user to Main screen directly.

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(context, MainActivity.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }
            }, 5000);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == PERMISSIONS) {

            // If permission is already granted then app will redirect user to Main screen directly.

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(context, MainActivity.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }
            }, 5000);
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}