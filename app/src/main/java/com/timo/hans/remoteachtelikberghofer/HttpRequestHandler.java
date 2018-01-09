package com.timo.hans.remoteachtelikberghofer;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Hans on 14.12.2017.
 */

public class HttpRequestHandler {
    private SharedPreferences prefMain;
    private int vol = 20;
    private String[] arr;
    private String[] arrChannel;
    private String[] arrChannelNumber;
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
                        .replace("\\"+"/","/")
                        .split("\\},");
                SharedPreferences.Editor ed = prefMain.edit();
                ed.putInt("ArraySize", getArrSize());
                for (int i = 0; i < getArrSize(); i++) {
                    ed.putString("Kanal" + i, arr[i]);
                }
                ed.commit();
            } catch(IOException e){
                e.printStackTrace();
                arr = ReadChannels().toArray(new String[0]);
            } catch(JSONException e){
                e.printStackTrace();
            }
        if (arr != null)
            CleanArr();
    }

    public void CleanArr() {
            arrChannel = new String[getArrSize()];
            arrChannelNumber = new String[getArrSize()];
            for (int i = 0; i < getArrSize(); i++) {
                arrChannelNumber[i] = getCHNmb(i);
                arrChannel[i] = getChannel(i);
            }
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
        return value;
    }

    public void setChannel(String channel) {
        if (isPIP())
            executeCmd("channelPip"+channel);
        else
            executeCmd("channelMain="+channel);
    }

    public String getChannel(int i) {
        String tmp = arr[i].substring(arr[i].indexOf("\"program\":")+11);
        return tmp.substring(0, tmp.indexOf("\""));
    }

    public String getCHNmb(int i) {
        String tmp = arr[i].substring(arr[i].indexOf("\"channel\":")+11);
        return tmp.substring(0, tmp.indexOf("\""));
    }

    public void checkZero() {
        if (CH < 0)
            setCH(getArrSize()-1);
        if (CHPip < 0)
            setCHPip(getArrSize()-1);
    }

    public void checkMax() {
        if (CH >= getArrSize())
            setCH(0);
        if (CHPip >= getArrSize())
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

    public String[] getArrCh() {
        String[] tmp = arrChannel;
        return tmp;
    }

    public String[] getArr() {
        String[] tmp = arr;
        return tmp;
    }

    public int getArrSize() {
        int tmp = arr.length;
        return tmp;
    }
}