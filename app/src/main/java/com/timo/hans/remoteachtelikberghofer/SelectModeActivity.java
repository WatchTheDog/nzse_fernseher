package com.timo.hans.remoteachtelikberghofer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

/**
 * Created by Timo on 30.11.2017.
 */

public class SelectModeActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("RemoteAchtelikBerghofer", "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pick_mode);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
    }
    public void showMainView(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
