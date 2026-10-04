Math Quest 1.0.1 — Google Play preparation

- Target Android 16 / API 36 and handle modern Android back navigation.
- Separate GitHub and Play editions. GitHub keeps verified APK updates; Play has no APK downloader or installation permission.
- Build a Play AAB candidate in CI without submitting it to Google Play.
- Add in-app privacy information, no-promotional-email promise, parent-funded reward clarification, and parent-confirmed device progress erasure.
- Preserve existing profiles, streaks and balances.
- Online signup and cloud sync remain pending Firebase setup and end-to-end testing.
- Document remaining Play account, privacy, child-audience, signing and testing requirements.

Math Quest 1.0.0

- First installable Android edition, based on game v13.
- Easy multiple-choice and Hard typed-answer modes, streak rewards, fixed Robux packages, daily tasks, badges, themes, and separate local player profiles.
- Main game stays focused on the question; extras open behind buttons.
- Automatic GitHub release checks on launch (at most once every six hours) and manual checks in More.
- Verified APK downloads with checksum, package, version, and signing-certificate checks; Android asks for install approval.
- Android document picker for backup import/export and diagnostics export.
- Audio failures no longer block scoring; JavaScript errors show recovery controls.
- Offline progress is preserved through correctly signed in-place updates.
