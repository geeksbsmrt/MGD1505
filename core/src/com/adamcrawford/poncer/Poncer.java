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

	static Play playScreen;
	Credits creditsScreen;
	Splash splashScreen;
	Menu menuScreen;
    Help helpScreen;
    End endScreen;

	@Override
	public void create () {
		splashScreen = new Splash(this);
		setScreen(splashScreen);
		Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                showMenu();
            }
        }, 5);
	}

	@Override
	public void render () {
		super.render();
    }

    public void startGame(){
        playScreen = new Play(this);
        setScreen(playScreen);
    }

    public void showCredits(){
        creditsScreen = new Credits(this);
        setScreen(creditsScreen);
    }

    public void showMenu(){
        menuScreen = new Menu(this);
        setScreen(menuScreen);
    }

    public void showHelp(){
        helpScreen = new Help(this);
        setScreen(helpScreen);
    }

    public void showEnd(String winner){
        endScreen = new End(this, winner);
        setScreen(endScreen);
    }
}
