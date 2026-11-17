package com.nijatva.signature;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * The handful of views the two signature screens are built from, in one place so
 * Appearance and Developer cannot drift apart: same card radius, same pill height,
 * same note colour.
 *
 * <p>Every screen is built in code. That keeps the kit to source files with no
 * layout XML to merge into six different res/ folders.
 */
public final class SignatureViews {

    private SignatureViews() {
    }

    /** A vertical scroll body on the chosen surface. */
    public static ScrollView screen(Activity activity) {
        ScrollView scroll = new ScrollView(activity);
        scroll.setBackgroundColor(SignatureTheme.background(activity));
        scroll.setFillViewport(true);
        return scroll;
    }

    /** The back arrow and title at the top of every signature screen. */
    public static LinearLayout header(final Activity activity, String title) {
        LinearLayout head = new LinearLayout(activity);
        head.setOrientation(LinearLayout.HORIZONTAL);
        head.setGravity(Gravity.CENTER_VERTICAL);
        head.setPadding(dp(activity, 16), dp(activity, 14), dp(activity, 16), dp(activity, 14));

        SignatureIcon back = new SignatureIcon(activity, SignatureIcon.BACK,
                SignatureTheme.onSurface(activity));
        back.setLayoutParams(new LinearLayout.LayoutParams(dp(activity, 26), dp(activity, 26)));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
        head.addView(back);

        TextView label = new TextView(activity);
        label.setText(title);
        label.setTextColor(SignatureTheme.onSurface(activity));
        label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        label.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        label.setPadding(dp(activity, 16), 0, 0, 0);
        head.addView(label);
        head.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return head;
    }

    /** The body under the header: side padding, breathing room at the bottom. */
    public static LinearLayout body(Activity activity) {
        LinearLayout body = new LinearLayout(activity);
        body.setOrientation(LinearLayout.VERTICAL);
        body.setPadding(dp(activity, 16), 0, dp(activity, 16), dp(activity, 32));
        body.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return body;
    }

