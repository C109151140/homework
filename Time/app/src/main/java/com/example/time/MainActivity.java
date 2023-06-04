package com.example.time;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.os.Bundle;
import android.Manifest;
import androidx.core.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.location.Location;
import android.widget.Toast;
import android.content.Intent;
import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.widget.TextView;
import com.google.android.libraries.places.api.Places;
import android.os.Handler;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.Timer;
import android.view.View;
import com.google.android.gms.maps.model.LatLng;
import androidx.core.content.ContextCompat;
public class MainActivity extends AppCompatActivity {
    private TextView textView;
    private TextView textView2;
    private TextView textView3;
    private static final int REQUEST_CODE_MAIN_ACTIVITY_2 = 1;


    double lat = 0.0;
    public static final int PERMISSIONS_REQUEST_LOCATION = 100;
    double lng = 0.0;
    private LatLng myLatLng;

    //使用範例
    //LatLng destinationLatLng = new LatLng(37.416815, -122.104516);   設置目的地的經緯度坐標
    //String travelTime = getTime(destinationLatLng);   調用 getTime 方法並傳遞目的地的經緯度參數
    //顯示出x分到y分
    //自己的位置已經有了


    public String getTime(LatLng dest) {
        requestUserLocation();

        double latDiff = Math.abs(myLatLng.latitude - dest.latitude);
        double lngDiff = Math.abs(myLatLng.longitude - dest.longitude);
        double distance = latDiff + lngDiff;
        int maxMinute = (int) (distance * 125);
        int smallestMinute = (int) (distance * 300);
        String x = maxMinute + "分到" + smallestMinute + "分";
        return x;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView3 = (TextView) findViewById(R.id.textView3);
        requestUserLocation();

        Intent intent = new Intent(MainActivity.this, MainActivity2.class);
        startActivityForResult(intent, REQUEST_CODE_MAIN_ACTIVITY_2);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyDhtAWJQGbtQDq1X_Mlx0aKtus5kwnuiqY");
        }

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                requestUserLocation();
                handler.postDelayed(this, 5000); // 設定30秒後再次執行
            }
        };

        handler.postDelayed(runnable, 5000);

        TimerTask task = new TimerTask() {
            public void run() {
                // Empty task.
            }
        };

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(task, 0, 1000);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_MAIN_ACTIVITY_2 && resultCode == RESULT_OK) {
            if (data.hasExtra("latitude") && data.hasExtra("longitude")) {
                lat = data.getDoubleExtra("latitude", 0.0);
                lng = data.getDoubleExtra("longitude", 0.0);
            }
        }
    }


    public void requestUserLocation() {
        final LocationManager mLocation = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(MainActivity.this,"權限沒開",Toast.LENGTH_SHORT).show();
            requestCameraPermission();
            return;
        }

        mLocation.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                //測試用
                LatLng destinationLatLng = new LatLng(37.391958, -122.090048);
                myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                final StringBuffer sb = new StringBuffer();
                textView.setText("lat : " + location.getLatitude()+"  "+lat);
                textView2.setText("lng : " + location.getLongitude()+"  "+lng);
                textView3.setText("時間 : "+getTime(destinationLatLng));


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