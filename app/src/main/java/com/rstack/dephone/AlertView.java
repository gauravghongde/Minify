package com.rstack.dephone;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class AlertView extends AppCompatActivity {

    TextView mAppName;
    Button mSkipBtn, mCloseBtn;
    Context ctx;
    Intent i = getIntent();
    String appName, pkgName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle nameBundle = getIntent().getExtras();
        pkgName = nameBundle != null ? nameBundle.getString("msg") : "";

        setContentView(R.layout.alert_view);
        mAppName = findViewById(R.id.appName);
        mSkipBtn = findViewById(R.id.skip_alert_btn);
        mCloseBtn = findViewById(R.id.close_app_btn);
        ctx = this;
        final PackageManager pm = getApplicationContext().getPackageManager();
        ApplicationInfo ai;

        if (nameBundle != null) {
            try {
                ai = pm.getApplicationInfo(pkgName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                ai = null;
            }

            appName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");

            Toast.makeText(AlertView.this, appName + ": Time Limit Exceeded!", Toast.LENGTH_SHORT).show();
        }

        mSkipBtn.setOnClickListener(v -> finish());

        mCloseBtn.setOnClickListener(v -> {
            // Update shared preferences to mark the alert as dismissed
            SharedPrefsHelper prefsHelper = SharedPrefsHelper.getInstance(ctx);
            prefsHelper.setAlertDismissed(pkgName, true);

            // Navigate to the home screen
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
            finish();
        });

    }

    @Override
    public void onBackPressed() {
        // Update shared preferences to mark the alert as dismissed
        SharedPrefsHelper prefsHelper = SharedPrefsHelper.getInstance(ctx);
        prefsHelper.setAlertDismissed(pkgName, true);

        // Navigate to the home screen
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAppName.setText(appName);
    }

    public static String getApplicationName(Context context) {
        return context.getApplicationInfo().loadLabel(context.getPackageManager()).toString();
    }

    private boolean isAppInForeground(String packageName) {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

}
