package com.rstack.dephone;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import static android.app.AppOpsManager.MODE_ALLOWED;
import static android.app.AppOpsManager.OPSTR_GET_USAGE_STATS;

import androidx.appcompat.app.AppCompatActivity;

public class UsageCheckActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usage_check);
        Button allowUsage;
        Switch preview;

        allowUsage = findViewById(R.id.usage_activity_launcher);
        preview = findViewById(R.id.preview_switch);
        preview.setChecked(true);

        if(!checkForPermission(UsageCheckActivity.this)){
            allowUsage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                    finish();
                }
            });
        }
        if(checkForPermission(UsageCheckActivity.this)){
            Intent mainActivityIntent = new Intent(UsageCheckActivity.this, MainActivity.class);
            startActivity(mainActivityIntent);
            finish();
        }
    }

    private boolean checkForPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.getPackageName());
        return mode == MODE_ALLOWED;
    }
}
