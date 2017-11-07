package com.rayerrt.screenrecord;

import android.app.Activity;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private static final int PERMISSION_CODE = 1;
    private MediaProjectionManager mProjectionManager;
    private CheckBox mCheckBox;
    private ScreenRecordManager mScreenRecordManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_projection);
        mScreenRecordManager = ScreenRecordManager.getInstance(getApplicationContext());
        mProjectionManager = mScreenRecordManager.setMediaProjectionMgr();
        mCheckBox = (CheckBox) findViewById(R.id.checkbox);
        mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckBoxClicked(v);
            }
        });
        mScreenRecordManager.setRecordStateChangeCallBack(
                new ScreenRecordManager.RecordStateChangeCallBack() {
                    @Override
                    public void onStop() {
                        mCheckBox.setChecked(false);
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: requestCode " + requestCode + ", resultCode " + resultCode);
        if (requestCode != PERMISSION_CODE) {
            Log.e(TAG, "Unknown request code: " + requestCode);
            return;
        }
        if (resultCode != RESULT_OK) {
            Toast.makeText(this,
                    "Screen Cast Permission Denied", Toast.LENGTH_SHORT).show();
            mCheckBox.setChecked(false);
            return;
        }
        mScreenRecordManager.setMediaProjection(
                mProjectionManager.getMediaProjection(resultCode, data));
        Log.d(TAG, "onActivityResult: " + mScreenRecordManager.getMediaProjection());
        Intent intent = new Intent(this, ScreenRecordService.class);
        startService(intent);
    }

    public void onCheckBoxClicked(View view) {
        if (((CheckBox) view).isChecked()) {
            Log.d(TAG, "onCheckBoxClicked: haha");
            requestScreenRecord();
        }
    }

    private void requestScreenRecord() {
        MediaProjection mediaProjection = mScreenRecordManager.getMediaProjection();
        Log.d(TAG, "requestScreenRecord: mMediaProjection: " + mediaProjection);
        if (mediaProjection == null) {
            startActivityForResult(mProjectionManager.createScreenCaptureIntent(), PERMISSION_CODE);
            return;
        }
        Intent intent = new Intent(this, ScreenRecordService.class);
        startService(intent);
    }
}
