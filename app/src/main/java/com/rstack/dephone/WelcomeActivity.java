package com.rstack.dephone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.rstack.dephone.MainActivity;

public class WelcomeActivity extends AppCompatActivity {

    private Button mGetStarted;
    private CheckBox mTnCchkBox;
    boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mGetStarted = findViewById(R.id.get_sarted_btn);
        mTnCchkBox = findViewById(R.id.tandc_chkbox);
        mGetStarted.setEnabled(false);

        mTnCchkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mGetStarted.setEnabled(true);
                }
                else{
                    mGetStarted.setEnabled(false);
                }
            }
        });

        mGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                flag = true;
                Intent i = new Intent();
                i.putExtra("flag",flag);
                setResult(RESULT_OK,i);
                finish();
            }
        });

    }
}
