package com.nijatva.signature;

import android.content.Context;
import android.content.Intent;

/**
 * Who made the app, and the two screens that say so.
 *
 * <p>Every Nijatva app carries this, unchanged, so the signature reads the same
 * whether it is opened from the dialer, the gallery or the file manager. The
 * figures come from {@link SignatureDevInfo}, which a build-time script regenerates
 * from GitHub and the site.
 */
public final class Signature {

    public static final String NAME = "Vasudev Dangaya";
    public static final String HANDLE = "@vasudev-work";
    public static final String INITIALS = "VD";
    public static final String PLACE = "India";
    public static final String SITE = "https://vasudev-work.github.io";
    public static final String SITE_LABEL = "vasudev-work.github.io";
    public static final String GITHUB = "https://github.com/vasudev-work";
    public static final String EMAIL = "Vasudev.work@rediffmail.com";

    public static final String CRAFT =
            "Builds Android apps in Kotlin and Jetpack Compose, without Google Play Services.";

    private Signature() {
    }

    /** "Nijatva is the current project: 24 small apps, no accounts, no ads, no analytics." */
    public static String project() {
        return "Nijatva is the current project: " + SignatureDevInfo.APPS_PLANNED
                + " small apps, no accounts, no ads, no analytics.";
    }

    /** The line under the maintainer's chips, kept in step with the generated figures. */
    public static String figuresNote() {
        int planned = SignatureDevInfo.APPS_PLANNED - SignatureDevInfo.WITH_CODE;
        return SignatureDevInfo.WITH_CODE + " of the " + SignatureDevInfo.APPS_PLANNED
                + " apps have a repository today; the rest " + (planned == 1 ? "is" : "are")
                + " planned. Figures read on " + SignatureDevInfo.FIGURES_DATE + ".";
    }

    public static Intent appearance(Context context) {
        return new Intent(context, SignatureAppearanceActivity.class);
    }

    public static Intent developer(Context context) {
        return new Intent(context, SignatureDeveloperActivity.class);
    }
}
