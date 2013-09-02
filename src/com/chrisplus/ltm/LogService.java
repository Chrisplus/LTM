
package com.chrisplus.ltm;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import com.chrisplus.rootmanager.RootManager;

/**
 * @author Chris Jiang
 */
public class LogService extends Service {

    private final static String TAG = LogService.class.getSimpleName();

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
        // TODO Auto-generated method stub
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
    
    
}
