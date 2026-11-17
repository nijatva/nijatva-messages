package com.nijatva.signature;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.View;
import android.view.Window;

/**
 * Theme and accent for the Nijatva signature screens, shared by every app in the suite.
 *
 * <p>Four surfaces (System, Dark, Light, Black) and three accents (Green, Saffron,
 * Indigo). The choice is stored under one key name in every app, so a screen looks
 * the same whichever app you opened it from, and an app can read {@link #accent(Context)}
 * to tint its own chrome with the same colour.
 *
 * <p>Nothing here needs AndroidX, Kotlin or a resource file: it is plain framework
 * code so it can be dropped into any of the apps, Java or Kotlin, minSdk 21 and up.
 */
public final class SignatureTheme {

    /** Follow the device. */
    public static final int SYSTEM = 0;
    /** One step up from black; the suite's default dark surface. */
    public static final int DARK = 1;
    public static final int LIGHT = 2;
    /** Pure black for OLED panels. */
    public static final int BLACK = 3;

    /** Nijatva green: the default accent. */
    public static final int GREEN = 0xFF1E7A46;
    public static final int SAFFRON = 0xFFD98431;
    public static final int INDIGO = 0xFF3F45A8;

    public static final String[] THEME_NAMES = {"System", "Dark", "Light", "Black"};
    public static final int[] THEME_VALUES = {SYSTEM, DARK, LIGHT, BLACK};

    public static final String[] ACCENT_NAMES = {"Green", "Saffron", "Indigo"};
    public static final int[] ACCENT_VALUES = {GREEN, SAFFRON, INDIGO};

    private static final String PREFS = "nijatva_signature";
    private static final String KEY_THEME = "theme";
    private static final String KEY_ACCENT = "accent";

    private SignatureTheme() {
    }

    public static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    /**
     * Seeds the store the first time a screen is shown.
     *
     * <p>For an app with no theme engine of its own - the SMS app and the contacts app
     * are dark-only by design - this makes the signature screens open in the app's own
     * surface instead of the device's. A choice the user makes later is never
     * overwritten: this only writes when nothing is stored yet.
     */
    public static void seedDefault(Context context, int theme, int accent) {
        SharedPreferences prefs = prefs(context);
        if (!prefs.contains(KEY_THEME)) {
            prefs.edit().putInt(KEY_THEME, theme).putInt(KEY_ACCENT, accent).apply();
        }
    }

    public static int theme(Context context) {
        return prefs(context).getInt(KEY_THEME, SYSTEM);
    }

    public static int accent(Context context) {
        return prefs(context).getInt(KEY_ACCENT, GREEN);
    }

    public static void setTheme(Context context, int theme) {
        prefs(context).edit().putInt(KEY_THEME, theme).apply();
    }

    public static void setAccent(Context context, int accent) {
        prefs(context).edit().putInt(KEY_ACCENT, accent).apply();
    }

    /** True when the surface in use is a dark one, SYSTEM resolved against the device. */
    public static boolean isDark(Context context) {
        int theme = theme(context);
        if (theme == DARK || theme == BLACK) {
            return true;
        }
        if (theme == LIGHT) {
            return false;
        }
        int mode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return mode == Configuration.UI_MODE_NIGHT_YES;
    }

    public static int background(Context context) {
        if (!isDark(context)) {
            return 0xFFF6F5F2;
        }
        return theme(context) == BLACK ? 0xFF000000 : 0xFF0B0B0C;
    }

    public static int surface(Context context) {
        if (!isDark(context)) {
            return 0xFFFFFFFF;
        }
        return theme(context) == BLACK ? 0xFF0E0E10 : 0xFF141416;
    }

    public static int onSurface(Context context) {
        return isDark(context) ? 0xFFFFFFFF : 0xFF121214;
    }

    public static int muted(Context context) {
        return isDark(context) ? 0xFFA8A8AE : 0xFF5B5B63;
    }

    public static int hairline(Context context) {
        return isDark(context) ? 0xFF26262A : 0xFFDCDAD4;
    }

    /**
     * Text colour that stays legible on top of an accent.
     *
     * <p>The cut-off is 0.179 relative luminance, the point where black and white
     * have the same contrast ratio against a colour. Using 0.5 instead would put
     * white on saffron at about 2.9:1, under the 4.5:1 body text needs.
     */
    public static int onAccent(int accent) {
        double luminance = 0.2126 * channel((accent >> 16) & 0xFF)
                + 0.7152 * channel((accent >> 8) & 0xFF)
                + 0.0722 * channel(accent & 0xFF);
        return luminance > 0.179 ? 0xFF101820 : 0xFFFFFFFF;
    }

    private static double channel(int value) {
        double c = value / 255.0;
        return c <= 0.03928 ? c / 12.92 : Math.pow((c + 0.055) / 1.055, 2.4);
    }

    /** Accent mixed toward the surface, for avatar discs and soft fills. */
    public static int soft(int accent, int alpha) {
        return (alpha << 24) | (accent & 0x00FFFFFF);
    }

    /** Paints the window in the chosen surface and keeps the system bars in step. */
    public static void applyTo(Activity activity) {
        Window window = activity.getWindow();
        int background = background(activity);
        window.setBackgroundDrawable(new ColorDrawable(background));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(background);
            window.setNavigationBarColor(background);
        }
        if (!isDark(activity) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    public static int dp(Context context, int value) {
        return (int) (value * context.getResources().getDisplayMetrics().density + 0.5f);
    }
}
