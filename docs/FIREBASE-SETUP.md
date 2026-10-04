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

## Android integration candidate — 1.1.0-beta.1

The owner supplied `google-services.json`; project ID, app ID and package were validated. It is non-secret client configuration, not an administrative credential. The SDK uses Firebase BoM 34.19.0, Authentication and Firestore only; no Analytics SDK. AGP 8.13.2 and Gradle 8.13 support the Kotlin metadata used by the current Firebase SDK.

Implemented behind the device Parent PIN:
- Native parent email/password signup and sign-in; password fields stay outside WebView JavaScript. Signup requires the adult notice and a 12-character password in the client.
- Verification emails, verification refresh, password reset and signout.
- Explicit cloud Save and Restore actions. These are manual transfers, not continuous/background syncing. Gameplay saves remain local automatically.
- Cloud uploads exclude the device PIN. Parents confirm uploading all profiles, learning history, reward balances/rules/requests and parent reward emails.
- First connection never replaces device data. Transactions reject mismatching revisions. On a conflict, export the device save before restoring the cloud copy. Balances are not merged.
- Remote JSON is validated before restore. A changed local save aborts an in-flight restore. A local recovery snapshot is saved before replacement; the current device PIN stays in place.
- Native account/cloud deletion reauthenticates and writes an empty deletion marker before deleting the Auth user. The marker blocks stale tokens from recreating progress; retry deletion if login removal fails. Local device saves, recovery snapshots, exports and emails require separate deletion. Device erasure waits for in-flight account work, then signs out the parent account.
- Firestore uses memory-only caching; cloud transfer reads require the server. Network failures do not replace local progress.

## Deployment status

With explicit owner approval, `firebase/firestore.rules` was published to production on 2026-10-04 at 2:18 PM America/Chicago. Firebase Console shows this version as the active starred ruleset. Each account can access only its own household; progress writes require verified email. Emulator tests passed before deployment. No real signup/verification/reset emails or parent cloud records were created during tests.

The candidate is kept on `feature/parent-accounts`, without promoting a GitHub update or submitting to Play. Before promotion, exercise signup, verification, password reset, cloud save/restore on two Android devices, conflict recovery, deletion retry and a signed upgrade from 1.0.1.

Google Play remains blocked on the public support/privacy contact, a public account-deletion route, children's privacy/consent review, store declarations/assets and required device/closed testing. A parent checkbox is not verified parental consent. No paid billing upgrade or Play purchase has been made.

## Validation

- `npm test`: actual game controls plus parent gate, PIN exclusion, login preservation, malformed/PIN-bearing/stale restore rejection, recovery, revision tracking, signout and local erase.
- `npm run test:rules` (JDK 21): real Firestore emulator tests for authentication, household isolation, verification, schema, size, revisions, timestamps, recent-auth deletion and tombstone protection. Uses only `demo-mathquest`; no production access.
- Build/lint GitHub APK and Play AAB with JDK 17; verify distribution manifests separately.
