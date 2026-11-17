package com.nijatva.messages;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/** Conversation view: message bubbles and reply box. */
public class ThreadActivity extends Activity {

    private static final int BG = 0xFF14141C;
    private static final int IN_BG = 0xFF1E1E2A;
    private static final int OUT_BG = 0xFF2F5FA8;
    private static final int WHITE = 0xFFFFFFFF;
    private static final int GREY = 0xFF9AA0A6;

    private long threadId;
    private String address = "";
    private String name = "";

    private final List<SmsUtils.Msg> msgs = new ArrayList<SmsUtils.Msg>();
    private MsgsAdapter adapter;
    private ListView list;
    private EditText input;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        threadId = getIntent().getLongExtra("threadId", -1);
        address = getIntent().getStringExtra("address");
        name = getIntent().getStringExtra("address");
        String n = getIntent().getStringExtra("name");
        if (n != null && n.length() > 0) name = n;
        if (address == null) address = "";
        getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(BG));
        setContentView(buildUi());
        load();
    }

    private int dp(int v) { return (int) (v * getResources().getDisplayMetrics().density + 0.5f); }

    private View buildUi() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);

        LinearLayout head = new LinearLayout(this);
        head.setOrientation(LinearLayout.HORIZONTAL);
        head.setGravity(Gravity.CENTER_VERTICAL);
        head.setPadding(dp(10), dp(14), dp(14), dp(10));

        ImageView back = new ImageView(this);
        back.setImageResource(android.R.drawable.ic_menu_revert);
        back.setColorFilter(WHITE);
        back.setLayoutParams(new LinearLayout.LayoutParams(dp(32), dp(32)));
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { finish(); }
        });
        head.addView(back);

        LinearLayout names = new LinearLayout(this);
        names.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams nlp = new LinearLayout.LayoutParams(0, -2, 1f);
        nlp.leftMargin = dp(12);
        names.setLayoutParams(nlp);

        TextView t = new TextView(this);
        t.setText(name);
        t.setTextColor(WHITE);
        t.setTextSize(17);
        t.setTypeface(null, Typeface.BOLD);
        names.addView(t);

        if (!name.equals(address)) {
            TextView sub = new TextView(this);
            sub.setText(address);
            sub.setTextColor(GREY);
            sub.setTextSize(11);
            names.addView(sub);
        }
        head.addView(names);
        root.addView(head);

        list = new ListView(this);
        list.setDivider(null);
        list.setDividerHeight(0);
        list.setBackgroundColor(BG);
        adapter = new MsgsAdapter();
        list.setAdapter(adapter);
        root.addView(list, new LinearLayout.LayoutParams(-1, 0, 1f));

        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setGravity(Gravity.CENTER_VERTICAL);
        bar.setPadding(dp(12), dp(8), dp(12), dp(12));
        bar.setBackgroundColor(0xFF1A1A24);

        input = new EditText(this);
        input.setHint("Message");
        input.setHintTextColor(GREY);
        input.setTextColor(WHITE);
        input.setTextSize(15);
        input.setMaxLines(4);
        GradientDrawable ib = new GradientDrawable();
        ib.setColor(0xFF2A2A36);
        ib.setCornerRadius(dp(22));
        input.setBackground(ib);
        input.setPadding(dp(18), dp(12), dp(18), dp(12));
        bar.addView(input, new LinearLayout.LayoutParams(0, -2, 1f));

        TextView send = new TextView(this);
        send.setText("Send");
        send.setTextColor(Color.WHITE);
        send.setTextSize(14);
        send.setTypeface(null, Typeface.BOLD);
        send.setGravity(Gravity.CENTER);
        GradientDrawable sb = new GradientDrawable();
        sb.setColor(0xFF5B8DEF);
        sb.setCornerRadius(dp(22));
        send.setBackground(sb);
        LinearLayout.LayoutParams slp = new LinearLayout.LayoutParams(dp(72), dp(44));
        slp.leftMargin = dp(8);
        send.setLayoutParams(slp);
        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { doSend(); }
        });
        bar.addView(send);

        root.addView(bar, new LinearLayout.LayoutParams(-1, -2));
        return root;
    }

    private void doSend() {
        String body = input.getText().toString().trim();
        if (body.length() == 0) return;
        if (Build.VERSION.SDK_INT >= 23
                && checkSelfPermission(android.Manifest.permission.SEND_SMS) != 0) {
            Toast.makeText(this, "SMS permission is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (SmsUtils.send(this, address, body)) {
            input.setText("");
            load();
        } else {
            Toast.makeText(this, "Unable to send the message", Toast.LENGTH_SHORT).show();
        }
    }

    private void load() {
        new Thread(new Runnable() {
            public void run() {
                final List<SmsUtils.Msg> m = SmsUtils.loadMessages(ThreadActivity.this, threadId);
                runOnUiThread(new Runnable() {
                    public void run() {
                        msgs.clear();
                        msgs.addAll(m);
                        adapter.notifyDataSetChanged();
                        if (!msgs.isEmpty()) list.setSelection(msgs.size() - 1);
                    }
                });
            }
        }).start();
    }

    class MsgsAdapter extends BaseAdapter {
        public int getCount() { return msgs.size(); }
        public Object getItem(int p) { return msgs.get(p); }
        public long getItemId(int p) { return p; }

        public View getView(int p, View reuse, ViewGroup parent) {
            SmsUtils.Msg m = msgs.get(p);
            boolean outgoing = m.type == 2;

            LinearLayout row = new LinearLayout(ThreadActivity.this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(outgoing ? Gravity.END : Gravity.START);
            row.setPadding(dp(12), dp(3), dp(12), dp(3));

            LinearLayout bubble = new LinearLayout(ThreadActivity.this);
            bubble.setOrientation(LinearLayout.VERTICAL);
            GradientDrawable bg = new GradientDrawable();
            bg.setColor(outgoing ? OUT_BG : IN_BG);
            bg.setCornerRadius(dp(16));
            bubble.setBackground(bg);
            bubble.setPadding(dp(14), dp(10), dp(14), dp(8));

            TextView body = new TextView(ThreadActivity.this);
            body.setText(m.body == null ? "" : m.body);
            body.setTextColor(WHITE);
            body.setTextSize(15);
            bubble.addView(body);

            TextView time = new TextView(ThreadActivity.this);
            time.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(m.date)));
            time.setTextColor(0xFFB0B6C0);
            time.setTextSize(10);
            time.setGravity(Gravity.END);
            time.setPadding(0, dp(4), 0, 0);
            bubble.addView(time);

            LinearLayout.LayoutParams blp = new LinearLayout.LayoutParams(-2, -2);
            blp.leftMargin = dp(40);
            blp.rightMargin = dp(40);
            bubble.setLayoutParams(blp);

            row.addView(bubble);
            return row;
        }
    }
}
