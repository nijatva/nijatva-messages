package com.nijatva.messages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Required by Android for the default SMS role. MMS download is handled by the
 * system, so nothing is processed here.
 */
public class MmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context ctx, Intent intent) {
        // no-op: required component for the default SMS role
    }
}
