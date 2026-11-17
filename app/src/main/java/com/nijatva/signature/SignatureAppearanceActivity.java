package com.nijatva.signature;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Appearance: the surface and the accent, nothing else.
 *
 * <p>Four themes across the top, three accents under them, and a line each that
 * says what the choice does. A choice is applied and stored the moment it is
 * tapped and the screen redraws itself, so there is no Apply button: a theme is
 * something you judge by looking at it.
 *
 * <p>There is deliberately no preview block here. A live screen is the preview.
 */
public class SignatureAppearanceActivity extends SignatureScreenActivity {

    private LinearLayout themeRow;
    private LinearLayout accentRow;
    private TextView themeNote;
    private TextView accentNote;
    private TextView privacyNote;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        mirror();
        SignatureTheme.applyTo(this);
        setContentView(build());
    }

    @Override
    protected void onResume() {
        super.onResume();
        render();
    }

    private View build() {
        ScrollView scroll = SignatureViews.screen(this);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(SignatureTheme.background(this));
        root.addView(SignatureViews.header(this, "Appearance"));

        LinearLayout body = SignatureViews.body(this);

        // Theme
        body.addView(SignatureViews.sectionLabel(this, "Theme"));
        themeRow = SignatureViews.evenRow(this, 14, 12);
        body.addView(themeRow);
        themeNote = SignatureViews.note(this, "");
        body.addView(themeNote);

        // Accent
        LinearLayout.LayoutParams accentLabel = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        accentLabel.topMargin = SignatureViews.dp(this, 26);
        TextView accentTitle = SignatureViews.sectionLabel(this, "Accent colour");
        accentTitle.setLayoutParams(accentLabel);
        body.addView(accentTitle);

        accentRow = SignatureViews.evenRow(this, 14, 12);
        body.addView(accentRow);
        accentNote = SignatureViews.note(this, "");
        body.addView(accentNote);

        // Where it is stored
        LinearLayout.LayoutParams privacyParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        privacyParams.topMargin = SignatureViews.dp(this, 28);
        privacyNote = SignatureViews.note(this, "");
        privacyNote.setLayoutParams(privacyParams);
        body.addView(privacyNote);

        root.addView(body);
        scroll.addView(root, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return scroll;
    }

    /** Rebuilds the chips and the copy from whatever is stored right now. */
    private void render() {
        int accent = storedAccent();
        int theme = storedTheme();

        themeRow.removeAllViews();
        for (int i = 0; i < SignatureTheme.THEME_NAMES.length; i++) {
            final int value = SignatureTheme.THEME_VALUES[i];
            TextView pill = SignatureViews.pill(this, SignatureTheme.THEME_NAMES[i]);
            SignatureViews.stylePill(this, pill, value == theme, accent);
            pill.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    store(value, storedAccent());
                    mirror();
                    recreate();
                }
            });
            themeRow.addView(pill, SignatureViews.share(this, i, SignatureTheme.THEME_NAMES.length));
        }

        accentRow.removeAllViews();
        for (int i = 0; i < SignatureTheme.ACCENT_NAMES.length; i++) {
            final int value = SignatureTheme.ACCENT_VALUES[i];
            TextView swatch = SignatureViews.swatch(this, SignatureTheme.ACCENT_NAMES[i],
                    value, value == accent);
            swatch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    store(storedTheme(), value);
                    mirror();
                    recreate();
                }
            });
            accentRow.addView(swatch, SignatureViews.share(this, i, SignatureTheme.ACCENT_NAMES.length));
        }

        themeNote.setText("Black uses a pure #000000 background for OLED screens and keeps the "
                + "accent you pick. System follows the device.");
        accentNote.setText("Each accent carries its own label colour, so text on a filled chip "
                + "stays at or above WCAG AA contrast.");
        privacyNote.setText("Saved on this device only. Nothing about your choice leaves this phone.");
    }
}
