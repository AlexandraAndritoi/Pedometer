package com.chs.pedometer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    String selectedDate;

    Stopwatch timer = new Stopwatch();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_history_for_aday);

        findViewsById();
        setDate(dateTextView);
        setDayParameters();
    }
    public void setDate (TextView view){
        Bundle bundle = getIntent().getExtras();
        selectedDate = (String) bundle.get("selectedDay");
        view.setText(selectedDate);
    }

    public void setDayParameters() {
        History historyJSON = getHistoryJSON();
        ArrayList<Route> routes = historyJSON.getRoutes();

        for(int rout = 0; rout < routes.size(); rout++) {
            Route route = routes.get(rout);
            String day = route.getDay();
            if(day.equals(selectedDate)){
                countedSteps.setText("" + route.getSteps());
                measuredDistance.setText("" + route.getDistance());
                measuredSpeed.setText("" + route.getSpeed());
                measuredTime.setText("" + route.getTime());
            }
        }
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
        Bundle bundle = new Bundle();
        bundle.putString("selectedDay", selectedDate);
        intent.putExtras(bundle);
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