    /** A rounded card with a hairline border. */
    public static LinearLayout card(Context context) {
        LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.VERTICAL);
        GradientDrawable background = new GradientDrawable();
        background.setColor(SignatureTheme.surface(context));
        background.setCornerRadius(dp(context, 22));
        background.setStroke(dp(context, 1), SignatureTheme.hairline(context));
        card.setBackground(background);
        card.setPadding(dp(context, 20), dp(context, 20), dp(context, 20), dp(context, 20));
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        card.setLayoutParams(params);
        return card;
    }

    /** "Theme", "Accent colour", "Maintains" - the bold label above a row. */
    public static TextView sectionLabel(Context context, String text) {
        TextView label = new TextView(context);
        label.setText(text);
        label.setTextColor(SignatureTheme.onSurface(context));
        label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        label.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        return label;
    }

    /** A one-line chip: theme names and the maintainer's app codes. */
    public static TextView pill(Context context, String text) {
        TextView pill = new TextView(context);
        pill.setText(text);
        pill.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        pill.setGravity(Gravity.CENTER);
        pill.setMaxLines(1);
        pill.setSingleLine(true);
        pill.setEllipsize(TextUtils.TruncateAt.END);
        pill.setPadding(dp(context, 12), dp(context, 11), dp(context, 12), dp(context, 11));
        pill.setClickable(true);
        return pill;
    }

    /** Fills or outlines a chip depending on whether it is the current choice. */
    public static void stylePill(Context context, TextView pill, boolean selected, int accent) {
        GradientDrawable background = new GradientDrawable();
        background.setCornerRadius(dp(context, 20));
        if (selected) {
            background.setColor(accent);
            background.setStroke(dp(context, 1), accent);
            pill.setTextColor(SignatureTheme.onAccent(accent));
            pill.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        } else {
            background.setColor(0x00000000);
            background.setStroke(dp(context, 1), SignatureTheme.hairline(context));
            pill.setTextColor(SignatureTheme.onSurface(context));
            pill.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
        }
        pill.setBackground(background);
    }

    /** An accent swatch with its name inside it, outlined when selected. */
    public static TextView swatch(Context context, String text, int colour, boolean selected) {
        TextView swatch = pill(context, text);
        GradientDrawable background = new GradientDrawable();
        background.setCornerRadius(dp(context, 20));
        background.setColor(colour);
        background.setStroke(dp(context, selected ? 3 : 1),
                selected ? SignatureTheme.onSurface(context) : SignatureTheme.hairline(context));
        swatch.setBackground(background);
        swatch.setTextColor(SignatureTheme.onAccent(colour));
        swatch.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        return swatch;
    }

    /** The muted paragraph under a row that says what the choice means. */
    public static TextView note(Context context, String text) {
        TextView note = new TextView(context);
        note.setText(text);
        note.setTextColor(SignatureTheme.muted(context));
        note.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        note.setLineSpacing(dp(context, 3), 1f);
        return note;
    }

    /** A full-width filled button in the accent. */
    public static TextView button(Context context, String text, int accent) {
        TextView button = new TextView(context);
        button.setText(text);
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        button.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        button.setGravity(Gravity.CENTER);
        button.setPadding(dp(context, 16), dp(context, 15), dp(context, 16), dp(context, 15));
        GradientDrawable background = new GradientDrawable();
        background.setCornerRadius(dp(context, 26));
        background.setColor(accent);
        button.setBackground(background);
        button.setTextColor(SignatureTheme.onAccent(accent));
        button.setClickable(true);
        return button;
    }

    /** The quiet counterpart of {@link #button}: an outline, for a second action. */
    public static TextView secondaryButton(Context context, String text) {
        TextView button = new TextView(context);
        button.setText(text);
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        button.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        button.setGravity(Gravity.CENTER);
        button.setPadding(dp(context, 12), dp(context, 13), dp(context, 12), dp(context, 13));
        GradientDrawable background = new GradientDrawable();
        background.setCornerRadius(dp(context, 22));
        background.setColor(0x00000000);
        background.setStroke(dp(context, 1), SignatureTheme.hairline(context));
        button.setBackground(background);
        button.setTextColor(SignatureTheme.onSurface(context));
        button.setClickable(true);
        button.setMaxLines(1);
        button.setSingleLine(true);
        button.setEllipsize(TextUtils.TruncateAt.END);
        return button;
    }

    /** An icon and a line of text, the way the Developer card lists a place or an address. */
    public static LinearLayout row(Context context, int icon, String text) {
        LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(context, 4), dp(context, 9), dp(context, 4), dp(context, 9));
        row.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        SignatureIcon glyph = new SignatureIcon(context, icon, SignatureTheme.muted(context));
        glyph.setLayoutParams(new LinearLayout.LayoutParams(dp(context, 20), dp(context, 20)));
        row.addView(glyph);

        TextView label = new TextView(context);
        label.setText(text);
        label.setTextColor(SignatureTheme.onSurface(context));
        label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        label.setPadding(dp(context, 16), 0, 0, 0);
        row.addView(label);
        return row;
    }

    /**
     * A settings row that opens one of the signature screens: title, a muted line
     * under it, and a chevron on the right.
     *
     * <p>Every app's settings screen is built differently, so the kit hands back a
     * finished row rather than asking each app to draw one.
     */
    public static LinearLayout linkRow(Context context, String title, String subtitle) {
        LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(context, 4), dp(context, 13), dp(context, 4), dp(context, 13));
        row.setClickable(true);
        row.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout text = new LinearLayout(context);
        text.setOrientation(LinearLayout.VERTICAL);
        text.setLayoutParams(new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView name = new TextView(context);
        name.setText(title);
        name.setTextColor(SignatureTheme.onSurface(context));
        name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        text.addView(name);

        if (subtitle != null && subtitle.length() > 0) {
            TextView detail = new TextView(context);
            detail.setText(subtitle);
            detail.setTextColor(SignatureTheme.muted(context));
            detail.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            detail.setPadding(0, dp(context, 3), 0, 0);
            text.addView(detail);
        }
        row.addView(text);

        TextView chevron = new TextView(context);
        chevron.setText("\u203A");
        chevron.setTextColor(SignatureTheme.muted(context));
        chevron.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        chevron.setPadding(dp(context, 8), 0, 0, 0);
        row.addView(chevron);
        return row;
    }

    /** One column of the three-figure stats strip: a big number over a small label. */
    public static LinearLayout stat(Context context, String value, String label) {
        LinearLayout column = new LinearLayout(context);
        column.setOrientation(LinearLayout.VERTICAL);
        column.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView number = new TextView(context);
        number.setText(value);
        number.setTextColor(SignatureTheme.onSurface(context));
        number.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        number.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        column.addView(number);

        TextView caption = new TextView(context);
        caption.setText(label);
        caption.setTextColor(SignatureTheme.muted(context));
        caption.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        caption.setGravity(Gravity.CENTER_HORIZONTAL);
        caption.setPadding(0, dp(context, 3), 0, 0);
        column.addView(caption);
        return column;
    }

    public static int dp(Context context, int value) {
        return SignatureTheme.dp(context, value);
    }

    /** A row of chips that share the width evenly, so four themes never wrap. */
    public static LinearLayout evenRow(Context context, int top, int bottom) {
        LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, dp(context, top), 0, dp(context, bottom));
        return row;
    }

    /** Layout params that give every child an equal share of a row. */
    public static LinearLayout.LayoutParams share(Context context, int index, int count) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        if (index < count - 1) {
            params.setMarginEnd(dp(context, 6));
        }
        return params;
    }
}
