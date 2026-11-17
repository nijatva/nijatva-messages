# Nijatva Messages

A fast, private SMS app for Android with smart categories, OTP detection and a dark interface.

![minSdk](https://img.shields.io/badge/minSdk-21-blue) ![targetSdk](https://img.shields.io/badge/targetSdk-34-green) ![license](https://img.shields.io/badge/license-Apache--2.0-orange)

## Features

- **Conversation list** with contact names, initials and timestamps
- **Smart categories** — All, Personal, OTP and Others (one-time passwords are detected automatically)
- **Search** across names, numbers and message text
- **Conversation view** with message bubbles and a reply box
- **New message** composer, plus `smsto:` / `sms:` link support
- **Default SMS app support** — optional; when set, incoming and sent messages are stored in the system database
- Dark interface, no ads, no trackers, **no internet permission**
- Lightweight: about 25 KB APK, no third-party libraries

## Download

Grab the latest APK from the [Releases](../../releases) page.

## Build

Pure framework Java — no AndroidX, no third-party libraries.

```
./gradlew assembleRelease
```

Signing configuration is intentionally omitted — use your own keystore.

## Becoming the default SMS app

Android only lets the default SMS app write to the message database. Open the app and tap the banner at the top (or the gear icon) to be taken to the system dialog. Nijatva Messages declares all the components Android requires for that role (`SMS_DELIVER`, `WAP_PUSH_DELIVER`, `RESPOND_VIA_MESSAGE` and `SENDTO`).

Without the default role the app still reads existing conversations and can send messages, but sent messages are not stored in the system database.

## Permissions

SMS (read/send/receive), Contacts (read, for names) and Notifications. No internet permission.

## License

Apache License 2.0 — see [LICENSE](LICENSE).
