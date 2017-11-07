package com.rayerrt.screenrecord;

import static com.rayerrt.screenrecord.ScreenRecordConstant.NOTI_SCREEN_RECORD_ID;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ScreenRecordService extends Service {


    private static final String TAG = "ScreenRecordService";
    private ScreenRecordManager mScreenRecordManager;

    public ScreenRecordService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mScreenRecordManager = ScreenRecordManager.getInstance(getApplicationContext());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        mScreenRecordManager.startRecord();
        startForgroundScreenRecord();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        mScreenRecordManager.stopRecord();
    }

    private void startForgroundScreenRecord() {
        Intent stopIntent = new Intent(ScreenRecordConstant.ACTION_STOP_SCREEN_RECORD);
        stopIntent.setPackage(getPackageName());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, stopIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .setContentTitle(getResources().getString(R.string.str_screen_record))
                .setAutoCancel(false)
                .setContentText(getResources().getString(R.string.str_screen_recording));
        Notification notification = builder.build();
        startForeground(NOTI_SCREEN_RECORD_ID, notification);
    }
}
