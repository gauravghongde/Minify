package com.rstack.dephone;

import static android.app.AppOpsManager.MODE_ALLOWED;
import static android.app.AppOpsManager.OPSTR_GET_USAGE_STATS;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;

import static android.app.AppOpsManager.MODE_ALLOWED;
import static android.app.AppOpsManager.OPSTR_GET_USAGE_STATS;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private static final int MAX_TOP_APPS = 10;

    private Button mButSetting;
    private Switch alertPauseSwitch;
    private TextView mHourDay,mMinDay,mHourWeek,mMinWeek,nse_exception;
    DatabaseClass dbClass = new DatabaseClass();
    Intent mServiceIntent;
    Intent mNotificationDrawer;
    ApkInfoExtractor apkInfoExtractor = new ApkInfoExtractor(this);
    ImageButton[] ib = new ImageButton[10];
    String [] top10Apps = new String [10];
    TreeMap<Long,String> treeMap = new TreeMap<>();

    public ActionBarDrawerToggle mDrawerToggle;
    DrawerLayout drawer;
    NavigationView navigationView;

    Context ctx;

    public Context getCtx(){
        return ctx;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!checkForPermission(MainActivity.this)){
            Intent UsageCheckAct = new Intent(MainActivity.this, UsageCheckActivity.class);
            startActivity(UsageCheckAct);
            finish();
        }

        toggleActionBar();

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

        final SharedPreferences dailyLimit = getSharedPreferences("dailyLimit", Context.MODE_PRIVATE);
        final SharedPreferences.Editor dailyLimitEditor = dailyLimit.edit();
        SharedPreferences contLimit = getSharedPreferences("contLimit", Context.MODE_PRIVATE);
        SharedPreferences.Editor contLimitEditor =contLimit.edit();

        //--------------------------------------------------------------------------------------------

        //--------------------SHARED PREF-------------------------------------------------------------

        final SharedPreferences spForPauseSwitch = getSharedPreferences("spForPauseSwitch", Context.MODE_PRIVATE);
        final SharedPreferences.Editor spPauseSwitchEditor = spForPauseSwitch.edit();

        //---------------------------------------------------------------------------------------------

        PhoneUnlockedReceiver receiver = new PhoneUnlockedReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(receiver, filter);

        ctx = this;

        alertPauseSwitch = findViewById(R.id.mainSwitch);
        nse_exception = findViewById(R.id.top_ten_exception_txt);
        //mButSetting = findViewById(R.id.settings);
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

        //TimeZone tz = TimeZone.getDefault();
        //String gmt1=TimeZone.getTimeZone(tz.getID()).getDisplayName(false,TimeZone.SHORT);
        //String gmt2=TimeZone.getTimeZone(tz.getID()).getDisplayName(false,TimeZone.LONG);
        Log.d("currenttime","Time : "+time);

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

                int userData1 = dailyLimit.getInt(PackageName, Integer.MAX_VALUE);
                if((userData1 == Integer.MAX_VALUE)){
                    //not been saved...
                    dailyLimitEditor.putInt(PackageName,Integer.MAX_VALUE);
                    dailyLimitEditor.apply();
                }
                else{
                    // already there...
                }

                int userData2 = contLimit.getInt(PackageName, Integer.MAX_VALUE);
                if((userData1 == Integer.MAX_VALUE)){
                    //not been saved...
                    contLimitEditor.putInt(PackageName,Integer.MAX_VALUE);
                    contLimitEditor.apply();
                }
                else{
                    // already there...
                }

                /////////////////////////////////////////////////////////////////////////////////

                treeMap.put(TimeInforground,PackageName);

                dbClass.setAppListTodaysTimings(PackageName,hours,minutes);
                Log.i("BAC123", "PackageName is" + PackageName + "Time is: " + hours + "h" + ":" + minutes + "m" + seconds + "s");
            }
        }

        mHourDay.setText(Integer.toString(h));
        mMinDay.setText(Integer.toString(m));

        try {
            Drawable drawable;
            nse_exception.setVisibility(View.GONE);
            //For Top 10 app list on the basis of daily usage
            Iterator<Long> iterator = treeMap.descendingKeySet().iterator();
            for (int i = 0; i <= 9; i++) {
                ib[i].setVisibility(View.VISIBLE);
                Long mapEntry = iterator.next();
                Log.d("iterator123", mapEntry.toString() + " " + treeMap.get(mapEntry));
                drawable = apkInfoExtractor.getAppIconByPackageName(treeMap.get(mapEntry));
                ib[i].setImageDrawable(drawable);
                ib[i].setOnClickListener(MainActivity.this);
                top10Apps[i] = treeMap.get(mapEntry);
            }

        }
        catch (NoSuchElementException nse){
            nse_exception.setVisibility(View.VISIBLE);
            for (int i = 0; i <= 9; i++) {
                ib[i].setVisibility(View.GONE);
            }
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

        int isSwitchChecked = spForPauseSwitch.getInt("chkStatus", -1);
        if((isSwitchChecked == -1)){
            //not been saved...
            alertPauseSwitch.setChecked(false);
            spPauseSwitchEditor.putInt("chkStatus",0);
            spPauseSwitchEditor.apply();
        }
        else{
            if(isSwitchChecked==1){
                alertPauseSwitch.setChecked(true);
                spPauseSwitchEditor.putInt("chkStatus",1);
                spPauseSwitchEditor.apply();
            }
            else{
                alertPauseSwitch.setChecked(false);
                spPauseSwitchEditor.putInt("chkStatus",0);
                spPauseSwitchEditor.apply();
            }
        }



        alertPauseSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(alertPauseSwitch.isChecked()){
                    spPauseSwitchEditor.putInt("chkStatus",1);
                    spPauseSwitchEditor.apply();
                }
                else{
                    spPauseSwitchEditor.putInt("chkStatus",0);
                    spPauseSwitchEditor.apply();
                }
            }
        });

        /*mButSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsPage = new Intent(MainActivity.this,AppSettingsActivity.class);
                startActivity(settingsPage);
            }
        });*/


        SensorService mSensorService = new SensorService(getCtx());
        mServiceIntent = new Intent(ctx, mSensorService.getClass());
        if (!isMyServiceRunning(mSensorService.getClass())) {   //starts if isn't already running
            startService(mServiceIntent);
        }

        //mNotificationDrawer = new Intent(this.getCtx(), NotificationDrawerActivity.class);

    }

    private void toggleActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                drawerView.bringToFront();
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();
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
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private boolean checkForPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.getPackageName());
        return mode == MODE_ALLOWED;
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

        int[] viewIds = new int[MAX_TOP_APPS];
        for (int j = 0; j < MAX_TOP_APPS; j++) {
            viewIds[j] = getResources().getIdentifier("top_app_" + (j + 1), "id", getPackageName());
        }

        for (int j = 0; j < MAX_TOP_APPS; j++) {
            if (v.getId() == viewIds[j]) {
                i.putExtra("app_name", apkInfoExtractor.getAppName(top10Apps[j]));
                i.putExtra("package_name", top10Apps[j]);
                break;
            }
        }

        v.getContext().startActivity(i);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        drawer.closeDrawer(GravityCompat.START);
        if (id == R.id.nav_applist) {
            navigateToAppSettings();
        } else if (id == R.id.nav_feedback) {
            sendFeedbackEmail();
        } else if (id == R.id.nav_share) {
            shareApp();
        } else if (id == R.id.nav_git) {
            openGithubRepo();
        } else if (id == R.id.nav_website) {
            openPersonalWebsite();
        }
        return true;
    }

