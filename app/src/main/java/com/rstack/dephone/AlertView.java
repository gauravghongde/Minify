package com.rstack.dephone;

import static android.content.Intent.getIntent;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AlertView extends AppCompatActivity {

    TextView mAppName;
    Button mSkipBtn,mCloseBtn;
    Context ctx;
    Intent i=getIntent();
    String appName,pkgName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_view);
        mAppName = findViewById(R.id.appName);
        mSkipBtn = findViewById(R.id.skip_alert_btn);
        mCloseBtn = findViewById(R.id.close_app_btn);
        ctx=this;
        final PackageManager pm = getApplicationContext().getPackageManager();
        ApplicationInfo ai;

        Bundle nameBundle = getIntent().getExtras();
        if(nameBundle!=null){
            pkgName = nameBundle.getString("msg");
            try{
                ai = pm.getApplicationInfo(pkgName,0);
            } catch (PackageManager.NameNotFoundException e) {
                ai = null;
            }

            appName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");

            Toast.makeText(AlertView.this,appName+": Exceeded!!!",Toast.LENGTH_SHORT).show();
        }

        mSkipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
                //finishAffinity();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAppName.setText(appName);
    }

    public static String getApplicationName(Context context) {
        return context.getApplicationInfo().loadLabel(context.getPackageManager()).toString();
    }

}
