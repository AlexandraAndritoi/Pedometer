package com.chs.pedometer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class StartStopActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_stop);
    }


    public void onClickSeeOnMap(View v){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}
