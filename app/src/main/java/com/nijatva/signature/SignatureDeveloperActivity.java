package com.nijatva.signature;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Developer: the signature card. Avatar, name, handle, what he builds, the three
 * figures, a place, a site, an address, and the proof that this build is his.
 *
 * <p>The figures are not typed in here. They are read from {@link SignatureDevInfo},
 * which {@code tools/generate_devinfo.py} rewrites from GitHub and the site, so a new
 * app in the suite updates every app's card at the next build.
 *
 * <p>Everything the card claims about the build - the version, the signing
 * certificate, whether the app may reach the network - is read from the device at
 * open time through {@link SignatureTrust}. There is no badge that is not backed by
 * something the user can check.
 */
public class SignatureDeveloperActivity extends SignatureScreenActivity {

    /** How many app codes are named before the count takes over. */
    private static final int NAMED = 4;

    /** Taps on the avatar before the about panel opens. */
    private static final int TAPS_FOR_ABOUT = 7;

    private int avatarTaps;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        mirror();
        SignatureTheme.applyTo(this);

        ScrollView scroll = SignatureViews.screen(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(SignatureTheme.background(this));
        root.addView(SignatureViews.header(this, "Developer"));

        LinearLayout body = SignatureViews.body(this);
        body.addView(profileCard());
        body.addView(maintains());

        root.addView(body);
        scroll.addView(root, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setContentView(scroll);
    }

    private View profileCard() {
        final int accent = SignatureTheme.accent(this);
        LinearLayout card = SignatureViews.card(this);

        // Avatar: initials on an accent disc, no image file to ship.
        TextView avatar = new TextView(this);
        avatar.setText(Signature.INITIALS);
        avatar.setTextColor(SignatureTheme.onAccent(accent));
        avatar.setTextSize(TypedValue.COMPLEX_UNIT_SP, 34);
        avatar.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        avatar.setGravity(Gravity.CENTER);
        GradientDrawable disc = new GradientDrawable();
        disc.setShape(GradientDrawable.OVAL);
        disc.setColor(accent);
        avatar.setBackground(disc);
        avatar.setClickable(true);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Seven taps open the small print: what signed this build, and what the
                // suite promises. Hidden rather than listed, so the card stays a card.
                if (++avatarTaps >= TAPS_FOR_ABOUT) {
                    avatarTaps = 0;
                    showAbout();
                }
            }
        });
        int size = SignatureViews.dp(this, 96);
        LinearLayout.LayoutParams avatarParams = new LinearLayout.LayoutParams(size, size);
        avatarParams.gravity = Gravity.CENTER_HORIZONTAL;
        card.addView(avatar, avatarParams);

        card.addView(centred(Signature.NAME, 24, SignatureTheme.onSurface(this),
                Typeface.BOLD, 16));
        card.addView(centred(Signature.HANDLE, 15, accent, Typeface.BOLD, 6));
        card.addView(centred(Signature.CRAFT, 14, SignatureTheme.onSurface(this),
                Typeface.NORMAL, 14));
        card.addView(centred(Signature.project(), 14, SignatureTheme.muted(this),
                Typeface.NORMAL, 10));

        card.addView(statsStrip());

        // Place, site, address. Each is a row; two of them open something.
        card.addView(SignatureViews.row(this, SignatureIcon.PIN, Signature.PLACE));

        LinearLayout site = SignatureViews.row(this, SignatureIcon.SHARE, Signature.SITE_LABEL);
        site.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open(Signature.SITE);
            }
        });
        card.addView(site);

        LinearLayout mail = SignatureViews.row(this, SignatureIcon.MAIL, Signature.EMAIL);
        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignatureTrust.feedback(SignatureDeveloperActivity.this, feedbackSubject());
            }
        });
        card.addView(mail);

        card.addView(trustStrip());

        TextView button = SignatureViews.button(this, "View GitHub Profile", accent);
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonParams.topMargin = SignatureViews.dp(this, 16);
        button.setLayoutParams(buttonParams);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open(Signature.GITHUB);
            }
        });
        card.addView(button);

        // Two quiet actions beside the loud one: hand the card to someone, or write back.
        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams actionsParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        actionsParams.topMargin = SignatureViews.dp(this, 10);
        actions.setLayoutParams(actionsParams);

        TextView share = SignatureViews.secondaryButton(this, "Share card");
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignatureTrust.share(SignatureDeveloperActivity.this, shareText());
            }
        });
        actions.addView(share, SignatureViews.share(this, 0, 2));

        TextView feedback = SignatureViews.secondaryButton(this, "Send feedback");
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignatureTrust.feedback(SignatureDeveloperActivity.this, feedbackSubject());
            }
        });
        actions.addView(feedback, SignatureViews.share(this, 1, 2));
        card.addView(actions);
        return card;
    }

    /** The three figures, on a strip one step lighter than the card. */
    private View statsStrip() {
        LinearLayout strip = new LinearLayout(this);
        strip.setOrientation(LinearLayout.HORIZONTAL);
        strip.setGravity(Gravity.CENTER_VERTICAL);
        GradientDrawable background = new GradientDrawable();
        background.setCornerRadius(SignatureViews.dp(this, 18));
        background.setColor(recessed());
        strip.setBackground(background);
        strip.setPadding(SignatureViews.dp(this, 12), SignatureViews.dp(this, 14),
                SignatureViews.dp(this, 12), SignatureViews.dp(this, 14));

        strip.addView(SignatureViews.stat(this, String.valueOf(SignatureDevInfo.REPOSITORIES),
                "Repositories"), SignatureViews.share(this, 0, 3));
        strip.addView(SignatureViews.stat(this, String.valueOf(SignatureDevInfo.APPS_PLANNED),
                "Apps planned"), SignatureViews.share(this, 1, 3));
        strip.addView(SignatureViews.stat(this, String.valueOf(SignatureDevInfo.WITH_CODE),
                "With code today"), SignatureViews.share(this, 2, 3));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = SignatureViews.dp(this, 20);
        strip.setLayoutParams(params);
        return strip;
    }

    /**
     * What can be checked about this build, in three short lines.
     *
     * <p>The certificate line is the signature in the literal sense: the same
     * fingerprint in two Nijatva apps means one key signed both. The privacy line only
     * claims what the manifest actually says.
     */
    private View trustStrip() {
        LinearLayout strip = new LinearLayout(this);
        strip.setOrientation(LinearLayout.VERTICAL);
        GradientDrawable background = new GradientDrawable();
        background.setCornerRadius(SignatureViews.dp(this, 18));
        background.setColor(recessed());
        strip.setBackground(background);
        strip.setPadding(SignatureViews.dp(this, 16), SignatureViews.dp(this, 14),
                SignatureViews.dp(this, 16), SignatureViews.dp(this, 14));

        String version = SignatureTrust.version(this);
        if (version.length() > 0) {
            strip.addView(fact("This build", version));
        }

        String certificate = SignatureTrust.certificate(this);
        if (certificate.length() > 0) {
            strip.addView(fact("APK signing certificate, SHA-256", certificate + "\u2026"));
        }

        if (SignatureTrust.hasInternet(this)) {
            strip.addView(fact("Privacy", "No accounts. Data stays on this device."));
        } else {
            strip.addView(fact("Privacy",
                    "No INTERNET permission. Nothing can leave this device."));
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = SignatureViews.dp(this, 16);
        strip.setLayoutParams(params);
        return strip;
    }

    private View fact(String label, String value) {
        LinearLayout line = new LinearLayout(this);
        line.setOrientation(LinearLayout.VERTICAL);
        line.setPadding(0, SignatureViews.dp(this, 4), 0, SignatureViews.dp(this, 4));

        TextView caption = new TextView(this);
        caption.setText(label);
        caption.setTextColor(SignatureTheme.muted(this));
        caption.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        line.addView(caption);

        TextView text = new TextView(this);
        text.setText(value);
        text.setTextColor(SignatureTheme.onSurface(this));
        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        text.setPadding(0, SignatureViews.dp(this, 2), 0, 0);
        line.addView(text);
        return line;
    }

    /** "Maintains" - the apps that already have a repository, then the planned count. */
    private View maintains() {
        LinearLayout block = new LinearLayout(this);
        block.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = SignatureViews.dp(this, 22);
        block.setLayoutParams(params);

        block.addView(SignatureViews.sectionLabel(this, "Maintains"));

        LinearLayout row = SignatureViews.evenRow(this, 12, 10);
        String[] codes = SignatureDevInfo.APP_CODES;
        int named = Math.min(NAMED, codes.length);
        for (int i = 0; i < named; i++) {
            TextView chip = SignatureViews.pill(this, codes[i]);
            LinearLayout.LayoutParams chipParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (i < named - 1) {
                chipParams.rightMargin = SignatureViews.dp(this, 8);
            }
            SignatureViews.stylePill(this, chip, false, SignatureTheme.accent(this));
            row.addView(chip, chipParams);
        }

        int planned = SignatureDevInfo.APPS_PLANNED - codes.length;
        if (planned > 0) {
            TextView more = SignatureViews.pill(this, "+" + planned);
            more.setBackground(outline());
            more.setTextColor(SignatureTheme.muted(this));
            row.addView(more, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showApps();
            }
        });
        block.addView(row);

        block.addView(SignatureViews.note(this, Signature.figuresNote()));
        return block;
    }

    /**
     * The whole suite, on tap.
     *
     * <p>One list, built from the generated rows: a shipped app opens if it is
     * installed, otherwise its page on the site. A planned app says so.
     */
    private void showApps() {
        final String[][] apps = SignatureDevInfo.APPS;
        final String[] labels = new String[apps.length];
        for (int i = 0; i < apps.length; i++) {
            String code = apps[i][0];
            String name = apps[i][1];
            String pkg = apps[i][4];
            boolean installed = SignatureTrust.isInstalled(this, pkg);
            String state = installed ? "installed"
                    : (apps[i][3].length() > 0 ? "code on GitHub" : "planned");
            labels[i] = code + "  \u00B7  " + name + "  \u00B7  " + state;
        }
        new AlertDialog.Builder(this)
                .setTitle("Nijatva \u2014 " + SignatureDevInfo.APPS_PLANNED + " apps")
                .setItems(labels, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openApp(apps[which]);
                    }
                })
                .setPositiveButton("Close", null)
                .show();
    }

    private void openApp(String[] app) {
        String pkg = app[4];
        if (SignatureTrust.isInstalled(this, pkg)) {
            Intent launch = getPackageManager().getLaunchIntentForPackage(pkg);
            if (launch != null) {
                startActivity(launch);
                return;
            }
        }
        if (app[3].length() > 0) {
            open("https://github.com/vasudev-work/" + app[3]);
            return;
        }
        open(SignatureTrust.pageFor(app[2]));
    }

    /** The small print behind seven taps on the avatar. */
    private void showAbout() {
        StringBuilder text = new StringBuilder();
        text.append(Signature.CRAFT).append("\n\n");
        text.append(Signature.project()).append("\n\n");
        text.append("Suite: ").append(SignatureDevInfo.APPS_PLANNED).append(" apps, ")
                .append(SignatureDevInfo.WITH_CODE).append(" with code today.\n");
        String certificate = SignatureTrust.certificate(this);
        if (certificate.length() > 0) {
            text.append("Signed with certificate ").append(certificate).append("\u2026\n");
        }
        text.append("Figures read on ").append(SignatureDevInfo.FIGURES_DATE).append(".");

        new AlertDialog.Builder(this)
                .setTitle("About this app")
                .setMessage(text.toString())
                .setPositiveButton("Close", null)
                .show();
    }

    private String appLabel() {
        try {
            PackageManager pm = getPackageManager();
            return String.valueOf(pm.getApplicationLabel(pm.getApplicationInfo(getPackageName(), 0)));
        } catch (Exception ignored) {
            return "a Nijatva app";
        }
    }

    private String feedbackSubject() {
        String version = SignatureTrust.version(this);
        return "Feedback for " + appLabel() + (version.length() > 0 ? " v" + version : "");
    }

    private String shareText() {
        return Signature.NAME + " (" + Signature.HANDLE + ")\n"
                + Signature.CRAFT + "\n"
                + Signature.project() + "\n\n"
                + SignatureDevInfo.REPOSITORIES + " repositories, "
                + SignatureDevInfo.APPS_PLANNED + " apps planned, "
                + SignatureDevInfo.WITH_CODE + " with code today.\n"
                + Signature.SITE + "\n"
                + Signature.GITHUB;
    }

    private int recessed() {
        return SignatureTheme.isDark(this) ? 0xFF1D1D21 : 0xFFF2F1ED;
    }

    private GradientDrawable outline() {
        GradientDrawable background = new GradientDrawable();
        background.setCornerRadius(SignatureViews.dp(this, 20));
        background.setColor(0x00000000);
        background.setStroke(SignatureViews.dp(this, 1), SignatureTheme.hairline(this));
        return background;
    }

    private TextView centred(String text, int size, int colour, int style, int topMargin) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextColor(colour);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        view.setTypeface(Typeface.create("sans-serif", style));
        view.setGravity(Gravity.CENTER_HORIZONTAL);
        view.setLineSpacing(SignatureViews.dp(this, 2), 1f);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = SignatureViews.dp(this, topMargin);
        view.setLayoutParams(params);
        return view;
    }

    private void open(String url) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (android.content.ActivityNotFoundException ignored) {
            // No browser to hand the link to; the link text is still on screen.
        }
    }
}
