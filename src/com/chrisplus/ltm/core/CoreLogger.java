
package com.chrisplus.ltm.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.chrisplus.ltm.R;
import com.chrisplus.ltm.utils.Constants;
import com.chrisplus.ltm.utils.ShellCommand;
import com.chrisplus.ltm.utils.SysUtils;

/**
 * This class is used to run log task.
 * 
 * @author Chris Jiang
 */
public class CoreLogger implements Runnable {

    private final static String TAG = CoreLogger.class.getSimpleName();

    private Context context;
    private CoreParser parser;
    private boolean running = false;
    private ShellCommand command;

    public CoreLogger(Context ctx) {
        context = ctx;
        parser = new CoreParser();
        startLoggerCommand();

    }

    @Override
    public void run() {
        Log.d(TAG, TAG + " starting");
        String result;
        running = true;

        while (true) {
            while (running && command.checkForExit() == false) {
                if (command.stdoutAvailable()) {
                    result = command.readStdout();
                } else {
                    try {
                        Thread.sleep(500);
                    } catch (Exception e) {
                        Log.d(TAG, "exception while sleeping", e);
                    }

                    continue;
                }

                if (running == false) {
                    break;
                }

                if (result == null) {
                    Log.d(TAG, " read null; exiting");
                    break;
                }
                parser.processRawLog(result);
            }

            if (running != false) {
                Log.d(TAG,
                        "terminated unexpectedly, restarting in 10 seconds");
                try {
                    Thread.sleep(10000);
                } catch (Exception e) {
                    // ignored
                }
                if (!startLoggerCommand()) {
                    running = false;
                }
            } else {
                Log.d(TAG, "reached end of loop; exiting");
                break;
            }
        }
    }

    public boolean startLoggerCommand() {

        command = new ShellCommand(new String[] {
                "su", "-c", "sh " + new ContextWrapper(context).getFilesDir().getAbsolutePath()
                        + File.separator + Constants.EXE_SCRIPT
        }, TAG);
        final String error = command.start(false);

        if (error != null) {
            SysUtils.showError(context, context.getString(R.string.error_default_title), error);
            return false;
        } else {
            return true;
        }

    }

    public void terminate() {
        running = false;

        /* Close Parser */
        if (parser != null) {
            parser.close();
        }

        /* Record the APP UID */
        if (android.os.Environment.getExternalStorageState().contains(
                android.os.Environment.MEDIA_MOUNTED)) {
            Log.d(TAG, "Try to Log All App Info");
            try {
                SysUtils.checkFileEnvironment(Constants.MAP_FILE);
                PrintWriter appIDWritter = new PrintWriter(new BufferedWriter(new FileWriter(
                        Constants.LOG_PATH + Constants.MAP_FILE, false)), true);
                appIDWritter.println("UID,PKG,PROCESS,TYPE");
                /* Get App List */
                PackageManager pm = context.getPackageManager();
                List<ApplicationInfo> apps = pm.getInstalledApplications(0);

                for (ApplicationInfo app : apps) {
                    if ((app.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                        /* System App */
                        appIDWritter.println(app.uid + "," + app.packageName + ","
                                + app.processName + ",System");
                    } else {
                        /* User App */
                        appIDWritter.println(app.uid + "," + app.packageName + ","
                                + app.processName + ",User");
                    }
                }

                /* Close Writter */
                appIDWritter.close();

            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }
}
