
package com.chrisplus.ltm.utils;

import java.io.File;

import android.app.Application;
import android.os.Build;

/**
 * This class is used to record the constants for LTM project.
 * 
 * @author Chris Jiang
 */
public class Constants {

    /* Passing Message between application and service */
    public final static int MSG_REG_CLIENT = 7301;
    public final static int MSG_UNREG_CLIENT = 7302;
    public final static int MSG_BROADCAST_LOG = 7303;
    public final static int MSG_UPDATE_NOTIFICATION = 7304;
    public final static int MSG_TOGGLE_FOREGROUND = 7305;

    /* Notification ID */
    public final static int NOTIFICATION_ID = Application.class.getName().hashCode();

    /* Execution Script & File Name */
    public final static String EXE_SCRIPT = "run_ltm.sh";
    public final static String CREATE_TIME = System.currentTimeMillis() + "";
    public final static String LOG_PATH = android.os.Environment.getExternalStorageDirectory()
            + File.separator + "LTM_LOG" + File.separator;
    public final static String LOG_FILE = CREATE_TIME + "_" + Build.USER + "_"
            + "NetworkTrafficLog.csv";
    public final static String MAP_FILE = CREATE_TIME + "_" + Build.PRODUCT + "_"
            + "ApplicationUIDMap.csv";

}
