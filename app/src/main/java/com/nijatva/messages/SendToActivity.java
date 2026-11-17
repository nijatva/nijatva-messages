package com.nijatva.messages;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

/** Handles smsto: / sms: intents and opens the composer. */
public class SendToActivity extends Activity {

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        String number = "";
        Uri data = getIntent() != null ? getIntent().getData() : null;
        if (data != null) {
            String ssp = data.getSchemeSpecificPart();
            if (ssp != null) {
                int q = ssp.indexOf('?');
                number = q >= 0 ? ssp.substring(0, q) : ssp;
            }
        }
        Intent i = new Intent(this, ComposeActivity.class);
        i.putExtra("number", number);
        startActivity(i);
        finish();
    }
}
