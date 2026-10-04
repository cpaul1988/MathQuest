MathQuest 1.3.0 — modern app experience

- Home, bottom navigation and profile/settings sheet with clear local/manual-cloud save status.
- Guided reward setup and a persistent chosen-reward progress card.
- Visual math hints, unscored guided retries, brief reward feedback and optional haptics.
- Includes configurable earning/shop and the temporary testing PIN bypass from 1.2.4.
- Phone visual and haptic checks remain pending.

MathQuest 1.2.4 — configurable reward shop (testing build)

- Temporarily disable parent PIN while testing; retain the central gate for re-enabling later.
- Configure points, dollars or reward stars per matching correct-answer goal and difficulty/operation.
- Show the earning rule, balance and question progress during play.
- Add configurable Robux, Minecoins, screen time, cash, outing, activity and custom rewards.
- Reserve credit for requests; mark given or refund once. Preserve learning stars and existing balances.

MathQuest 1.2.3 — visible accounts and modern interface

- Visible Sign in, Create parent account and Continue with Google buttons on player setup and gameplay.
- Direct native account routing with parent PIN protection, signed-in status and offline play retained.
- Theme-aware cards, rounded controls, clearer typography and refreshed native email forms.
- Backup utilities grouped behind an expandable section.

MathQuest 1.2.2 — custom milestone rewards

- Parent-PIN protected, per-child custom milestone rewards and on/off toggle.
- Choose a reward name and target for correct answers, Hard answers, new practice days or badges.
- Track progress, earned rewards and parent-confirmed fulfilment; preserve history when cancelled.
- Existing cash/Robux rules and saved progress are preserved.
- Learning counts from reward creation, including while disabled; enabling unlocks completed targets.
- Gameplay and reward regression tests passed; physical-device reward UI checks remain pending.

MathQuest 1.2.1 — profile resume and Google account support

- Reopen the last player and selected math/answer modes automatically.
- Existing single-player devices resume without re-entering the name. Switch player still opens selection.
- Add native Google sign-in/signup and optional linking to an existing parent account.
- Support Google reauthentication for account deletion and credential-state clearing on signout.
- Google provider enabled and release fingerprints registered with owner approval; public OAuth client configured.

MathQuest 1.2.0-beta.2 — Number Challenge icon

- Adopt selected 2, 4, 6, ? number-tile artwork for launcher and player selection.
- Include adaptive icon padding and matching monochrome number tiles.
- Gameplay and save format unchanged from beta.1.

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
