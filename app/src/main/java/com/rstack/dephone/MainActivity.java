package com.rstack.dephone;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mButSetting;
    private TextView mHourDay,mMinDay,mHourWeek,mMinWeek;
    DatabaseClass dbClass = new DatabaseClass();
    Intent mServiceIntent;
    ApkInfoExtractor apkInfoExtractor = new ApkInfoExtractor(this);
    ImageButton[] ib = new ImageButton[10];
    String [] top10Apps = new String [10];
    TreeMap<Long,String> treeMap = new TreeMap<>();

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

        //--------------------SHARED PREF-------------------------------------------------------------

        SharedPreferences spForDay = getSharedPreferences("dataForDay", Context.MODE_PRIVATE);
        SharedPreferences.Editor spForDayEditor = spForDay.edit();
        SharedPreferences spForWeek = getSharedPreferences("dataForWeek", Context.MODE_PRIVATE);
        SharedPreferences.Editor spForWeekEditor = spForWeek.edit();

        //--------------------------------------------------------------------------------------------

        //--------------------SHARED PREF-------------------------------------------------------------

        SharedPreferences dailyLimit = getSharedPreferences("dailyLimit", Context.MODE_PRIVATE);
        SharedPreferences.Editor dailyLimitEditor = dailyLimit.edit();
        SharedPreferences contLimit = getSharedPreferences("contLimit", Context.MODE_PRIVATE);
        SharedPreferences.Editor contLimitEditor =contLimit.edit();

        //--------------------------------------------------------------------------------------------


        PhoneUnlockedReceiver receiver = new PhoneUnlockedReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(receiver, filter);

        ctx = this;
        mButSetting = findViewById(R.id.settings);
        //mDailyUnlocks = findViewById(R.id.total_unlocks_daily);
        mHourDay = findViewById(R.id.hour_day);
        mMinDay = findViewById(R.id.min_day);
        mHourWeek = findViewById(R.id.hour_week);
        mMinWeek = findViewById(R.id.min_week);

        ib[0] = findViewById(R.id.top_app_1);
        ib[1] = findViewById(R.id.top_app_2);
        ib[2] = findViewById(R.id.top_app_3);
        ib[3] = findViewById(R.id.top_app_4);
        ib[4] = findViewById(R.id.top_app_5);
        ib[5] = findViewById(R.id.top_app_6);
        ib[6] = findViewById(R.id.top_app_7);
        ib[7] = findViewById(R.id.top_app_8);
        ib[8] = findViewById(R.id.top_app_9);
        ib[9] = findViewById(R.id.top_app_10);

        UsageStatsManager mUsageStatsManager = (UsageStatsManager)getSystemService(this.USAGE_STATS_SERVICE);
        //UsageStats usageStats;
        String PackageName = "Nothing" ;
        long TimeInforground;
        int minutes,seconds,hours,h=0,m=0,s=0;
        long time = System.currentTimeMillis();

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH,1);
        c.add(Calendar.HOUR_OF_DAY,0);
        c.add(Calendar.MINUTE,0);
        c.add(Calendar.SECOND,0);
        c.add(Calendar.MILLISECOND,0);
        long howMany = c.getTimeInMillis();
        Log.i("midnighttime",Long.toString(howMany)+" "+Long.toString(time));

        List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time-10*1000, time);
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

                spForDayEditor.putInt(PackageName,hours*60+minutes);
                spForDayEditor.apply();

                /////////////////////////////////////////////////////////////////////////////////
                dailyLimitEditor.putInt(PackageName,Integer.MAX_VALUE);
                dailyLimitEditor.apply();
                contLimitEditor.putInt(PackageName,Integer.MAX_VALUE);
                contLimitEditor.apply();
                /////////////////////////////////////////////////////////////////////////////////

                treeMap.put(TimeInforground,PackageName);

                dbClass.setAppListTodaysTimings(PackageName,hours,minutes);
                Log.i("BAC123", "PackageName is" + PackageName + "Time is: " + hours + "h" + ":" + minutes + "m" + seconds + "s");
            }
        }

        mHourDay.setText(Integer.toString(h));
        mMinDay.setText(Integer.toString(m));

        Drawable drawable;
        //For Top 10 app list on the basis of daily usage
        Iterator<Long> iterator = treeMap.descendingKeySet().iterator();
        for(int i=0; i<=9; i++){
            Long mapEntry = iterator.next();
            Log.d("iterator123",mapEntry.toString()+" "+treeMap.get(mapEntry));
            drawable = apkInfoExtractor.getAppIconByPackageName(treeMap.get(mapEntry));
            ib[i].setImageDrawable(drawable);
            ib[i].setOnClickListener(MainActivity.this);
            top10Apps[i] = treeMap.get(mapEntry);
        }

        h=0;
        m=0;
        s=0;

        List<UsageStats> statsWeekly = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_WEEKLY, time-10*1000, time);
        if(statsWeekly != null) {
            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
            for (UsageStats usageStats : statsWeekly) {
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

                spForWeekEditor.putInt(PackageName,(hours*60+minutes)/7);
                spForWeekEditor.apply();

                //treeMap.put(TimeInforground,PackageName);

                //dbClass.setAppListTodaysTimings(PackageName,hours,minutes);
                //Log.i("BAC123", "PackageName is" + PackageName + "Time is: " + hours + "h" + ":" + minutes + "m" + seconds + "s");
            }
        }

        m = h*60+m;
        m = m/7;
        h = m/60;
        m = m%60;

        mHourWeek.setText(Integer.toString(h));
        mMinWeek.setText(Integer.toString(m));

        //mDailyUsage.setText(Integer.toString(h)+"hr "+Integer.toString(m)+"min "+Integer.toString(s)+"sec");

        mButSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsPage = new Intent(MainActivity.this,AppSettingsActivity.class);
                startActivity(settingsPage);
            }
        });


        SensorService mSensorService = new SensorService(getCtx());
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

    @Override
    public void onClick(View v) {
        Intent i = new Intent(v.getContext(), AppWiseSettingActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        switch (v.getId()){
            case R.id.top_app_1:
                i.putExtra("app_name",apkInfoExtractor.GetAppName(top10Apps[0]));
                i.putExtra("package_name",top10Apps[0]);
                break;
            case R.id.top_app_2:
                i.putExtra("app_name",apkInfoExtractor.GetAppName(top10Apps[1]));
                i.putExtra("package_name",top10Apps[1]);
                break;
            case R.id.top_app_3:
                i.putExtra("app_name",apkInfoExtractor.GetAppName(top10Apps[2]));
                i.putExtra("package_name",top10Apps[2]);
                break;
            case R.id.top_app_4:
                i.putExtra("app_name",apkInfoExtractor.GetAppName(top10Apps[3]));
                i.putExtra("package_name",top10Apps[3]);
                break;
            case R.id.top_app_5:
                i.putExtra("app_name",apkInfoExtractor.GetAppName(top10Apps[4]));
                i.putExtra("package_name",top10Apps[4]);
                break;
            case R.id.top_app_6:
                i.putExtra("app_name",apkInfoExtractor.GetAppName(top10Apps[5]));
                i.putExtra("package_name",top10Apps[5]);
                break;
            case R.id.top_app_7:
                i.putExtra("app_name",apkInfoExtractor.GetAppName(top10Apps[6]));
                i.putExtra("package_name",top10Apps[6]);
                break;
            case R.id.top_app_8:
                i.putExtra("app_name",apkInfoExtractor.GetAppName(top10Apps[7]));
                i.putExtra("package_name",top10Apps[7]);
                break;
            case R.id.top_app_9:
                i.putExtra("app_name",apkInfoExtractor.GetAppName(top10Apps[8]));
                i.putExtra("package_name",top10Apps[8]);
                break;
            case R.id.top_app_10:
                i.putExtra("app_name",apkInfoExtractor.GetAppName(top10Apps[9]));
                i.putExtra("package_name",top10Apps[9]);
                break;
            default:
                break;
        }
        v.getContext().startActivity(i);
    }
}
