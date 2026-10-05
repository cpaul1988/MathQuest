# Play-first layout (1.6.0)

The remembered player opens on Play. Home is removed from navigation; old Home routes resolve to Play. Back from a tab or sheet returns to Play, while Back on Play allows normal Android exit. Navigating does not regenerate an unanswered question.

Play has the question, answer controls, hint, streak and one reward card. Progress contains missions, badges, adventure and skill assessment. Rewards presents earnings and the single-form setup, with historical shop balances in a disclosure. Settings contains theme/parent tools, tutorial, backups and other utilities. No profile schema or stored balances change.

Sign in remains visible at the top, changing to Account when signed in. The parent notice is displayed on the sign-in sheet. The Google button calls the existing native Credential Manager chooser without an additional checkbox dialog. Email sign-in and account creation remain available. No automatic cloud upload is introduced.

Google button image source: https://developers.google.com/static/identity/images/branding_guideline_sample_lt_sq_lg.png from https://developers.google.com/identity/branding-guidelines . Embedded locally, preserving aspect ratio and official artwork.

Verification: DOM tests cover direct startup and resume, three tabs, Back behavior, preserved question, sign-in routing and settings disclosures. Native build/lint validates Java. Live Google chooser and physical phone layout still require a device check.
