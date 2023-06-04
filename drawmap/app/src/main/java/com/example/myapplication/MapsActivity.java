package com.example.myapplication;

import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import com.example.myapplication.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import android.graphics.Color;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.location.Location;
import android.widget.Toast;
import com.google.android.gms.maps.model.PolylineOptions;
import com.android.volley.toolbox.JsonObjectRequest;
import java.util.ArrayList;
import java.util.List;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;
import java.util.HashMap;
import android.util.Log;
import com.android.volley.RequestQueue;



public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    public static final int PERMISSIONS_REQUEST_LOCATION = 100;
    private Location myLocation;
    private Handler mHandler;
    private Runnable mRunnable;
    private LatLng dest;

    //測試用
    public LatLng firstDest() {
        LatLng newDest = new LatLng(37.423363, -122.142234);
        return newDest;
    }
    //實際上是用這個
    public LatLng firstDest(LatLng newDest) {
        return newDest;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //firstDest(括號內放入LatLng變數)
        //↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
        setDest(firstDest());

        requestUserLocation();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    public void setDest(LatLng newDest) {
        dest = newDest;
    }

    public LatLng getDest() {
        return dest;
    }





    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }





    public void drawMap(LatLng myLatLng,LatLng dest) {


        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + myLatLng.latitude + "," + myLatLng.longitude + "&destination=" + dest.latitude + "," + dest.longitude + "&key=AIzaSyDhtAWJQGbtQDq1X_Mlx0aKtus5kwnuiqY";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                DirectionsJSONParser parser = new DirectionsJSONParser();
                List<List<HashMap<String, String>>> routes = parser.parse(response);

                drawPath(routes,dest);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", error.toString());
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);

    }
    private void drawPath(List<List<HashMap<String, String>>> routes,LatLng dest) {
        if (routes == null || routes.size() == 0) {
            return;
        }


        ArrayList<LatLng> points = new ArrayList<>();
        PolylineOptions polylineOptions = new PolylineOptions();


        for (int i = 0; i < routes.size(); i++) {

            List<HashMap<String, String>> path = routes.get(i);
            if (path == null) {
                continue;
            }

            for (int j = 2; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);
                if (point != null) {

                    String l1 = String.valueOf(dest.latitude);
                    String l2 = String.valueOf(dest.longitude);
                    double lat = Double.parseDouble(point.getOrDefault("lat", l1));
                    double lng = Double.parseDouble(point.getOrDefault("lng", l2));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                    Log.d("LatLng", "lat: " + lat + ", lng: " + lng);
                }
            }
        }

        polylineOptions.addAll(points);
        polylineOptions.width(10);
        polylineOptions.color(Color.BLUE);
        polylineOptions.geodesic(true);


        mMap.addPolyline(polylineOptions);
    }







    private void onLocationPermissionGranted() {


    }
    public void requestUserLocation() {
        final LocationManager mLocation = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MapsActivity.this, "權限沒開", Toast.LENGTH_SHORT).show();
            return;


        }else {

            onLocationPermissionGranted();
        }


        mLocation.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                final StringBuffer sb = new StringBuffer();
                LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());


                drawMap(myLatLng,getDest());
                sb.append("Now location is : \n")
                        .append("lat : " + location.getLatitude()).append("\n")
                        .append("lng : " + location.getLongitude());
                if (myLocation == null) {
                    myLocation = location;
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
                }
                myLocation = location;
                if (mHandler == null) {
                    mHandler = new Handler();
                    mRunnable = new Runnable() {
                        @Override
                        public void run() {
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 15));
                            mHandler.postDelayed(this, 5000);
                        }
                    };
                    mHandler.postDelayed(mRunnable, 5000);
                }
                updateMapWithLocation(myLatLng);
            }




            @Override
            public void onStatusChanged(final String s, final int i, final Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(final String s) {
            }

            public void onProviderDisabled(final String s) {
            }
        }, MapsActivity.this.getMainLooper());
    }
    private void updateMapWithLocation(LatLng myLatLng) {
        if (myLatLng != null) {
            drawMap(myLatLng,getDest());
        }
    }


}



