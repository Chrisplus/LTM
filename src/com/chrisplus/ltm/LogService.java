
package com.chrisplus.ltm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.chrisplus.ltm.core.CoreLogger;
import com.chrisplus.ltm.utils.Constants;
import com.chrisplus.rootmanager.RootManager;

/**
 * @author Chris Jiang
 */
public class LogService extends Service {

    private final static String TAG = LogService.class.getSimpleName();
    private CoreLogger coreLogger;

    private final Messenger messenger = new Messenger(new LTMHandler(getBaseContext()));

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "OnBind");

        if (RootManager.getInstance().hasRooted() && RootManager.getInstance().grantPermission()) {
            return messenger.getBinder();
        } else {
            return null;
        }
    }

    private class LTMHandler extends Handler {

        private Context context;

        private LTMHandler(Context ctx) {
            context = ctx;
        }

        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "Get Message " + msg.toString());
            super.handleMessage(msg);
        }

    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        coreLogger = new CoreLogger(this);
        new Thread(coreLogger, "TestLogger").start();
        addNotification();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "OnDestroy");
        removeNotification();
        if (coreLogger != null) {
            coreLogger.terminate();
        }
        super.onDestroy();
    }

    private void updateNotification(boolean active) {
        NotificationManager notiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notiManager.notify(Constants.NOTIFICATION_ID, getNotification(active));
    }

    private void removeNotification() {
        stopForeground(true);
    }

    private void addNotification() {
        startForeground(Constants.NOTIFICATION_ID, getNotification(false));
    }

    private Notification getNotification(boolean isActavied) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        if (isActavied) {
            builder.setSmallIcon(R.drawable.notification_actived);
        } else {
            builder.setSmallIcon(R.drawable.notification_deactived);
        }
        builder.setContentTitle(getString(R.string.notification_title)).setAutoCancel(false);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(
                Intent.FLAG_ACTIVITY_SINGLE_TOP
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        builder.setContentIntent(contentIntent);
        Notification noti = builder.build();
        noti.flags |= Notification.FLAG_ONGOING_EVENT;
        noti.flags |= Notification.FLAG_NO_CLEAR;
        noti.flags |= Notification.FLAG_SHOW_LIGHTS;
        return noti;
    }

}
