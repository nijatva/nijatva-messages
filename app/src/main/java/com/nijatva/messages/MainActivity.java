package com.nijatva.messages;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
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

import com.nijatva.signature.Signature;
import com.nijatva.signature.SignatureTheme;

/** Conversation list with categories (All / Personal / OTP / Others) and search. */
public class MainActivity extends Activity {

    private static final int REQ_PERM = 7;
    private static final int BG = 0xFF14141C;
    private static final int ROW = 0xFF1E1E2A;
    private static final int WHITE = 0xFFFFFFFF;
    private static final int GREY = 0xFF9AA0A6;
    private static final int BLUE = 0xFF5B8DEF;
    private static final int[] AV = {0xFFE57373, 0xFFF06292, 0xFFBA68C8, 0xFF9575CD,
            0xFF7986CB, 0xFF64B5F6, 0xFF4FC3F7, 0xFF4DB6AC, 0xFF81C784, 0xFFFFB74D};

    private final List<SmsUtils.Thread> all = new ArrayList<SmsUtils.Thread>();
    private final List<SmsUtils.Thread> shown = new ArrayList<SmsUtils.Thread>();
    private ThreadsAdapter adapter;
    private EditText search;
    private TextView chipAll, chipPersonal, chipOtp, chipOther, empty, defaultBanner;
    private int mode = 0;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        // This app is dark by design, so the signature screens open dark too.
        SignatureTheme.seedDefault(this, SignatureTheme.DARK, SignatureTheme.GREEN);
        getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(BG));
        setContentView(buildUi());
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{
                    android.Manifest.permission.READ_SMS,
                    android.Manifest.permission.SEND_SMS,
                    android.Manifest.permission.RECEIVE_SMS,
                    android.Manifest.permission.READ_CONTACTS}, REQ_PERM);
        } else {
            load();
        }
    }

    private int dp(int v) { return (int) (v * getResources().getDisplayMetrics().density + 0.5f); }

    private View buildUi() {
        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(BG);

        LinearLayout col = new LinearLayout(this);
        col.setOrientation(LinearLayout.VERTICAL);
        root.addView(col, new FrameLayout.LayoutParams(-1, -1));

        LinearLayout head = new LinearLayout(this);
        head.setOrientation(LinearLayout.HORIZONTAL);
        head.setGravity(Gravity.CENTER_VERTICAL);
        head.setPadding(dp(18), dp(18), dp(18), dp(6));
        TextView title = new TextView(this);
        title.setText("Nijatva Messages");
        title.setTextColor(WHITE);
        title.setTextSize(22);
        title.setTypeface(null, Typeface.BOLD);
        head.addView(title, new LinearLayout.LayoutParams(0, -2, 1f));
        ImageView gear = new ImageView(this);
        gear.setImageResource(android.R.drawable.ic_menu_preferences);
        gear.setColorFilter(GREY);
        gear.setLayoutParams(new LinearLayout.LayoutParams(dp(36), dp(36)));
        gear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { openSettings(); }
        });
        head.addView(gear);
        col.addView(head);

        defaultBanner = new TextView(this);
        defaultBanner.setTextColor(Color.WHITE);
        defaultBanner.setTextSize(12);
        defaultBanner.setPadding(dp(18), dp(10), dp(18), dp(10));
        GradientDrawable bb = new GradientDrawable();
        bb.setColor(0xFF3A2E12);
        bb.setCornerRadius(dp(12));
        defaultBanner.setBackground(bb);
        defaultBanner.setText("Set Nijatva Messages as your default SMS app to send and store messages.");
        defaultBanner.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { openSettings(); }
        });
        LinearLayout.LayoutParams blp = new LinearLayout.LayoutParams(-1, -2);
        blp.setMargins(dp(14), dp(6), dp(14), dp(6));
        defaultBanner.setLayoutParams(blp);
        col.addView(defaultBanner);

        search = new EditText(this);
        search.setHint("Search messages");
        search.setHintTextColor(GREY);
        search.setTextColor(WHITE);
        search.setTextSize(15);
        search.setSingleLine(true);
        GradientDrawable sb = new GradientDrawable();
        sb.setColor(0xFF2A2A36);
        sb.setCornerRadius(dp(26));
        search.setBackground(sb);
        search.setPadding(dp(20), dp(12), dp(20), dp(12));
        LinearLayout.LayoutParams slp = new LinearLayout.LayoutParams(-1, -2);
        slp.setMargins(dp(14), dp(10), dp(14), dp(6));
        search.setLayoutParams(slp);
        search.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            public void onTextChanged(CharSequence s, int a, int b, int c) {}
            public void afterTextChanged(Editable s) { applyFilter(); }
        });
        col.addView(search);

        LinearLayout chips = new LinearLayout(this);
        chips.setOrientation(LinearLayout.HORIZONTAL);
        chips.setPadding(dp(14), dp(4), dp(14), dp(8));
        chipAll = chip(chips, "All");
        chipPersonal = chip(chips, "Personal");
        chipOtp = chip(chips, "OTP");
        chipOther = chip(chips, "Others");
        chipAll.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { mode = 0; styleChips(); applyFilter(); }
        });
        chipPersonal.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { mode = 1; styleChips(); applyFilter(); }
        });
        chipOtp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { mode = 2; styleChips(); applyFilter(); }
        });
        chipOther.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { mode = 3; styleChips(); applyFilter(); }
        });
        col.addView(chips);

        ListView list = new ListView(this);
        list.setDivider(null);
        list.setDividerHeight(0);
        list.setBackgroundColor(BG);
        adapter = new ThreadsAdapter();
        list.setAdapter(adapter);
        list.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            public void onItemClick(android.widget.AdapterView<?> p, View v, int pos, long id) {
                SmsUtils.Thread t = shown.get(pos);
                Intent i = new Intent(MainActivity.this, ThreadActivity.class);
                i.putExtra("threadId", t.threadId);
                i.putExtra("address", t.address);
                i.putExtra("name", t.name);
                startActivity(i);
            }
        });
        col.addView(list, new LinearLayout.LayoutParams(-1, 0, 1f));

        empty = new TextView(this);
        empty.setText("No messages yet");
        empty.setTextColor(GREY);
        empty.setTextSize(14);
        empty.setGravity(Gravity.CENTER);
        empty.setVisibility(View.GONE);
        col.addView(empty);

        TextView fab = new TextView(this);
        fab.setText("+");
        fab.setTextColor(Color.WHITE);
        fab.setTextSize(28);
        fab.setGravity(Gravity.CENTER);
        GradientDrawable fb = new GradientDrawable();
        fb.setColor(BLUE);
        fb.setShape(GradientDrawable.OVAL);
        fab.setBackground(fb);
        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(dp(56), dp(56));
        flp.gravity = Gravity.BOTTOM | Gravity.END;
        flp.rightMargin = dp(18);
        flp.bottomMargin = dp(22);
        fab.setLayoutParams(flp);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ComposeActivity.class));
            }
        });
        root.addView(fab);

        styleChips();
        return root;
    }

    private TextView chip(LinearLayout parent, String label) {
        TextView t = new TextView(this);
        t.setText(label);
        t.setTextSize(13);
        t.setPadding(dp(16), dp(9), dp(16), dp(9));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, -2);
        lp.rightMargin = dp(8);
        t.setLayoutParams(lp);
        parent.addView(t);
        return t;
    }

    private void styleChips() {
        TextView[] all = {chipAll, chipPersonal, chipOtp, chipOther};
        for (int i = 0; i < all.length; i++) {
            boolean sel = i == mode;
            GradientDrawable bg = new GradientDrawable();
            bg.setCornerRadius(dp(20));
            bg.setColor(sel ? BLUE : 0xFF2A2A36);
            all[i].setBackground(bg);
            all[i].setTextColor(sel ? Color.WHITE : 0xFFDDDDDD);
        }
    }

    /**
     * The gear opens the app's own settings, which are the two signature screens plus
     * the one system thing this app cannot set for itself.
     */
    private void openSettings() {
        final boolean isDefault = SmsUtils.isDefaultSms(this);
        final String[] items = isDefault
                ? new String[]{"Appearance", "Developer"}
                : new String[]{"Appearance", "Developer", "Set as default SMS app"};
        new AlertDialog.Builder(this)
                .setTitle("Settings")
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            startActivity(Signature.appearance(MainActivity.this));
                        } else if (which == 1) {
                            startActivity(Signature.developer(MainActivity.this));
                        } else {
                            askToBeDefault();
                        }
                    }
                })
                .show();
    }

    private void askToBeDefault() {
        try {
            startActivity(new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT)
                    .putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getPackageName()));
            return;
        } catch (Exception ignore) {
        }
        try {
            startActivity(new Intent(android.provider.Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS));
            return;
        } catch (Exception ignore) {
        }
        Toast.makeText(this, "Set Nijatva Messages as your default SMS app in Android settings",
                Toast.LENGTH_SHORT).show();
    }

    private void load() {
        defaultBanner.setVisibility(SmsUtils.isDefaultSms(this) ? View.GONE : View.VISIBLE);
        new Thread(new Runnable() {
            public void run() {
                final List<SmsUtils.Thread> t = SmsUtils.loadThreads(MainActivity.this);
                runOnUiThread(new Runnable() {
                    public void run() {
                        all.clear();
                        all.addAll(t);
                        applyFilter();
                    }
                });
            }
        }).start();
    }

    private void applyFilter() {
        String q = search.getText().toString().trim().toLowerCase();
        shown.clear();
        for (SmsUtils.Thread t : all) {
            if (mode == 1 && t.otp) continue;
            if (mode == 2 && !t.otp) continue;
            if (mode == 3 && (t.otp || t.name.length() > 0)) continue;
            if (q.length() > 0) {
                boolean m = (t.name != null && t.name.toLowerCase().contains(q))
                        || (t.address != null && t.address.contains(q))
                        || (t.snippet != null && t.snippet.toLowerCase().contains(q));
                if (!m) continue;
            }
            shown.add(t);
        }
        adapter.notifyDataSetChanged();
        empty.setVisibility(shown.isEmpty() ? View.VISIBLE : View.GONE);
    }

    class ThreadsAdapter extends BaseAdapter {
        public int getCount() { return shown.size(); }
        public Object getItem(int p) { return shown.get(p); }
        public long getItemId(int p) { return p; }

        public View getView(int p, View reuse, ViewGroup parent) {
            SmsUtils.Thread t = shown.get(p);

            LinearLayout wrap = new LinearLayout(MainActivity.this);
            wrap.setOrientation(LinearLayout.VERTICAL);
            wrap.setPadding(dp(14), dp(3), dp(14), dp(3));

            LinearLayout row = new LinearLayout(MainActivity.this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            GradientDrawable bg = new GradientDrawable();
            bg.setColor(ROW);
            bg.setCornerRadius(dp(16));
            row.setBackground(bg);
            row.setPadding(dp(12), dp(12), dp(12), dp(12));

            String label = t.name.length() > 0 ? t.name : t.address;
            TextView av = new TextView(MainActivity.this);
            av.setText(initial(label));
            av.setTextColor(Color.WHITE);
            av.setTextSize(17);
            av.setTypeface(null, Typeface.BOLD);
            av.setGravity(Gravity.CENTER);
            GradientDrawable ab = new GradientDrawable();
            ab.setShape(GradientDrawable.OVAL);
            ab.setColor(colorFor(label));
            av.setBackground(ab);
            row.addView(av, new LinearLayout.LayoutParams(dp(46), dp(46)));

            LinearLayout col = new LinearLayout(MainActivity.this);
            col.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(0, -2, 1f);
            clp.leftMargin = dp(14);
            col.setLayoutParams(clp);

            TextView name = new TextView(MainActivity.this);
            name.setText(label);
            name.setTextColor(WHITE);
            name.setTextSize(16);
            name.setTypeface(null, Typeface.BOLD);
            col.addView(name);

            TextView snip = new TextView(MainActivity.this);
            snip.setText(t.snippet == null ? "" : t.snippet);
            snip.setTextColor(GREY);
            snip.setTextSize(13);
            snip.setMaxLines(1);
            col.addView(snip);
            row.addView(col);

            TextView time = new TextView(MainActivity.this);
            time.setText(timeOf(t.date));
            time.setTextColor(GREY);
            time.setTextSize(11);
            row.addView(time);

            wrap.addView(row, new LinearLayout.LayoutParams(-1, -2));
            return wrap;
        }
    }

    private static String timeOf(long millis) {
        try {
            java.util.Calendar c = java.util.Calendar.getInstance();
            java.util.Calendar now = java.util.Calendar.getInstance();
            c.setTimeInMillis(millis);
            if (c.get(java.util.Calendar.YEAR) == now.get(java.util.Calendar.YEAR)
                    && c.get(java.util.Calendar.DAY_OF_YEAR) == now.get(java.util.Calendar.DAY_OF_YEAR)) {
                return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(millis));
            }
            return new SimpleDateFormat("dd MMM", Locale.getDefault()).format(new Date(millis));
        } catch (Exception e) {
            return "";
        }
    }

    private static String initial(String s) {
        if (s == null || s.length() == 0) return "?";
        return s.substring(0, 1).toUpperCase();
    }

    private static int colorFor(String s) {
        int h = 0;
        for (int i = 0; i < s.length(); i++) h = h * 31 + s.charAt(i);
        return AV[Math.abs(h) % AV.length];
    }

    @Override
    public void onRequestPermissionsResult(int req, String[] perms, int[] res) {
        if (req == REQ_PERM) {
            for (int r : res) {
                if (r != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "SMS and contacts permissions are required", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            load();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT < 23
                || checkSelfPermission(android.Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            load();
        }
    }
}
