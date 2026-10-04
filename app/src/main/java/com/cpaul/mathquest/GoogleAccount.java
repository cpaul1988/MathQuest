package com.cpaul.mathquest;

import android.app.Activity;
import android.os.CancellationSignal;
import androidx.credentials.*;
import androidx.credentials.exceptions.*;
import com.google.android.libraries.identity.googleid.*;
import com.google.firebase.auth.*;
import java.util.function.Consumer;

/** Google tokens stay native and are used only for Firebase authentication. */
final class GoogleAccount {
    private final Activity activity;
    private final CredentialManager manager;
    private CancellationSignal pending;
    GoogleAccount(Activity activity){this.activity=activity;manager=CredentialManager.create(activity);}
    void close(){if(pending!=null)pending.cancel();}
    void request(Consumer<AuthCredential> success,Consumer<Exception> failure){
        int id=activity.getResources().getIdentifier("default_web_client_id","string",activity.getPackageName());
        if(id==0){failure.accept(new IllegalStateException("Google sign-in needs the updated Firebase configuration."));return;}
        GetSignInWithGoogleOption option=new GetSignInWithGoogleOption.Builder(activity.getString(id)).build();
        GetCredentialRequest request=new GetCredentialRequest.Builder().addCredentialOption(option).build();
        pending=new CancellationSignal();
        manager.getCredentialAsync(activity,request,pending,androidx.core.content.ContextCompat.getMainExecutor(activity),new CredentialManagerCallback<GetCredentialResponse,GetCredentialException>(){
            @Override public void onResult(GetCredentialResponse response){pending=null;try{
                Credential credential=response.getCredential();
                if(!(credential instanceof CustomCredential)||!GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL.equals(credential.getType()))throw new IllegalStateException("Unexpected Google credential.");
                GoogleIdTokenCredential google=GoogleIdTokenCredential.createFrom(credential.getData());
                success.accept(GoogleAuthProvider.getCredential(google.getIdToken(),null));
            }catch(Exception e){failure.accept(e);}}
            @Override public void onError(GetCredentialException e){pending=null;failure.accept(e);}
        });
    }
    void clear(){manager.clearCredentialStateAsync(new ClearCredentialStateRequest(),null,androidx.core.content.ContextCompat.getMainExecutor(activity),new CredentialManagerCallback<Void,ClearCredentialException>(){
        @Override public void onResult(Void unused){}
        @Override public void onError(ClearCredentialException e){} // Firebase is already signed out; next sign-in uses explicit account selection.
    });}
}
