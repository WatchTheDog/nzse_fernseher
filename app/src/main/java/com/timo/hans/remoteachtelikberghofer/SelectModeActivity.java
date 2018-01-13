package com.timo.hans.remoteachtelikberghofer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

/**
 * Created by Timo on 30.11.2017.
 */

public class SelectModeActivity extends AppCompatActivity {
    private SharedPreferences pref;
    private SharedPreferences.Editor ed;
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("RemoteAchtelikBerghofer", "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pick_mode);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        if(pref.getBoolean("select_executed", false)){
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("beginner", pref.getBoolean("beginner",false));
            startActivity(intent);
            finish();
        } else {
            SharedPreferences.Editor ed = pref.edit();
            ed.putBoolean("select_executed", true);
            ed.commit();
        }

    }
       public void showBeginnerView(View view){
        ed = pref.edit();
        ed.putBoolean("beginner", true);
        ed.commit();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("beginner", true);
        startActivity(intent);
    }

    public void showMainView(View view){
        ed = pref.edit();
        ed.putBoolean("beginner", false);
        ed.commit();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("beginner", false);
        startActivity(intent);
    }
}
