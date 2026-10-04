# Firebase setup checkpoint

Checked 2026-10-04. This is infrastructure setup, not a released accounts feature.

## Created and verified in Firebase Console

- Project: `MathQuest` / `mathquest-4003c`
- Project number: `554590806579`
- Plan: Spark, no-cost ($0/month). No billing upgrade.
- Android application: `com.cpaul.mathquest`
- Firebase Android app ID: `1:554590806579:android:5d8e8d2b91199bbc5a3946`
- Email/Password sign-in: enabled. Passwordless email links were not enabled.
- Firestore: Standard edition, `(default)` database, `nam5` (United States).
- Initial security rules: production mode; all client reads and writes denied.
- Analytics and optional Gemini assistance were disabled during project creation.
- Scheduled Firestore backups were not enabled because they require Blaze.
- No parent accounts or household progress have been uploaded by this setup.

## Current blocker

The Android configuration download did not complete through the setup wizard or the registered-app settings page. Obtain `google-services.json` from Firebase Console → Project settings → General → MathQuest Android before integrating the SDK. Confirm the project ID and Android package match this checkpoint. This is client configuration; do not substitute a service-account private key.

## Next implementation gates

1. Add native Firebase email/password sign-in, email verification, reset and reauthentication. Keep passwords out of WebView JavaScript and local progress backups.
2. Use explicit parent opt-in before uploading household data; exclude the local PIN. Keep local play and export available without an account.
3. Implement owner-only verified-account rules with schema/size limits and optimistic revision checks. Test unauthorized, unverified, cross-account, stale-revision and deletion cases before deploying those rules. Do not use open test-mode rules.
4. Require a deliberate upload/restore choice on first connection. Preserve local progress on login, account changes, network failures and conflicts. Never merge reward balances by addition.
5. Implement account/data deletion with reauthentication and protection against stale-session recreation. Provide an external deletion route before a Play release with signup.
6. Update privacy text and Data safety documentation to match the actual feature. Supply a public support contact and complete the children's privacy review before commercial release.
7. Validate the native integration, real-device offline recovery and two-device conflicts. Publish a new signed GitHub version only after these checks pass.

MathQuest 1.0.1 remains the current GitHub release. Its local saves and update channel are unchanged. Signup and cloud sync are not included in that APK; the Play bundle remains a preparation candidate.
