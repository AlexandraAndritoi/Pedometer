package com.chs.pedometer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationListener locationListener;
    private LocationManager locationManager;
    private final long MIN_TIME = 1000; // 1 sec
    private final long MIN_DIST = 5;
    private String jsonString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        String currentDayString = "" + dateFormat.format(date);

        History historyJSON = getHistoryJSON();
        Route routes[] = historyJSON.getRoutes();

        for(int rout = 0; rout < routes.length; rout++) {
            String day = routes[rout].day;
            if(day.equals(currentDayString)){
                Location locations[] = routes[rout].getLocations();
                if(locations.length > 0) {
                    ArrayList<LatLng> points = new ArrayList();
                    PolylineOptions polyLineOptions = new PolylineOptions();
                    Polyline line = mMap.addPolyline(polyLineOptions.width(3).color(Color.RED));
                    for(int loc = 0; loc < locations.length; loc++) {
                        Double latitude = locations[loc].getLatitude();
                        Double longitude = locations[loc].getLongitude();
                        LatLng point = new LatLng(latitude,longitude);
                        points.add(point);
                        String title = "Point" + loc;
                        mMap.addMarker(new MarkerOptions().position(point).title(title));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
                    }
                    line.setPoints(points);
                    mMap.animateCamera( CameraUpdateFactory.zoomTo( 17.0f ) );
                }
            }
        }
    }

    public History getHistoryJSON() {
        JSONResourceReader reader = new JSONResourceReader(getResources(), R.raw.history);
        History jsonObj = reader.constructUsingGson(History.class);

        return jsonObj;
    }
}
