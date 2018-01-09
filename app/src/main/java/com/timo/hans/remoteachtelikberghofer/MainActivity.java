package com.timo.hans.remoteachtelikberghofer;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.json.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private HttpRequestHandler Requester;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private GridLayout grid;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    private ImageButton pip;
    private Boolean longclick=false;
    private android.view.Display display;
    ImageButton VolUp;
    ImageButton VolDown;
    ImageButton ChUp;
    ImageButton ChDown;
    ImageButton Pip ;
    ImageButton Ratio;
    ImageButton Rewind;
    ImageButton PlayPause;
    ImageButton FastForward;

    public MainActivity() {
    }

    /** @brief wird beim Erstellen ausgeführt.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("RemoteAchtelikBerghofer", "onCreate: ");
        Requester = new HttpRequestHandler(this);
        Requester.ChannelScan();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        display = ((android.view.WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        pip = (ImageButton)findViewById(R.id.btnPicInPic);
        Toolbar t = (Toolbar) findViewById(R.id.toolbar);
        t.setNavigationIcon(R.drawable.ic_menu_white_18dp);
        GridSetup();
        setSupportActionBar(t);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();
        setupDrawer();
        pip.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                piplongclick();
                return true;
            }
        });
        mDrawerList = (ListView)findViewById(R.id.navList);
        addDrawerItems();
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
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout
                , R.string.drawer_open, R.string.drawer_close) {
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
        String[] arr = Requester.getArrCh();
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arr);
        mDrawerList.setAdapter(mAdapter);
    }

    private void GridSetup() {
        VolUp = findViewById(R.id.btnVolUp);
        VolDown = findViewById(R.id.btnVolDown);
        ChUp = findViewById(R.id.btnCHup);
        ChDown = findViewById(R.id.btnCHdown);
        Pip = findViewById(R.id.btnPicInPic);
        Ratio = findViewById(R.id.btnChangeRatio);
        Rewind = findViewById(R.id.btnRewind);
        PlayPause = findViewById(R.id.btnPlayPause);
        FastForward = findViewById(R.id.btnFastForward);
        grid = (GridLayout)findViewById(R.id.GridLayout);
        LayoutParams layoutParams = grid.getLayoutParams();
        layoutParams.height = ((int)(display.getHeight()*0.92));
        layoutParams.width = ((int)(display.getWidth()*0.965));
        grid.setLayoutParams(layoutParams);
        LayoutParams VolUpParams = VolUp.getLayoutParams();
        VolUpParams.width = ((int)(display.getWidth()*0.94/2));
        VolUpParams.height = ((int)(display.getHeight()*0.86/4));
        LayoutParams VolDownParams = VolDown.getLayoutParams();
        VolDownParams.width = ((int)(display.getWidth()*0.94/2));
        VolDownParams.height = ((int)(display.getHeight()*0.86/4));
        LayoutParams ChUpParams = ChUp.getLayoutParams();
        ChUpParams.width = ((int)(display.getWidth()*0.94/2));
        ChUpParams.height = ((int)(display.getHeight()*0.86/4));
        LayoutParams ChDownParams = ChDown.getLayoutParams();
        ChDownParams.width = ((int)(display.getWidth()*0.94/2));
        ChDownParams.height = ((int)(display.getHeight()*0.86/4));
        LayoutParams PipParams = Pip.getLayoutParams();
        PipParams.width = ((int)(display.getWidth()*0.94/2));
        PipParams.height = ((int)(display.getHeight()*0.86/4));
        LayoutParams RatioParams = Ratio.getLayoutParams();
        RatioParams.width = ((int)(display.getWidth()*0.93/2));
        RatioParams.height = ((int)(display.getHeight()*0.86/4));
        LayoutParams RewindParams = Rewind.getLayoutParams();
        RewindParams.width = ((int)(display.getWidth()*0.93/3));
        RewindParams.height = ((int)(display.getHeight()*0.7/4));
        LayoutParams PlayPauseParams = PlayPause.getLayoutParams();
        PlayPauseParams.width = ((int)(display.getWidth()*0.93/3));
        PlayPauseParams.height = ((int)(display.getHeight()*0.7/4));
        LayoutParams FastForwardParams = FastForward.getLayoutParams();
        FastForwardParams.width = ((int)(display.getWidth()*0.93/3));
        FastForwardParams.height = ((int)(display.getHeight()*0.7/4));
        VolUp.setLayoutParams(VolUpParams);
        VolDown.setLayoutParams(VolDownParams);
        ChUp.setLayoutParams(ChUpParams);
        ChDown.setLayoutParams(ChDownParams);
        Pip.setLayoutParams(PipParams);
        Ratio.setLayoutParams(RatioParams);
        Rewind.setLayoutParams(RewindParams);
        PlayPause.setLayoutParams(PlayPauseParams);
        FastForward.setLayoutParams(FastForwardParams);
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
