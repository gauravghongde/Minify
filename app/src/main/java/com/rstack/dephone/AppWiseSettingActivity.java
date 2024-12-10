package com.rstack.dephone;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AppWiseSettingActivity extends AppCompatActivity {

    private String pkgName, appName;
    private TextView pkgNameLable;
    private TextView mHourDay, mMinDay, mHourWeek, mMinWeek;
    private ImageView mAppImage;
    private NumberPicker dailyHrLimit, dailyMinLimit;
    private Switch dailyUsageLimitSwitch, continuousUsageLimitSwitch;
    private SeekBar seekbar;
    private TextView progIndicator;
    private Button mCancelBtn, mApplyBtn;
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
        final SharedPreferences.Editor contLimitEditor = contLimit.edit();

        //---------------------------------------------------------------------

        pkgNameLable = findViewById(R.id.app_heading);
        mHourDay = findViewById(R.id.hour_day);
        mMinDay = findViewById(R.id.min_day);
        mHourWeek = findViewById(R.id.hour_week);
        mMinWeek = findViewById(R.id.min_week);
        mAppImage = findViewById(R.id.app_setting_image_view);

        dailyUsageLimitSwitch = findViewById(R.id.dailyUsageLimitSwitch);
        continuousUsageLimitSwitch = findViewById(R.id.continuousUsageLimitSwitch);

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

        //dailyMinLimit.setVisibility(View.INVISIBLE);
        //dailyHrLimit.setVisibility(View.INVISIBLE);

        mApplyBtn = findViewById(R.id.apply_btn);
        mCancelBtn = findViewById(R.id.cancel_btn);

        Bundle nameBundle = getIntent().getExtras();
        if (nameBundle != null) {
            pkgName = nameBundle.getString("package_name");
            appName = nameBundle.getString("app_name");
        }

        Drawable drawable = apkInfoExtractor.getAppIconByPackageName(pkgName);
        int totalMinutes = 0;

        pkgNameLable.setText(appName);
        mAppImage.setImageDrawable(drawable);

        if (dailyLimit.getInt(pkgName, Integer.MAX_VALUE) == Integer.MAX_VALUE) { //LIMIT NOT SET
            dailyUsageLimitSwitch.setChecked(false);
            dailyMinLimit.setEnabled(false);
            dailyHrLimit.setEnabled(false);
        } else {
            dailyUsageLimitSwitch.setChecked(true);
            dailyMinLimit.setEnabled(true);
            dailyHrLimit.setEnabled(true);
            dailyHrLimit.setValue(dailyLimit.getInt(pkgName, Integer.MAX_VALUE) / 60);
            dailyMinLimit.setValue(dailyLimit.getInt(pkgName, Integer.MAX_VALUE) % 60);
        }

        if (contLimit.getInt(pkgName, Integer.MAX_VALUE) == Integer.MAX_VALUE) { //LIMIT NOT SET
            continuousUsageLimitSwitch.setChecked(false);
            seekbar.setEnabled(false);
        } else {
            continuousUsageLimitSwitch.setChecked(true);
            seekbar.setEnabled(true);
            seekbar.setProgress(contLimit.getInt(pkgName, Integer.MAX_VALUE));
        }

        SharedPreferences spgetDay = getSharedPreferences("dataForDay", Context.MODE_PRIVATE);
        totalMinutes = spgetDay.getInt(pkgName, 0);
        mHourDay.setText(Integer.toString(totalMinutes / 60));
        mMinDay.setText(Integer.toString((int) totalMinutes % 60));

        SharedPreferences spgetWeek = getSharedPreferences("dataForWeek", Context.MODE_PRIVATE);
        totalMinutes = spgetWeek.getInt(pkgName, 0);
        mHourWeek.setText(Integer.toString(totalMinutes / 60));
        mMinWeek.setText(Integer.toString((int) totalMinutes % 60));

        dailyUsageLimitSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dailyUsageLimitSwitch.isChecked()) {
                    dailyMinLimit.setEnabled(false);
                    dailyHrLimit.setEnabled(false);
                    dailyHrLimit.setValue(0);
                    dailyMinLimit.setValue(10);
                } else {
                    dailyMinLimit.setEnabled(true);
                    dailyHrLimit.setEnabled(true);
                }
            }
        });

        continuousUsageLimitSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!continuousUsageLimitSwitch.isChecked()) {
                    seekbar.setEnabled(false);
                    progressValue = 1;
                } else {
                    seekbar.setEnabled(true);
                }
            }
        });

        progIndicator.setText(String.valueOf(progressValue));

        //int progress = seekbar.getProgress();
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressValue = progress * 10;
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
                if (dailyUsageLimitSwitch.isChecked()) {
                    int totalDailyLimitMin = dailyHrLimit.getValue() * 60 + dailyMinLimit.getValue();
                    dailyLimitEditor.putInt(pkgName, totalDailyLimitMin);
                    Toast.makeText(AppWiseSettingActivity.this, appName + ": " + totalDailyLimitMin, Toast.LENGTH_SHORT).show();
                    dailyLimitEditor.apply();
                } else {
                    int totalDailyLimitMin = Integer.MAX_VALUE;
                    dailyLimitEditor.putInt(pkgName, totalDailyLimitMin);
//                    Toast.makeText(AppWiseSettingActivity.this, appName + ": " + totalDailyLimitMin, Toast.LENGTH_SHORT).show();
                    dailyLimitEditor.apply();
                }

                if (continuousUsageLimitSwitch.isChecked()) {
                    contLimitEditor.putInt(pkgName, progressValue);
                    Toast.makeText(AppWiseSettingActivity.this, appName + ": " + progressValue, Toast.LENGTH_SHORT).show();
                    contLimitEditor.apply();
                } else {
                    contLimitEditor.putInt(pkgName, Integer.MAX_VALUE);
//                    Toast.makeText(AppWiseSettingActivity.this, appName + ": " + progressValue, Toast.LENGTH_SHORT).show();
                    contLimitEditor.apply();
                }

                finish();
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
