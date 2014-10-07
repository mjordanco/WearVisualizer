package com.mjordan.wearvisualizer;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.audiofx.Visualizer;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.List;

/**
 * Created by matt on 9/18/14.
 */
public class VisualizerService extends IntentService {

    private static final int NOTIFICATION_ID = 1;

    private static final String KEY_ACTION = "action";

    public static final int ACTION_START_SENDING = 0;
    public static final int ACTION_STOP_SENDING = 1;

    private NotificationManager mNotificationManager;

    private NotificationCompat.Builder mNotificationBuilder;

    private static VisualizerThread mVisualizerThread;

    public static Intent createIntent(Context context, int action) {
        Intent intent = new Intent(context, VisualizerService.class);
        intent.putExtra(KEY_ACTION, action);
        return intent;
    }

    public static PendingIntent createPendingIntent(Context context, int action) {
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, createIntent(context, action), 0);
        return pendingIntent;
    }

    public VisualizerService() {
        super(VisualizerService.class.getSimpleName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationBuilder = new NotificationCompat.Builder(this);
        mNotificationBuilder.setSmallIcon(android.R.drawable.ic_media_play);
        mNotificationBuilder.setContentTitle("Visualizing on wearable...");
        mNotificationBuilder.addAction(android.R.drawable.ic_media_pause, "Stop", createPendingIntent(this, ACTION_STOP_SENDING));
        mNotificationBuilder.setOngoing(true);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int action = intent.getIntExtra(KEY_ACTION, ACTION_START_SENDING);
        switch (action) {
            case ACTION_START_SENDING:
                if (mVisualizerThread == null) {
                    mNotificationManager.notify(NOTIFICATION_ID, mNotificationBuilder.build());
                    mVisualizerThread = new VisualizerThread(this);
                    mVisualizerThread.begin();
                }
                break;
            case ACTION_STOP_SENDING:
                if (mVisualizerThread != null) {
                    mNotificationManager.cancel(NOTIFICATION_ID);
                    mVisualizerThread.cease();
                    mVisualizerThread = null;
                }
                break;
        }
    }

    private static class VisualizerThread extends Thread {

        private byte[] mWaveformData;
        private Visualizer mVisualizer;
        public boolean shouldContinue;
        private GoogleApiClient mGoogleApiClient;
        private Context mContext;
        private Handler mHandler;

        public VisualizerThread(Context context) {
            shouldContinue = true;
            mContext = context;
            mWaveformData = new byte[1024];
        }

        @Override
        public void run() {
//            Log.d("VisualizerService", "run");

            while (shouldContinue) {
                try {
//                    Log.d("VisualizerService", "run");
                    mVisualizer.getWaveForm(mWaveformData);

                    List<Node> nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await().getNodes();
                    if (nodes.size() > 0) {
                        String nodeId = nodes.get(0).getId();
                        MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(mGoogleApiClient, nodeId, "waveform", mWaveformData).await();
                        if (result.getStatus().isSuccess()) {
                            Log.d("VisualizerService", "Sending message was a success!");
                        } else {
//                            Log.e("VisualizerService", "Sending message was a failure!");
                        }
                    } else {
//                        Log.e("VisualizerService", "No nodes connected...");
                    }

                    Thread.sleep(1000 / 30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void begin() {
//            Log.d("VisualizerService", "begin");

            mGoogleApiClient = new GoogleApiClient.Builder(mContext).addApi(Wearable.API).build();
            ConnectionResult result = mGoogleApiClient.blockingConnect();
            if (result.isSuccess()) {
                mVisualizer = new Visualizer(0);
                mVisualizer.setScalingMode(Visualizer.SCALING_MODE_NORMALIZED);
                mVisualizer.setEnabled(true);
                start();
            } else {
//                Log.e("VisualizerService", "Failed to connect to Google Api's: " + result.getErrorCode());
            }
        }

        public void cease() {
            mVisualizer.setEnabled(false);
            shouldContinue = false;

            List<Node> nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await().getNodes();
            if (nodes.size() > 0) {
                String nodeId = nodes.get(0).getId();
                MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(mGoogleApiClient, nodeId, "kill", mWaveformData).await();
                if (result.getStatus().isSuccess()) {
                    Log.d("VisualizerService", "Sending message was a success!");
                } else {
//                            Log.e("VisualizerService", "Sending message was a failure!");
                }
            } else {
//                        Log.e("VisualizerService", "No nodes connected...");
            }
        }

        private class SendMessageRunnable implements Runnable {

            private final byte[] mData;

            public SendMessageRunnable(byte[] data) {
                mData = data;
//                Log.d("VisualizerService", "runnable created");

            }

            @Override
            public void run() {


            }
        }
    }
}