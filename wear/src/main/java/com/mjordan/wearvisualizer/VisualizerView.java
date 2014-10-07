package com.mjordan.wearvisualizer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;

/**
 * Created by matt on 9/18/14.
 */
public class VisualizerView extends View implements VisualizerListenerService.WearVisualizerMessageCallback {
    private byte[] mWaveformData;
    private float[] mPoints;
    private float[] mPeaks;
    private Paint mPaint;
    private byte[] mFftData;

    private float hue;
    private int timeSize = 1024;
    private float sampleRate = 440000;
    private float[] mBands;

    public VisualizerView(Context context) {
        super(context);

        VisualizerListenerService.setWearVisualizerMessageCallback(this);

        mWaveformData = new byte[0];
        mFftData = new byte[0];
        mBands = new float[12];
        mPaint = new Paint();
        mPaint.setColor(Color.CYAN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);

        hue = 0;

        setKeepScreenOn(true);

    }

    @Override
    public void onWaveformDataReceived(byte[] data) {
        mWaveformData = data;

        postInvalidate();
    }

    @Override
    public void onKillMessageReceived() {
        if (getContext() instanceof Activity) {
            ((Activity) getContext()).finish();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        hue += .1f;

        int color = Color.HSVToColor(new float[] {(hue) % 360, 1.0f, .1f});
        int inverseColor = Color.HSVToColor(new float[] {(hue + 180) % 360, 1.0f, 1f});

        canvas.drawColor(color);
        mPaint.setColor(inverseColor);

        if (mPoints == null || mPoints.length < mWaveformData.length * 4) {
            mPoints = new float[mWaveformData.length * 4];
            Log.d("VisualizerView", "Had to expand the float array");
        }

        // Calculate points for line
        for (int i = 0; i < mWaveformData.length - 1; i++) {
            int current = (int) mWaveformData[i];
            current = (current + 256) % 256;
            int next = (int) mWaveformData[i + 1];
            next = (next + 256) % 256;
            mPoints[i * 4] = getWidth() * i / (mWaveformData.length - 1);
            mPoints[i * 4 + 1] =  getHeight() / 2
                    + (current) / 128.0f * (getHeight()) / 2
                    - 128 / 128.0f * getHeight() / 2;
            mPoints[i * 4 + 2] = getWidth() * (i + 1) / (mWaveformData.length - 1);
            mPoints[i * 4 + 3] = getHeight() / 2
                    + (next) / 128.0f * (getHeight()) / 2
                    - 128 / 128.0f * getHeight() / 2;
        }

        canvas.drawLines(mPoints, mPaint);

    }
}
