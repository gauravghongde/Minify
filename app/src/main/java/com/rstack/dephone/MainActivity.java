package com.rstack.dephone;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;


public class MainActivity extends AppCompatActivity {

    private Button mButSetting;
    private TextView mDailyUsage,mDailyUnlocks;

    Intent mServiceIntent;
    private SensorService mSensorService;

    Context ctx;
    public Context getCtx(){
        return ctx;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Intent i = new Intent(this, AlertView.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);*/
        //startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));

        PhoneUnlockedReceiver receiver = new PhoneUnlockedReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(receiver, filter);

        ctx = this;
        mButSetting = findViewById(R.id.settings);
        mDailyUsage = findViewById(R.id.total_usage_daily);
        mDailyUnlocks = findViewById(R.id.total_unlocks_daily);

        UsageStatsManager mUsageStatsManager = (UsageStatsManager)getSystemService(this.USAGE_STATS_SERVICE);
        //UsageStats usageStats;
        String PackageName = "Nothing" ;
        long TimeInforground;
        int minutes,seconds,hours,h=0,m=0,s=0;
        long time = System.currentTimeMillis();

        List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000*10, time);

        if(stats != null) {
            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
            for (UsageStats usageStats : stats) {
                TimeInforground = usageStats.getTotalTimeInForeground();
                PackageName = usageStats.getPackageName();
                Log.i("pkg_name", "PackageName is" + PackageName);

                minutes = (int) ((TimeInforground / (1000 * 60)) % 60);
                seconds = (int) (TimeInforground / 1000) % 60;
                hours = (int) ((TimeInforground / (1000 * 60 * 60)) % 24);

                h=h+hours;
                m=m+minutes;
                if(m>=60){
                    h = h+m/60;
                    m = m%60;
                }
                s = s+seconds;
                if(s>=60){
                    m = m+s/60;
                    s = s%60;
                }

                Log.i("BAC123", "PackageName is" + PackageName + "Time is: " + hours + "h" + ":" + minutes + "m" + seconds + "s");
            }
        }

        mDailyUsage.setText(Integer.toString(h)+"hr "+Integer.toString(m)+"min "+Integer.toString(s)+"sec");
        mButSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsPage = new Intent(MainActivity.this,AppSettingsActivity.class);
                startActivity(settingsPage);
            }
        });



        mSensorService = new SensorService(getCtx());
        mServiceIntent = new Intent(ctx, mSensorService.getClass());
        if (!isMyServiceRunning(mSensorService.getClass())) {   //starts if isn't already running
            startService(mServiceIntent);
        }

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }



    @Override
    protected void onDestroy() {
        stopService(mServiceIntent);
        Log.i("MAINACT", "onDestroy!");
        super.onDestroy();
    }
}
