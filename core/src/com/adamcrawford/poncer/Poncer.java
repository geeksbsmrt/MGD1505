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
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;

public class Poncer extends ApplicationAdapter implements InputProcessor {

    float screenWidth;
    float screenHeight;
	SpriteBatch batch;
	Texture field;
    Texture AIPlayerTexture;
	Sprite AIPlayerSprite;
    Texture userPlayerTexture;
    Sprite userPlayerSprite;
    Texture ballTexture;
    Sprite ballSprite;
    Circle ballBounds;
    Rectangle ballRect;
    Rectangle AIPlayerBounds;
    Rectangle userPlayerBounds;
    Rectangle screenBounds;
    float screenLeft;
    float screenBottom;
    float screenTop;
    float screenRight;
    int AIScore;
    String AIScoreString;
    BitmapFont AIBitmapFont;
    int userScore;
    String userScoreString;
    BitmapFont userBitmapFont;
    String winnerString = "Game Over";
    BitmapFont winnerFont;

    Sound ballSound;
    Sound cheer;

    float ballXSpeed;
    float ballYSpeed;
    float ballX;
    float ballY;

	@Override
	public void create () {

        Gdx.input.setInputProcessor(this);

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

		batch = new SpriteBatch();
        // Setup Field graphic
		field = new Texture("soccerField.jpg");

        //Setup player 1 sprite
        AIPlayerTexture = new Texture("player1.gif");
		AIPlayerSprite = new Sprite(AIPlayerTexture);
        AIPlayerSprite.setScale(4);
        AIPlayerSprite.setCenter(30, screenHeight / 2);

        //setup Player2 sprite
        userPlayerTexture = new Texture("player2.gif");
        userPlayerSprite = new Sprite(userPlayerTexture);
        userPlayerSprite.setScale(4);
        userPlayerSprite.setCenter(screenWidth - 30, screenHeight / 2);

        //setup ball sprite
        ballTexture = new Texture("SoccerBall.png");
        ballSprite = new Sprite(ballTexture);
        ballSprite.setSize(48, 48);
        ballX = screenWidth/2 - ballSprite.getWidth()/2;
        ballY = screenHeight/2 - ballSprite.getHeight()/2;
        ballSprite.setPosition(ballX, ballY);

        // set bounds
        AIPlayerBounds = new Rectangle();
        userPlayerBounds = new Rectangle();
        ballBounds = new Circle();
        ballBounds.set(ballX + ballSprite.getWidth()/2, ballY + ballSprite.getHeight()/2, 24);

        screenBounds = new Rectangle(0, 0, screenWidth, screenHeight);
        screenLeft = screenBounds.getX();
        screenBottom = screenBounds.getY();
        screenTop = screenBounds.getHeight();
        screenRight = screenBounds.getWidth();

        //set scores
        userScore = 0;
        AIScore = 0;
        FileHandle fontFile = Gdx.files.internal("Roboto.ttf");
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 150;

        userBitmapFont = generator.generateFont(parameter);
        AIBitmapFont = generator.generateFont(parameter);
        userBitmapFont.setColor(Color.WHITE);
        AIBitmapFont.setColor(Color.WHITE);
        userScoreString = String.valueOf(userScore);
        AIScoreString = String.valueOf(AIScore);

        winnerFont = generator.generateFont(parameter);
        winnerFont.setColor(Color.WHITE);

        //setup sounds
        ballSound = Gdx.audio.newSound(Gdx.files.internal("kick.mp3"));
        cheer = Gdx.audio.newSound(Gdx.files.internal("Cheer.mp3"));
	}

	@Override
	public void render () {

        float delta = Gdx.graphics.getDeltaTime();

        if (userScore < 3 && AIScore < 3) {
            update(delta);
            play();
        } else if (userScore == 3){
            //Player Wins
            end("user");
        } else {
            //AI Wins
            end("ai");
        }
	}

    private void play(){
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(field, 0, 0, screenWidth, screenHeight);
        AIPlayerSprite.draw(batch);
        userPlayerSprite.draw(batch);
        ballSprite.draw(batch);
        userBitmapFont.draw(batch, userScoreString, (screenWidth / 2) + (screenWidth / 4) + 100, (screenHeight / 2) + 75);
        AIBitmapFont.draw(batch, AIScoreString, (screenWidth/2)-(screenWidth/4)-200, (screenHeight/2) + 75);
        batch.end();
    }

    private void end(String winner){

        if (winner.equals("user")){
            winnerString = winnerString + "\nYou won!";
        } else {
            winnerString = winnerString + "\nYou lost";
        }

        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(field, 0, 0, screenWidth, screenHeight);
        winnerFont.draw(batch, winnerString, screenWidth/2, screenHeight/2);
        batch.end();
    }

    private void update(float time){
        // BallSprite Location
        ballRect = ballSprite.getBoundingRectangle();
        float ballLeft = ballRect.getX();
        float ballBottom = ballRect.getY();
        float ballTop = ballBottom + ballRect.getHeight();
        float ballRight = ballLeft + ballRect.getWidth();

        //AIPlayer Location
        AIPlayerBounds.set(AIPlayerSprite.getBoundingRectangle());

        //userPlayer Location
        userPlayerBounds.set(userPlayerSprite.getBoundingRectangle());

        if(ballRight < screenLeft || ballLeft > screenRight)
        {
            if(ballRight < screenLeft) {
                userScore++;
                userScoreString = String.valueOf(userScore);
            } else {
                AIScore++;
                AIScoreString = String.valueOf(AIScore);
            }
            cheer.stop();
            cheer.play();
            ballYSpeed = 0.0f;
            ballXSpeed = 0.0f;
            ballX = screenWidth/2 - ballSprite.getWidth()/2;
            ballY = screenHeight/2 - ballSprite.getHeight()/2;
        }

        if(ballBottom < screenBottom || ballTop > screenTop)
        {
            ballSound.stop();
            ballSound.play();
            ballYSpeed = -ballYSpeed;
        }

        ballX += time * ballXSpeed;
        ballY += time * ballYSpeed;
        ballSprite.setPosition(ballX, ballY);
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
        if (ballBounds.contains(screenX, screenY) && ballXSpeed == 0 && ballYSpeed == 0){
            //ball touched
            ballSound.stop();
            ballSound.play();
            ballXSpeed = -screenWidth/4;
            ballYSpeed = screenHeight/3;
        } else if (AIPlayerBounds.contains(screenX, screenY) || userPlayerBounds.contains(screenX, screenY)) {
            //player touched
            cheer.stop();
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
