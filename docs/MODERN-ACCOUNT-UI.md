# 1.2.3 — modern interface and visible parent accounts

Account entry is always visible above player setup and gameplay. Signed-out parents see Sign in, Create parent account and Continue with Google. The buttons route directly to native credential/Google flows after device parent authorization. Signed-in parents see Parent account / Cloud save. The browser receives only a signed-in boolean, not credentials or an email address. Native validation, Google consent and explicit cloud Save/Restore are retained.

Theme-aware rounded surfaces, system typography, touch-friendly controls and simplified player setup refresh the game. Backup/privacy utilities are grouped in an expandable section. Native email forms have a welcome message, roomier fields and clear parent/privacy context. Offline play and automatic player resume remain unchanged.

Validation: game, cloud, adventure, resume and milestone suites passed. Added account-entry tests for visibility, direct native routing, wrong-PIN rejection and sign-in/out state refresh. Native build/lint results are recorded when complete. Physical-device visual review and real sign-in interaction remain required; automated routing checks do not test Google services.

This update does not implement the teaching features planned for 1.3.
