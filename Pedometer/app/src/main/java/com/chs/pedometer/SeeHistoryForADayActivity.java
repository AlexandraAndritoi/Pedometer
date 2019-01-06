package com.chs.pedometer;

import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SeeHistoryForADayActivity extends AppCompatActivity {
    Button seeOnMapButton;
    TableLayout measuredDataTable;
    TextView dateTextView;
    TextView countedSteps;
    SensorManager sensorManager;
    TextView measuredTime;
    TextView measuredDistance;
    TextView measuredSpeed;

    private String fileName = "history10.json";

    Stopwatch timer = new Stopwatch();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_history_for_aday);
        findViewsById();
        setDate(dateTextView);
    }
    public void setDate (TextView view){
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM, yyyy");
        String date = formatter.format(today);
        view.setText(date);
    }

    public void setCountedSteps(TextView view) {

    }

    public void setMeasuredTime(TextView view) {

    }

    public void setMeasuredDistance(TextView view) {

    }

    public void setMeasuredSpeed(TextView view) {

    }

    private void findViewsById() {
        seeOnMapButton = (Button) findViewById(R.id.seeOnMapButton);
        measuredDataTable = (TableLayout) findViewById(R.id.measuredDataTable2);
        dateTextView = findViewById(R.id.date2);
        countedSteps = (TextView)findViewById(R.id.countedSteps2);
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        measuredTime = (TextView) findViewById(R.id.measuredTime2);
        measuredDistance = (TextView) findViewById(R.id.measuredDistance2);
        measuredSpeed = (TextView) findViewById(R.id.measuredSpeed2);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void seeRouteOnMap(View v){
        Intent intent = new Intent(this, HistoryMapsActivity.class);
        startActivity(intent);

        //File f = new File(getBaseContext().getFilesDir().getAbsolutePath() + "/history.json");

    }

    public History getHistoryJSON() {
        String filePath = getBaseContext().getFilesDir().getAbsolutePath() + "/" + fileName;
        JSONResourceReader reader = new JSONResourceReader(filePath);
        History jsonObj = reader.constructUsingGson(History.class);

        return jsonObj;
    }
}
