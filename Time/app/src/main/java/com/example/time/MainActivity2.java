package com.example.time;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.Manifest;
import androidx.core.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.location.Location;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.app.Activity;
import com.google.android.libraries.places.api.Places;
import android.os.Handler;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.Timer;
import com.google.android.gms.maps.model.LatLng;
import androidx.core.content.ContextCompat;

public class MainActivity2 extends AppCompatActivity {
    private TextView textView4;
    public static final int PERMISSIONS_REQUEST_LOCATION = 100;
    double lat = 0.0;
    double lng = 0.0;
    private LatLng myLatLng;
    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_LOCATION);
        }

    }
    //取得車手的經緯度用這個↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
    private LatLng getDriverLatLng() {
        requestUserLocation();
        return myLatLng;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        requestUserLocation();
        textView4 = (TextView) findViewById(R.id.textView4);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyDhtAWJQGbtQDq1X_Mlx0aKtus5kwnuiqY");
        }

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                requestUserLocation();

                Intent intent = new Intent();
                intent.putExtra("latitude", lat);
                intent.putExtra("longitude", lng);
                setResult(Activity.RESULT_OK, intent);
                textView4.setText(lat+"有"+lng);

                finish();
                handler.postDelayed(this, 5000);
            }
        };

        handler.postDelayed(runnable, 3000);



        TimerTask task = new TimerTask() {
            public void run() {
            }
        };


        Timer timer = new Timer();
        timer.scheduleAtFixedRate(task, 0, 1000);
    }




    public void requestUserLocation() {
        final LocationManager mLocation = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //判斷當前是否已經獲得了定位權限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(MainActivity2.this,"權限沒開",Toast.LENGTH_SHORT).show();
            requestCameraPermission();
            return;
        }

        mLocation.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                final StringBuffer sb = new StringBuffer();
                lat = location.getLatitude();
                lng = location.getLongitude();
            }




            @Override
            public void onStatusChanged(final String s, final int i, final Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(final String s) {
            }

            public void onProviderDisabled(final String s) {
            }
        },  MainActivity2.this.getMainLooper());
    }
    private void requestCameraPermission(){
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M)
            return;

        final List<String> permissionsList = new ArrayList<>();
        if(this.checkSelfPermission(Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED)
            permissionsList.add(Manifest.permission.CAMERA);
        if(this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
            permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionsList.size()<1)
            return;
        if(this.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA))
            this.requestPermissions(permissionsList.toArray(new String[permissionsList.size()]) , 0x00);
        else
            goToAppSetting();
    }

    private void goToAppSetting(){
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", this.getPackageName(), null));
        startActivityForResult(intent , 0x00);
    }

}