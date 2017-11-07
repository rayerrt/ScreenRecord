package com.rayerrt.screenrecord.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.rayerrt.screenrecord.ScreenRecordConstant;

public class StopRecordReceiver extends BroadcastReceiver {

    private static final String TAG = "StopRecordReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        if (context == null || intent == null) {
            return;
        }
        Intent screenRecordService = new Intent(ScreenRecordConstant.ACTION_START_SCREEN_RECORD);
        screenRecordService.setPackage("com.rayerrt.screenrecord");
        context.stopService(screenRecordService);
    }
}
