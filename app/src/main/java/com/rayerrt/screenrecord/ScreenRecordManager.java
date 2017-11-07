package com.rayerrt.screenrecord;

import static com.rayerrt.screenrecord.ScreenRecordConstant.DISPLAY_HEIGHT;
import static com.rayerrt.screenrecord.ScreenRecordConstant.DISPLAY_WIDTH;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import java.io.IOException;

public class ScreenRecordManager {
    private static final String TAG = "ScreenRecordManager";

    public static ScreenRecordManager sScreenRecordManager;
    private final Context mContext;
    private MediaProjectionManager mProjectionManager;
    private MediaProjection mMediaProjection;
    private MediaProjectionCallback mMediaProjectionCallback;
    private boolean isRecording = false;
    private MediaRecorder mMediaRecorder;
    private int mScreenDensity;
    private VirtualDisplay mVirtualDisplay;

    public interface RecordStateChangeCallBack {
        void onStop();
    }

    private RecordStateChangeCallBack mRecordStateChangeCallBack;

    public void setRecordStateChangeCallBack(RecordStateChangeCallBack callBack) {
        mRecordStateChangeCallBack = callBack;
    }

    private ScreenRecordManager(Context context) {
        mContext = context;
        initRecordParams();
    }

    public static ScreenRecordManager getInstance(Context context) {
        if (sScreenRecordManager == null) {
            sScreenRecordManager = new ScreenRecordManager(context);
        }
        return sScreenRecordManager;
    }

    public void release() {
        releaseMediaProjection();
    }

    public MediaProjectionManager setMediaProjectionMgr() {
        if (mProjectionManager == null) {
            mProjectionManager = (MediaProjectionManager) mContext.getSystemService(
                    Context.MEDIA_PROJECTION_SERVICE);
        }
        return mProjectionManager;
    }

    public void setMediaProjection(MediaProjection mediaProjection) {
        mMediaProjection = mediaProjection;
    }

    public MediaProjection getMediaProjection() {
        return mMediaProjection;
    }

    public void setMediaProjectionCallback() {
        mMediaProjectionCallback = new MediaProjectionCallback();
    }

    public MediaProjectionCallback getMediaProjectionCallback() {
        return mMediaProjectionCallback;
    }

    private void prepareRecorder() {
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.w(TAG, "prepareRecorder: " + e);
        } catch (IOException e) {
            Log.w(TAG, "prepareRecorder: " + e);
        }
    }

    public void initRecordParams() {
        WindowManager windowManager = (WindowManager) mContext.getSystemService(
                Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        mScreenDensity = metrics.densityDpi;
        if (mMediaRecorder == null) {
            mMediaRecorder = new MediaRecorder();
            initMediaRecorder();
        }
        prepareRecorder();
        mMediaProjectionCallback = new MediaProjectionCallback();
    }

    public void initMediaRecorder() {
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mMediaRecorder.setVideoEncodingBitRate(ScreenRecordConstant.BIT_RATE);
        mMediaRecorder.setVideoFrameRate(ScreenRecordConstant.FRAME_RATE);
        mMediaRecorder.setVideoSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);
        mMediaRecorder.setOutputFile("/sdcard/capture.mp4");
    }

    public void startRecord() {
        mVirtualDisplay = createVirtualDisplay();
        mMediaRecorder.start();
    }

    public void stopRecord() {
        if (mMediaRecorder != null) {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
        }
        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
        }
        mMediaProjection = null;
    }

    private VirtualDisplay createVirtualDisplay() {
        return mMediaProjection.createVirtualDisplay("ScreenRecord",
                DISPLAY_WIDTH, DISPLAY_HEIGHT, mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mMediaRecorder.getSurface(), null /*Callbacks*/, null /*Handler*/);
    }

    public void releaseMediaProjection() {
        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
    }

    private class MediaProjectionCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            Log.d(TAG, "onStop");
            stopRecord();
            mMediaProjectionCallback.onStop();
        }
    }
}
