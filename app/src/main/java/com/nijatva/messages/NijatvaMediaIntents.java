package com.nijatva.messages;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

/**
 * Soft hand-offs to the other Nijatva apps.
 *
 * Every call is a SOFT dependency: try the matching Nijatva app, else let the system
 * resolve it (chooser), else show a short message. Nothing here throws, so a missing
 * sibling app can never crash this app and no feature is blocked.
 */
public final class NijatvaMediaIntents {

    public static final String PHOTOS = "com.nijatva.photos";
    public static final String GALLERY = "com.nijatva.gallery";
    public static final String CAMERA = "com.nijatva.camera";
    public static final String PHOTO_EDITOR = "com.nijatva.photo.editor";
    public static final String VIDEO_PLAYER = "com.nijatva.videoplayer";
    public static final String MUSIC_PLAYER = "com.nijatva.music.player";
    public static final String FILE_MANAGER = "com.nijatva.file";
    public static final String BROWSER = "in.nijatva.browser";

    private NijatvaMediaIntents() { }

    public static boolean isInstalled(Context c, String pkg) {
        try {
            return c.getPackageManager().getLaunchIntentForPackage(pkg) != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean startOn(Context c, Intent i, String pkg) {
        try {
            c.startActivity(new Intent(i).setPackage(pkg));
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean startAny(Context c, Intent i, String title) {
        try {
            c.startActivity(Intent.createChooser(i, title));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static Intent viewOf(String uri, String mime) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse(uri), mime);
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return i;
    }

    private static boolean nothingFound(Context c, String what) {
        Toast.makeText(c, "No app found to " + what, Toast.LENGTH_SHORT).show();
        return false;
    }

    private static boolean launch(Context c, String pkg, String what) {
        try {
            Intent i = c.getPackageManager().getLaunchIntentForPackage(pkg);
            if (i != null && startOn(c, i, pkg)) return true;
        } catch (Exception ignore) { }
        return nothingFound(c, what);
    }

    /** Opens the Nijatva gallery app. */
    public static boolean openGallery(Context c) {
        return launch(c, GALLERY, "open the gallery");
    }

    /** Opens the Nijatva photos app. */
    public static boolean openPhotos(Context c) {
        return launch(c, PHOTOS, "open the photos app");
    }

    public static boolean openImage(Context c, String uri, String mime) {
        String m = mime == null ? "image/*" : mime;
        Intent i = viewOf(uri, m);
        if (startOn(c, i, GALLERY)) return true;
        if (startOn(c, i, PHOTOS)) return true;
        return startAny(c, i, "Open in") || nothingFound(c, "open this item");
    }

    public static boolean editImage(Context c, String uri, String mime) {
        String m = mime == null ? "image/*" : mime;
        Intent i = new Intent(Intent.ACTION_EDIT);
        i.setDataAndType(Uri.parse(uri), m);
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        if (startOn(c, i, PHOTO_EDITOR)) return true;
        return startAny(c, i, "Edit with") || nothingFound(c, "edit this image");
    }

    public static boolean playVideo(Context c, String uri, String mime) {
        String m = mime == null ? "video/*" : mime;
        Intent i = viewOf(uri, m);
        if (startOn(c, i, VIDEO_PLAYER)) return true;
        return startAny(c, i, "Play with") || nothingFound(c, "play this video");
    }

    public static boolean playAudio(Context c, String uri, String mime) {
        String m = mime == null ? "audio/*" : mime;
        Intent i = viewOf(uri, m);
        if (startOn(c, i, MUSIC_PLAYER)) return true;
        return startAny(c, i, "Play with") || nothingFound(c, "play this audio");
    }

    public static boolean openCamera(Context c) {
        try {
            Intent launch = c.getPackageManager().getLaunchIntentForPackage(CAMERA);
            if (launch != null && startOn(c, launch, CAMERA)) return true;
        } catch (Exception ignored) { }
        try {
            c.startActivity(new Intent("android.media.action.STILL_IMAGE_CAMERA"));
            return true;
        } catch (Exception e) {
            return nothingFound(c, "open the camera");
        }
    }

    public static boolean openFileManager(Context c) {
        try {
            Intent launch = c.getPackageManager().getLaunchIntentForPackage(FILE_MANAGER);
            if (launch != null && startOn(c, launch, FILE_MANAGER)) return true;
        } catch (Exception ignored) { }
        return nothingFound(c, "open the file manager");
    }

    public static boolean openInBrowser(Context c, String url) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        if (startOn(c, i, BROWSER)) return true;
        return startAny(c, i, "Open with") || nothingFound(c, "open this link");
    }

    public static void shareMedia(Context c, String uri, String mime) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType(mime);
        i.putExtra(Intent.EXTRA_STREAM, Uri.parse(uri));
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startAny(c, i, "Share");
    }
}
