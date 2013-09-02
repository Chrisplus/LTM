
package com.chrisplus.ltm;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

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
        updateButtonState();
        actionButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean isRunning = (Boolean) v.getTag();
                if (isRunning) {
                    /* stop service */
                    // TODO
                } else {
                    /* start service */
                    // TODO
                }
                /* Without Check */
                updateButtonState(!isRunning);
            }
        });
    }

    private void updateButtonState() {
        /* Check the Service State */
        // TODO

        boolean serviceState = false;
        updateButtonState(serviceState);
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

}
