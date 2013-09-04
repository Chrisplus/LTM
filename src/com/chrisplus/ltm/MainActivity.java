
package com.chrisplus.ltm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.chrisplus.ltm.utils.Constants;
import com.chrisplus.ltm.utils.SysUtils;

public class MainActivity extends Activity {

    private final static String TAG = MainActivity.class.getSimpleName();

    /* UI Component */
    private Button actionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void init() {
        actionButton = (Button) findViewById(R.id.ActionButton);
        actionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isRunning = (Boolean) v.getTag();
                if (isRunning) {
                    /* stop service */
                    // TODO
                    isRunning = !stopService();
                } else {
                    /* start service */
                    // TODO
                    isRunning = startService();
                }
                /* Without Check */
                updateButtonState(isRunning);
            }
        });
    }

    private void updateButtonState(boolean isRunning) {

        boolean serviceState = isRunning;

        if (serviceState) {
            /* Set Stop */
            actionButton.setText(R.string.button_stop);
            /* Set is running */
            actionButton.setTag(true);
        } else {
            /* Set Start */
            actionButton.setText(R.string.button_start);
            /* Set is running */
            actionButton.setTag(false);
        }
    }

    private boolean startService() {
        installBinaries();

        ComponentName name = null;
        try {
            name = startService(new Intent(this, LogService.class));
        } catch (SecurityException e) {
            e.printStackTrace();
            return false;
        }

        if (name != null) {
            Log.d(TAG, "Get Component Name is " + name.getClassName());
            return true;
        } else {
            return false;
        }
    }

    private boolean stopService() {       
        return stopService(new Intent(this, LogService.class));
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "Resume and Check State");
        boolean isRunning = SysUtils.isServiceRunning(this, LogService.class.getName());
        updateButtonState(isRunning);
        super.onResume();
    }

    private void installBinaries() {
        SysUtils.installBinaries(this);

        /* Write SH file */
        synchronized (Constants.EXE_SCRIPT) {
            String scriptFile = new ContextWrapper(this).getFilesDir().getAbsolutePath()
                    + File.separator + Constants.EXE_SCRIPT;
            String binaryPath = getFilesDir().getAbsolutePath() + File.separator
                    + SysUtils.getGrepBinary();

            try {
                PrintWriter script = new PrintWriter(new BufferedWriter(new FileWriter(scriptFile)));
                script.println(binaryPath + " {NL} /proc/kmsg");
                script.close();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }
    }

}
