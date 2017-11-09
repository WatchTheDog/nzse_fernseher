package com.timo.hans.remoteachtelikberghofer;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("RemoteAchtelikBerghofer", "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
    public void onHelloWorldClick (View v) {
        Log.i("RemoteAchtelikBerghofer", "onHelloWorldClick: ");
    }

    @Override
    protected void onStart() {
        Log.i ("RemoteAchtelikBerghofer", "onStart");
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.i ("RemoteAchtelikBerghofer", "onStop");
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i ("RemoteAchtelikBerghofer", "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        Log.i ("RemoteAchtelikBerghofer", "onResume");
        super.onResume();
    }

}
