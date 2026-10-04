# Google Play release readiness

Status: preparation build; NOT approved for Play submission or sale.

## Distribution

- GitHub is the current customer/tester channel. Its signed APK continues to use the GitHub update feed.
- The `play` flavor uses Google Play for updates and excludes the APK installer permission, provider and downloader class.
- Both flavors preserve `com.cpaul.mathquest`, local storage origin and version history. The existing signing certificate must be considered when enrolling in Play App Signing; do not assume Google-generated keys will update existing GitHub installs. Plan and test the transition before publishing.
- CI builds the GitHub APK and a Play AAB candidate. The AAB is a workflow artifact only, not a Play submission.
- Raise versionCode and versionName for every release. Never distribute a lower versionCode through another channel.

## Parent accounts and syncing (candidate implemented; rules deployed, device testing pending)

Use Firebase Spark with email/password Authentication and Firestore Standard. Disable Analytics, ads, AI features and optional data-sharing integrations. No paid billing upgrade is authorized. Firebase terms were accepted with the owner's explicit approval on 2026-10-04. Project `mathquest-4003c` is on Spark; Email/Password is enabled, and the default Standard Firestore database in `nam5` is provisioned in production mode; the tested owner-only access rules were published with approval on 2026-10-04. See [FIREBASE-SETUP.md](FIREBASE-SETUP.md) for the checkpoint and remaining integration work.

The `feature/parent-accounts` candidate implements native account controls and manual cloud Save/Restore. Owner-only production rules were deployed with owner approval on 2026-10-04; device testing remains pending. Automatic multi-device syncing is not implemented. Remaining gates before promotion:
1. Create and configure the project, register the Android application and restrict the client API key appropriately. Never include service-account keys in the application or source repository.
2. Require a parent-owned account, verification before cloud writes, password reset, reauthentication for deletion, and explicit agreement to upload household data. Email signup is not itself verified parental consent.
3. Keep child nicknames, progress, rules and reward requests under the parent UID. Do not upload local PINs, passwords or unrelated device data.
4. Enforce owner-only Firestore rules and test unauthenticated, unverified and cross-household denial with the emulator.
5. Use a versioned snapshot and optimistic concurrency. Detect simultaneous-device edits; preserve both versions for parent resolution rather than silently overwriting progress or adding balances. Make first-device upload versus restore an explicit parent choice.
6. Preserve offline play; show pending/error/conflict status. Do not replace device data merely on login, switching accounts or receiving an invalid remote snapshot. Test interrupted writes, account changes, stale sessions, quota exhaustion and migration.
7. Add in-app account/data deletion and an external web deletion-request route, including revoking sessions and explaining what happens to offline copies and exported backups.
8. Verify signup, verification, reset, deletion, offline recovery and cross-device syncing against the real project before publishing the feature. No online accounts are included in 1.0.1.

## Required before a commercial listing

- Owner selects seller identity, support contact, countries and price; complete developer identity/device/payment setup. Do not purchase an account or accept agreements without approval.
- Recommend a one-time paid app without advertising for the initial release. No price has been set. Do not promise unlimited/lifetime cloud service.
- Review Families/children's privacy obligations and parental consent requirements for intended markets. A parent login alone does not establish compliance.
- Public privacy policy, in-app policy access, public account deletion request route when accounts exist; accurate Data safety declaration covering Firebase/Google/GitHub and all SDKs actually included.
- Original or licensed store assets, name/trademark review, screenshots, support URL, content rating, target audience and review access credentials.
- Parent-funded rewards only: no representation of guaranteed cash/Robux payouts or Roblox affiliation. Confirm the reward design and store wording comply with current Play policies before submission.
- Confirm the then-current target API and Android App Bundle/Play App Signing requirements in Console. This preparation build targets Android 16 / API 36, the current new-app requirement; recheck at submission.
- Test on physical phones and tablets: keyboard/Enter, small screens, accessibility, email/file picker, rotation, cold start, upgrades and preserved progress. Run Play pre-launch reports.
- If applicable to the account, run a closed test with at least 12 testers continuously opted in for 14 days, then apply for production access. GitHub installs do not count as Play closed-test enrollment.

## Official references (checked 2026-10-04)

- https://support.google.com/googleplay/android-developer/answer/6112435
- https://support.google.com/googleplay/android-developer/answer/14151465
- https://support.google.com/googleplay/android-developer/answer/9893335
- https://support.google.com/googleplay/android-developer/answer/13327111
- https://support.google.com/googleplay/android-developer/answer/11926878
- https://firebase.google.com/pricing
