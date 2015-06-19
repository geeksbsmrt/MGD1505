package com.adamcrawford.poncer.android;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.adamcrawford.poncer.ActionResolver;
import com.adamcrawford.poncer.Poncer;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.leaderboard.Leaderboards;
import com.google.example.games.basegameutils.BaseGameUtils;


public class AndroidLauncher extends AndroidApplication implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ActionResolver, ResultCallback<Leaderboards.SubmitScoreResult> {

    private static GoogleApiClient mGoogleApiClient;

    private static int RC_SIGN_IN = 9001;

    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInFlow = true;
    private boolean mSignInClicked = false;

    CallbackManager callbackManager;
    ShareDialog shareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        initialize(new Poncer(this), config);

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        // this part is optional
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {

            @Override
            public void onSuccess(Sharer.Result result) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this, this, this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();
    }

    @Override
    public void onStart(){
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop(){
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);
        if (request == RC_SIGN_IN) {
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (response == RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                // Bring up an error dialog to alert the user that sign-in
                // failed. The R.string.signin_failure should reference an error
                // string in your strings.xml file that tells the user they
                // could not be signed in, such as "Unable to sign in."
                BaseGameUtils.showActivityResultError(this,
                        request, response, R.string.signin_failure);
            }
        } else {
            callbackManager.onActivityResult(request, response, data);
        }
    }

    @Override
    public boolean getSignedInGPGS() {
        return mGoogleApiClient != null && mGoogleApiClient.isConnected();
    }

    @Override
    public void loginGPGS() {
        try {
            runOnUiThread(new Runnable(){
                public void run() {
//                    gameHelper.beginUserInitiatedSignIn();
                }
            });
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void submitScoreGPGS(int score) {
        Log.i("AL", String.valueOf(score));
        if (mGoogleApiClient.isConnected()) {
            Games.Leaderboards.submitScoreImmediate(mGoogleApiClient, getString(R.string.leaderboard_all), score).setResultCallback(this);
        } else {
            Log.e("AL", "Not Connected");
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void unlockAchievementGPGS(String achievementId) {
        Games.Achievements.unlock(mGoogleApiClient, achievementId);
    }

    @Override
    public void getLeaderboardGPGS() {
        startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient, getString(R.string.leaderboard_all)), 0);
    }

    @Override
    public void getAchievementsGPGS() {
        startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient), 101);
    }


    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mResolvingConnectionFailure) {
            // already resolving
            return;
        }

        // if the sign-in button was clicked or if auto sign-in is enabled,
        // launch the sign-in flow
        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = true;

            // Attempt to resolve the connection failure using BaseGameUtils.
            // The R.string.signin_other_error value should reference a generic
            // error string in your strings.xml file, such as "There was
            // an issue with sign-in, please try again later."
            if (!BaseGameUtils.resolveConnectionFailure(this,
                    mGoogleApiClient, connectionResult,
                    RC_SIGN_IN, "error")) {
                mResolvingConnectionFailure = false;
            }
        }

        // Put code here to display the sign-in button
    }

    @Override
    public void onResult(Leaderboards.SubmitScoreResult submitScoreResult) {
        Log.e("Result", submitScoreResult.getStatus().toString());
    }

    @Override
    public void shareFB(int score, String name){

        if (ShareDialog.canShow(ShareOpenGraphContent.class)){
            ShareOpenGraphObject openGraphObject = new ShareOpenGraphObject.Builder()
                    .putString("og:type", "smrtgeekappsponcer:goal")
                    .putString("og:title", "Poncer")
                    .putInt("smrtgeekappsponcer:total", score)
                    .build();

            ShareOpenGraphAction action = new ShareOpenGraphAction.Builder()
                    .setActionType("smrtgeekappsponcer:score")
                    .putObject("smrtgeekappsponcer:goal", openGraphObject)
                    .build();

            ShareOpenGraphContent content = new ShareOpenGraphContent.Builder()
                    .setPreviewPropertyName("smrtgeekappsponcer:goal")
                    .setAction(action)
                    .build();

            shareDialog.show(content);
        }
    }
}
