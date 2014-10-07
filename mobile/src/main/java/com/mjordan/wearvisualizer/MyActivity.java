package com.mjordan.wearvisualizer;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(new VisualizerView(this));

        startService(VisualizerService.createIntent(this, VisualizerService.ACTION_START_SENDING));
    }
}
