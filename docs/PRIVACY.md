# MathQuest privacy information — candidate 1.2.0-beta.1

We never send promotional emails.

MathQuest stores player nicknames, learning history, streaks, badges, balances, parent reward rules, reward requests, parent email addresses and a household PIN on the device. Optional parent accounts use Google Firebase Authentication for email/password login. We do not place account passwords in the game WebView or progress backups. Verification and password-reset messages are transactional account emails. Parents explicitly choose cloud Save or Restore; there is no automatic background upload. Cloud saves contain household player nicknames, progress, balances, rules, reward requests and parent reward email addresses, but exclude the local device PIN. Firestore stores them in the United States, under the parent account. Firebase receives account and connection data needed to provide the service. Use children's nicknames, not full names. The PIN is a local household control, not an online account password.

Reward emails open as drafts in the user's email app. The user chooses whether to send them; that email provider then handles the message. Parents supply and approve rewards. The game does not supply cash or Robux and is not affiliated with Roblox.

The GitHub edition contacts GitHub for update checks and downloads. The Play edition opens Google Play when the user checks for updates. Those services can receive network information, including the device's IP address, under their own privacy policies. MathQuest does not include advertising or analytics SDKs.

Diagnostics are local and use error categories, app version and Android version, without player names, balances, PINs or parent email addresses. They are shared only if the user exports them and sends the file. Progress backups include household data and the local PIN; store them privately.

Parents can export a progress backup or erase device progress through Privacy & family rewards. Erasure signs the parent account out and removes the game's local profile data, cloud revision metadata and pre-restore recovery snapshot; it does not delete previously exported files or messages in an email account. Clearing Android app storage also removes local game data. Android cloud backup is disabled.

Parent account provides account and cloud-data deletion after password reauthentication. The cloud progress is erased before the login is deleted. A minimal marker (account UID/document path, deletion state, revision, schema and timestamp; no progress or email) remains to stop older sessions from recreating deleted data. If deletion only partly succeeds, retry Delete account to finish removing the login. Account deletion does not erase copies on devices, exported backups or sent emails. Erase each device separately.

Firestore uses memory-only SDK caching, and Restore requires a live server response. The game keeps one local pre-restore recovery snapshot until device erasure or the next cloud restore; it contains household data and the device PIN.

## Publication status

This describes the parent-account candidate, not the currently published 1.0.1 APK. Owner-only production access rules are deployed; real-device validation of this candidate remains pending. Before Play submission, the developer must supply a public support/privacy contact, publish a stable policy URL, and update this information to describe any enabled account/sync service and its deletion/retention procedures. This document is not a completed Play privacy-policy submission.

Adventure progress includes adaptive practice levels, recent answer windows, guardian completion counts and the chosen profile pin. It travels with parent-approved cloud saves and backups. Sound, motion and text-size preferences stay on the device. Read-aloud sends only the current arithmetic question to the configured Android speech engine; that engine controls voice processing and availability.
