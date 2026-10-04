MathQuest 1.2.0-beta.1 — learning adventure candidate (not promoted)

- Original compass-and-book Android icon, adaptive and monochrome variants.
- Adaptive practice tiers, explanations, permanent stars and a six-stop adventure map.
- Recommended practice, weak-skill missions and untimed mixed guardian challenges.
- Optional rewards for new players; existing reward rules and balances preserved.
- Main game focused on questions, stars and streaks; balances and extras behind buttons.
- Three daily targets, fewer new badge notifications, and recent accuracy/review summaries.
- Device sound, reduced-motion, larger text and optional read-aloud controls.
- Save format 13 with migration from format 12. Upgrade both devices before cloud transfer.
- Manual cloud transfers retained; real-device and Play release gates remain pending.

Math Quest 1.1.0-beta.1 — parent account candidate (not promoted)

- Add native parent email/password accounts, verification, reset and signout. No promotional emails.
- Add opt-in manual cloud Save/Restore with revision conflict detection, PIN exclusion and a local recovery copy.
- Add reauthenticated account/cloud deletion with stale-session protection.
- Keep offline gameplay and automatic local saving. Cloud transfers require parent action.
- Add Firebase emulator security tests and cloud-restore regression tests.
- Upgrade Android build tooling for current Firebase SDK compatibility.
- Production rules deployed with approval; real-device validation and Play requirements remain pending.

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
