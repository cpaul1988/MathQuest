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

## 1.1.0-beta.1 parent account candidate

- `npm test` passed: easy/hard controls, persistence, privacy/erase plus parent gate, PIN exclusion, login preservation, invalid and stale cloud restore rejection, retained recovery copy, revision handling, signout, blocked erasure during account work and signout on erasure.
- Firestore emulator 1.22.0 (demo-mathquest, JDK 21) passed authorization and schema tests: unauthenticated/unverified/cross-household denial, no listing, revisions, size cap, timestamp enforcement, recent reauthentication for deletion, no recreation after tombstone, and deletion of never-verified accounts.
- Clean signed GitHub APK and Play AAB build plus both lint tasks passed with AGP 8.13.2 / Gradle 8.13 / JDK 17. One expected JavaScript-enabled WebView warning remains; WebView serves bundled assets and blocks other origins. The legacy onBackPressed lint suppression applies only to the API 26–32 path; API 33+ registers OnBackInvokedDispatcher.
- Distribution isolation checks passed: stable package identity, target API 36, APK installer only in GitHub, no advertising-ID permission, disabled cloud/device-transfer backup.
- Signed APK certificate matches 1.0.1 (SHA-256 14f7671e5649cb5fc9234a3d79c8ee69cd538c3c1fb56d8ef0234b4ecd43197d). APK ZIP alignment and all four packaged native library ELF LOAD segments passed 16KB alignment checks.
- Production rules are staged but not published. No real email or household upload was sent during validation.
- Not yet verified: physical Android signup/login/verification/reset, two-device transfers/conflicts, deletion retry, offline recovery and in-place update from 1.0.1. Emulator rule tests do not establish that the native UI or live project is fully tested.
- No GitHub update promotion and no Google Play submission for this candidate. The public deletion route, public contact, consent/legal review and Play listing/testing work remain pending.
