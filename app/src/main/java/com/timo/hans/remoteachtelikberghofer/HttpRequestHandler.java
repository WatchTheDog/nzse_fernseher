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
    private boolean zoomed = false;
    private HttpRequest TV;

    public HttpRequestHandler(Context context) {
        TV = new HttpRequest("192.168.178.21",5000, true);
        prefMain = context.getSharedPreferences("HttpRequestHandlerPREF", Context.MODE_PRIVATE);
        executeCmd("volume="+vol);
    }

    public String ReadChannel(int num) {
        return prefMain.getString("Kanal"+num,null);
    }

    public void ChannelScan() {
        try {
            JSONObject channelsjson;
            channelsjson = TV.execute("scanChannels=");
            String[] arr = channelsjson.toString().replace("}],\"status\":\"ok\"}", " ")
                    .replace("{", " ")
                    .replace("\"channels\":[ ", " ")
                    .split("\\},");
            SharedPreferences.Editor ed = prefMain.edit();
            ed.putInt("ArraySize", arr.length);
            for (int i = 0; i < arr.length; i++) {
                ed.putString("Kanal"+i,arr[i]);
            }
            ed.commit();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void executeCmd(String command){
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
        int size = prefMain.getInt("ArraySize",0);
        ArrayList value = new ArrayList<String>(size);
        for (int i = 0; i < size; i++) {
            value.add(prefMain.getString("Kanal" + i, null));
        }
        return value;
    }

    public int getVol() {
        return vol;
    }

    public void setVol(int vol) {
        this.vol = vol;
    }

    public boolean isZoomed() {
        return zoomed;
    }

    public void setZoomed(boolean zoomed) {
        this.zoomed = zoomed;
    }
}
