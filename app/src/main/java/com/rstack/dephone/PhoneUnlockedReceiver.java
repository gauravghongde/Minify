package com.rstack.dephone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


/**
 * Created by Admin on 3/25/2018.
 */

public class PhoneUnlockedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
            Log.d("captainlock", "Phone unlocked");
        }
        if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
                Log.d("captainlock", "Phone unlocked");
        }
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
            Log.d("captainlock", "Phone locked");
        }
    }
}
