package com.rstack.dephone;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import static android.app.AppOpsManager.MODE_ALLOWED;
import static android.app.AppOpsManager.OPSTR_GET_USAGE_STATS;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class UsageCheckActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_OVERLAY_PERMISSION = 101;
    private static final int REQUEST_CODE_USAGE_ACCESS = 102;
    private Button allowUsageBtn;
    private Button overlayUsageAccessBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usage_check);
        Switch preview;

        allowUsageBtn = findViewById(R.id.usage_activity_launcher);
        overlayUsageAccessBtn = findViewById(R.id.overlay_access_launcher);
        preview = findViewById(R.id.preview_switch);
        preview.setChecked(true);

        if(!checkForAllRequiredPermissions(UsageCheckActivity.this)){
            allowUsageBtn.setOnClickListener(v -> {
                startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS,
                        Uri.parse("package:" + UsageCheckActivity.this.getPackageName())), REQUEST_CODE_USAGE_ACCESS);
                jumpToMainActivityIfPermissionGiven();
            });
            overlayUsageAccessBtn.setOnClickListener(v -> {
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + UsageCheckActivity.this.getPackageName())), REQUEST_CODE_OVERLAY_PERMISSION);
                jumpToMainActivityIfPermissionGiven();
            });
        }
        jumpToMainActivityIfPermissionGiven();
    }

    private void jumpToMainActivityIfPermissionGiven() {
        if(checkForAllRequiredPermissions(UsageCheckActivity.this)){
            Intent mainActivityIntent = new Intent(UsageCheckActivity.this, MainActivity.class);
            startActivity(mainActivityIntent);
            finish();
        }
    }

    private boolean checkForAppUsageAccessPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.getPackageName());
        return mode == MODE_ALLOWED;
    }

    private boolean checkForAllRequiredPermissions(Context context) {
        return checkForAppUsageAccessPermission(context)
                && Settings.canDrawOverlays(UsageCheckActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_USAGE_ACCESS) {
            if (checkForAppUsageAccessPermission(this)) { // Check if permission is granted
                disableBtn(allowUsageBtn);
            } else {
                Toast.makeText(UsageCheckActivity.this,"Failed while trying to provide access! Try Again!!", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CODE_OVERLAY_PERMISSION) {
            if (Settings.canDrawOverlays(this)) {
                disableBtn(overlayUsageAccessBtn);
            } else {
                Toast.makeText(UsageCheckActivity.this,"Failed while trying to provide access! Try Again!!", Toast.LENGTH_SHORT).show();
            }
        }
        jumpToMainActivityIfPermissionGiven();
    }

    private void disableBtn(Button btn) {
        btn.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
        btn.setTextColor(ContextCompat.getColor(this, android.R.color.secondary_text_light));
        btn.setEnabled(false);
    }
}
