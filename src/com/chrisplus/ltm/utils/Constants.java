
package com.chrisplus.ltm.utils;

import android.app.Application;

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

    /* Execution Script Name */
    public final static String EXE_SCRIPT = "run_ltm.sh";

}
