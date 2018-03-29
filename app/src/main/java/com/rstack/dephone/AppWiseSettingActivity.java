package com.rstack.dephone;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class AppWiseSettingActivity extends AppCompatActivity {

    private String pkgName,appName;
    private TextView pkgNameLable;
    private TextView mHourDay,mMinDay;
    private ImageView mAppImage;
    ApkInfoExtractor apkInfoExtractor = new ApkInfoExtractor(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_wise_setting);

        pkgNameLable = findViewById(R.id.app_heading);
        mHourDay = findViewById(R.id.hour_day);
        mMinDay = findViewById(R.id.min_day);
        mAppImage = findViewById(R.id.app_setting_image_view);

        Bundle nameBundle = getIntent().getExtras();
        if(nameBundle!=null){
            pkgName = nameBundle.getString("package_name");
            appName = nameBundle.getString("app_name");
        }

        Drawable drawable = apkInfoExtractor.getAppIconByPackageName(pkgName);

        pkgNameLable.setText(appName);
        mAppImage.setImageDrawable(drawable);

        //dbClass.getHourByPackageName(pkgName);
        //mHourDay.setText(dbClass.getHourByPackageName(pkgName));
        //mMinDay.setText(dbClass.getMinByPackageName(pkgName));
    }
}
