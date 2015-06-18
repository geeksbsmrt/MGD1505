package com.adamcrawford.poncer.android;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.adamcrawford.poncer.ActionResolver;
import com.adamcrawford.poncer.Poncer;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.google.example.games.basegameutils.GameHelper;


public class AndroidLauncher extends AndroidApplication implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ActionResolver {

    private GameHelper gameHelper;
    private GoogleApiClient mGoogleApiClient;

    private static int RC_SIGN_IN = 9001;

    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInFlow = true;
    private boolean mSignInClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        initialize(new Poncer(this), config);

//        gameHelper = new GameHelper(this, GameHelper.CLIENT_ALL);
//        gameHelper.enableDebugLog(true);
//        gameHelper.setup(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();
    }

    @Override
    public void onStart(){
        super.onStart();
//        gameHelper.onStart(this);
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop(){
        super.onStop();
//        gameHelper.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);
//        gameHelper.onActivityResult(request, response, data);
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
        }
    }

    @Override
    public boolean getSignedInGPGS() {
//        return gameHelper.isSignedIn();
        return true;
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
        Log.i("AL", "Submitting Score");
        Games.Leaderboards.submitScore(mGoogleApiClient, getString(R.string.leaderboard_all), score);
    }

    @Override
    public void unlockAchievementGPGS(String achievementId) {
        //gameHelper.getGamesClient().unlockAchievement(achievementId);
    }

    @Override
    public void getLeaderboardGPGS() {
        startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient, getString(R.string.leaderboard_all)), 0);
    }

    @Override
    public void getAchievementsGPGS() {
        //startActivityForResult(gameHelper.getGamesClient().getAchievementsIntent(), 101);
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
}
