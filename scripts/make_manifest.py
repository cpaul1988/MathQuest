"""Create the public manifest from the exact APK being released, never from a placeholder."""
import hashlib, json, os, pathlib, re
root=pathlib.Path(__file__).resolve().parents[1]
v=dict(line.split('=',1) for line in (root/'version.properties').read_text().splitlines() if '=' in line)
repo=os.environ.get('GH_REPO','cpaul1988/MathQuest')
if repo!='cpaul1988/MathQuest': raise SystemExit('Update UPDATE_REPO in app/build.gradle and this script before changing repository.')
name=v['versionName']; code=int(v['versionCode'])
if not re.fullmatch(r'\d+\.\d+\.\d+(-beta\.\d+)?',name) or code<1: raise SystemExit('Invalid release version')
apk=root/'release/MathQuest.apk'; digest=hashlib.sha256(apk.read_bytes()).hexdigest()
(root/'release/update.json').write_text(json.dumps({'versionCode':code,'versionName':name,'apkUrl':f'https://github.com/{repo}/releases/download/v{name}/MathQuest.apk','sha256':digest},indent=2)+'\n')
(root/'release/MathQuest.apk.sha256').write_text(digest+'  MathQuest.apk\n')
