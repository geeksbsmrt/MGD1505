/*
 *  Adam Crawford
 *  Poncer
 *  MGD 1505
 */

package com.adamcrawford.poncer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.utils.Timer;

public class Poncer extends Game implements ApplicationListener {

    Play playScreen;
    Credits creditsScreen;
    Splash splashScreen;
    static Menu menuScreen;
    Help helpScreen;
    End endScreen;
    Leaderboards leaderboards;
    ActionResolver actionResolver;


    public Poncer(ActionResolver actionResolver) {
        this.actionResolver = actionResolver;
    }

    @Override
    public void create() {
        splashScreen = new Splash(this);
        setScreen(splashScreen);
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if (actionResolver.getSignedInGPGS()) {
                    showMenu();
                }
            }
        }, 5);
    }

    @Override
    public void render() {
        super.render();
    }

    public void startGame() {
        playScreen = new Play(this);
        actionResolver.incrementAchievementGPGS("CgkIst6Fo-kUEAIQBw");
        setScreen(playScreen);
    }

    public void showCredits() {
        creditsScreen = new Credits(this);
        setScreen(creditsScreen);
    }

    public void showMenu() {
        menuScreen = new Menu(this);
        setScreen(menuScreen);
    }

    public void showHelp() {
        helpScreen = new Help(this);
        setScreen(helpScreen);
    }

    public void showEnd(int score) {
        endScreen = new End(this, score);
        setScreen(endScreen);
    }

    public void showLeaders(){
        leaderboards = new Leaderboards(this);
        setScreen(leaderboards);
    }
}
