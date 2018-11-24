package com.chs.pedometer;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.IntDef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class StartStopActivity extends AppCompatActivity implements SensorEventListener{

    Button startButton;
    Button stopButton;
    TableLayout measuredDataTable;
    TextView seeOnMapTextView;
    TextView countedSteps;
    SensorManager sensorManager;
    boolean running = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_stop);
        findViewsById();
    }

    private void findViewsById() {
        startButton = (Button) findViewById(R.id.startButton);
        stopButton = (Button) findViewById(R.id.stopButton);
        measuredDataTable = (TableLayout) findViewById(R.id.measuredDataTable);
        seeOnMapTextView = (TextView) findViewById(R.id.seeOnMap);
        countedSteps = (TextView)findViewById(R.id.countedSteps);
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        running = true;
        Sensor countStepsSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if(countStepsSensor != null) {
            sensorManager.registerListener(this, countStepsSensor, sensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Sensor not found!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //        if you unregister the hardware will stop detecting steps
        //        sensorManager.unregisterListener(this);
    }

    public void onClickStartButton(View v){
        startButton.setVisibility(View.INVISIBLE);
        stopButton.setVisibility(View.VISIBLE);
        measuredDataTable.setVisibility(View.VISIBLE);
        seeOnMapTextView.setVisibility(View.VISIBLE);
    }

    public void onClickStopButton(View v){
        startButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.INVISIBLE);
        measuredDataTable.setVisibility(View.INVISIBLE);
        seeOnMapTextView.setVisibility(View.INVISIBLE);
    }

    public void onClickSeeOnMap(View v){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(running){
            countedSteps.setText(String.valueOf(sensorEvent.values[0]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
