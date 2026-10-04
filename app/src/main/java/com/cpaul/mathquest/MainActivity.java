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

public class MainActivity extends Activity {
    private static final String HOME="https://appassets.androidplatform.net/assets/index.html";
    private WebView web;
    private final ExecutorService io=Executors.newSingleThreadExecutor();
    private ValueCallback<Uri[]> fileCallback;
    private String pendingExport;
    private boolean updating=false, checking=false;
    private File pendingApk;
    private final Set<String> allowedCodes=new HashSet<>(Arrays.asList("SCRIPT_ERROR","PROMISE_ERROR","SAVE_ERROR","LOAD_ERROR","IMPORT_ERROR","NATIVE_ERROR","UPDATE_ERROR"));

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        Thread.UncaughtExceptionHandler previous=Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((thread,error)->{log("NATIVE_ERROR",error.getClass().getSimpleName());if(previous!=null)previous.uncaughtException(thread,error);else android.os.Process.killProcess(android.os.Process.myPid());});
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(0xff180c2a);
        root.setOnApplyWindowInsetsListener((v,insets)->{v.setPadding(insets.getSystemWindowInsetLeft(),insets.getSystemWindowInsetTop(),insets.getSystemWindowInsetRight(),insets.getSystemWindowInsetBottom());return insets;});
        web=new WebView(this);root.addView(web,new LinearLayout.LayoutParams(-1,-1));setContentView(root);
        WebSettings settings=web.getSettings();settings.setJavaScriptEnabled(true);settings.setDomStorageEnabled(true);settings.setAllowFileAccess(false);settings.setAllowContentAccess(false);settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);settings.setJavaScriptCanOpenWindowsAutomatically(false);settings.setSupportMultipleWindows(false);
        WebViewAssetLoader loader=new WebViewAssetLoader.Builder().addPathHandler("/assets/",new WebViewAssetLoader.AssetsPathHandler(this)).build();
        web.setWebViewClient(new WebViewClient(){
            @Override public WebResourceResponse shouldInterceptRequest(WebView view,WebResourceRequest request){WebResourceResponse result=loader.shouldInterceptRequest(request.getUrl());return result!=null?result:new WebResourceResponse("text/plain","UTF-8",403,"Blocked",Collections.emptyMap(),new ByteArrayInputStream(new byte[0]));}
            @Override public boolean shouldOverrideUrlLoading(WebView view,WebResourceRequest req){Uri uri=req.getUrl();if(HOME.equals(uri.toString()))return false;if(req.isForMainFrame()&&"mailto".equals(uri.getScheme()))openMail(uri);return true;}
            @Override public void onReceivedError(WebView view,WebResourceRequest req,WebResourceError error){if(req.isForMainFrame()){log("LOAD_ERROR","WebView");showMessage("Game could not load","Your saved progress has not been cleared. Close and reopen the app. If this continues, update Android System WebView.");}}
            @Override public boolean onRenderProcessGone(WebView view,RenderProcessGoneDetail detail){log("NATIVE_ERROR","Renderer");((android.view.ViewGroup)view.getParent()).removeView(view);view.destroy();new AlertDialog.Builder(MainActivity.this).setTitle("Game display restarted").setMessage("Saved progress is safe. Reopen the game to continue.").setPositiveButton("Reopen",(d,w)->recreate()).setCancelable(false).show();return true;}
        });
        web.setWebChromeClient(new WebChromeClient(){
            @Override public boolean onJsAlert(WebView v,String url,String text,JsResult result){new AlertDialog.Builder(MainActivity.this).setMessage(text).setPositiveButton("OK",(d,w)->result.confirm()).setOnCancelListener(d->result.cancel()).show();return true;}
            @Override public boolean onJsConfirm(WebView v,String url,String text,JsResult result){new AlertDialog.Builder(MainActivity.this).setMessage(text).setPositiveButton("OK",(d,w)->result.confirm()).setNegativeButton("Cancel",(d,w)->result.cancel()).setOnCancelListener(d->result.cancel()).show();return true;}
            @Override public boolean onJsPrompt(WebView v,String url,String text,String initial,JsPromptResult result){EditText input=new EditText(MainActivity.this);input.setSingleLine(true);input.setText(initial);if(text.toLowerCase(Locale.ROOT).contains("pin"))input.setInputType(18);new AlertDialog.Builder(MainActivity.this).setMessage(text).setView(input).setPositiveButton("OK",(d,w)->result.confirm(input.getText().toString())).setNegativeButton("Cancel",(d,w)->result.cancel()).setOnCancelListener(d->result.cancel()).show();return true;}
            @Override public boolean onShowFileChooser(WebView v,ValueCallback<Uri[]> callback,FileChooserParams params){if(fileCallback!=null)fileCallback.onReceiveValue(null);fileCallback=callback;Intent intent=new Intent(Intent.ACTION_OPEN_DOCUMENT).setType("application/json").addCategory(Intent.CATEGORY_OPENABLE);try{startActivityForResult(intent,11);}catch(ActivityNotFoundException e){fileCallback.onReceiveValue(null);fileCallback=null;showMessage("File picker unavailable","Install or enable the Files app, then try again.");}return true;}
        });
        web.addJavascriptInterface(new GameBridge(),"AndroidGame");web.loadUrl(HOME);
        long last=getPreferences(0).getLong("lastCheck",0);
        if(System.currentTimeMillis()-last>6*60*60*1000L)checkUpdates(false);
    }
    public class GameBridge {
        @JavascriptInterface public String versionName(){return BuildConfig.VERSION_NAME;}
        @JavascriptInterface public void checkUpdates(){runOnUiThread(()->MainActivity.this.checkUpdates(true));}
        @JavascriptInterface public void exportBackup(String json){if(json==null||json.length()>5_000_000)return;runOnUiThread(()->exportText(json,"math-quest-backup.json","application/json"));}
        @JavascriptInterface public void exportDiagnostics(){runOnUiThread(()->{try{File f=new File(getFilesDir(),"diagnostics.txt");String text="Math Quest "+BuildConfig.VERSION_NAME+" ("+BuildConfig.VERSION_CODE+")\nAndroid API "+Build.VERSION.SDK_INT+"\nNo player data included.\n"+(f.exists()?new String(java.nio.file.Files.readAllBytes(f.toPath()),StandardCharsets.UTF_8):"No errors recorded.");exportText(text,"math-quest-diagnostics.txt","text/plain");}catch(Exception e){showMessage("Export unavailable","Please try again.");}});}
        @JavascriptInterface public void recordError(String code){log(allowedCodes.contains(code)?code:"SCRIPT_ERROR","JavaScript");}
    }
    private void openMail(Uri uri){try{startActivity(new Intent(Intent.ACTION_SENDTO,uri));}catch(ActivityNotFoundException e){showMessage("No email app found","Your reward request is saved. Install an email app, then open the draft again from Reward requests.");}}
    private void exportText(String text,String name,String mime){if(pendingExport!=null){showMessage("Export in progress","Finish the current save first.");return;}pendingExport=text;try{startActivityForResult(new Intent(Intent.ACTION_CREATE_DOCUMENT).setType(mime).addCategory(Intent.CATEGORY_OPENABLE).putExtra(Intent.EXTRA_TITLE,name),12);}catch(ActivityNotFoundException e){pendingExport=null;showMessage("Cannot save file","Enable the Files app and try again.");}}
    @Override protected void onActivityResult(int request,int result,Intent data){super.onActivityResult(request,result,data);if(request==11&&fileCallback!=null){fileCallback.onReceiveValue(result==RESULT_OK&&data!=null&&data.getData()!=null?new Uri[]{data.getData()}:null);fileCallback=null;}else if(request==12){String text=pendingExport;pendingExport=null;if(result==RESULT_OK&&data!=null&&data.getData()!=null&&text!=null){Uri uri=data.getData();io.execute(()->{try(OutputStream out=getContentResolver().openOutputStream(uri)){if(out==null)throw new IOException();out.write(text.getBytes(StandardCharsets.UTF_8));runOnUiThread(()->Toast.makeText(this,"File saved",Toast.LENGTH_SHORT).show());}catch(Exception e){log("SAVE_ERROR",e.getClass().getSimpleName());showMessage("Could not save file","Choose another location and try again.");}});}}}
    private synchronized void log(String code,String kind){try{File f=new File(getFilesDir(),"diagnostics.txt");String old=f.exists()?new String(java.nio.file.Files.readAllBytes(f.toPath()),StandardCharsets.UTF_8):"";if(old.length()>12000)old=old.substring(old.length()-8000);try(FileOutputStream out=new FileOutputStream(f)){out.write((old+System.currentTimeMillis()+" "+code+" "+kind.replaceAll("[^A-Za-z0-9_$]","")+"\n").getBytes(StandardCharsets.UTF_8));}}catch(Exception ignored){}}
    private void showMessage(String title,String text){runOnUiThread(()->{if(!isFinishing()&&!isDestroyed())new AlertDialog.Builder(this).setTitle(title).setMessage(text).setPositiveButton("OK",null).show();});}
    private void checkUpdates(boolean manual){
        if(checking||updating)return;checking=true;if(manual)Toast.makeText(this,"Checking GitHub for updates…",Toast.LENGTH_SHORT).show();
        io.execute(()->{try{String path="https://github.com/"+BuildConfig.UPDATE_REPO+"/releases/latest/download/update.json";JSONObject manifest=new JSONObject(new String(download(path,256000),StandardCharsets.UTF_8));int code=manifest.getInt("versionCode");String apk=manifest.getString("apkUrl"),sha=manifest.getString("sha256"),name=manifest.getString("versionName");
            if(!apk.startsWith("https://github.com/"+BuildConfig.UPDATE_REPO+"/releases/download/")||!apk.endsWith("/MathQuest.apk")||!sha.matches("[a-fA-F0-9]{64}")||name.length()>40)throw new IOException("Invalid manifest");
            getPreferences(0).edit().putLong("lastCheck",System.currentTimeMillis()).apply();
            if(code>BuildConfig.VERSION_CODE)runOnUiThread(()->{if(!isFinishing())new AlertDialog.Builder(this).setTitle("Math Quest "+name+" is ready").setMessage("Download this update? Your saved progress stays on this device. Android will ask you to approve installation.").setNegativeButton("Later",null).setPositiveButton("Download",(d,w)->downloadUpdate(apk,sha,code)).show();});else if(manual)showMessage("Up to date","You have Math Quest "+BuildConfig.VERSION_NAME+".");
        }catch(Exception e){log("UPDATE_ERROR",e.getClass().getSimpleName());if(manual)showMessage("Could not check for updates","Check your internet connection and try again. If this is the first build, the GitHub release may not be published yet. You can keep playing offline.");}finally{runOnUiThread(()->checking=false);}});
    }
    private boolean allowedHost(String host){return "github.com".equals(host)||"release-assets.githubusercontent.com".equals(host)||"objects.githubusercontent.com".equals(host);}
    private byte[] download(String address,int max) throws Exception {
        URL url=new URL(address);for(int redirects=0;redirects<6;redirects++){
            if(!"https".equals(url.getProtocol())||!allowedHost(url.getHost())||url.getUserInfo()!=null)throw new IOException("Blocked download host");
            HttpURLConnection connection=(HttpURLConnection)url.openConnection();connection.setConnectTimeout(15000);connection.setReadTimeout(30000);connection.setInstanceFollowRedirects(false);connection.setRequestProperty("User-Agent","MathQuest/"+BuildConfig.VERSION_NAME);
            try{int status=connection.getResponseCode();if(status>=300&&status<400){url=new URL(url,connection.getHeaderField("Location"));continue;}if(status!=200)throw new IOException("HTTP "+status);if(connection.getContentLengthLong()>max)throw new IOException("Too large");try(InputStream input=connection.getInputStream();ByteArrayOutputStream out=new ByteArrayOutputStream()){byte[] buffer=new byte[16384];int n;while((n=input.read(buffer))!=-1){if(out.size()+n>max)throw new IOException("Too large");out.write(buffer,0,n);}return out.toByteArray();}}finally{connection.disconnect();}
        }throw new IOException("Too many redirects");
    }
    private void downloadUpdate(String url,String expected,int code){if(updating)return;updating=true;Toast.makeText(this,"Downloading update…",Toast.LENGTH_LONG).show();io.execute(()->{File file=null;try{byte[] bytes=download(url,50_000_000);StringBuilder actual=new StringBuilder();for(byte b:MessageDigest.getInstance("SHA-256").digest(bytes))actual.append(String.format(Locale.ROOT,"%02x",b));if(!actual.toString().equalsIgnoreCase(expected))throw new SecurityException("Checksum");File dir=new File(getCacheDir(),"updates");if(!dir.exists()&&!dir.mkdirs())throw new IOException();file=new File(dir,"MathQuest.apk");try(FileOutputStream out=new FileOutputStream(file)){out.write(bytes);}verifyApk(file,code);pendingApk=file;runOnUiThread(this::installPending);}catch(Exception e){if(file!=null)file.delete();log("UPDATE_ERROR",e.getClass().getSimpleName());showMessage("Update could not be installed","The download failed or did not pass verification. Your installed game and progress are unchanged. Try again later.");}finally{runOnUiThread(()->updating=false);}});}
    @SuppressWarnings("deprecation") private void verifyApk(File file,int code) throws Exception {PackageManager pm=getPackageManager();int flags=PackageManager.GET_SIGNATURES;PackageInfo candidate=pm.getPackageArchiveInfo(file.getAbsolutePath(),flags);PackageInfo installed=pm.getPackageInfo(getPackageName(),flags);if(candidate==null||!getPackageName().equals(candidate.packageName)||candidate.versionCode!=code||candidate.versionCode<=BuildConfig.VERSION_CODE||candidate.signatures==null||!new HashSet<>(Arrays.asList(candidate.signatures)).equals(new HashSet<>(Arrays.asList(installed.signatures))))throw new SecurityException("APK identity");}
    private void installPending(){if(pendingApk==null)return;if(!getPackageManager().canRequestPackageInstalls()){new AlertDialog.Builder(this).setTitle("Allow game updates").setMessage("Enable 'Allow from this source' for Math Quest. Then return here to approve the update.").setPositiveButton("Open settings",(d,w)->{try{startActivity(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,Uri.parse("package:"+getPackageName())));}catch(Exception e){showMessage("Settings unavailable","Open Android Settings and allow Math Quest to install updates.");}}).setNegativeButton("Later",null).show();return;}try{Uri uri=FileProvider.getUriForFile(this,getPackageName()+".files",pendingApk);startActivity(new Intent(Intent.ACTION_VIEW).setDataAndType(uri,"application/vnd.android.package-archive").addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION));pendingApk=null;}catch(Exception e){log("UPDATE_ERROR",e.getClass().getSimpleName());showMessage("Installer unavailable","Try checking for updates again.");}}
    @Override protected void onResume(){super.onResume();if(pendingApk!=null&&getPackageManager().canRequestPackageInstalls())installPending();}
    @Override public void onBackPressed(){web.evaluateJavascript("(function(){const opened=[...document.querySelectorAll(\".modal-overlay\")].some(x=>!x.classList.contains(\"hidden\"));if(opened)closeModals();return opened;})()",value->{if("false".equals(value))finish();});}
    @Override protected void onDestroy(){if(fileCallback!=null)fileCallback.onReceiveValue(null);if(web!=null){web.removeJavascriptInterface("AndroidGame");web.destroy();}io.shutdown();super.onDestroy();}
}
