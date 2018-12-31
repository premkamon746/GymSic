package com.gymsic.kara.gymsic;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.gymsic.kara.gymsic.Interface.OnHttpComplete;
import com.gymsic.kara.gymsic.Interface.OnServerDataComplete;
import com.gymsic.kara.gymsic.Model.UserModel;
import com.gymsic.kara.gymsic.Plugin.Server;
import com.gymsic.kara.gymsic.Service.User;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    //Facebook
    private static final String EMAIL = "email";
    private LoginButton loginButton;
    private CallbackManager callbackManager;

    private final String TAG = "LOG";


    //Google
    private SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private final int RC_SIGN_IN = 10000;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        user = new User(getApplicationContext());
        gotoMain();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        loginButton = (LoginButton) findViewById(R.id.login_button);
        //loginButton.setReadPermissions(Arrays.asList(EMAIL));
        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends"));
        callbackManager = CallbackManager.Factory.create();

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject user_info, GraphResponse graphResponse) {
                        /*Log.d(TAG, "Email : " + user.optString("email"));*/
                        saveUserInfo(user_info.optString("id"), user_info.optString("name"), user_info.optString("email"));
                        gotoMain();
                    }
                });
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });/**/
    }

    private boolean saveUserInfo(String id, String name, String email){
        UserModel userModle = new UserModel();
        userModle.setFbID(id);
        userModle.setName(name);
        userModle.setEmail(email);
        user.saveUserInfo(userModle);
        String url = "http://192.168.1.33:3000/user/login";
        Log.d("log",":::::::::::::::::::::::::"+url);
        boolean status = true;
        try {
            new Server().postRegister(url, userModle,new OnHttpComplete(){
                public void onTaskCompleted(Gson gson){
                    Log.d("log",":::::::::::::::::::::::::"+gson.toString());
                }
            });
        } catch (IOException e) {
            status = false;
            e.printStackTrace();
        }
        return status;
    }

    private void gotoMain(){
        if( user.isLogin() )
        {
            Intent intent = new Intent(this, TestActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        Log.d(TAG,"::::::::::::::::::::::::::::::::::::::::::::"+RC_SIGN_IN);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            saveUserInfo("", account.getDisplayName(), account.getEmail());
            gotoMain();
            // Signed in successfully, show authenticated UI.
            //updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }
    }
}
