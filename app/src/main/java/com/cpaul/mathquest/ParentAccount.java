package com.cpaul.mathquest;

import android.app.Activity;
import android.app.AlertDialog;
import android.text.InputType;
import android.widget.*;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;
import org.json.*;
import java.util.*;
import java.util.function.Consumer;

/** Native credentials never enter the WebView. Cloud transfers are explicit parent actions. */
final class ParentAccount {
    private final Activity activity;
    private final Consumer<String> callback;
    private final FirebaseAuth auth;
    private final GoogleAccount google;
    private final FirebaseFirestore cloud;
    private String snapshot, meta;
    private volatile boolean busy;
    private boolean closed;
    ParentAccount(Activity activity, Consumer<String> callback) {
        this.activity=activity; this.callback=callback;
        google=new GoogleAccount(activity);auth=FirebaseAuth.getInstance(); cloud=FirebaseFirestore.getInstance();
        cloud.setFirestoreSettings(new FirebaseFirestoreSettings.Builder()
            .setLocalCacheSettings(MemoryCacheSettings.newBuilder().build()).build());
    }
    void close(){closed=true;google.close();}
    boolean isBusy(){return busy;}
    void signOutFromDevice(){if(!busy){auth.signOut();google.clear();emit("signedOut","",0,"");}}
    private boolean live(){return !closed&&!activity.isFinishing()&&!activity.isDestroyed();}
    private void message(String title,String text){if(live())new AlertDialog.Builder(activity).setTitle(title).setMessage(text).setPositiveButton("OK",null).show();}
    private void fail(Exception error){busy=false; String code=error instanceof FirebaseAuthException?((FirebaseAuthException)error).getErrorCode():"";
        String text="Check your connection and try again. Local progress is unchanged. Cloud transfers need internet and configured access rules.";
        if("ERROR_WEAK_PASSWORD".equals(code))text="Choose a stronger password with at least 12 characters.";
        else if("ERROR_INVALID_EMAIL".equals(code))text="Enter a valid parent email address.";
        else if(error instanceof FirebaseAuthException)text="The account action could not finish. Check the email and password, verify the email if needed, or use Reset password. Too many attempts may require waiting.";
        else if(error instanceof FirebaseFirestoreException){FirebaseFirestoreException.Code c=((FirebaseFirestoreException)error).getCode();
            if(c==FirebaseFirestoreException.Code.ABORTED)text="Another device changed the cloud save, or this device has not restored it yet. Export a local backup, then restore the cloud save before saving again. Both copies remain unchanged.";
            if(c==FirebaseFirestoreException.Code.FAILED_PRECONDITION)text="This account is marked for deletion. Cloud saves are disabled. Use Delete account again to finish deleting the login.";
        }
        message("Action could not finish",text);
    }
    void open(String payload,String metadata){if(busy||!live())return;
        try {JSONObject p=new JSONObject(payload); if(p.has("pin")||p.getInt("version")!=13||!p.has("profiles")||payload.length()>650000)throw new JSONException("payload");}
        catch(Exception e){message("Cloud save unavailable","The save is invalid or too large. Export a device backup instead.");return;}
        snapshot=payload;meta=metadata;
        FirebaseUser user=auth.getCurrentUser();
        if(user==null){new AlertDialog.Builder(activity).setTitle("Parent account")
            .setItems(new String[]{"Continue with Google", "Sign up with email", "Sign in with email", "Reset email password"},(d,w)->{switch(w){case 0:googleNotice();break;case 1:credentials(true);break;case 2:credentials(false);break;case 3:reset();break;default:break;}})
            .setNegativeButton("Cancel",null).show();return;}
        new AlertDialog.Builder(activity).setTitle("Parent account: "+user.getEmail())
            .setItems(new String[]{"Refresh email verification", "Send verification email", "Save this device to cloud", "Restore cloud save to this device", "Sign out", "Delete account and cloud data", "Link Google to this account"},(d,w)->{
                switch(w){case 0:refresh(user);break;case 1:verify(user);break;case 2:save(user);break;case 3:restore(user);break;case 4:auth.signOut();google.clear();emit("signedOut","",0,"");message("Signed out","Local progress remains on this device.");break;case 5:deletePrompt(user);break;case 6:googleLink(user);break;default:break;}
            }).setNegativeButton("Close",null).show();
    }
    private void googleFailure(Exception e){busy=false;if(!live())return;
        if(e instanceof androidx.credentials.exceptions.GetCredentialCancellationException)return;
        String text="Google sign-in could not finish. Check your connection and Google Play services, then try again. You can still use email sign-in and play offline.";
        if(e instanceof IllegalStateException)text="Google sign-in is not configured for this build yet. Email sign-in and offline play are still available.";
        if(e instanceof FirebaseAuthUserCollisionException)text="An account already uses this email or Google login. Sign in using your original method, then choose Link Google to this account. Your existing cloud save has not been replaced.";
        message("Google sign-in unavailable",text);
    }
    private void googleNotice(){
        CheckBox notice=new CheckBox(activity);notice.setText("I am the parent/adult account holder. Google shares my basic profile and email with Firebase for sign-in. Cloud progress uploads require separate confirmation. No promotional emails.");notice.setPadding(24,16,24,16);
        AlertDialog dialog=new AlertDialog.Builder(activity).setTitle("Continue with Google").setView(notice).setNegativeButton("Cancel",null).setPositiveButton("Continue",null).create();
        dialog.setOnShowListener(d->dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v->{if(!notice.isChecked()){notice.setError("Please confirm the parent notice.");return;}dialog.dismiss();busy=true;google.request(credential->{if(!live()){busy=false;return;}auth.signInWithCredential(credential).addOnSuccessListener(r->{busy=false;emit("signedIn",r.getUser().getUid(),0,"");message("Signed in with Google","Your player profiles remain on this device. Use Parent account to Save or Restore cloud progress.");}).addOnFailureListener(this::googleFailure);},this::googleFailure);}));dialog.show();
    }
    private void googleLink(FirebaseUser user){
        new AlertDialog.Builder(activity).setTitle("Link Google sign-in?").setMessage("Choose your Google account to add it as a sign-in method for this parent account. Your account ID and cloud save stay the same.").setNegativeButton("Cancel",null).setPositiveButton("Choose Google account",(d,w)->{busy=true;google.request(c->{if(!live()){busy=false;return;}user.linkWithCredential(c).addOnSuccessListener(r->{busy=false;message("Google linked","You can now use Google to sign into this same parent account.");}).addOnFailureListener(this::googleFailure);},this::googleFailure);}).show();
    }
    private EditText input(String hint,int type){EditText x=new EditText(activity);x.setHint(hint);x.setInputType(type);x.setSingleLine(true);return x;}
    private void credentials(boolean create){
        LinearLayout form=new LinearLayout(activity);form.setOrientation(LinearLayout.VERTICAL);form.setPadding(32,12,32,8);
        EditText email=input("Parent email",InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        EditText password=input("Password (12+ characters for signup)",InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
        form.addView(email);form.addView(password);
        CheckBox consent=new CheckBox(activity);consent.setText(R.string.parent_account_notice);if(create)form.addView(consent);
        AlertDialog dialog=new AlertDialog.Builder(activity).setTitle(create?"Create parent account":"Sign in").setView(form).setNegativeButton("Cancel",null).setPositiveButton(create?"Create account":"Sign in",null).create();
        dialog.setOnShowListener(d->dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v->{
            String mail=email.getText().toString().trim(),pass=password.getText().toString();
            if(mail.isEmpty()||pass.isEmpty()||(create&&(pass.length()<12||!consent.isChecked()))){password.setError("Enter credentials and confirm the parent notice. Signup needs 12+ characters.");return;}
            busy=true;password.setText("");dialog.dismiss();
            (create?auth.createUserWithEmailAndPassword(mail,pass):auth.signInWithEmailAndPassword(mail,pass))
                .addOnSuccessListener(r->{busy=false;FirebaseUser u=r.getUser();emit("signedIn",u.getUid(),0,"");if(create)verify(u);else message("Signed in","Local progress has not been replaced. Open Parent account to verify your email and choose Save or Restore.");})
                .addOnFailureListener(this::fail);
        }));dialog.show();
    }
    private void reset(){EditText email=input("Parent email",InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        new AlertDialog.Builder(activity).setTitle("Reset password").setView(email).setNegativeButton("Cancel",null).setPositiveButton("Send reset email",(d,w)->{String mail=email.getText().toString().trim();if(mail.isEmpty()){message("Email required","Enter the parent email address and try again.");return;}busy=true;auth.sendPasswordResetEmail(mail).addOnSuccessListener(v->{busy=false;message("Reset requested","If this email has an account, follow the reset email. Check spam too.");}).addOnFailureListener(this::fail);}).show();}
    private void verify(FirebaseUser user){busy=true;user.sendEmailVerification().addOnSuccessListener(v->{busy=false;message("Verification email sent","Open the link in your email, then choose Refresh email verification in Parent account.");}).addOnFailureListener(this::fail);}
    private void refresh(FirebaseUser user){busy=true;user.reload().continueWithTask(t->{if(!t.isSuccessful())throw Objects.requireNonNull(t.getException());return user.getIdToken(true);}).addOnSuccessListener(v->{busy=false;message("Email verification",user.isEmailVerified()?"Email verified. Cloud save and restore are available.":"Your email is not verified yet. Open the verification email first.");}).addOnFailureListener(this::fail);}
    private DocumentReference doc(FirebaseUser user){return cloud.collection("households").document(user.getUid());}
    private boolean verified(FirebaseUser user){if(!user.isEmailVerified()){message("Verify your email first","Open your verification email, then refresh verification in Parent account.");return false;}return true;}
    private Map<String,Object> record(long revision,String state,String payload){Map<String,Object> m=new HashMap<>();m.put("schema",1);m.put("revision",revision);m.put("state",state);m.put("payload",payload);m.put("updatedAt",FieldValue.serverTimestamp());return m;}
    private long revision(DocumentSnapshot d) throws FirebaseFirestoreException {
        Long rev=d.getLong("revision"); if(rev==null||rev<1||rev>=Long.MAX_VALUE)throw new FirebaseFirestoreException("Invalid revision",FirebaseFirestoreException.Code.DATA_LOSS);return rev;
    }
    private void save(FirebaseUser user){if(!verified(user))return;
        long base=-1;try{JSONObject m=new JSONObject(meta);if(user.getUid().equals(m.optString("uid")))base=m.getLong("revision");}catch(Exception ignored){}
        final long expected=base;final String payload=snapshot;
        new AlertDialog.Builder(activity).setTitle("Save household to cloud?").setMessage("Upload all player nicknames, learning history, balances, rules, reward requests and parent reward emails to your private Firebase account ("+user.getEmail()+")? Your device PIN is excluded. Use nicknames. Existing cloud data can only be updated from its matching revision.")
            .setNegativeButton("Cancel",null).setPositiveButton("Save to cloud",(d,w)->{busy=true;
                cloud.runTransaction(tx->{DocumentSnapshot old=tx.get(doc(user));long rev=old.exists()?revision(old):0;
                    if(old.exists()&&"deleted".equals(old.getString("state")))throw new FirebaseFirestoreException("Deleted",FirebaseFirestoreException.Code.FAILED_PRECONDITION);
                    if(old.exists()&&rev!=expected)throw new FirebaseFirestoreException("Conflict",FirebaseFirestoreException.Code.ABORTED);
                    tx.set(doc(user),record(rev+1,"active",payload));return rev+1;
                }).addOnSuccessListener(rev->{busy=false;emit("saved",user.getUid(),rev,payload);message("Cloud save complete","Open Parent account → Restore on your other device. Later changes stay local until you save again.");}).addOnFailureListener(this::fail);
            }).show();
    }
    private void restore(FirebaseUser user){if(!verified(user))return;busy=true;
        doc(user).get(Source.SERVER).addOnSuccessListener(remote->{busy=false;
            try{if(!remote.exists()){message("No cloud save","Save from the device with your progress first.");return;}
                if(!"active".equals(remote.getString("state")))throw new FirebaseFirestoreException("Deleted",FirebaseFirestoreException.Code.FAILED_PRECONDITION);
                String payload=remote.getString("payload");long rev=revision(remote);
                if(payload==null||payload.length()>650000||!Long.valueOf(1).equals(remote.getLong("schema")))throw new JSONException("Invalid snapshot");
                if(live())new AlertDialog.Builder(activity).setTitle("Restore cloud progress?").setMessage("This replaces this device's player profiles and rewards. A recovery copy of the current device save is kept locally. Your device PIN stays the same. Export a backup first if you want a separate copy.")
                    .setNegativeButton("Cancel",null).setPositiveButton("Restore",(d,w)->emit("restore",user.getUid(),rev,payload)).show();
            }catch(Exception e){fail(e);}
        }).addOnFailureListener(this::fail);
    }
    private void deletePrompt(FirebaseUser user){
        boolean viaGoogle=user.getProviderData().stream().anyMatch(p->GoogleAuthProvider.PROVIDER_ID.equals(p.getProviderId()));
        EditText password=input("Current account password",InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
        AlertDialog.Builder dialog=new AlertDialog.Builder(activity).setTitle("Delete account and cloud data?").setMessage("This permanently removes your login and cloud progress. A minimal deletion marker blocks old sessions from recreating it. Device saves and exported backups are not deleted. "+(viaGoogle?"Choose the same Google account to confirm.":"Enter your current account password."));
        if(!viaGoogle)dialog.setView(password);
        dialog.setNegativeButton("Cancel",null).setPositiveButton("Delete permanently",(d,w)->{
            if(viaGoogle){busy=true;google.request(c->{if(!live()){busy=false;return;}deleteAuthenticated(user,c);},this::googleFailure);}
            else {String pass=password.getText().toString();password.setText("");if(pass.isEmpty())return;busy=true;deleteAuthenticated(user,EmailAuthProvider.getCredential(Objects.requireNonNull(user.getEmail()),pass));}
        }).show();
    }
    private void deleteAuthenticated(FirebaseUser user,AuthCredential credential){
        user.reauthenticate(credential)
            .continueWithTask(t->{if(!t.isSuccessful())throw Objects.requireNonNull(t.getException());return user.getIdToken(true);})
            .continueWithTask(t->{if(!t.isSuccessful())throw Objects.requireNonNull(t.getException());return cloud.runTransaction(tx->{DocumentSnapshot old=tx.get(doc(user));long rev=old.exists()?revision(old):0;tx.set(doc(user),record(rev+1,"deleted",""));return null;});})
            .continueWithTask(t->{if(!t.isSuccessful())throw Objects.requireNonNull(t.getException());return user.delete();})
            .addOnSuccessListener(v->{busy=false;auth.signOut();google.clear();emit("deleted","",0,"");message("Account deleted","Your login and cloud progress were deleted. Erase local progress on each device separately if desired.");})
            .addOnFailureListener(e->{busy=false;message("Deletion did not finish","Local progress is unchanged. Cloud data may already have been erased. Retry Delete account using the same sign-in account. No new cloud saves can be made after its deletion marker is written.");});
    }
    private void emit(String type,String uid,long revision,String payload){if(!live())return;try{JSONObject event=new JSONObject();event.put("type",type);event.put("uid",uid);event.put("revision",revision);event.put("payload",payload);callback.accept(event.toString());}catch(JSONException e){message("Account response unavailable","Reopen Parent account. Local progress is unchanged.");}}
}
