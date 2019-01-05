package com.chs.pedometer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.model.LatLng;
import java.math.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class StartStopActivity extends AppCompatActivity implements SensorEventListener, StepListener, LocationListener{

    Button startButton;
    Button stopButton;
    TableLayout measuredDataTable;
    TextView dateTextView;
    TextView seeOnMapTextView;
    TextView countedSteps;
    SensorManager sensorManager;
    boolean running = false;
    TextView measuredTime;
    TextView measuredDistance;
    TextView measuredSpeed;

    private StepDetector simpleStepDetector;
    private Sensor accelerator;
    private int numSteps;

    private LocationManager locationManager;

    Stopwatch timer = new Stopwatch();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_stop);
        findViewsById();
        setDate(dateTextView);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerator = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);
    }

    public void setDate (TextView view){

        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM, yyyy");
        String date = formatter.format(today);
        view.setText(date);
    }

    private void findViewsById() {
        startButton = (Button) findViewById(R.id.startButton);
        stopButton = (Button) findViewById(R.id.stopButton);
        measuredDataTable = (TableLayout) findViewById(R.id.measuredDataTable);
        dateTextView = findViewById(R.id.date);
        seeOnMapTextView = (TextView) findViewById(R.id.seeOnMap);
        countedSteps = (TextView)findViewById(R.id.countedSteps);
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        measuredTime = (TextView) findViewById(R.id.measuredTime);
        measuredDistance = (TextView) findViewById(R.id.measuredDistance);
        measuredSpeed = (TextView) findViewById(R.id.measuredSpeed);

    }

    @Override
    protected void onResume() {
        super.onResume();
//        running = true;
//        Sensor countStepsSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if(accelerator != null) {
            //sensorManager.registerListener(this, accelerator, sensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Sensor not found!", Toast.LENGTH_SHORT).show();
        }
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        //        if you unregister the hardware will stop detecting steps
//        //        sensorManager.unregisterListener(this);
//    }

    public void onClickStartButton(View v){
        startButton.setVisibility(View.INVISIBLE);
        stopButton.setVisibility(View.VISIBLE);
        seeOnMapTextView.setVisibility(View.VISIBLE);

        numSteps = 0;
        sensorManager.registerListener(StartStopActivity.this, accelerator, SensorManager.SENSOR_DELAY_FASTEST);
        timer.start();
        measuredSpeed.setText("");
        measuredTime.setText("");
        measuredDistance.setText("");
        measuredDataTable.setVisibility(View.INVISIBLE);

        requestLocationUpdateEverySecond();
    }

    private void requestLocationUpdateEverySecond() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
            this.onLocationChanged(null);
        } catch (SecurityException se) {
            Toast.makeText(this, "Permission Access Location Denied!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickStopButton(View v){
        timer.stop();
        startButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.INVISIBLE);
        measuredDataTable.setVisibility(View.VISIBLE);
        measuredDistance.append(distanceToString(this.getDistance()));
        measuredTime.append(timer.ToString());
        measuredSpeed.append(getSpeedToString(getSpeed(this.getDistance(),(int)timer.getElapsedTime())));

        sensorManager.unregisterListener(StartStopActivity.this);

        locationManager.removeUpdates(this);
    }

    public void onClickSeeOnMap(View v){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

//    @Override
//    public void onSensorChanged(SensorEvent sensorEvent) {
//        if(running){
//            countedSteps.setText(String.valueOf(sensorEvent.values[0]));
//        }
//    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel( event.timestamp, event);
        }
    }

    @Override
    public void step(long timeNs) {
        numSteps++;
        countedSteps.setText(String.valueOf(numSteps));
    }

    @Override
    public void onLocationChanged(Location location) {
        //save location here
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public Double getSpeed(int d, int t)
    {
        Double v = (double)d/t;
        v = v * 100.0;
        return v.intValue()/100.0;
    }

    public String getSpeedToString(Double v)
    {
        return v + " m/s";
    }

    public History getHistoryJSON() {
        JSONResourceReader reader = new JSONResourceReader(getResources(), R.raw.history);
        History jsonObj = reader.constructUsingGson(History.class);

        return jsonObj;
    }

    public int getDistance()
    {
        int R=6371,dist;
        Double dLat,dLon,lat1,lat2,long1,long2,a,c,d=0.0;
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        String currentDayString = "" + dateFormat.format(date);

        History historyJSON = getHistoryJSON();
        Route routes[] = historyJSON.getRoutes();

        for(int rout = 0; rout < routes.length; rout++) {
            String day = routes[rout].day;
            if(day.equals(currentDayString)){
                com.chs.pedometer.Location locations[] = routes[rout].getLocations();
                if(locations.length > 0) {

                    for(int loc = 0; loc < locations.length-1; loc++) {
                        lat1 = locations[loc].getLatitude();
                        long1 = locations[loc].getLongitude();
                        lat2 = locations[loc+1].getLatitude();
                        long2 = locations[loc+1].getLongitude();
                        dLat = deg2rad(lat2-lat1);
                        dLon = deg2rad(long2-long1);
                        a =Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.sin(dLon/2) * Math.sin(dLon/2);
                        c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
                        d+=R * c;
                    }
                    d=d*1000;
                    dist = d.intValue();
                    return dist;
                }
            }
        }
        return 0;
    }

    public String distanceToString(int dist)
    {
        return dist/1000 + " km  " + dist%1000 + " m";
    }

    public Double deg2rad(Double deg) {
        return deg * (Math.PI/180);
    }
}
