# 1.3.0 modern app candidate

Includes the unreleased 1.2.4 reward-shop changes and temporary PIN bypass.

- Home welcomes the remembered player, continues the existing question, shows today's practice mission, learning totals and selected reward progress.
- Persistent Home / Play / Rewards / Progress navigation. Rewards and Progress use page-style surfaces; Profile & settings contains account access, preferences, switch player and advanced options. Android Back returns to Home before exiting.
- Four-step reward setup: choose currency, save question goal, add shop rewards, preview. Includes a 10-correct / 20-points preset. Goal changes are saved explicitly at step 2; shop rewards save with Add reward. Navigation never silently changes a question or clears earned balances.
- Player reward wish list: choose an active shop item as a goal. The selection is saved with the profile and displayed on Home; spending never removes permanent learning stars.
- Brief earning toast, existing celebrations and optional system-respecting haptic feedback. Motion preferences and device reduced-motion settings remain respected.
- Profile shows local-save state/time and the last successful cloud upload observed on this device. Cloud backup is manual. Network status is a device-reported hint, not proof of cloud service availability. Signout clears cloud timestamp metadata.
- Visual number-line hints for addition/subtraction and equal-group hints for multiplication/division. Large quantities are labelled as grouped/truncated drawings. Wrong answers offer a guided retry using the explanation, without credit, streak, accuracy or mission changes. Continue with the normal Next question button.

Regression tests cover navigation without question replacement, guided setup, selected reward persistence, hints, unscored retry, preferences, save-status wording, and existing gameplay/cloud/reward/account behavior. Android build and lint run for both distributions. Physical-device visual, keyboard and haptic checks remain pending. Browser visual QA could not run because the browser-runtime download failed.

This is a test APK candidate, not promoted to GitHub Latest or Google Play. PIN remains disabled for owner testing and must be restored before broader distribution.
