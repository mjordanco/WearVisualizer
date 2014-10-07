package com.mjordan.wearvisualizer;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by matt on 9/18/14.
 */
public class VisualizerListenerService extends WearableListenerService {

    private static WearVisualizerMessageCallback wearVisualizerMessageCallback;

    public static void setWearVisualizerMessageCallback(WearVisualizerMessageCallback waveformDataReceivedListener) {
        VisualizerListenerService.wearVisualizerMessageCallback = waveformDataReceivedListener;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("VisualizerListenerService", "I got unbound :(");

        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
//        Log.d("VisualizerListenerService", "Received Message!");

        if (wearVisualizerMessageCallback != null) {
            if (messageEvent.getPath().equals("kill")) {
                wearVisualizerMessageCallback.onKillMessageReceived();
            } else if (messageEvent.getPath().equals("waveform")) {
                wearVisualizerMessageCallback.onWaveformDataReceived(messageEvent.getData());
//                Log.d("VisualizerListenerService", "Sending data to view!");
            }
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

//        Log.d("VisualizerListenerService", "Low memory warning...");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d("VisualizerListenerService", "I got destroyed :(");
    }

    public static interface WearVisualizerMessageCallback {
        public void onWaveformDataReceived(byte[] data);
        public void onKillMessageReceived();
    }

}
