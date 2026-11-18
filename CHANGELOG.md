# Changelog

## 1.3 — 18 November 2026

- **Fixed:** the APK shipped without a `<uses-sdk>` block, so minSdk and targetSdk were missing from the manifest. Now declares minSdk 21 / targetSdk 34, which also lets aapt2 drop redundant per-version resources (smaller APK).
- Icon: uses the app's own drawable / adaptive foreground, matching the website

## 1.2 — 18 November 2026

- New app icon: bold outlined glyph, centred in the slate rounded square
- Landing page added under `docs/` (GitHub Pages)

## 1.1 — 17 November 2026

- Added the Nijatva family media helper (`NijatvaMediaIntents`)
- New "Nijatva apps" section on the Developer screen (Gallery, Photos, Camera, Files)

## 1.0 — 17 November 2026

First public release.

- Conversation list with contact names, initials and timestamps
- Smart categories: All, Personal, OTP, Others
- Search across names, numbers and message text
- Conversation view with message bubbles and reply box
- New message composer and smsto:/sms: link support
- Optional default SMS app role with incoming/sent message storage
- Dark interface, no ads, no trackers, no internet permission
