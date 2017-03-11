package com.example.whp.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.whp.models.DevicesConnect;

/**
 * Created by hieuho on 10/03/2017.
 */

public class WifiHotsPotManager {
    private WifiManager wifiManager;
    private enum WifiState {
        WIFI_AP_STATE_DISABLING, WIFI_AP_STATE_DISABLED, WIFI_AP_STATE_ENABLING, WIFI_AP_STATE_ENABLED, WIFI_AP_STATE_FAILED
    }

    public WifiHotsPotManager(Context context) {
        wifiManager = (WifiManager)context.getSystemService(context.WIFI_SERVICE);
    }

    public Boolean setEnableWifi(WifiConfiguration wifiConfig, boolean enabled){

        try {
            if (enabled) {
                wifiManager.setWifiEnabled(false);
            }
            Method method = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            return (Boolean) method.invoke(wifiManager, wifiConfig, enabled);
        } catch (Exception e) {
            Log.e(this.getClass().toString(), "", e);
            return false;
        }
    }

    public WifiState getWifiApState() {
        try {
            Method method = wifiManager.getClass().getMethod("getWifiApState");

            int tmp = ((Integer)method.invoke(wifiManager));

            // Fix for Android 4
            if (tmp >= 10) {
                tmp = tmp - 10;
            }

            return WifiState.class.getEnumConstants()[tmp];
        } catch (Exception e) {
            Log.e(this.getClass().toString(), "", e);
            return WifiState.WIFI_AP_STATE_FAILED;
        }
    }
    public boolean isWifiApEnabled() {
        return getWifiApState() == WifiState.WIFI_AP_STATE_ENABLED;
    }



    public boolean setWifiApConfiguration(WifiConfiguration wifiConfig) {
        try {
            Method method = wifiManager.getClass().getMethod("setWifiApConfiguration", WifiConfiguration.class);
            return (Boolean) method.invoke(wifiManager, wifiConfig);
        } catch (Exception e) {
            Log.e(this.getClass().toString(), "", e);
            return false;
        }
    }

    public void getClientList(boolean onlyReachables, FinishScan finishScan) {
        getClientList(onlyReachables, 1, finishScan );
    }
    public void getClientList(final boolean onlyReachables, final int reachableTimeout, final FinishScan finishListener) {
        Runnable runnable = new Runnable() {
            public void run() {
                BufferedReader br = null;
                final ArrayList<DevicesConnect> result = new ArrayList<DevicesConnect>();
                try {
                    br = new BufferedReader(new FileReader("/proc/net/arp"));
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] splitted = line.split(" +");
                        if ((splitted != null) && (splitted.length >= 4)) {
                            String mac = splitted[3];
                            if (mac.matches("..:..:..:..:..:..")) {
                                boolean isReachable = InetAddress.getByName(splitted[0]).isReachable(reachableTimeout);
                                if (!onlyReachables || isReachable) {
                                    result.add(new DevicesConnect(splitted[0], splitted[3], splitted[5], isReachable));
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e(this.getClass().toString(), e.toString());
                } finally {
                    try {
                        br.close();
                    } catch (IOException e) {
                        Log.e(this.getClass().toString(), e.getMessage());
                    }
                }
                Handler mainHandler = new Handler(Looper.getMainLooper());
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        finishListener.onFinishScan(result);
                    }
                };
                mainHandler.post(myRunnable);
            }
        };

        Thread mythread = new Thread(runnable);
        mythread.start();
    }
public interface FinishScan{
    void onFinishScan(ArrayList<DevicesConnect> result);
    }
}
