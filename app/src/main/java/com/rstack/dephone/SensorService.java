package com.rstack.dephone;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import static android.app.Service.START_STICKY;

import androidx.annotation.Nullable;

public class SensorService extends Service {
    public int mcounter=0;
    public int counter=0;
    private long DailyTimeStamp;
    String PackageName,ChkPackageName;
    long TimeInforground;
    int minutes,seconds,hours,h=0,m=0,s=0;
    long time = System.currentTimeMillis();
    private SharedPreferences dailyLimit;
    private SharedPreferences contLimit;
    //ActivityManager mActivityManager =(ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);

    public SensorService(Context applicationContext) {
        super();
        Log.i("HERE", "here I am!");
    }

    public SensorService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        //int counter=0;

        //Toast.makeText(SensorService.this,"started",Toast.LENGTH_LONG).show();

        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                getCurrApp();
                //Toast.makeText(SensorService.this,"5 sec has passed",Toast.LENGTH_SHORT).show();
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        Thread.sleep(8000);
                        handler.sendEmptyMessage(0);
                    }
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        /*Intent i = new Intent(this, AlertView.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("msg",str);
        startActivity(i);*/

        /*ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List< ActivityManager.AppTask > taskInfo = am.getAppTasks();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d("topActivity", "CURRENT Activity ::"
                    + taskInfo.get(0).getTaskInfo().topActivity.getClassName());
        }
        ComponentName componentInfo = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            componentInfo = taskInfo.get(0).getTaskInfo().topActivity;
        }
        componentInfo.getPackageName();*/

        //counter = startTimer(counter);
        return START_STICKY;
    }

    private void getCurrApp() {

        /*ActivityManager am = (ActivityManager) SensorService.this.getSystemService(ACTIVITY_SERVICE);
        // The first in the list of RunningTasks is always the foreground task.
        ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
        String foregroundTaskPackageName = foregroundTaskInfo .topActivity.getPackageName();
        PackageManager pm = SensorService.this.getPackageManager();
        PackageInfo foregroundAppPackageInfo = null;
        try {
            foregroundAppPackageInfo = pm.getPackageInfo(foregroundTaskPackageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }*/

        //UsageStats usageStats;


        PackageName = "Nothing" ;

        UsageStatsManager mUsageStatsManager = (UsageStatsManager)getSystemService(this.USAGE_STATS_SERVICE);
        long time = System.currentTimeMillis();

        List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000*10, time);

        final UsageEvents usageEvents = mUsageStatsManager.queryEvents(time - 1000*10, time);
        while (usageEvents.hasNextEvent()) {
            mcounter = 0;
            UsageEvents.Event event = new UsageEvents.Event();
            usageEvents.getNextEvent(event);
            switch (event.getEventType()) {
                case UsageEvents.Event.MOVE_TO_FOREGROUND:
                    PackageName = event.getPackageName();
                    DailyTimeStamp = event.getTimeStamp();
                    break;
                /*case UsageEvents.Event.MOVE_TO_BACKGROUND:
                    if (event.getPackageName().equals(PackageName)) {
                        PackageName = null;
                    }*/
            }

        }
        if(!usageEvents.hasNextEvent() && !PackageName.equalsIgnoreCase("com.rstack.dephone")
                ){
            //Log.i("BAC123", "PackageName is " + PackageName + "is running");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mcounter++;
            //Log.i("BAC123456", "counter: "+ mcounter);
            if(mcounter>=10){
                Intent i = new Intent(this, AlertView.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("msg",PackageName);
                startActivity(i);
            }
        }
        if(stats != null) {
            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
            for (UsageStats usageStats : stats) {
                TimeInforground = usageStats.getTotalTimeInForeground();
                ChkPackageName = usageStats.getPackageName();
                if(ChkPackageName.equalsIgnoreCase(PackageName)) {
                    //Log.i("pkg_name", "PackageName is" + PackageName);

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

                    //Log.i("BAC123", "PackageName is " + ChkPackageName + " Time is: " + hours + "h" + ":" + minutes + "m" + seconds + "s");
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
                    //--------------------SHARED PREF--------------------------------------
                    dailyLimit = getSharedPreferences("dailyLimit", Context.MODE_PRIVATE);
                    contLimit = getSharedPreferences("contLimit", Context.MODE_PRIVATE);
                    //---------------------------------------------------------------------

                    if(dailyLimit.getInt(PackageName,Integer.MAX_VALUE)<hours*60+minutes){
                        Intent i = new Intent(this, AlertView.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.putExtra("msg",PackageName);
                        startActivity(i);
                        //Toast.makeText(SensorService.this,PackageName+": Exceeded!!!",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

       /* List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000*10, time);

        if(stats != null) {
            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
            for (UsageStats usageStats : stats) {

                TimeInforground = usageStats.getTotalTimeInForeground();
                PackageName = usageStats.getPackageName();

                minutes = (int) ((TimeInforground / (1000 * 60)) % 60);
                seconds = (int) (TimeInforground / 1000) % 60;
                hours = (int) ((TimeInforground / (1000 * 60 * 60)) % 24);

                if(PackageName.equalsIgnoreCase("com.facebook.katana")){
                    Log.i("BAC123456", "PackageName is " + PackageName + "Time is: " + hours + "h" + ":" + minutes + "m" + seconds + "s");
                    int k = usageEvents.getEventType();
                    if(k==2){
                        Log.i("BAC123456", "PackageName is " + PackageName + "is in BackGround");
                    }
                    else if(k==1){
                        Log.i("BAC123456", "PackageName is " + PackageName + "is Opened");
                    }
                    else{
                        Log.i("BAC123456", "PackageName is " + PackageName + "is i d k");
                    }
            }

            }
        }*/


            //String foregroundTaskAppName = foregroundAppPackageInfo.applicationInfo.loadLabel(pm).toString();
        //Toast.makeText(SensorService.this,foregroundTaskAppName,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
        Intent broadcastIntent = new Intent("com.rstack.dephone.RestartSensor");
        sendBroadcast(broadcastIntent);
        stoptimertask();
    }

    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    public int startTimer(int counter) {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        counter = initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000); //
        return counter;
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public int initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.i("in timer", "in timer ++++  "+ (counter++));
            }
        };
        return counter;
    }


    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
