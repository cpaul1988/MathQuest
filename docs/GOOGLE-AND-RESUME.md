# MathQuest 1.2.1 candidate

Last-player selection is stored in device-local `mqPlayerSession`, outside household backups. It remembers nickname, operation, answer mode and review mode. The app resumes that profile on reopening without new signup or a Parent PIN. Parent controls remain separately gated. A single existing profile auto-resumes on first upgrade. Multiple profiles require one selection; missing/stale names never create profiles. Switch player, import and cloud restore pause automatic entry until a player is selected again.

Parent Google sign-in uses Android Credential Manager and exchanges the Google ID token directly with Firebase. Tokens never enter the WebView or diagnostics. Email/password remains supported. Existing signed-in parents can link Google to preserve their account UID and cloud history. Account deletion reauthenticates the same Firebase user, including Google-only users; selecting another Google account cannot delete the current account. No progress uploads happen during signup. Cloud Save/Restore remains manual.

## Configuration required before release

Enable Google in Firebase Authentication for mathquest-4003c, use MathQuest as the public name and choose the owner support email. Register the existing release SHA-1 AF:0F:DE:BD:16:0A:D4:78:5D:2D:CF:94:82:FE:EB:82:C5:3C:58:18 and SHA-256 14:F7:67:1E:56:49:CB:5F:C9:23:4A:3D:79:C8:EE:69:CD:53:8C:3C:1F:B5:6D:8E:F0:23:4B:4E:CD:43:19:7D. Download the refreshed google-services.json containing the web OAuth client ID and rebuild. Google Play later needs its separate app-signing certificate registered.

Current config has no OAuth client ID, so this candidate reports Google configuration unavailable until replaced. It is not published or promoted. Browser security rules require action-time confirmation for enabling the new authentication method and registering its credentials.

## Validation

Game/cloud/adventure suites and restart regression tests pass. Restart tests cover retained money and streak, hard mode/operation, no parent authorization, explicit switch, single-player upgrade, missing selection and multiple players. Native Google chooser, linking and deletion still need physical-device tests after Firebase configuration.
