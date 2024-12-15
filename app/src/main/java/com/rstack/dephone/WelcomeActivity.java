package com.rstack.dephone;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.app.AppOpsManager.MODE_ALLOWED;
import static android.app.AppOpsManager.OPSTR_GET_USAGE_STATS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    private Button mGetStarted;
    private CheckBox mTnCchkBox;
    private TextView mTermsBtn;

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    Intent usagePermissionCheckActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mGetStarted = findViewById(R.id.get_sarted_btn);
        mTnCchkBox = findViewById(R.id.tandc_chkbox);
        mGetStarted.setEnabled(false);
        mTermsBtn = findViewById(R.id.termsWebsiteLabel);

        usagePermissionCheckActivity = new Intent(WelcomeActivity.this, UsageCheckActivity.class);

        mTermsBtn.setOnClickListener(v -> {
            Intent webintent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://gauravghongde.github.io/portfolio/Minify/terms"));
            startActivity(webintent);
        });

        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if(user!=null){
                Log.d("auth",user.getUid());
                usagePermissionCheckActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(usagePermissionCheckActivity);
                finish();
            }
        };

        if(!checkForPermission(WelcomeActivity.this)){
//            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }

        mTnCchkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mGetStarted.setEnabled(isChecked);
            }
        });

        mGetStarted.setOnClickListener(v -> butSignIn());

    }


    private boolean checkForPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.getPackageName());
        return mode == MODE_ALLOWED;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthStateListener);
    }

    public void butSignIn(){
        mAuth.signInAnonymously().addOnCompleteListener(this, task -> {
            if(!task.isSuccessful()) {
                Log.w("Auth", task.getException());
                Toast.makeText(WelcomeActivity.this,"SignInProblem",Toast.LENGTH_LONG).show();
            }
            else {
                startActivity(usagePermissionCheckActivity);
                finish();
            }
        });
    }
}
