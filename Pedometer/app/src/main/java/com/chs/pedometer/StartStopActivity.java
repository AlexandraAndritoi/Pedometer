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
    Button historyButton;
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
    private ArrayList<Point> newLocations;
    private static int interval = 0;

    private File file;
    private String fileName = "history12.json";

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

        String filePath = getBaseContext().getFilesDir().getAbsolutePath() + "/" + fileName;
        if(!isFilePresent(fileName)){
            file = new File(filePath);
            file.setWritable(true);
            //String initialFileContent = "{\"routes\":[{\"day\":\"2019/01/05\",\"distance\":41,\"steps\":67,\"speed\":29,\"locations\":[{\"latitude\":45.988507,\"longitude\":20.660400},{\"latitude\":45.988582,\"longitude\":20.660845},{\"latitude\":45.988742,\"longitude\":20.661736},{\"latitude\":45.988809,\"longitude\":20.662138},{\"latitude\":45.989152,\"longitude\":20.662009}]}";
            String initialFileContent = "{\"routes\":[]}";
            FileOutputStream outputStream;
            try {
                outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
                outputStream.write(initialFileContent.getBytes());
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
        historyButton = (Button) findViewById(R.id.historyButton);
        measuredDataTable = (TableLayout) findViewById(R.id.measuredDataTable);
        dateTextView = findViewById(R.id.date2);
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

        newLocations = new ArrayList<>();

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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, this);

        } catch (SecurityException se) {
            Toast.makeText(this, "Permission Access Location Denied!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickStopButton(View v){
        timer.stop();
        startButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.INVISIBLE);
        measuredDataTable.setVisibility(View.VISIBLE);
        measuredDistance.append(distanceToString(this.getComputedDistance()));
        measuredTime.append(timer.ToString());
        measuredSpeed.append(getSpeedToString(getAverageSpeed(this.getComputedDistance(),(int)timer.getElapsedTime())));

        writeInHistory();

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

    public void onClickHistoryButton(View v)
    {
        Intent intent = new Intent(StartStopActivity.this, HistoryActivity.class);
        startActivity(intent);
        //WelcomeActivity.this.finish();
    }

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

        CharSequence cStepsText =  countedSteps.getText();
        int cStepsInt = Integer.parseInt(cStepsText.toString());
        int mDistanceInt = getComputedDistance();
        Double mSpeedDouble = getAverageSpeed(this.getComputedDistance(),(int)timer.getElapsedTime());
        long mTimeLong = timer.getElapsedTime();


        History historyJSON = getHistoryJSON();

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
                //set locations for new route
                loc.addAll(historyJSON.getRoutes().get(rout).getLocations());
                loc.addAll(newLocations);
                routeIndex = rout;
                route.setLocations(loc);
                //set parameters of new route
                int oldSteps = route.getSteps();
                long oldTime = route.getTime();
                int totalDistance = this.getComputedDistance();
                long totalTime = oldTime + mTimeLong;
                Double avgSpeed = getAverageSpeed(totalDistance,(int)totalTime);

                route.setSteps(oldSteps + cStepsInt);
                route.setTime(totalTime);
                route.setDistance(totalDistance);
                route.setSpeed(avgSpeed);

            }
        }
        if(currentDayFound == false) {
            ArrayList<Point> newLocation = new ArrayList<>();
            int newDistance = mDistanceInt;
            Double newSpeed = mSpeedDouble;
            int newSteps = cStepsInt;
            long newTime = mTimeLong;
            newLocation.addAll(newLocations);

            Route newRoute = new Route(currentDayString, newDistance, newSpeed, newSteps, newTime, newLocation);
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
        // write new route in json file
        try {
            outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(jsonStr.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("DIST1 " + distanceToString(this.getComputedDistance()));
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

    public Double getAverageSpeed(int d, int t)
    {
        Double v = (double)d/t;
        v= (v * 1000.0)/3600.0;
        v = v * 100.0;
        return v.intValue()/100.0;
    }

    public String getSpeedToString(Double v)
    {
        return v + " km/h";
    }

    public int getComputedDistance()
    {
        int R=6371,dist;
        Double dLat,dLon,lat1,lat2,long1,long2,a,c,d=0.0;
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        String currentDayString = "" + dateFormat.format(date);

        History historyJSON = getHistoryJSON();
        ArrayList<Route> routes = historyJSON.getRoutes();

        for(int rout = 0; rout < routes.size(); rout++) {
            String day = routes.get(rout).getDay();
            if(day.equals(currentDayString)){
                ArrayList<Point> locations = routes.get(rout).getLocations();
                if(locations.size() > 0) {

                    for(int loc = 0; loc < locations.size()-1; loc++) {
                        lat1 = locations.get(loc).getLatitude();
                        long1 = locations.get(loc).getLongitude();
                        lat2 = locations.get(loc).getLatitude();
                        long2 = locations.get(loc + 1).getLongitude();
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

    public History getHistoryJSON() {
        String filePath = getBaseContext().getFilesDir().getAbsolutePath() + "/" + fileName;
        JSONResourceReader reader = new JSONResourceReader(filePath);
        History jsonObj = reader.constructUsingGson(History.class);

        return jsonObj;
    }
}