// Helper methods

    private void navigateToAppSettings() {
        Intent intent = new Intent(MainActivity.this, AppSettingsActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendFeedbackEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"777gaurav.g7@gmail.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Minify - Feedback");
        emailIntent.setType("text/plain");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "\n");
        resolveEmailIntent(emailIntent);
        startActivity(emailIntent);
    }

    private void resolveEmailIntent(Intent emailIntent) {
        final PackageManager pm = getPackageManager();
        final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
        ResolveInfo best = null;
        for (final ResolveInfo info : matches)
            if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
                best = info;
        if (best != null)
            emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
    }

    private void shareApp() {
        String textToSend = "Hey There! I am using this awesome app called 'Minify'. " +
                "It helps to reduce Smartphone usage, by providing App Usage Alerts and Usage Statistics. " +
                "We can set Timers on every app and restrict our daily usage. You could save a lot of precious time " +
                "and focus on essentials, Download it now! \n\nGet the App from - " +
                "https://play.google.com/store/apps/details?id=com.rstack.dephone&hl=en";
        Intent intentShare = new Intent();
        intentShare.setAction(Intent.ACTION_SEND);
        intentShare.putExtra(Intent.EXTRA_TEXT, textToSend);
        intentShare.setType("text/plain");
        startActivity(Intent.createChooser(intentShare, "Share via - "));
    }

    private void openGithubRepo() {
        Intent webintent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/gauravghongde/Minify"));
        startActivity(webintent);
    }

    private void openPersonalWebsite() {
        Intent webintent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://gauravghongde.github.io/portfolio/"));
        startActivity(webintent);
    }
}
