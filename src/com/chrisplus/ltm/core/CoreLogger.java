
package com.chrisplus.ltm.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.chrisplus.ltm.utils.Constants;
import com.chrisplus.ltm.utils.SysUtils;
import com.chrisplus.rootmanager.RootManager;
import com.chrisplus.rootmanager.container.Command;

/**
 * This class is used to run log task.
 * 
 * @author Chris Jiang
 */
public class CoreLogger implements Runnable {

    private final static String TAG = CoreLogger.class.getSimpleName();

    private Context context;
    private Command command;
    private CoreParser parser;

    public CoreLogger(Context ctx) {
        context = ctx;
        parser = new CoreParser();
        command = new Command("sh "
                + new ContextWrapper(context).getFilesDir().getAbsolutePath()
                + File.separator + Constants.EXE_SCRIPT) {

            @Override
            public void onFinished(int arg0) {

            }

            @Override
            public void onUpdate(int arg0, String arg1) {
                /* To Process the Log */
                parser.processRawLog(arg1);
            }

        };
    }

    @Override
    public void run() {
        if (command != null) {
            RootManager.getInstance().runCommandOnline(command);
        }
    }

    public void terminate() {
        if (command != null) {
            command.terminate("Serice Shut it Down");
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
