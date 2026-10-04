# 1.3.1 — first-run tutorial

After a player is first opened on this device, a seven-step tour introduces offline play, an interactive 2 + 3 demo (multiple choice or typing), hints/retries, navigation, earning rules, reward-shop setup and next actions. The demo never changes player statistics, balances, questions or reward rules.

Completion and skipping are remembered using mqTutorialV1 in device storage. Replay is available on player selection and Profile & settings. Android Back dismisses and records the tour as skipped. Existing players see the introduction once when upgrading. It is not repeated for every player on a shared device.

The final Set up rewards with me action opens the existing configuration wizard for the selected player. It does not create rewards or apply presets automatically. Without a selected player, the tutorial asks the user to choose one first. Each wizard step now has plain-language instructions explaining its controls and when changes are saved. The tutorial includes an example: 20 points per 10 correct answers means 50 qualifying correct answers to reach 100 points from zero.

Tests cover first-run display, interactive exercise, wrong-answer behavior, no gameplay mutations, setup handoff, completion persistence, replay and Back dismissal. Existing regression suites pass. Signed Android build and lint gates apply; physical-device readability and interaction checks remain pending. PIN remains temporarily disabled for owner testing. Candidate not published automatically.
