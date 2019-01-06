package com.chs.pedometer;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
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

    private StepDetector simpleStepDetector;
    private Sensor accelerator;
    private int numSteps;

    private LocationManager locationManager;
    private ArrayList<Point> newLocations;

    private File file;
    private String fileName = "history7.json";


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

        String filePath = getBaseContext().getFilesDir().getAbsolutePath() + "/" + fileName;
        if(!isFilePresent(fileName)){
            file = new File(filePath);
            file.setWritable(true);
            String fileContents = "{\"routes\":[]}";
            FileOutputStream outputStream;
            try {
                outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
                outputStream.write(fileContents.getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(accelerator != null) {
            sensorManager.registerListener(this, accelerator, sensorManager.SENSOR_DELAY_UI);
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
        newLocations = new ArrayList<>();

        requestLocationUpdateEverySecond();
    }

    private void requestLocationUpdateEverySecond() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, this);

        } catch (SecurityException se) {
            Toast.makeText(this, "Permission Access Location Denied!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickStopButton(View v){
        startButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.INVISIBLE);
        measuredDataTable.setVisibility(View.VISIBLE);

        sensorManager.unregisterListener(StartStopActivity.this);

        locationManager.removeUpdates(this);

        writeInHistory();
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
        System.out.println("Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
        Double latitude = location.getLatitude();
        Double longitude = location.getLongitude();
        Point currentLocation = new Point(latitude,longitude);
        newLocations.add(currentLocation);
    }

    public void writeInHistory () {
        Boolean currentDayFound = false;
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        String currentDayString = "" + dateFormat.format(date);

        String filePath = getBaseContext().getFilesDir().getAbsolutePath() + "/" + fileName;
        JSONResourceReader reader = new JSONResourceReader(filePath);
        History historyJSON = reader.constructUsingGson(History.class);

        ArrayList<Route> routes = historyJSON.getRoutes();
        Route route = null;
        ArrayList<Point> loc = new ArrayList<Point>();
        loc.ensureCapacity(20);
        int routeIndex = 0;
        for(int rout = 0; rout < routes.size(); rout++) {
            String day = routes.get(rout).getDay();
            if(day.equals(currentDayString)){
                currentDayFound = true;
                route = historyJSON.getRoutes().get(rout);
                loc.addAll(historyJSON.getRoutes().get(rout).getLocations());
                loc.addAll(newLocations);
                routeIndex = rout;
                route.setLocations(loc);
            }
        }
        if(currentDayFound == false) {
            ArrayList<Point> newLocation = new ArrayList<>();
            Double newDistance = 0.; //TODO: compute distance
            Double newSpeed = 0.; //TODO: compute speed
            int newSteps = 0; //TODO: compute steps
            newLocation.addAll(newLocations);
            Route newRoute = new Route(currentDayString, newDistance, newSpeed, newSteps, newLocation);
            routes.add(newRoute);
            historyJSON.setRoutes(routes);
        } else {
            ArrayList<Route> r = new ArrayList<>();
            r.addAll(historyJSON.getRoutes());
            r.remove(routeIndex);
            r.add(route);
            historyJSON.setRoutes(r);
        }

        Gson gsonObj = new Gson();
        String jsonStr = gsonObj.toJson(historyJSON);
        System.out.println("STRING JSON: " + jsonStr);
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(jsonStr.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public boolean isFilePresent(String fileName) {
        String path = getBaseContext().getFilesDir().getAbsolutePath() + "/" + fileName;
        file = new File(path);
        return file.exists();
    }

}
