package com.mjordan.wearvisualizer;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.media.audiofx.Visualizer;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by matt on 9/18/14.
 */
public class VisualizerListenerService extends WearableListenerService {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);

        Log.d("VisualizerListenerService", "Received Message!");

        if (messageEvent.getPath().equals("start")) {
            startService(VisualizerService.createIntent(this, VisualizerService.ACTION_START_SENDING));
        } else if (messageEvent.getPath().equals("stop")) {
            startService(VisualizerService.createIntent(this, VisualizerService.ACTION_STOP_SENDING));
        }
    }
}
