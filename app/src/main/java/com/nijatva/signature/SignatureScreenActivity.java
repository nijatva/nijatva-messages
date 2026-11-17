package com.nijatva.signature;

import android.app.Activity;

/**
 * Base for the two signature screens.
 *
 * <p>An app that already has a theme engine of its own (the file manager and the
 * dialer both do) should not grow a second one. Subclass this, override the three
 * hooks to read and write that engine, and the screens render in the app's real
 * theme while the choice lands in the app's real settings.
 *
 * <p>{@link #mirror()} copies the app's stored choice into the suite store before a
 * screen is built, so the kit's own colours and the app's agree whichever of the two
 * wrote last.
 */
public abstract class SignatureScreenActivity extends Activity {

    /** The theme this app is in: one of {@link SignatureTheme#SYSTEM} and friends. */
    protected int storedTheme() {
        return SignatureTheme.theme(this);
    }

    /** The accent this app is in, as an ARGB colour. */
    protected int storedAccent() {
        return SignatureTheme.accent(this);
    }

    /** Saves a choice. The default writes the suite store. */
    protected void store(int theme, int accent) {
        SignatureTheme.setTheme(this, theme);
        SignatureTheme.setAccent(this, accent);
    }

    /** Pulls the app's own choice into the suite store before drawing. */
    protected final void mirror() {
        SignatureTheme.setTheme(this, storedTheme());
        SignatureTheme.setAccent(this, storedAccent());
    }
}
