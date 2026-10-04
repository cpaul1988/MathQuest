package com.cpaul.mathquest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;
/** Play edition delegates updates to Google Play; contains no APK downloader or installer. */
final class DistributionUpdater {
    private final Activity activity;
    private final BiConsumer<String,String> message;
    DistributionUpdater(Activity a,ExecutorService io,BiConsumer<String,String> logger,BiConsumer<String,String> m){activity=a;message=m;}
    void onLaunch(){}
    void onResume(){}
    void checkUpdates(boolean manual){if(!manual)return;try{activity.startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id="+activity.getPackageName())).setPackage("com.android.vending"));}catch(Exception e){message.accept("Google Play updates","Open Google Play to check for updates. The store listing may not be available during testing.");}}
}
