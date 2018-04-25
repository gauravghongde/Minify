package com.rstack.dephone;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class AppWiseSettingActivity extends AppCompatActivity {

    private String pkgName,appName;
    private TextView pkgNameLable;
    private TextView mHourDay,mMinDay,mHourWeek,mMinWeek;
    private ImageView mAppImage;
    private NumberPicker dailyHrLimit,dailyMinLimit;
    private Switch switch1,switch2;
    private SeekBar seekbar;
    private TextView progIndicator;
    private Button mCancelBtn,mApplyBtn;
    private int progressValue = 10;

    MainActivity mainActivity = new MainActivity();
    ApkInfoExtractor apkInfoExtractor = new ApkInfoExtractor(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_wise_setting);

        //--------------------SHARED PREF--------------------------------------

        SharedPreferences dailyLimit = getSharedPreferences("dailyLimit", Context.MODE_PRIVATE);
        final SharedPreferences.Editor dailyLimitEditor = dailyLimit.edit();
        SharedPreferences contLimit = getSharedPreferences("contLimit", Context.MODE_PRIVATE);
        final SharedPreferences.Editor contLimitEditor =contLimit.edit();

        //---------------------------------------------------------------------

        pkgNameLable = findViewById(R.id.app_heading);
        mHourDay = findViewById(R.id.hour_day);
        mMinDay = findViewById(R.id.min_day);
        mHourWeek = findViewById(R.id.hour_week);
        mMinWeek = findViewById(R.id.min_week);
        mAppImage = findViewById(R.id.app_setting_image_view);

        switch1 = findViewById(R.id.switch1);
        switch2 = findViewById(R.id.switch2);

        progIndicator = findViewById(R.id.progress_indicator);

        seekbar = findViewById(R.id.cont_seek_bar);

        dailyHrLimit = findViewById(R.id.daily_hr_limit);
        dailyMinLimit = findViewById(R.id.daily_min_limit);
        //contHrLimit = findViewById(R.id.cont_hr_limit);
        //contMinLimit = findViewById(R.id.cont_min_limit);

        dailyHrLimit.setMaxValue(23);
        dailyMinLimit.setMaxValue(59);
        //dailyHrLimit.setMinValue(0);
        dailyMinLimit.setMinValue(1);
        dailyMinLimit.setValue(10);

        dailyMinLimit.setVisibility(View.INVISIBLE);
        dailyHrLimit.setVisibility(View.INVISIBLE);

        mApplyBtn = findViewById(R.id.apply_btn);
        mCancelBtn = findViewById(R.id.cancel_btn);

        Bundle nameBundle = getIntent().getExtras();
        if(nameBundle!=null){
            pkgName = nameBundle.getString("package_name");
            appName = nameBundle.getString("app_name");
        }

        Drawable drawable = apkInfoExtractor.getAppIconByPackageName(pkgName);
        int totalMinutes=0;

        pkgNameLable.setText(appName);
        mAppImage.setImageDrawable(drawable);

        SharedPreferences spgetDay = getSharedPreferences("dataForDay", Context.MODE_PRIVATE);
        totalMinutes = spgetDay.getInt(pkgName,0);
        mHourDay.setText(Integer.toString(totalMinutes/60));
        mMinDay.setText(Integer.toString((int)totalMinutes%60));

        SharedPreferences spgetWeek = getSharedPreferences("dataForWeek", Context.MODE_PRIVATE);
        totalMinutes = spgetWeek.getInt(pkgName,0);
        mHourWeek.setText(Integer.toString(totalMinutes/60));
        mMinWeek.setText(Integer.toString((int)totalMinutes%60));

        switch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dailyMinLimit.setVisibility(View.VISIBLE);
                dailyHrLimit.setVisibility(View.VISIBLE);
            }
        });

        progIndicator.setText(String.valueOf(progressValue));

        //int progress = seekbar.getProgress();
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressValue = progress*10;
                progIndicator.setText(String.valueOf(progressValue));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //called when user first touches the bar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //progIndicator.setText(progressValue);
                //called after the user finishes moving the bar
            }
        });

        mApplyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int totalDailyLimitMin = dailyHrLimit.getValue()*60+dailyMinLimit.getValue();
                dailyLimitEditor.putInt(pkgName,totalDailyLimitMin);
                Toast.makeText(AppWiseSettingActivity.this,pkgName+": "+totalDailyLimitMin,Toast.LENGTH_SHORT).show();
                dailyLimitEditor.apply();
                contLimitEditor.putInt(pkgName,progressValue);
                Toast.makeText(AppWiseSettingActivity.this,pkgName+": "+progressValue,Toast.LENGTH_SHORT).show();
                contLimitEditor.apply();
            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
