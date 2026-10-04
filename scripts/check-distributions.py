"""Fail a release if the Play variant can request APK installation."""
from pathlib import Path
import xml.etree.ElementTree as ET
root=Path(__file__).resolve().parents[1]
android='{http://schemas.android.com/apk/res/android}'
for flavor in ('github','play'):
    files=list((root/'app/build/intermediates/merged_manifests'/f'{flavor}Release').rglob('AndroidManifest.xml'))
    if len(files)!=1: raise SystemExit(f'Expected exactly one merged {flavor} release manifest; build it first.')
    manifest=ET.parse(files[0]).getroot()
    permissions={p.get(android+'name') for p in manifest.findall('uses-permission')}
    install='android.permission.REQUEST_INSTALL_PACKAGES' in permissions
    assert install==(flavor=='github'),f'Wrong installer permissions for {flavor}'
    assert manifest.find('uses-sdk').get(android+'targetSdkVersion')=='36','Target API must be 36'
    assert manifest.get('package')=='com.cpaul.mathquest','Application identity changed'
    assert not any('AD_ID' in p for p in permissions),'Unexpected advertising identifier permission'
    assert manifest.find('application').get(android+'allowBackup')=='false'
    print(f'PASS {flavor}: package identity, installer isolation, no advertising ID, no automatic backup')
play=(root/'app/src/play/java/com/cpaul/mathquest/DistributionUpdater.java').read_text()
assert not any(s in play for s in ['HttpURLConnection','REQUEST_INSTALL_PACKAGES','FileProvider','downloadUpdate']), 'Play updater contains sideload code'
print('PASS Play source has no APK downloader/installer')
