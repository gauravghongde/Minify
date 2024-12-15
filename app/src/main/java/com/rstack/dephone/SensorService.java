package com.rstack.dephone;

import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SensorService extends Service implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String TAG = "SensorService";
    public int mcounter = 0;
    public int counter = 0;
    private long DailyTimeStamp;
    String packageName, ChkPackageName;
    long TimeInforground;
    int minutes, seconds, hours, h = 0, m = 0, s = 0;
    long time = System.currentTimeMillis();
    private ScheduledExecutorService scheduler;
    SharedPrefsHelper prefsHelper;

    public SensorService(Context applicationContext) {
        super();
        Log.i(TAG, "SensorService Constructor");
    }

    public SensorService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        prefsHelper = SharedPrefsHelper.getInstance(this);
        Log.i(TAG, "onStartCommand!");

        prefsHelper.getPauseSwitchPrefs().registerOnSharedPreferenceChangeListener(this);

        if (prefsHelper.getPauseSwitch() == 0) {
            Log.i(TAG, "onStartCommand: pause disabled!");
            if (scheduler == null || scheduler.isShutdown()) {
                Log.i(TAG, "onStartCommand: enabling scheduler!");
                scheduler = Executors.newSingleThreadScheduledExecutor();
                scheduler.scheduleWithFixedDelay(this::getCurrApp, 0, 5, TimeUnit.SECONDS);
            }
        }

        return START_STICKY;
    }

    private void getCurrApp() {
        packageName = "Nothing";

        UsageStatsManager mUsageStatsManager = (UsageStatsManager) getSystemService(this.USAGE_STATS_SERVICE);
        long time = System.currentTimeMillis();

        List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 10, time);

        final UsageEvents usageEvents = mUsageStatsManager.queryEvents(time - 1000 * 10, time);
        while (usageEvents.hasNextEvent()) {
            mcounter = 0;
            UsageEvents.Event event = new UsageEvents.Event();
            usageEvents.getNextEvent(event);
            switch (event.getEventType()) {
                case UsageEvents.Event.MOVE_TO_FOREGROUND:
                    packageName = event.getPackageName();
                    DailyTimeStamp = event.getTimeStamp();
                    break;
            }

        }
        if (!usageEvents.hasNextEvent() && !packageName.equalsIgnoreCase("com.rstack.dephone")) {
            //Log.i("BAC123", "PackageName is " + PackageName + "is running");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mcounter++;
            //Log.i("BAC123456", "counter: "+ mcounter);
            if (mcounter >= 10) {
                Intent i = new Intent(this, AlertView.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("msg", packageName);
                startActivity(i);
            }
        }
        if (stats != null) {
            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
            for (UsageStats usageStats : stats) {
                TimeInforground = usageStats.getTotalTimeInForeground();
                ChkPackageName = usageStats.getPackageName();

                if (ChkPackageName.equalsIgnoreCase(packageName) && !packageName.equalsIgnoreCase("com.rstack.dephone")) {
                    Log.i(TAG, "Current PackageName opened is " + ChkPackageName);
                    if (prefsHelper.isAlertDismissed(packageName)) {
                        prefsHelper.setAlertDismissed(packageName, false);
                    }
                    minutes = (int) ((TimeInforground / (1000 * 60)) % 60);
                    seconds = (int) (TimeInforground / 1000) % 60;
                    hours = (int) ((TimeInforground / (1000 * 60 * 60)) % 24);

                    h = h + hours;
                    m = m + minutes;
                    if (m >= 60) {
                        h = h + m / 60;
                        m = m % 60;
                    }
                    s = s + seconds;
                    if (s >= 60) {
                        m = m + s / 60;
                        s = s % 60;
                    }

                    Log.i(TAG, "For Package: " + ChkPackageName + " The Time is: " + hours + "h" + ":" + minutes + "m" + seconds + "s");
                    //TODO:
                    // check hasLimit()
                    // chk less than limit

                    /*
                    if(hasLimit(Package)){
                        if(getAppLimit(Package)<currAppUsage){
                            Intent i = new Intent(this, AlertView.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.putExtra("msg",PackageName);
                            startActivity(i);
                        }
                    }
                    */

                    if (prefsHelper.getDailyLimit(packageName) < hours * 60 + minutes) {
                        if (!prefsHelper.isAlertDismissed(packageName)) { // Check if alert is already dismissed
                            Intent i = new Intent(this, AlertView.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.putExtra("msg", packageName);
                            startActivity(i);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
        Intent broadcastIntent = new Intent("com.rstack.dephone.RestartSensor");
        sendBroadcast(broadcastIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String key) {
        if (key != null) {
            if (key.equals("chkStatus")) {
                Log.i(TAG, "onSharedPreferenceChanged value of chkStatus changed!");
                if (prefsHelper.getPauseSwitch() == 0) {
                    Log.i(TAG, "onSharedPreferenceChanged: pause disabled!");
                    if (scheduler == null || scheduler.isShutdown()) {
                        Log.i(TAG, "onSharedPreferenceChanged: enabling scheduler!");
                        scheduler = Executors.newSingleThreadScheduledExecutor();
                        scheduler.scheduleWithFixedDelay(this::getCurrApp, 0, 5, TimeUnit.SECONDS);
                    }
                } else {
                    Log.i(TAG, "onSharedPreferenceChanged: pause enabled!");
                    if (scheduler != null && !scheduler.isShutdown()) {
                        Log.i(TAG, "onSharedPreferenceChanged: scheduler.shutdownNow()!");
                        scheduler.shutdownNow(); // Stop the scheduler if it's running
                        scheduler = null; // Reset the scheduler
                    }
                }
            }
        } else {
            Log.i(TAG, "onSharedPreferenceChanged: key is NULL!");
        }

    }
}
