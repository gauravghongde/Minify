package com.rstack.dephone;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ApkInfoExtractor {

    private final Context context;

    public ApkInfoExtractor(Context context) {
        this.context = context;
    }

    public List<String> getAllInstalledAppPackageNames() {
        List<String> packageNames = new ArrayList<>();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentActivities(intent, 0);

        for (ResolveInfo resolveInfo : resolveInfoList) {
            packageNames.add(resolveInfo.activityInfo.applicationInfo.packageName);
//            TODO: Distinguish Core Service System Apps and User System Apps like Chrome, Youtube, etc
//            if (!isSystemPackage(resolveInfo)) {
//                packageNames.add(resolveInfo.activityInfo.applicationInfo.packageName);
//            }
        }

        return packageNames;
    }

    public boolean isSystemPackage(ResolveInfo resolveInfo) {
        return (resolveInfo.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }

    public Drawable getAppIconByPackageName(String packageName) {
        try {
            return context.getPackageManager().getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            // Use a logging library like Timber here instead of e.printStackTrace()
            e.printStackTrace();
            return ContextCompat.getDrawable(context, R.mipmap.ic_launcher);
        }
    }

    public String getAppName(String packageName) {
        String UNKNOWN = "UNKNOWN APP";
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(packageName, 0);
            return applicationInfo != null ? (String) context.getPackageManager().getApplicationLabel(applicationInfo) : UNKNOWN;
        } catch (PackageManager.NameNotFoundException e) {
            // Use a logging library like Timber here instead of e.printStackTrace()
            e.printStackTrace();
            return UNKNOWN;
        }
    }
}