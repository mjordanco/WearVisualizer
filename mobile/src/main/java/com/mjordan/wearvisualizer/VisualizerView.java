package com.mjordan.wearvisualizer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.audiofx.Visualizer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Random;

/**
 * Created by matt on 9/16/14.
 */
public class VisualizerView extends View {

    private Random rand;
    private Visualizer mVisualizer;
    private byte[] mBytes;
    private float[] mPoints;
    private Rect mRect;
    private Paint mForePaint;

    private long points = 1;

    private long sum = 0;
    private long sumSquares = 0;

    private float average = 0;

    private boolean setDefault;
    private float std_dev;
    private float[] mFFTPoints;
    private byte[] data;
    private int mDivisions = 2;
    public VisualizerView(Context context) {
        super(context);
        initialize();
    }

    public VisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public VisualizerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    public void initialize() {
        rand = new Random();
        mRect = new Rect();
        mForePaint = new Paint();
        mForePaint.setColor(Color.BLUE);

        mVisualizer = new Visualizer(0);
        mVisualizer.setScalingMode(Visualizer.SCALING_MODE_AS_PLAYED);
        mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            @Override
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
            }

            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
                data = fft;
                invalidate();
            }
        }, Visualizer.getMaxCaptureRate() / 2, true, true);

        mVisualizer.setEnabled(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mFFTPoints == null || mFFTPoints.length < data.length * 4) {
            mFFTPoints = new float[data.length * 4];
        }

        for (int i = 0; i < data.length / mDivisions; i++) {
            mFFTPoints[i * 4] = i * 4 * mDivisions;
            mFFTPoints[i * 4 + 2] = i * 4 * mDivisions;
            byte rfk = data[mDivisions * i];
            byte ifk = data[mDivisions * i + 1];
            float magnitude = (rfk * rfk + ifk * ifk);
            int dbValue = (int) (10 * Math.log10(magnitude));

            mFFTPoints[i * 4 + 1] = 0;
            mFFTPoints[i * 4 + 3] = (dbValue * 200 - 10);
        }

        canvas.drawLines(mFFTPoints, mForePaint);
    }

}
