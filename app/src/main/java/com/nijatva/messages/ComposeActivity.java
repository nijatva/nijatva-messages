package com.nijatva.messages;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/** New message composer. */
public class ComposeActivity extends Activity {

    private static final int BG = 0xFF14141C;
    private static final int WHITE = 0xFFFFFFFF;
    private static final int GREY = 0xFF9AA0A6;

    private EditText number, message;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(BG));
        setContentView(buildUi());
    }

    private int dp(int v) { return (int) (v * getResources().getDisplayMetrics().density + 0.5f); }

    private View buildUi() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);
        root.setPadding(dp(16), dp(16), dp(16), dp(16));

        LinearLayout head = new LinearLayout(this);
        head.setOrientation(LinearLayout.HORIZONTAL);
        head.setGravity(Gravity.CENTER_VERTICAL);
        head.setPadding(0, 0, 0, dp(14));

        ImageView back = new ImageView(this);
        back.setImageResource(android.R.drawable.ic_menu_revert);
        back.setColorFilter(WHITE);
        back.setLayoutParams(new LinearLayout.LayoutParams(dp(32), dp(32)));
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { finish(); }
        });
        head.addView(back);

        TextView title = new TextView(this);
        title.setText("New message");
        title.setTextColor(WHITE);
        title.setTextSize(20);
        title.setTypeface(null, Typeface.BOLD);
        title.setPadding(dp(12), 0, 0, 0);
        head.addView(title);
        root.addView(head);

        number = new EditText(this);
        number.setHint("Phone number");
        number.setHintTextColor(GREY);
        number.setTextColor(WHITE);
        number.setTextSize(16);
        number.setInputType(InputType.TYPE_CLASS_PHONE);
        GradientDrawable nb = new GradientDrawable();
        nb.setColor(0xFF2A2A36);
        nb.setCornerRadius(dp(14));
        number.setBackground(nb);
        number.setPadding(dp(16), dp(14), dp(16), dp(14));
        LinearLayout.LayoutParams nlp = new LinearLayout.LayoutParams(-1, -2);
        nlp.bottomMargin = dp(12);
        number.setLayoutParams(nlp);
        String prefill = getIntent() != null ? getIntent().getStringExtra("number") : null;
        if (prefill != null && prefill.length() > 0) number.setText(prefill);
        root.addView(number);

        message = new EditText(this);
        message.setHint("Message");
        message.setHintTextColor(GREY);
        message.setTextColor(WHITE);
        message.setTextSize(15);
        message.setMinLines(4);
        message.setGravity(Gravity.TOP);
        message.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        GradientDrawable mb = new GradientDrawable();
        mb.setColor(0xFF2A2A36);
        mb.setCornerRadius(dp(14));
        message.setBackground(mb);
        message.setPadding(dp(16), dp(14), dp(16), dp(14));
        root.addView(message, new LinearLayout.LayoutParams(-1, 0, 1f));

        TextView send = new TextView(this);
        send.setText("Send message");
        send.setTextColor(Color.WHITE);
        send.setTextSize(15);
        send.setTypeface(null, Typeface.BOLD);
        send.setGravity(Gravity.CENTER);
        GradientDrawable sb = new GradientDrawable();
        sb.setColor(0xFF5B8DEF);
        sb.setCornerRadius(dp(24));
        send.setBackground(sb);
        LinearLayout.LayoutParams slp = new LinearLayout.LayoutParams(-1, dp(50));
        slp.topMargin = dp(14);
        send.setLayoutParams(slp);
        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { doSend(); }
        });
        root.addView(send);

        return root;
    }

    private void doSend() {
        String to = number.getText().toString().trim();
        String body = message.getText().toString().trim();
        if (to.length() == 0) {
            Toast.makeText(this, "Enter a phone number", Toast.LENGTH_SHORT).show();
            return;
        }
        if (body.length() == 0) {
            Toast.makeText(this, "Enter a message", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Build.VERSION.SDK_INT >= 23
                && checkSelfPermission(android.Manifest.permission.SEND_SMS) != 0) {
            Toast.makeText(this, "SMS permission is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (SmsUtils.send(this, to, body)) {
            Toast.makeText(this, "Message sent", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Unable to send the message", Toast.LENGTH_SHORT).show();
        }
    }
}
