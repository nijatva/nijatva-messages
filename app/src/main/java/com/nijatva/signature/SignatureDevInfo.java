package com.nijatva.signature;

/**
 * GENERATED FILE - do not edit by hand.
 *
 * Rewritten by {@code tools/generate_devinfo.py}, which reads the figures from
 * GitHub and from {@code tools/apps.json}. Edit those, then run the script.
 *
 * Repositories:  36
 * Apps planned:  24
 * With code:     9
 * Figures read:  17 November 2026
 */
public final class SignatureDevInfo {

    /** Public repositories on github.com/vasudev-work. */
    public static final int REPOSITORIES = 36;

    /** Apps in the Nijatva suite, from tools/apps.json. */
    public static final int APPS_PLANNED = 24;

    /** Apps that already have a repository. */
    public static final int WITH_CODE = 9;

    /** The day the figures above were read, as it appears on the card. */
    public static final String FIGURES_DATE = "17 November 2026";

    /** Short codes of the apps with a repository, in manifest order. */
    public static final String[] APP_CODES = {"Br", "Cm", "Co", "Di", "Fi", "Ga", "Mu", "Me", "Va"};

    /**
     * The whole suite, in manifest order:
     * {code, name, site slug, repository, Android package}.
     *
     * <p>Blank where a value is not known yet. The Developer card lists these, so a
     * planned app and a shipped one are described by the same generated row.
     */
    public static final String[][] APPS = {
        {"Ap", "App Manager", "appmanager", "", ""},
        {"Br", "Browser", "browser", "nijatva-browser", "in.nijatva.browser"},
        {"Ca", "Calculator", "calc", "", ""},
        {"Cl", "Calendar", "calendar", "", ""},
        {"Cm", "Camera", "camera", "nijatva-camera", "com.nijatva.camera"},
        {"Ck", "Clock", "clock", "", ""},
        {"Co", "Contacts", "contacts", "nijatva-contacts", "com.nijatva.contacts"},
        {"Di", "Dialer", "dialer", "nijatva-dialer", "com.nijatva.dialer"},
        {"De", "Device Info", "deviceinfo", "", ""},
        {"Ex", "Expenses", "expenses", "", ""},
        {"Fi", "Files", "files", "nijatva-file", "com.nijatva.file"},
        {"Ga", "Gallery", "gallery", "nijatva-gallery", "com.nijatva.gallery"},
        {"Kb", "Keyboard", "keyboard", "", ""},
        {"La", "Launcher", "launcher", "", ""},
        {"Mu", "Music", "music", "nijatva-music-player", "com.nijatva.music.player"},
        {"No", "Notes", "notes", "", ""},
        {"Pd", "PDF", "pdf", "", ""},
        {"Qr", "QR Code", "qr", "", ""},
        {"Re", "Recorder", "recorder", "", ""},
        {"Rs", "RSS", "rss", "", ""},
        {"Me", "Messages", "sms", "nijatva-messages", "com.nijatva.messages"},
        {"Ta", "Tasks", "tasks", "", ""},
        {"Va", "Vault", "vault", "nijatva-drive", ""},
        {"We", "Weather", "weather", "", ""},
    };

    private SignatureDevInfo() {
    }
}
