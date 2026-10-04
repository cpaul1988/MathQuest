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
    private DistributionUpdater updater;
    private ParentAccount parentAccount;
    private android.speech.tts.TextToSpeech speech;
    private boolean speechReady;
    private String pendingSpeech;
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
        updater=new DistributionUpdater(this,io,this::log,this::showMessage);
        web.addJavascriptInterface(new GameBridge(),"AndroidGame");web.loadUrl(HOME);
        try{parentAccount=new ParentAccount(this,event->web.evaluateJavascript("window.onParentAccount("+event+")",null));}catch(Exception e){log("NATIVE_ERROR","AccountInit");}
        updater.onLaunch();
        if(Build.VERSION.SDK_INT>=33)getOnBackInvokedDispatcher().registerOnBackInvokedCallback(android.window.OnBackInvokedDispatcher.PRIORITY_DEFAULT,this::handleBack);
    }
    public class GameBridge {
        @JavascriptInterface public void speakQuestion(String text){if(text!=null&&text.length()<200&&text.matches("[0-9a-zA-Z ×÷+−? .-]+"))runOnUiThread(()->readQuestionAloud(text));}
        @JavascriptInterface public boolean accountSignedIn(){return parentAccount!=null&&parentAccount.isSignedIn();}
        @JavascriptInterface public void parentAccountAction(String payload,String metadata,String action){if(payload==null||metadata==null||payload.length()>650000||metadata.length()>2000||action==null||!java.util.Arrays.asList("menu","signin","signup","google").contains(action))return;runOnUiThread(()->{if(parentAccount!=null)parentAccount.open(payload,metadata,action);else showMessage("Account unavailable","Keep playing offline and try again later.");});}
        @JavascriptInterface public void answerHaptic(){runOnUiThread(()->{if(web!=null)web.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP);});}
        @JavascriptInterface public boolean accountBusy(){return parentAccount!=null&&parentAccount.isBusy();}
        @JavascriptInterface public void signOutAccount(){runOnUiThread(()->{if(parentAccount!=null)parentAccount.signOutFromDevice();});}
        @JavascriptInterface public void parentAccount(String payload,String metadata){if(payload==null||metadata==null||payload.length()>650000||metadata.length()>2000)return;runOnUiThread(()->{if(parentAccount!=null)parentAccount.open(payload,metadata);else showMessage("Account unavailable","You can keep playing locally. Reopen the app and try again.");});}
        @JavascriptInterface public String distribution(){return BuildConfig.FLAVOR;}
        @JavascriptInterface public String versionName(){return BuildConfig.VERSION_NAME;}
        @JavascriptInterface public void checkUpdates(){runOnUiThread(()->updater.checkUpdates(true));}
        @JavascriptInterface public void exportBackup(String json){if(json==null||json.length()>5_000_000)return;runOnUiThread(()->exportText(json,"math-quest-backup.json","application/json"));}
        @JavascriptInterface public String feedbackDiagnostics(){
            StringBuilder report=new StringBuilder("App: MathQuest "+BuildConfig.VERSION_NAME+" ("+BuildConfig.VERSION_CODE+")\nAndroid API: "+Build.VERSION.SDK_INT+"\nError codes only (no profiles, email addresses or credentials):\n");
            try{File f=new File(getFilesDir(),"diagnostics.txt");if(f.exists()){String[] lines=new String(java.nio.file.Files.readAllBytes(f.toPath()),StandardCharsets.UTF_8).split("\n");for(int i=Math.max(0,lines.length-30);i<lines.length;i++){String[] fields=lines[i].split(" ");if(fields.length>1&&allowedCodes.contains(fields[1]))report.append(fields[1]).append("\n");}}}catch(Exception e){report.append("Diagnostics unavailable\n");}return report.toString();
        }
        @JavascriptInterface public void exportFeedback(String report){if(report==null||report.length()>12000)return;runOnUiThread(()->exportText(report,"mathquest-feedback.txt","text/plain"));}
        @JavascriptInterface public void exportDiagnostics(){runOnUiThread(()->{try{File f=new File(getFilesDir(),"diagnostics.txt");String text="Math Quest "+BuildConfig.VERSION_NAME+" ("+BuildConfig.VERSION_CODE+")\nAndroid API "+Build.VERSION.SDK_INT+"\nNo player data included.\n"+(f.exists()?new String(java.nio.file.Files.readAllBytes(f.toPath()),StandardCharsets.UTF_8):"No errors recorded.");exportText(text,"math-quest-diagnostics.txt","text/plain");}catch(Exception e){showMessage("Export unavailable","Please try again.");}});}
        @JavascriptInterface public void recordError(String code){log(allowedCodes.contains(code)?code:"SCRIPT_ERROR","JavaScript");}
    }
    private void readQuestionAloud(String text){
        pendingSpeech=text;
        if(speech==null){speech=new android.speech.tts.TextToSpeech(this,status->{
            if(isFinishing()||isDestroyed())return;
            speechReady=status==android.speech.tts.TextToSpeech.SUCCESS;
            if(speechReady){int language=speech.setLanguage(Locale.getDefault());speechReady=language!=android.speech.tts.TextToSpeech.LANG_MISSING_DATA&&language!=android.speech.tts.TextToSpeech.LANG_NOT_SUPPORTED;}
            if(speechReady)readQuestionAloud(pendingSpeech);else showMessage("Voice unavailable","Enable a text-to-speech voice in Android Settings. You can keep answering normally.");
        });return;}
        if(speechReady&&speech.speak(text,android.speech.tts.TextToSpeech.QUEUE_FLUSH,null,"math-question")==android.speech.tts.TextToSpeech.ERROR)showMessage("Voice unavailable","Check Android text-to-speech settings and try again.");
    }
    private void openMail(Uri uri){try{startActivity(new Intent(Intent.ACTION_SENDTO,uri));}catch(ActivityNotFoundException e){showMessage("No email app found","Your reward request is saved. Install an email app, then open the draft again from Reward requests.");}}
    private void exportText(String text,String name,String mime){if(pendingExport!=null){showMessage("Export in progress","Finish the current save first.");return;}pendingExport=text;try{startActivityForResult(new Intent(Intent.ACTION_CREATE_DOCUMENT).setType(mime).addCategory(Intent.CATEGORY_OPENABLE).putExtra(Intent.EXTRA_TITLE,name),12);}catch(ActivityNotFoundException e){pendingExport=null;showMessage("Cannot save file","Enable the Files app and try again.");}}
    @Override protected void onActivityResult(int request,int result,Intent data){super.onActivityResult(request,result,data);if(request==11&&fileCallback!=null){fileCallback.onReceiveValue(result==RESULT_OK&&data!=null&&data.getData()!=null?new Uri[]{data.getData()}:null);fileCallback=null;}else if(request==12){String text=pendingExport;pendingExport=null;if(result==RESULT_OK&&data!=null&&data.getData()!=null&&text!=null){Uri uri=data.getData();io.execute(()->{try(OutputStream out=getContentResolver().openOutputStream(uri)){if(out==null)throw new IOException();out.write(text.getBytes(StandardCharsets.UTF_8));runOnUiThread(()->Toast.makeText(this,"File saved",Toast.LENGTH_SHORT).show());}catch(Exception e){log("SAVE_ERROR",e.getClass().getSimpleName());showMessage("Could not save file","Choose another location and try again.");}});}}}
    private synchronized void log(String code,String kind){try{File f=new File(getFilesDir(),"diagnostics.txt");String old=f.exists()?new String(java.nio.file.Files.readAllBytes(f.toPath()),StandardCharsets.UTF_8):"";if(old.length()>12000)old=old.substring(old.length()-8000);try(FileOutputStream out=new FileOutputStream(f)){out.write((old+System.currentTimeMillis()+" "+code+" "+kind.replaceAll("[^A-Za-z0-9_$]","")+"\n").getBytes(StandardCharsets.UTF_8));}}catch(Exception ignored){}}
    private void showMessage(String title,String text){runOnUiThread(()->{if(!isFinishing()&&!isDestroyed())new AlertDialog.Builder(this).setTitle(title).setMessage(text).setPositiveButton("OK",null).show();});}
    @Override protected void onResume(){super.onResume();if(updater!=null)updater.onResume();}
    // Legacy API 26–32 path. API 33+ uses the OnBackInvokedDispatcher registered in onCreate.
    @android.annotation.SuppressLint("GestureBackNavigation")
    @Override public void onBackPressed(){handleBack();}
    private void handleBack(){web.evaluateJavascript("typeof appBack==='function'?appBack():false",value->{if("false".equals(value))finish();});}
    @Override protected void onDestroy(){if(speech!=null){speech.stop();speech.shutdown();}if(parentAccount!=null)parentAccount.close();if(fileCallback!=null)fileCallback.onReceiveValue(null);if(web!=null){web.removeJavascriptInterface("AndroidGame");web.destroy();}io.shutdown();super.onDestroy();}
}
