package com.timo.hans.remoteachtelikberghofer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

/**
 * Created by Timo on 30.11.2017.
 */

public class SelectModeActivity extends AppCompatActivity {
    private SharedPreferences pref;
    private SharedPreferences.Editor ed;
    private boolean first;
    private Button Beginner, Expert;
    private RelativeLayout Loading;

    protected void onCreate(Bundle savedInstanceState) {
        Log.i("RemoteAchtelikBerghofer", "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pick_mode);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Beginner = findViewById(R.id.select_beginner);
        Expert = findViewById(R.id.select_experte);
        Loading = findViewById(R.id.loadingPanel);
        pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        first = !pref.getBoolean("select_executed", false);
        if(pref.getBoolean("select_executed", false)){
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("beginner", pref.getBoolean("beginner",false));
            intent.putExtra("first",first);
            startActivity(intent);
            finish();
        } else {
            SharedPreferences.Editor ed = pref.edit();
            ed.putBoolean("select_executed", true);
            ed.commit();
        }

    }
       public void showBeginnerView(View view){
        Beginner.setVisibility(View.INVISIBLE);
        Expert.setVisibility(View.INVISIBLE);
        Loading.setVisibility(View.VISIBLE);
        ed = pref.edit();
        ed.putBoolean("beginner", true);
        ed.commit();
        Intent intent = new Intent(this, MainActivity.class);
           intent.putExtra("first",first);
        intent.putExtra("beginner", true);
        startActivity(intent);
    }

    public void showMainView(View view){
        Beginner.setVisibility(View.INVISIBLE);
        Expert.setVisibility(View.INVISIBLE);
        Loading.setVisibility(View.VISIBLE);
        ed = pref.edit();
        ed.putBoolean("beginner", false);
        ed.commit();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("first",first);
        intent.putExtra("beginner", false);
        startActivity(intent);
    }
}
