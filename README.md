# Math Quest for Android

Offline family math game. Based on the v13 game; local profiles, streak-based rewards, Easy multiple choice / Hard typed answers, weak-fact review, missions, badges, seasonal worlds and parent reward requests. This is an independent family game, not affiliated with Roblox. Parents supply rewards manually.

## Install and keep progress

Install `MathQuest.apk`. Android may ask you to allow installation from the app you opened the download with. The game supports Android 8.0+ and needs an up-to-date Android System WebView. No server or child email account is required.

To move from the HTML edition: export a backup from that game, launch the APK, and use Restore backup on the opening screen. The APK cannot directly read browser-local progress. Existing v10/v11/v12 backup data migrates on import. Exported backups contain the parent PIN and emails: keep them private.

Future signed APK updates keep the same application ID and HTTPS asset origin, preserving localStorage. Install updates over the existing app. **Do not uninstall or clear app data** unless you have an exported backup.

## Updating installed copies

The app checks `https://github.com/cpaul1988/MathQuest/releases/latest/download/update.json` on launch, throttled to six hours after a successful check. More → Check for updates runs an immediate check. This is polling when the app opens, not instant background push and not silent installation. Offline devices receive the offer after reconnecting and opening the game.

A user approves the download; the app checks SHA-256, package ID, version code and the original signing certificate, then Android requests installation consent. An interrupted/invalid download does not replace the installed game. When Android requires it, enable “Allow from this source” for Math Quest and return to the app.

## First GitHub setup (Windows)

The source ZIP contains no signing key. Extract the separate **private signing backup** somewhere safe outside this source folder. Do not upload that backup to GitHub or share it with players. It is required to keep future APKs compatible with the first APK.

1. Install Git and GitHub CLI; run `gh auth login` for `cpaul1988`.
2. In PowerShell in this source folder run:

   ```powershell
   powershell -ExecutionPolicy Bypass -File .\Publish-MathQuest.ps1 -SigningFolder "C:\Private\MathQuest-Signing"
   ```

   Point the argument at the extracted folder containing `mathquest.jks` and `signing.json`.
3. The script creates public `cpaul1988/MathQuest`, sets three encrypted Actions secrets, pushes only the source, and triggers the build/release workflow. It does not force-push over existing history.
4. Watch Actions → Build and release Android. A successful run publishes `MathQuest.apk`, `update.json` and the APK checksum to Releases.

The update URL will not work until that public repository and first release exist. The app remains playable offline before publication.

## Publish the next update

Change the game in `app/src/main/assets/index.html` or Android code. Increment **both** `versionCode` (integer) and `versionName` in `version.properties`; update the changelog. Commit/push to main. GitHub Actions tests, signs, builds, lints, and publishes the new release. Existing version tags are not overwritten. Never generate a replacement signing key for routine updates.

The default repository is wired into `app/build.gradle` and `scripts/make_manifest.py`. Change both before the first release if choosing a different repository. Keep the app ID and signing key unchanged thereafter.

## Error handling and privacy

- JavaScript failures open a reload-from-saved-progress screen; sound errors cannot interrupt scoring.
- Saving conflicts/storage failures stop play instead of overwriting saved progress.
- Malformed backups are rejected; imports require a parent PIN and explicit replacement confirmation.
- Missing email/file apps display recovery instructions. Reward requests remain pending locally.
- More → Export error report saves a diagnostic text file through the Android document picker. Logs contain error categories, exception class names, app version and Android API level—no player names, email addresses, balances, PINs or backup contents. Nothing is emailed/uploaded automatically.
- Reward emails open a draft in an installed email app. The person using the device must press Send.
- Android cloud backup is disabled. Use explicit progress backups; progress is not synchronized between devices.

## Local build

Use JDK 17, Android SDK platform/build-tools 35, and Gradle 8.11.1. Set `ANDROID_HOME`. Run `gradle assembleDebug` for a development APK. Development APKs are **not** update-compatible with the release APK.

For release builds set `MQ_KEYSTORE` (absolute key path), `MQ_STORE_PASSWORD`, and `MQ_KEY_PASSWORD` in your local environment, then run `gradle assembleRelease lintRelease`. Key alias: `mathquest`. Do not commit credentials or `local.properties`.

Run `npm ci && npm test` for game regressions (Node 22.22.2+, 24.15+, or 26+). Native installation, file selection, email handoff and installer approval still require an Android-device smoke test.

## Price reference

Locked packages follow the user-provided October 4, 2026 screenshot, rounded to the nearest US dollar: 80/$1, 400/$5, 800/$10, 1200/$15, 1700/$20, 3150/$35, 4500/$50, 10000/$100, 22500/$200. Tax is excluded. The 4500 tier was marked “For you”; parent verifies availability. This is an offline snapshot, not a live Roblox price feed.
