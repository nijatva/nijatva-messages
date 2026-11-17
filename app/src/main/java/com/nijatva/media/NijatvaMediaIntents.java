package com.nijatva.media;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

/**
 * Soft hand-offs to the other Nijatva apps.
 *
 * Every call is a SOFT dependency: try the matching Nijatva app, else let the system
 * resolve it (a chooser), else show a short message. Nothing here throws, so a missing
 * sibling app can never crash this app and no feature is blocked.
 *
 * The packages match the Kotlin helper the media apps ship, so one family talks to
 * itself the same way from a Java app as from a Kotlin one. Contacts, dialer and
 * messages are Java apps, which is why this copy is Java: adding the Kotlin plugin to
 * three apps just to share forty lines would be a worse trade than one duplicated file
 * per app.
 *
 * Family: Photos com.nijatva.photos - Gallery com.nijatva.gallery - Camera
 * com.nijatva.camera - Photo Editor com.nijatva.photo.editor - Video Player
 * com.nijatva.videoplayer - Music Player com.nijatva.music.player - File Manager
 * com.nijatva.file - Browser in.nijatva.browser
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

    private NijatvaMediaIntents() {
    }

    /** True when the app is installed and has something to launch. */
    public static boolean isInstalled(Context context, String pkg) {
        try {
            return context.getPackageManager().getLaunchIntentForPackage(pkg) != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean startOn(Context context, Intent intent, String pkg) {
        try {
            context.startActivity(new Intent(intent).setPackage(pkg));
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean startAny(Context context, Intent intent, String title) {
        try {
            context.startActivity(Intent.createChooser(intent, title));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static Intent viewOf(String uri, String mime) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(uri), mime == null ? "*/*" : mime);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return intent;
    }

    private static boolean nothingFound(Context context, String what) {
        Toast.makeText(context, "No app found to " + what, Toast.LENGTH_SHORT).show();
        return false;
    }

    /** Opens an image or video in Nijatva Gallery, else Photos, else any viewer. */
    public static boolean openImage(Context context, String uri, String mime) {
        Intent intent = viewOf(uri, mime == null ? "image/*" : mime);
        if (startOn(context, intent, GALLERY)) return true;
        if (startOn(context, intent, PHOTOS)) return true;
        return startAny(context, intent, "Open in") || nothingFound(context, "open this item");
    }

    /** ACTION_EDIT in Nijatva Photo Editor, else any editor. */
    public static boolean editImage(Context context, String uri, String mime) {
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setDataAndType(Uri.parse(uri), mime == null ? "image/*" : mime);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        if (startOn(context, intent, PHOTO_EDITOR)) return true;
        return startAny(context, intent, "Edit with") || nothingFound(context, "edit this image");
    }

    /** Plays a video in Nijatva Video Player, else any player. */
    public static boolean playVideo(Context context, String uri, String mime) {
        Intent intent = viewOf(uri, mime == null ? "video/*" : mime);
        if (startOn(context, intent, VIDEO_PLAYER)) return true;
        return startAny(context, intent, "Play with") || nothingFound(context, "play this video");
    }

    /** Plays audio in Nijatva Music Player, else any player. */
    public static boolean playAudio(Context context, String uri, String mime) {
        Intent intent = viewOf(uri, mime == null ? "audio/*" : mime);
        if (startOn(context, intent, MUSIC_PLAYER)) return true;
        return startAny(context, intent, "Play with") || nothingFound(context, "play this audio");
    }

    /** Opens Nijatva Camera, falling back to the system still-image camera. */
    public static boolean openCamera(Context context) {
        Intent launch = context.getPackageManager().getLaunchIntentForPackage(CAMERA);
        if (launch != null && startOn(context, launch, CAMERA)) return true;
        try {
            context.startActivity(new Intent("android.media.action.STILL_IMAGE_CAMERA"));
            return true;
        } catch (Exception e) {
            return nothingFound(context, "open the camera");
        }
    }

    /** Opens Nijatva File Manager. */
    public static boolean openFileManager(Context context) {
        Intent launch = context.getPackageManager().getLaunchIntentForPackage(FILE_MANAGER);
        if (launch != null && startOn(context, launch, FILE_MANAGER)) return true;
        return nothingFound(context, "open the file manager");
    }

    /** Opens a URL in Nijatva Browser, else any browser. */
    public static boolean openInBrowser(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        if (startOn(context, intent, BROWSER)) return true;
        return startAny(context, intent, "Open with") || nothingFound(context, "open this link");
    }

    /** Shares through the system sheet, always with a content:// uri. */
    public static void shareMedia(Context context, String uri, String mime) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(mime == null ? "*/*" : mime);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(uri));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startAny(context, intent, "Share");
    }
}
