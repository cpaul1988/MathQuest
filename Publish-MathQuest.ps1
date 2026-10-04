param([Parameter(Mandatory=$true)][string]$SigningFolder)
$ErrorActionPreference='Stop'
Set-Location $PSScriptRoot
if (-not (Get-Command gh -ErrorAction SilentlyContinue)) { throw 'Install GitHub CLI first: winget install GitHub.cli' }
if (-not (Get-Command git -ErrorAction SilentlyContinue)) { throw 'Install Git for Windows first.' }
gh auth status
if ($LASTEXITCODE -ne 0) { throw 'Run gh auth login first.' }
$repo='cpaul1988/MathQuest'
$who=gh api user --jq .login
if ($who.Trim() -ne 'cpaul1988') { throw 'Sign in to cpaul1988 before publishing.' }
$sign=Get-Content (Join-Path $SigningFolder 'signing.json') -Raw | ConvertFrom-Json
$key=[Convert]::ToBase64String([IO.File]::ReadAllBytes((Join-Path $SigningFolder 'mathquest.jks')))
gh repo view $repo 2>$null
if ($LASTEXITCODE -ne 0) { gh repo create $repo --public --description 'Offline family math game for Android with streak rewards and parent-managed requests'; if ($LASTEXITCODE -ne 0) { throw 'Repository creation failed.' } }
# Values go through stdin to GitHub encrypted secrets, never into tracked files or command arguments.
$key | gh secret set MQ_KEYSTORE_BASE64 --repo $repo
if ($LASTEXITCODE -ne 0) { throw 'Could not save signing key secret.' }
$sign.storePassword | gh secret set MQ_STORE_PASSWORD --repo $repo
if ($LASTEXITCODE -ne 0) { throw 'Could not save store password secret.' }
$sign.keyPassword | gh secret set MQ_KEY_PASSWORD --repo $repo
if ($LASTEXITCODE -ne 0) { throw 'Could not save key password secret.' }
Remove-Variable key,sign
if (-not (Test-Path .git)) { git init -b main; git remote add origin "https://github.com/$repo.git" }
# Explicit safe paths: no private-signing folder, binaries, local SDK paths, or secret files.
git add .github .gitignore app/src app/build.gradle app/google-services.json firebase firebase.json docs scripts build.gradle settings.gradle gradle.properties version.properties package.json package-lock.json README.md CHANGELOG.md Publish-MathQuest.ps1
if (Test-Path gradlew) { git add gradlew gradlew.bat gradle }
git diff --cached --quiet
if ($LASTEXITCODE -ne 0) {
 git -c user.name=cpaul1988 -c user.email=34406291+cpaul1988@users.noreply.github.com commit -m 'Release Math Quest Android'
 if ($LASTEXITCODE -ne 0) { throw 'Commit failed.' }
}
gh auth setup-git
git push -u origin main
if ($LASTEXITCODE -ne 0) { throw 'Push failed. No force push was attempted. Check whether the repository already contains files.' }
Write-Host "Source uploaded. Watch the build: https://github.com/$repo/actions"
