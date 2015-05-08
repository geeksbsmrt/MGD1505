/*
 *  Adam Crawford
 *  Poncer
 *  MGD 1505
 */

package com.adamcrawford.poncer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;

public class Poncer extends ApplicationAdapter implements InputProcessor {
	SpriteBatch batch;
	Texture field;
    Texture player1Texture;
	Sprite player1Sprite;
    Texture player2Texture;
    Sprite player2Sprite;
    Texture ballTexture;
    Sprite ballSprite;
    Circle ballBounds;
    Rectangle player1Bounds;
    Rectangle player2Bounds;

    Sound ballSound;
    Sound cheer;
	
	@Override
	public void create () {

        Gdx.input.setInputProcessor(this);

		batch = new SpriteBatch();
        // Setup Field graphic
		field = new Texture("soccerField.jpg");

        //Setup player 1 sprite
        player1Texture = new Texture("player1.gif");
		player1Sprite = new Sprite(player1Texture);
        player1Sprite.setScale(4);
        player1Sprite.setCenter(100, Gdx.graphics.getHeight() / 2);


        //setup Player2 sprite
        player2Texture = new Texture("player2.gif");
        player2Sprite = new Sprite(player2Texture);
        player2Sprite.setScale(4);
        player2Sprite.setCenter(Gdx.graphics.getWidth() - 100, Gdx.graphics.getHeight() / 2);


        //setup ball sprite
        ballTexture = new Texture("SoccerBall.png");
        ballSprite = new Sprite(ballTexture);
        ballSprite.setSize(48, 48);
        ballSprite.setCenter(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);

        // set bounds
        ballBounds = new Circle(ballSprite.getX() + 24, ballSprite.getY() + 24, 24);
        player1Bounds = new Rectangle();
        player1Bounds.set(player1Sprite.getBoundingRectangle());
        player2Bounds = new Rectangle();
        player2Bounds.set(player2Sprite.getBoundingRectangle());

        //setup sounds
        ballSound = Gdx.audio.newSound(Gdx.files.internal("kick.mp3"));
        cheer = Gdx.audio.newSound(Gdx.files.internal("Cheer.mp3"));
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
        //draw resources
		batch.draw(field, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        player1Sprite.draw(batch);
        player2Sprite.draw(batch);
        ballSprite.draw(batch);
		batch.end();
	}

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        //check if ball or players touched
        if (ballBounds.contains(screenX, screenY)){
            //ball touched
            ballSound.play();
        } else if (player1Bounds.contains(screenX, screenY) || player2Bounds.contains(screenX, screenY)) {
            //player touched
            cheer.play();
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
