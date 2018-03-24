package com.rstack.dephone;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;

    private Button mButLogin;
    private Button mButSetting;

    boolean flag;

    Intent mServiceIntent;
    private SensorService mSensorService;

    Context ctx;
    public Context getCtx(){
        return ctx;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Intent i = new Intent(this, AlertView.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);*/
        ctx = this;
        mButLogin = findViewById(R.id.btn_signin);
        mButSetting = findViewById(R.id.settings);

        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!=null){
                    Log.d("auth",user.getUid());

                    //startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));

                    mButSetting.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent settingsPage = new Intent(MainActivity.this,AppSettingsActivity.class);
                            startActivity(settingsPage);
                        }
                    });

                    mSensorService = new SensorService(getCtx());
                    mServiceIntent = new Intent(ctx, mSensorService.getClass());
                    if (!isMyServiceRunning(mSensorService.getClass())) {   //starts if isn't already running
                        startService(mServiceIntent);
                    }


                }
                else{
                    Intent welcomeScreen = new Intent(MainActivity.this, WelcomeActivity.class);
                    //welcomeScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(welcomeScreen);
                }
            }
        };

        Log.i("flag123",Boolean.toString(flag));

        //TODO:Welcome screen
        if(!flag){
            Intent welcomeScreen = new Intent(this, WelcomeActivity.class);
            //welcomeScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(welcomeScreen);
            flag=getIntent().getExtras().getBoolean("flag");
            Log.i("flag123","resume not flag"+Boolean.toString(flag));
        }

        if(flag){
            butSignIn();
            Log.i("flag123",Boolean.toString(flag)+" in else");
        }

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
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
        mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()) {
                    Log.w("Auth", task.getException());
                    Toast.makeText(MainActivity.this,"SignInProblem",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        stopService(mServiceIntent);
        Log.i("MAINACT", "onDestroy!");
        super.onDestroy();
    }
}
