# MathQuest 1.2.1 candidate

Last-player selection is stored in device-local `mqPlayerSession`, outside household backups. It remembers nickname, operation, answer mode and review mode. The app resumes that profile on reopening without new signup or a Parent PIN. Parent controls remain separately gated. A single existing profile auto-resumes on first upgrade. Multiple profiles require one selection; missing/stale names never create profiles. Switch player, import and cloud restore pause automatic entry until a player is selected again.

Parent Google sign-in uses Android Credential Manager and exchanges the Google ID token directly with Firebase. Tokens never enter the WebView or diagnostics. Email/password remains supported. Existing signed-in parents can link Google to preserve their account UID and cloud history. Account deletion reauthenticates the same Firebase user, including Google-only users; selecting another Google account cannot delete the current account. No progress uploads happen during signup. Cloud Save/Restore remains manual.

## Configuration completed — October 4, 2026

Google sign-in is enabled with the owner’s action-time approval. Public name: MathQuest. Support email: cpaul1988@gmail.com. The existing release SHA-1 and SHA-256 fingerprints are registered for com.cpaul.mathquest. Firebase’s configuration download did not return a file in the browser; the generated public Web client ID was read from the provider’s Web SDK configuration and added as a type-3 oauth_client to the existing google-services.json. No OAuth client secret is packaged. The Google Services Gradle plugin generates default_web_client_id from this entry. Google Play later needs its own app-signing certificate registered. Billing remains Spark.

## Validation

Game/cloud/adventure suites and restart regression tests pass. Restart tests cover retained money and streak, hard mode/operation, no parent authorization, explicit switch, single-player upgrade, missing selection and multiple players. Native Google chooser, linking and deletion still need physical-device tests after Firebase configuration.
