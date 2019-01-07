package com.chs.pedometer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;

import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class HistoryActivity extends AppCompatActivity {

    private String fileName = "history12.json";
    private String buttonText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        LinearLayout linear = (LinearLayout) findViewById(R.id.history_layout);
        Button btn1;

        History historyJSON = getHistoryJSON();
        ArrayList<Route> routes = historyJSON.getRoutes();

        for (int rout = 0; rout < routes.size(); rout++) {
            String day = routes.get(rout).getDay();

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            Button btn = new Button(this);
            btn.setId(rout);
            final int id_ = btn.getId();
            btn.setText(day);
            params.setMargins(0, 0, 0, 20);
            btn.setLayoutParams(params);
            btn.setHeight(60);

            linear.addView(btn, params);
            btn1 = ((Button) findViewById(id_));
            final Button finalBtn = btn1;
            btn1.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        buttonText = finalBtn.getText().toString();
                        goToDayHistory();
                    }
                });
        }

        }

    public void goToDayHistory () {
        Intent intent = new Intent(this, SeeHistoryForADayActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("selectedDay", buttonText);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public History getHistoryJSON() {
        String filePath = getBaseContext().getFilesDir().getAbsolutePath() + "/" + fileName;
        JSONResourceReader reader = new JSONResourceReader(filePath);
        History jsonObj = reader.constructUsingGson(History.class);

        return jsonObj;
    }
}
