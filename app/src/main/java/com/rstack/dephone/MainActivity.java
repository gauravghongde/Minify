package com.rstack.dephone;

import android.app.usage.UsageStatsManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    private Button mButLogin;
    private Button mButSetting;

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
        ctx = this;
        mButLogin = findViewById(R.id.btn_signin);
        mButSetting = findViewById(R.id.settings);


        UsageStatsManager mUsageStatsManager = (UsageStatsManager)getSystemService(this.USAGE_STATS_SERVICE);


        //startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));

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
