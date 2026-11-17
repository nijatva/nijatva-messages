package com.nijatva.signature;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.security.MessageDigest;
import java.util.Locale;

/**
 * The few facts about the running app that can be checked on the device rather than
 * claimed on a card: which version it is, which key signed it, whether it can reach
 * the network, and which sibling Nijatva apps are installed.
 *
 * <p>Everything here is read from {@link PackageManager}. Nothing is asserted that the
 * device cannot confirm, which is why the card says "signed by" and "no INTERNET
 * permission" instead of a badge.
 */
public final class SignatureTrust {

    private SignatureTrust() {
    }

    /** "1.3 (4)" - the version name and code of the app the card is shown in. */
    public static String version(Context context) {
        try {
            PackageInfo info = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            String name = info.versionName == null ? "1.0" : info.versionName;
            return name + " (" + info.versionCode + ")";
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * The first 16 hex characters of the SHA-256 of the certificate that signed this
     * APK.
     *
     * <p>This is the one claim on the card a user can verify for themselves: every
     * Nijatva app is signed with the same key, so the same fingerprint appearing in
     * two apps is proof they came from the same hand.
     */
    public static String certificate(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            android.content.pm.Signature[] signatures = info.signatures;
            if (signatures == null || signatures.length == 0) {
                return "";
            }
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(signatures[0].toByteArray());
            StringBuilder hex = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                hex.append(String.format(Locale.US, "%02X", digest[i]));
            }
            return hex.toString();
        } catch (Exception ignored) {
            return "";
        }
    }

    /** True when this app asks for the INTERNET permission at all. */
    public static boolean hasInternet(Context context) {
        return context.getPackageManager()
                .checkPermission("android.permission.INTERNET", context.getPackageName())
                == PackageManager.PERMISSION_GRANTED;
    }

    /** True when a sibling app is installed and has something to launch. */
    public static boolean isInstalled(Context context, String pkg) {
        if (pkg == null || pkg.length() == 0) {
            return false;
        }
        try {
            return context.getPackageManager().getLaunchIntentForPackage(pkg) != null;
        } catch (Exception ignored) {
            return false;
        }
    }

    /** Hands the card to whatever the user shares with. Text only: no image, no permission. */
    public static void share(Context context, String text) {
        Intent send = new Intent(Intent.ACTION_SEND);
        send.setType("text/plain");
        send.putExtra(Intent.EXTRA_TEXT, text);
        Intent chooser = Intent.createChooser(send, "Share the developer card");
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(chooser);
        } catch (Exception ignored) {
            // No share sheet on this device.
        }
    }

    /** Opens the mail app with the subject already filled in. */
    public static void feedback(Context context, String subject) {
        Intent mail = new Intent(Intent.ACTION_SENDTO,
                android.net.Uri.parse("mailto:" + Signature.EMAIL));
        mail.putExtra(Intent.EXTRA_SUBJECT, subject);
        try {
            context.startActivity(mail);
        } catch (Exception ignored) {
            // No mail client; the address is on the card anyway.
        }
    }

    /** The public page for one app in the suite, or the site root when it has none. */
    public static String pageFor(String slug) {
        if (slug == null || slug.length() == 0) {
            return Signature.SITE;
        }
        return Signature.SITE + "/apps/" + slug + ".html";
    }
}
