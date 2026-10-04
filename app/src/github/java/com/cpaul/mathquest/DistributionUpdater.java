package com.cpaul.mathquest;

import android.app.*;
import android.os.*;
import android.content.*;
import android.content.pm.*;
import android.net.Uri;
import android.provider.Settings;
import android.view.*;
import android.webkit.*;
import android.widget.*;
import androidx.core.content.FileProvider;
import androidx.webkit.WebViewAssetLoader;
import org.json.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.*;

final class DistributionUpdater {
    private final Activity activity;
    private final ExecutorService io;
    private final java.util.function.BiConsumer<String,String> logger, message;
    private boolean checking=false,updating=false;
    private File pendingApk;
    DistributionUpdater(Activity activity, ExecutorService io, java.util.function.BiConsumer<String,String> logger, java.util.function.BiConsumer<String,String> message){this.activity=activity;this.io=io;this.logger=logger;this.message=message;}
    private void log(String code,String kind){logger.accept(code,kind);}
    private void showMessage(String title,String text){message.accept(title,text);}
    void onLaunch(){long last=activity.getPreferences(0).getLong("lastCheck",0);if(System.currentTimeMillis()-last>6*60*60*1000L)checkUpdates(false);}
    void onResume(){if(pendingApk!=null&&activity.getPackageManager().canRequestPackageInstalls())installPending();}
    void checkUpdates(boolean manual){
        if(checking||updating)return;checking=true;if(manual)Toast.makeText(activity,"Checking GitHub for updates…",Toast.LENGTH_SHORT).show();
        io.execute(()->{try{String path="https://github.com/"+BuildConfig.UPDATE_REPO+"/releases/latest/download/update.json";JSONObject manifest=new JSONObject(new String(download(path,256000),StandardCharsets.UTF_8));int code=manifest.getInt("versionCode");String apk=manifest.getString("apkUrl"),sha=manifest.getString("sha256"),name=manifest.getString("versionName");
            if(!apk.startsWith("https://github.com/"+BuildConfig.UPDATE_REPO+"/releases/download/")||!apk.endsWith("/MathQuest.apk")||!sha.matches("[a-fA-F0-9]{64}")||name.length()>40)throw new IOException("Invalid manifest");
            activity.getPreferences(0).edit().putLong("lastCheck",System.currentTimeMillis()).apply();
            if(code>BuildConfig.VERSION_CODE)activity.runOnUiThread(()->{if(!activity.isFinishing())new AlertDialog.Builder(activity).setTitle("Math Quest "+name+" is ready").setMessage("Download this update? Your saved progress stays on this device. Android will ask you to approve installation.").setNegativeButton("Later",null).setPositiveButton("Download",(d,w)->downloadUpdate(apk,sha,code)).show();});else if(manual)showMessage("Up to date","You have Math Quest "+BuildConfig.VERSION_NAME+".");
        }catch(Exception e){log("UPDATE_ERROR",e.getClass().getSimpleName());if(manual)showMessage("Could not check for updates","Check your internet connection and try again. If this is the first build, the GitHub release may not be published yet. You can keep playing offline.");}finally{activity.runOnUiThread(()->checking=false);}});
    }
    private boolean allowedHost(String host){return "github.com".equals(host)||"release-assets.githubusercontent.com".equals(host)||"objects.githubusercontent.com".equals(host);}
    private byte[] download(String address,int max) throws Exception {
        URL url=new URL(address);for(int redirects=0;redirects<6;redirects++){
            if(!"https".equals(url.getProtocol())||!allowedHost(url.getHost())||url.getUserInfo()!=null)throw new IOException("Blocked download host");
            HttpURLConnection connection=(HttpURLConnection)url.openConnection();connection.setConnectTimeout(15000);connection.setReadTimeout(30000);connection.setInstanceFollowRedirects(false);connection.setRequestProperty("User-Agent","MathQuest/"+BuildConfig.VERSION_NAME);
            try{int status=connection.getResponseCode();if(status>=300&&status<400){url=new URL(url,connection.getHeaderField("Location"));continue;}if(status!=200)throw new IOException("HTTP "+status);if(connection.getContentLengthLong()>max)throw new IOException("Too large");try(InputStream input=connection.getInputStream();ByteArrayOutputStream out=new ByteArrayOutputStream()){byte[] buffer=new byte[16384];int n;while((n=input.read(buffer))!=-1){if(out.size()+n>max)throw new IOException("Too large");out.write(buffer,0,n);}return out.toByteArray();}}finally{connection.disconnect();}
        }throw new IOException("Too many redirects");
    }
    private void downloadUpdate(String url,String expected,int code){if(updating)return;updating=true;Toast.makeText(activity,"Downloading update…",Toast.LENGTH_LONG).show();io.execute(()->{File file=null;try{byte[] bytes=download(url,50_000_000);StringBuilder actual=new StringBuilder();for(byte b:MessageDigest.getInstance("SHA-256").digest(bytes))actual.append(String.format(Locale.ROOT,"%02x",b));if(!actual.toString().equalsIgnoreCase(expected))throw new SecurityException("Checksum");File dir=new File(activity.getCacheDir(),"updates");if(!dir.exists()&&!dir.mkdirs())throw new IOException();file=new File(dir,"MathQuest.apk");try(FileOutputStream out=new FileOutputStream(file)){out.write(bytes);}verifyApk(file,code);pendingApk=file;activity.runOnUiThread(this::installPending);}catch(Exception e){if(file!=null)file.delete();log("UPDATE_ERROR",e.getClass().getSimpleName());showMessage("Update could not be installed","The download failed or did not pass verification. Your installed game and progress are unchanged. Try again later.");}finally{activity.runOnUiThread(()->updating=false);}});}
    @SuppressWarnings("deprecation") private void verifyApk(File file,int code) throws Exception {PackageManager pm=activity.getPackageManager();int flags=PackageManager.GET_SIGNATURES;PackageInfo candidate=pm.getPackageArchiveInfo(file.getAbsolutePath(),flags);PackageInfo installed=pm.getPackageInfo(activity.getPackageName(),flags);if(candidate==null||!activity.getPackageName().equals(candidate.packageName)||candidate.versionCode!=code||candidate.versionCode<=BuildConfig.VERSION_CODE||candidate.signatures==null||!new HashSet<>(Arrays.asList(candidate.signatures)).equals(new HashSet<>(Arrays.asList(installed.signatures))))throw new SecurityException("APK identity");}
    private void installPending(){if(pendingApk==null)return;if(!activity.getPackageManager().canRequestPackageInstalls()){new AlertDialog.Builder(activity).setTitle("Allow game updates").setMessage("Enable 'Allow from this source' for Math Quest. Then return here to approve the update.").setPositiveButton("Open settings",(d,w)->{try{activity.startActivity(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,Uri.parse("package:"+activity.getPackageName())));}catch(Exception e){showMessage("Settings unavailable","Open Android Settings and allow Math Quest to install updates.");}}).setNegativeButton("Later",null).show();return;}try{Uri uri=FileProvider.getUriForFile(activity,activity.getPackageName()+".files",pendingApk);activity.startActivity(new Intent(Intent.ACTION_VIEW).setDataAndType(uri,"application/vnd.android.package-archive").addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION));pendingApk=null;}catch(Exception e){log("UPDATE_ERROR",e.getClass().getSimpleName());showMessage("Installer unavailable","Try checking for updates again.");}}
}
