package com.timo.hans.remoteachtelikberghofer;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Hans on 14.12.2017.
 */

public class HttpRequestHandler {
    private SharedPreferences prefMain;
    private int vol = 20;
    String[] arr;
    private int CH = 0;
    private int CHPip = 0;
    private boolean isPIP = false;
    private boolean zoomed = false;
    private HttpRequest TV;

    public HttpRequestHandler(Context context) {
        TV = new HttpRequest("192.168.178.21", 5000, true);
        prefMain = context.getSharedPreferences("HttpRequestHandlerPREF", Context.MODE_PRIVATE);
        executeCmd("volume=" + vol);
    }

    public String ReadChannel(int num) {
        return prefMain.getString("Kanal" + num, null);
    }

    public void ChannelScan() {
        try {
            JSONObject channelsjson;
            channelsjson = TV.execute("scanChannels=");
            arr = channelsjson.toString().replace("}],\"status\":\"ok\"}", " ")
                    .replace("{", " ")
                    .replace("\"channels\":[ ", " ")
                    .split("\\},");
            SharedPreferences.Editor ed = prefMain.edit();
            ed.putInt("ArraySize", arr.length);
            for (int i = 0; i < arr.length; i++) {
                ed.putString("Kanal" + i, arr[i]);
            }
            ed.commit();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("Kek", getCHNmb(5));
    }

    public void executeCmd(String command) {
        try {
            JSONObject tmp;
            tmp = TV.execute(command);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> ReadChannels() {
        int size = prefMain.getInt("ArraySize", 0);
        ArrayList value = new ArrayList<String>(size);
        for (int i = 0; i < size; i++) {
            value.add(prefMain.getString("Kanal" + i, null));
        }
        setChannel("");
        return value;
    }

    public void setChannel(String channel) {
        if (isPIP())
            executeCmd("channelPip=8a");
        else
            executeCmd("channelMain=8b");
    }

    public String getChannel(String name) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].contains(name))
                return arr[i];
        }
        return null;
    }

    public String getCHNmb(int i) {
        String tmp = arr[i].substring(arr[i].indexOf("\"channel\":")+11);
        return tmp.substring(0, tmp.indexOf("\""));
    }

    public void checkZero() {
        if (CH < 0)
            setCH(arr.length-1);
        if (CHPip < 0)
            setCHPip(arr.length-1);
    }

    public void checkMax() {
        if (CH >= arr.length)
            setCH(0);
        if (CHPip >= arr.length)
            setCHPip(0);
    }

    public int getVol() {
        return vol;
    }

    public void setVol(int vol) {
        this.vol = vol;
    }

    public void setCH(int CH) {
        this.CH = CH;
    }

    public int getCH() {
        return CH;
    }

    public boolean isZoomed() {
        return zoomed;
    }

    public void setZoomed(boolean zoomed) {
        this.zoomed = zoomed;
    }

    public boolean isPIP() {
        return isPIP;
    }

    public void setPIP(boolean PIP) {isPIP = PIP;}

    public int getCHPip() {
        return CHPip;
    }

    public void setCHPip(int CHPip) {
        this.CHPip = CHPip;
    }
}