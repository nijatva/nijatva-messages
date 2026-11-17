package com.nijatva.messages;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/** Required by Android for the default SMS role (quick replies from the system UI). */
public class HeadlessSmsService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        stopSelf();
        return START_NOT_STICKY;
    }
}
