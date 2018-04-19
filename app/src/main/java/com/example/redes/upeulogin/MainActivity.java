package com.example.redes.upeulogin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;


import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;

import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;


import java.util.concurrent.Callable;
import io.fabric.sdk.android.Fabric;



public class MainActivity extends AppCompatActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "YbPjYhXfQKi04ojUY4j5QSv0R";
    private static final String TWITTER_SECRET = "rzudSnlgDCG59W4K8Evyv2IxRgbZPZUKetX6PGF7WT07NdcSPh";

    private TwitterLoginButton loginButton;


    private SignInButton mGoogleSignInButton;
    private GoogleApiClient mGoogleApiClient;
    private LoginButton mFacebookSignInButton;
    private CallbackManager mFacebookCallbackManager;
    private TwitterAuthConfig authConfig;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        //Fabric.with(this, new Crashlytics());
        Fabric.with(this, new Twitter(authConfig), new Crashlytics());
        //Fabric.with(this, new TwitterCore(authConfig));
        // Fabric.with(this, new Twitter(authConfig));


        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_main);

        mFacebookCallbackManager = CallbackManager.Factory.create();
        mFacebookSignInButton = (LoginButton) findViewById(R.id.login_button);
        mFacebookSignInButton.registerCallback(mFacebookCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        //TODO: Use the Profile class to get information about the current user.
                        handleSignInResult(new Callable<Void>() {
                            @Override
                            public Void call() throws Exception {
                                LoginManager.getInstance().logOut();
                                //Agregado
                                Intent intent=new Intent();
                                intent.setClass(getApplication(), HomeActivity.class);
                                startActivity(intent);

                                return null;
                            }
                        });
                    }

                    @Override
                    public void onCancel() {
                        handleSignInResult(null);
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(MainActivity.class.getCanonicalName(), error.getMessage());
                        handleSignInResult(null);
                    }
                }

        );

        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_sign_in_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // The TwitterSession is also available through:
                //TwitterCore.getInstance().getSessionManager().getActiveSession();
                //TwitterCore.getInstance().logOut();
                Twitter.getInstance().core.getSessionManager().getActiveSession();
                Twitter.logOut();

                TwitterSession session = result.data;
                // TODO: Remove toast and use the TwitterSession's userID
                // with your app's user model
                String msg = "@" + session.getUserName() + " logged in! DMP (#" + session.getUserId() + ")";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                //Agregado
                Intent intent=new Intent();
                intent.setClass(getApplication(), HomeActivity.class);
                startActivity(intent);

            }
            @Override
            public void failure(TwitterException exception) {
                Log.d("TwitterKit", "Login with Twitter failure", exception);
            }
        });





        mGoogleSignInButton = (SignInButton) findViewById(R.id.google_sign_in_button);
        mGoogleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();

            }

        });





    }







    private void checkInitialized() {
    }

    private void handleSignInResult(Object o) {
//Handle sign result here
    }


    private static final int RC_SIGN_IN = 9001;

    private void signInWithGoogle() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        final Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                        // Toast.makeText(getApplicationContext(),"Si ingreso y Paso por aqui",Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        loginButton.onActivityResult(requestCode,resultCode,data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if(result.isSuccess()) {
                final GoogleApiClient client = mGoogleApiClient;
                result.getSignInAccount();
                //Toast.makeText(getApplicationContext(),"Si ingreso con Google  Uno",Toast.LENGTH_SHORT).show();
            } else {

                startActivity(new Intent(this, HomeActivity.class));
                //Toast.makeText(getApplicationContext(),"Si ingreso con Google Dos",Toast.LENGTH_SHORT).show();
                //handleSignInResult(...);
            }
        } else {
            // Handle other values for requestCode
            // Toast.makeText(getApplicationContext(),"Si ingreso con Google  Error",Toast.LENGTH_SHORT).show();
        }
    }

}
