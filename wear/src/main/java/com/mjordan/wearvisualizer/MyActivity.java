package com.mjordan.wearvisualizer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.List;

public class MyActivity extends Activity implements ServiceConnection {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new VisualizerView(this));
    }

    @Override
    protected void onResume() {
        super.onResume();

        new SendMessageAsyncTask(this).execute("start");

        Intent bindTo = new Intent(this, VisualizerListenerService.class);
        bindService(bindTo, this, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();

        new SendMessageAsyncTask(this).execute("stop");


        unbindService(this);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    private static class SendMessageAsyncTask extends AsyncTask<String, Void, Void> {

        private final Context mContext;

        public SendMessageAsyncTask(Context context) {
            mContext = context;
        }
        
        @Override
        protected Void doInBackground(String... params) {
            GoogleApiClient client = new GoogleApiClient.Builder(mContext).addApi(Wearable.API).build();
            ConnectionResult result = client.blockingConnect();

            List<Node> nodes = Wearable.NodeApi.getConnectedNodes(client).await().getNodes();

            if (result.isSuccess() && nodes.size() > 0) {
                MessageApi.SendMessageResult msgResult = Wearable.MessageApi.sendMessage(client, nodes.get(0).getId(), params[0], new byte[0]).await();

                if (!msgResult.getStatus().isSuccess()) {
                    Log.e("MyActivity", "Send message failed");

                }
            }

            return null;
        }
    }
}
