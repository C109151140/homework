package com.example.getlocation;


import android.Manifest;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.view.View;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.libraries.places.api.Places;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.Timer;


public class MainActivity extends Activity implements View.OnClickListener {


    private TextView textView;
    private TextView textView2;
    private TextView textView3;
    private Button button;



    double lat = 0.0;
    double lng = 0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyDhtAWJQGbtQDq1X_Mlx0aKtus5kwnuiqY");
        }
        textView = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView3 = (TextView) findViewById(R.id.textView3);
        findViewById(R.id.button).setOnClickListener(this);

        Button button_start = (Button) findViewById(R.id.button2);
        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent main2ActivityIntent = new Intent(MainActivity.this, MapsActivityCurrentPlace.class);
                startActivity(main2ActivityIntent);
            }
        });

        TimerTask task = new TimerTask() {
            public void run() {
                lat += 0.001;
                lng += 0.001;
            }
        };


        Timer timer = new Timer();
        timer.scheduleAtFixedRate(task, 0, 1000);
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                requestUserLocation();
                break;
        }
    }
    public void requestUserLocation() {
        final LocationManager mLocation = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //判斷當前是否已經獲得了定位權限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(MainActivity.this,"權限沒開",Toast.LENGTH_SHORT).show();
            requestCameraPermission();
            return;
        }

        mLocation.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                final StringBuffer sb = new StringBuffer();
                sb.append("Now location is : \n")
                        .append("lat : " + location.getLatitude()).append("\n")
                        .append("lng : " + location.getLongitude());
                textView.setText("lat : " + location.getLatitude());
                textView2.setText("lng : " + location.getLongitude());
                textView3.setText("時間 : "+Manhattan(location));//時間
                Toast.makeText(MainActivity.this,sb.toString(),Toast.LENGTH_SHORT).show();
            }

            private String Manhattan(final Location location) {
                double x0=location.getLatitude();
                double y0=location.getLatitude();
                double distance = Math.abs(x0-x0) + Math.abs(y0-y0);
                int maxMinute,smallestMinute;
                distance=distance/0.015;
                maxMinute=(int)distance*3;
                smallestMinute=(int)distance*4;
                String x=maxMinute+"分到"+smallestMinute+"分";
                return x;
            }

            @Override
            public void onStatusChanged(final String s, final int i, final Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(final String s) {
            }

            public void onProviderDisabled(final String s) {
            }
        },  MainActivity.this.getMainLooper());
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
