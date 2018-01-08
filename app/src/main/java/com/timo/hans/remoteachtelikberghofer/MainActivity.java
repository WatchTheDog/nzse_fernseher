package com.timo.hans.remoteachtelikberghofer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.json.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private HttpRequestHandler Requester;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    private ImageButton pip;
    private Boolean longclick=false;
    public MainActivity() {
    }

    /** @brief wird beim Erstellen ausgeführt.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("RemoteAchtelikBerghofer", "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pip = (ImageButton)findViewById(R.id.btnPicInPic);
        mDrawerList = (ListView)findViewById(R.id.navList);
        addDrawerItems();
        Toolbar t = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(t);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();
        setupDrawer();
        Requester = new HttpRequestHandler(this);
        Requester.ChannelScan();
        pip.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                piplongclick();
                return true;
            }
        });
    }

    private void piplongclick() {
        Toast toast;
        if(longclick){
            longclick=false;
            toast = Toast.makeText(this, "PIP navigation deactivated", Toast.LENGTH_LONG);
        }
        else {
            longclick = true;
            toast = Toast.makeText(this, "PIP navigation activated", Toast.LENGTH_LONG);
        }
        toast.show();
    }

    /** @brief wird beim Klicken auf Hello World ausgeführt.
     *     * @param v
     */
    public void onHelloWorldClick (View v) {
        Log.i("RemoteAchtelikBerghofer", "onHelloWorldClick: ");
    }

    public void IncreaseVol(View v){
        Requester.setVol(Requester.getVol()+1);
        String tmp="volume=" + Requester.getVol();
        Requester.executeCmd(tmp);
    }

    public void DecreaseVol(View v){
        Requester.setVol(Requester.getVol()-1);
        String tmp="volume=" + Requester.getVol();
        Requester.executeCmd(tmp);
    }

    public void Zoom(View v){
        if(!longclick) {
            String tmp = "";
            if (!Requester.isZoomed()) {
                tmp = "zoomMain=1";
                Requester.setZoomed(true);
            } else {
                tmp = "zoomMain=0";
                Requester.setZoomed(false);
            }
            Requester.executeCmd(tmp);
        }
        else{
            String tmp = "";
            if (!Requester.isZoomed()) {
                tmp = "zoomPip=1";
                Requester.setZoomed(true);
            } else {
                tmp = "zoomPip=0";
                Requester.setZoomed(false);
            }
            Requester.executeCmd(tmp);
        }
    }

    public void switchChannelUp(View v){
        if(!longclick) {
            Requester.setCH(Requester.getCH()+1);
            Requester.checkMax();
            String tmp = "channelMain=" + Requester.getCHNmb(Requester.getCH());
            Requester.executeCmd(tmp);
        }
        else{
            Requester.setCHPip(Requester.getCHPip()+1);
            Requester.checkMax();
            String tmp = "channelPip=" + Requester.getCHNmb(Requester.getCHPip());
            Requester.executeCmd(tmp);
        }
    }

    public void switchChannelDown(View v){
        if(!longclick) {
            Requester.setCH(Requester.getCH()-1);
            Requester.checkZero();
            String tmp = "channelMain=" + Requester.getCHNmb(Requester.getCH());
            Requester.executeCmd(tmp);
        }
        else{
            Requester.setCHPip(Requester.getCHPip()-1);
            Requester.checkZero();
            String tmp = "channelPip=" + Requester.getCHNmb(Requester.getCHPip());
            Requester.executeCmd(tmp);
        }
    }

    public void PIP(View v){
        String tmp="";
        if(!Requester.isPIP()){
            tmp="showPip=1";
            Requester.setPIP(true);
        }
        else{
            tmp="showPip=0";
            Requester.setPIP(false);
            longclick=false;
            Toast toast;
            toast = Toast.makeText(this, "PIP navigation deactivated", Toast.LENGTH_LONG);
            toast.show();
        }
        Requester.executeCmd(tmp);
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {
            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }

    private void addDrawerItems() {
        String[] osArray = {"Android", "iOS", "Windows", "OS X", "Linux"};
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);
    }

    /** @brief wird beim Starten ausgeführt.
     *
     */
    @Override
    protected void onStart() {
        Log.i ("RemoteAchtelikBerghofer", "onStart");
        super.onStart();
    }

    /** @brief wird beim Stoppen ausgeführt.
     *
     */
    @Override
    protected void onStop() {
        Log.i ("RemoteAchtelikBerghofer", "onStop");
        super.onStop();
    }

    /** @brief wird beim Speichern des aktuellen Stands ausgeführt.
     *
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i ("RemoteAchtelikBerghofer", "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    /** @brief wird beim weiter ausführen ausgeführt.
     *
     */
    @Override
    protected void onResume() {
        Log.i ("RemoteAchtelikBerghofer", "onResume");
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
