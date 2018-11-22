package com.chs.pedometer;

import android.content.Intent;
import android.support.annotation.IntDef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

public class StartStopActivity extends AppCompatActivity {

    Button startButton;
    Button stopButton;
    TableLayout measuredDataTable;
    TextView seeOnMapTextView;

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
}
