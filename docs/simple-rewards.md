# Simple streak rewards (1.5.0)

Configure rewards opens a single form. Default $1 per 10 consecutive answers, Robux display. Family rewards remain opt-in for new players. Screen time and custom rewards use direct quantities rather than a points shop. Difficulty and operation filters pause progress on nonqualifying questions; a wrong qualifying answer resets only reward progress. Daily dollar cap can partially pay the final award. Switching rules resets partial reward progress, preserving balances and immutable earned descriptions. Turning off pauses progress.

Existing rewardShop, milestones, requests, dollar balances and learning stars remain intact. Saving a simple rule disables old earning; the old shop remains accessible for existing balances. Optional simpleReward fields retain database version 13. Histories are bounded and validated on restore. Minecoins reservations refund once and remain in history. Email drafts are manual. No purchase or currency transfer occurs.

Fixed Robux packs retain the user-supplied rounded US prices. Minecoins reference pack: 1,720 / $9.99, rounded to $10, tax excluded; a single pack is shown rather than inventing a per-dollar rate. Verified 2026-10-04 at https://www.target.com/p/-/A-1013006704 and https://www.bestbuy.com/product/minecraft-minecoins-pack-1720-coins-digital/JCQ6HPFT9X/sku/6287030 . Platform/region prices can differ. No live price updates offline.

Validation: automated DOM gameplay, restart, duplicate submission, wrong-answer reset, custom awards, toggle, reservation/refund, invalid configuration. Physical-device UX still needs checking.
