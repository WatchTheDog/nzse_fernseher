package com.timo.hans.remoteachtelikberghofer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.shapes.Shape;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
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
    private ListView Favs;
    private GridLayout grid;
    private ArrayList<String> Favorites;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private GradientDrawable pipShape;
    private String mActivityTitle;
    private ImageButton Pip;
    private Boolean longclick=false;
    private Boolean on = false;
    private android.view.Display display;

    public MainActivity() {
    }

    /** @brief wird beim Erstellen ausgeführt.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("RemoteAchtelikBerghofer", "onCreate: ");
        display = ((android.view.WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        if (!getIntent().getBooleanExtra("beginner",false)) {
            setContentView(R.layout.activity_main);
            grid = findViewById(R.id.GridLayout);
            Pip = (ImageButton)findViewById(R.id.btnPicInPic);
            pipShape = (GradientDrawable) Pip.getBackground();
            GridSetup(5);
        }
        else {
            setContentView(R.layout.activity_beginner);
            grid = findViewById(R.id.GridLayout);
            GridSetup(3);
        }
        Requester = new HttpRequestHandler(this);
        Requester.ChannelScan();
        Favs = (ListView) findViewById(R.id.favList);
        Toolbar t = (Toolbar) findViewById(R.id.toolbar);
        t.setNavigationIcon(R.drawable.ic_menu_white_36dp);
        Favs.setDivider(new ColorDrawable(getResources().getColor(R.color.colorPrimaryLight)));
        Favs.setDividerHeight(5);
        setSupportActionBar(t);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();
        setupDrawer();
        if (!getIntent().getBooleanExtra("beginner",false))
        Pip.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                piplongclick();
                return true;
            }
        });
        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerList.setDivider(new ColorDrawable(getResources().getColor(R.color.colorPrimaryLight)));
        mDrawerList.setDividerHeight(5);
        addDrawerItems();
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Requester.setCurrentChannel(Requester.getArrNumber(position));
                if (!on)
                    on = true;
            }
        });
        mDrawerList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Requester.ToggleIsFav(position);
                UpdateFavs();
                return false;
            }
        });
        Favs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String[] tmp = Requester.getArrCh();
                    for (int i = 0; i <tmp.length; i++) {
                        if (tmp[i].equals(Favorites.get(position))) {
                            Requester.setCurrentChannel(Requester.getArrNumber(i));
                            if (!on)
                                on = true;
                            break;
                        }
                    }
            }
        });
    }

    private void piplongclick() {
        if(longclick){
            longclick=false;
            pipShape.setColor(getResources().getColor(R.color.colorPrimary));
        }
        else {
            longclick = true;
            pipShape.setColor(getResources().getColor(R.color.colorPrimaryDark));
            if(!Requester.isPIP()){
                String tmp="showPip=1";
                Requester.setPIP(true);
                Requester.executeCmd(tmp);
            }
        }
        if (!on)
            on = true;
    }

    public void OpenFavs (View v) {
        mDrawerLayout.openDrawer(Favs);
    }

    public void IncreaseVol(View v){
        Requester.setVol(Requester.getVol()+1);
        String tmp="volume=" + Requester.getVol();
        Requester.executeCmd(tmp);
        if (!on)
            on = true;
    }

    public void Standby(View v){
        String tmp;
        if (on) {
            tmp = "standby=1";
            on = false;
        }
        else {
            tmp = "standby=0";
            on = true;
        }
        Requester.executeCmd(tmp);
    }

    public void DecreaseVol(View v){
        Requester.setVol(Requester.getVol()-1);
        String tmp="volume=" + Requester.getVol();
        Requester.executeCmd(tmp);
        if (!on)
            on = true;
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
        if (!on)
            on = true;
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
        if (!on)
            on = true;
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
        if (!on)
            on = true;
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
            pipShape.setColor(getResources().getColor(R.color.colorPrimary));
        }
        Requester.executeCmd(tmp);
        if (!on)
            on = true;
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
        if (Requester.getArr() != null) {
            String[] arrAll = Requester.getArrCh();
            mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrAll);
            mDrawerList.setAdapter(mAdapter);
            UpdateFavs();
        }
    }

    private void UpdateFavs() {
        Favorites = new ArrayList<>();
        for (int i = 0; i < Requester.getArrQuality().length; i++) {
            if (Requester.getIsFav(i))
                Favorites.add(Requester.getArrCh()[i]);
        }
        ArrayAdapter<String> FavAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,Favorites);
        Favs.setAdapter(FavAdapter);
        Requester.SaveData();
    }

    private void GridSetup(int rows) {
        ImageButton VolUp = findViewById(R.id.btnVolUp);
        ImageButton VolDown = findViewById(R.id.btnVolDown);
        ImageButton ChUp = findViewById(R.id.btnCHup);
        ImageButton ChDown = findViewById(R.id.btnCHdown);
        ImageButton Ratio = findViewById(R.id.btnChangeRatio);
        ImageButton Rewind = findViewById(R.id.btnRewind);
        ImageButton PlayPause = findViewById(R.id.btnPlayPause);
        ImageButton FastForward = findViewById(R.id.btnFastForward);
        ImageButton Power = findViewById(R.id.btnPower);
        LayoutParams layoutParams = grid.getLayoutParams();
        layoutParams.height = ((int)(display.getHeight()*0.94));
        layoutParams.width = ((int)(display.getWidth()*0.964));
        grid.setLayoutParams(layoutParams);
        LayoutParams PowerParams = Power.getLayoutParams();
        PowerParams.width = ((int)(display.getWidth()*0.951));
        PowerParams.height = ((int)(display.getHeight()*0.86/rows));
        LayoutParams VolUpParams = VolUp.getLayoutParams();
        VolUpParams.width = ((int)(display.getWidth()*0.937/2));
        VolUpParams.height = ((int)(display.getHeight()*0.86/rows));
        LayoutParams VolDownParams = VolDown.getLayoutParams();
        VolDownParams.width = ((int)(display.getWidth()*0.937/2));
        VolDownParams.height = ((int)(display.getHeight()*0.86/rows));
        LayoutParams ChUpParams = ChUp.getLayoutParams();
        ChUpParams.width = ((int)(display.getWidth()*0.937/2));
        ChUpParams.height = ((int)(display.getHeight()*0.86/rows));
        LayoutParams ChDownParams = ChDown.getLayoutParams();
        ChDownParams.width = ((int)(display.getWidth()*0.937/2));
        ChDownParams.height = ((int)(display.getHeight()*0.86/rows));
        if (!getIntent().getBooleanExtra("beginner", false)) {
            LayoutParams PipParams = Pip.getLayoutParams();
            PipParams.width = ((int)(display.getWidth()*0.937/2));
            PipParams.height = ((int)(display.getHeight()*0.86/rows));
            LayoutParams RatioParams = Ratio.getLayoutParams();
            RatioParams.width = ((int) (display.getWidth() * 0.937 / 2));
            RatioParams.height = ((int) (display.getHeight() * 0.86 / rows));
            LayoutParams RewindParams = Rewind.getLayoutParams();
            RewindParams.width = ((int) (display.getWidth() * 0.9225 / 3));
            RewindParams.height = ((int) (display.getHeight() * 0.74 / rows));
            LayoutParams PlayPauseParams = PlayPause.getLayoutParams();
            PlayPauseParams.width = ((int) (display.getWidth() * 0.9225 / 3));
            PlayPauseParams.height = ((int) (display.getHeight() * 0.74 / rows));
            LayoutParams FastForwardParams = FastForward.getLayoutParams();
            FastForwardParams.width = ((int) (display.getWidth() * 0.9225 / 3));
            FastForwardParams.height = ((int) (display.getHeight() * 0.74 / rows));
            Pip.setLayoutParams(PipParams);
            Ratio.setLayoutParams(RatioParams);
            Rewind.setLayoutParams(RewindParams);
            PlayPause.setLayoutParams(PlayPauseParams);
            FastForward.setLayoutParams(FastForwardParams);
            Power.setLayoutParams(PowerParams);
        }
        VolUp.setLayoutParams(VolUpParams);
        VolDown.setLayoutParams(VolDownParams);
        ChUp.setLayoutParams(ChUpParams);
        ChDown.setLayoutParams(ChDownParams);
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
        Requester.SaveData();
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
