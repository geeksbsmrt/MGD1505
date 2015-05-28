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
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Poncer extends ApplicationAdapter implements InputProcessor {

    float screenWidth;
    float screenHeight;
	SpriteBatch batch;
	Texture field;
    Texture AIPlayerTexture;
	Sprite AIPlayerSprite;
    Texture userPlayerTexture;
    Sprite userPlayerSprite;
    Sprite ballSprite;
    Rectangle ballRect;
    Rectangle AIPlayerBounds;
    Rectangle userPlayerBounds;
    Rectangle screenBounds;
    Rectangle userScreenHalf;
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

    private static final int FRAME_COLS = 2;
    private static final int FRAME_ROWS = 4;

    Animation rollAnim;
    Texture rollSheet;
    TextureRegion[] rollFrames;
    TextureRegion currentFrame;
    float stateTime;

    Sound ballSound;
    Sound cheer;

    float ballXSpeed;
    float ballYSpeed;
    float ballX;
    float ballY;

    Vector2 touchPoint = new Vector2();
    float userY;
    float userYMove;

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
        userY = userPlayerSprite.getY();
        userYMove = 0;

        //setup Ball SpriteSheet
        rollSheet = new Texture("ui_ball.png");
        TextureRegion[][] tmp = TextureRegion.split(rollSheet, rollSheet.getWidth()/FRAME_COLS, rollSheet.getHeight()/FRAME_ROWS);
        rollFrames = new TextureRegion[FRAME_COLS * (FRAME_ROWS -1)];
        int index = 0;
        for (int i = 0, j = FRAME_ROWS -1; i < j; i++) {
            for (int k = 0, l = FRAME_COLS; k < l; k++) {
                rollFrames[index++] = tmp[i][k];
            }
        }
        rollAnim = new Animation(0.05f, rollFrames);
        stateTime = 0f;

        //setup ball sprite
        ballSprite = new Sprite(rollFrames[0]);
        Gdx.app.log("Width", String.valueOf(rollFrames[0].getRegionWidth()));
        Gdx.app.log("Height", String.valueOf(rollFrames[0].getRegionHeight()));
        ballX = screenWidth/2 - ballSprite.getWidth()/2;
        ballY = screenHeight/2 - ballSprite.getHeight()/2;
        ballSprite.setPosition(ballX, ballY);
        ballSprite.setOriginCenter();

        // set bounds
        AIPlayerBounds = new Rectangle();
        userPlayerBounds = new Rectangle();

        screenBounds = new Rectangle(0, 0, screenWidth, screenHeight);
        screenLeft = screenBounds.getX();
        screenBottom = screenBounds.getY();
        screenTop = screenBounds.getHeight();
        screenRight = screenBounds.getWidth();

        userScreenHalf = new Rectangle(screenWidth/2, 0, screenWidth/2, screenHeight);

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
        batch.draw(currentFrame, ballX, ballY);
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
        if (ballXSpeed > 0){
            stateTime -= time;
        } else {
            stateTime += time;
        }

        currentFrame = rollAnim.getKeyFrame(stateTime, true);
        ballSprite.setTexture(currentFrame.getTexture());
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
        float userBottom = userPlayerBounds.getY();
        float userTop = userBottom + userPlayerBounds.getHeight();

        if (userTop > screenHeight){
            //contain player to top of screen
            userPlayerSprite.setY(screenHeight-userPlayerBounds.getHeight());
        }

        if (userBottom < 0){
            //contain player to bottom of screen
            userPlayerSprite.setY(0);
        }

        if (ballRect.overlaps(AIPlayerBounds) || ballRect.overlaps(userPlayerBounds)){
            ballSound.stop();
            ballSound.play();
            ballXSpeed = -ballXSpeed;
        }

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

        userPlayerSprite.translateY(-userYMove);
        userYMove = 0;
        //userPlayerSprite.setPosition(userX, userY);
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
        touchPoint.set(screenX, screenY);
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        //check if ball or players touched
        if (ballRect.contains(screenX, screenY) && ballXSpeed == 0 && ballYSpeed == 0){
            //ball touched
            ballSound.stop();
            ballSound.play();
            ballXSpeed = -screenWidth/4;
            ballYSpeed = screenHeight/3;
        }
        userYMove = 0f;
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (userScreenHalf.contains(screenX, screenY)){
            //Move user Sprite
            Vector2 newTouch = new Vector2(screenX, screenY);
            // delta will now hold the difference between the last and the current touch positions
            // delta.x > 0 means the touch moved to the right, delta.x < 0 means a move to the left
            Vector2 delta = newTouch.cpy().sub(touchPoint);
            if (delta.y > 0) {
                //Player up
                userYMove = delta.y;
            } else if (delta.y < 0) {
                //Player down
                userYMove = delta.y;
            } else {
                userYMove = 0f;
            }
            touchPoint = newTouch;
        }
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
