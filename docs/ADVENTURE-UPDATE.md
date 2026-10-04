# MathQuest adventure candidate — 1.2.0-beta.2

The game now emphasizes learning and exploration. Stars, streaks and practice progress work without monetary rewards. The main game shows the question, answer controls, feedback, streak and permanent stars. My Adventure, Missions and More lead to secondary screens.

## Learning

- Each correct answer contributes one permanent star, derived from existing learning history. Mistakes reset the current correct-answer streak but never remove stars or earned money.
- Practice has three difficulty tiers per operation. Every six regular answers, five or six correct raise the tier, zero to two correct lower it, and three or four hold it. The range is 1–5, 1–9, then 1–12 for multiplication/division factors; 1–10, 1–25, then 1–50 for addition/subtraction operands. Division stays whole-number and subtraction stays nonnegative. Easy/Hard still chooses multiple-choice versus typed answers.
- Weak-fact review does not raise difficulty. This is a conservative practice heuristic, not a validated mastery assessment.
- Incorrect answers show an explanation: making a ten/counting on, checking subtraction with addition, equal groups/repeated addition, or checking division with multiplication. Correct answers offer an expandable explanation.
- Recommended paths are ten questions in an under-practiced or lower-accuracy skill. Review missions are eight questions focused on weak facts. Guardian challenges mix all four operations across five questions and are untimed. Answers save immediately; session position is temporary. Guardian completion is recorded when the learner presses Next after the final answer. Leaving early does not remove saved answers.

## Adventure and interface

- Six discovery stops unlock at 0, 20, 60, 120, 240 and 400 correct answers and offer themed worlds plus collectible profile pins. Equipped pins appear next to the player name. Existing parent theme choices remain available.
- Badges and learning progress are inside My Adventure. Family rewards and accessibility settings are inside More.
- New daily boards have three achievable targets and offer practice, review and guardian starting buttons. Missing a day never deducts progress. Previous daily boards and earned badges remain. New per-task badge spam is removed; whole-board and milestone badges remain.
- Parent learning progress includes a suggested next skill and accuracy/review counts for the last seven active days. Reward requests retain their existing pending/fulfilled/declined history and duplicate-request safeguards.
- Sound, reduced motion and text-size settings save on this device. Theme changes preserve them. Read-aloud uses the Android text-to-speech engine in the APK, with Web Speech as a browser fallback, and reports when unavailable; availability/voice behavior needs phone testing.

## Rewards and compatibility

- Rewards are OFF for newly created players. Parents can enable them under Parent controls. Existing players keep their reward settings, balances and request history. Existing balances can still be requested when future earnings are disabled.
- Cash and Robux balances are behind Family rewards. Fixed Robux package amounts/prices are unchanged. Missions do not award bonus money.
- Save format 13 migrates format 12 without resetting profiles. Old cloud snapshots migrate when restored. Upgrade BOTH devices before exchanging new-format saves; older APKs cannot read format 13.
- Accounts and cloud Save/Restore remain manual. Automatic background syncing, remote parent approvals and new account-deletion website work are not included in this candidate. The published Firestore rules do not need changes.

## Icon

The selected Number Challenge artwork uses 2, 4, 6 and ? tiles. Includes launcher bitmap, padded Android adaptive layers, a matching monochrome tile icon and preview in `artwork/number-challenge.png`. Earlier compass/book artwork is retained only as archived source.

## Before release

Automated game/cloud/adventure tests and Android build/lint checks are required. Physical-device verification remains required for icon masking, smallest/largest text settings, keyboard, sound/read-aloud, account flows, two-device cloud transfers and signed upgrades. The candidate stays out of the automatic update feed and Google Play until those checks are complete.
