# Validation — 1.0.0

- Signed release compilation: passed (AGP 8.9.2, Gradle 8.11.1, JDK 17).
- Android lint: zero errors. One expected warning for enabling JavaScript; required for the bundled game. Remote WebView requests/navigation are blocked; content is bundled under a fixed HTTPS asset origin.
- APK signature verification: passed, APK Signature Scheme v2; RSA 3072.
- Package: com.cpaul.mathquest, versionCode 1, versionName 1.0.0, Android API 26+.
- Game assets in the APK match current source byte-for-byte.
- Game regression suite: easy-mode clicks, hard-mode Submit/Enter, duplicate-submission prevention, audio failure resilience, full streak reward, saved progress, hidden extra panels, and recovery UI passed.
- Update manifest SHA-256 is generated from the release APK. Updater checks package ID, higher version code and matching installed signing certificate.
- Not yet verified on a physical Android device: installation, document picker, mail-app handoff and in-place update approval. The first public GitHub release must be published before live update checks can succeed.
- Public signing certificate SHA-256: 14f7671e5649cb5fc9234a3d79c8ee69cd538c3c1fb56d8ef0234b4ecd43197d

## 1.0.1 distribution preparation

Regression coverage now checks Play-specific update labeling, privacy panel navigation and local erasure: wrong PIN and cancellation leave the serialized save unchanged; confirmed erasure resets profiles and the household PIN.

The release workflow builds/lints both variants and checks merged manifests for stable package identity, installer permission only in GitHub, no advertising-ID permission, and disabled automatic backup. Play candidate remains a CI artifact and is not submitted to Google Play. Signup/cloud tests cannot run until the Firebase project is created and configured; those features are not shipped in this release.
