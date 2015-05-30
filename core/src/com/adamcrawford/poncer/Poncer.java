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
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

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
    GlyphLayout glyphLayout = new GlyphLayout();

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
    float lastBallXSpeed;
    float lastBallYSpeed;
    float ballX;
    float ballY;

    Vector2 touchPoint = new Vector2();
    float userY;

    float aiY;
    float aiPlayerYSpeed;
    float lastAIPlayerYSpeed;

    Texture pauseTexture;
    Sprite pauseSprite;
    Rectangle pauseBounds;
    Texture playTexture;
    Sprite playSprite;

    enum GAME_STATE{
        PLAY,
        PAUSED,
        OVER
    }
    GAME_STATE state;

    OrthographicCamera camera;

	@Override
	public void create () {

        Gdx.input.setInputProcessor(this);

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        camera = new OrthographicCamera(screenWidth, screenHeight);
        camera.setToOrtho(false, screenWidth, screenHeight);
        camera.update();

		batch = new SpriteBatch();
        // Setup Field graphic
		field = new Texture("soccerField.jpg");

        pauseTexture = new Texture("pause.png");
        pauseSprite = new Sprite(pauseTexture);
        pauseSprite.setScale(0.5f);
        pauseSprite.setPosition(150, 150);
        pauseBounds = new Rectangle();

        playTexture = new Texture("play.png");
        playSprite = new Sprite(playTexture);
        playSprite.setScale(0.5f);
        playSprite.setPosition(150, 150);

        //Setup player 1 sprite
        AIPlayerTexture = new Texture("player1.gif");
		AIPlayerSprite = new Sprite(AIPlayerTexture);
        AIPlayerSprite.setScale(3);
        AIPlayerSprite.setPosition(30, screenHeight / 2);
        aiY = AIPlayerSprite.getY();
        aiPlayerYSpeed = 0;

        //setup Player2 sprite
        userPlayerTexture = new Texture("player2.gif");
        userPlayerSprite = new Sprite(userPlayerTexture);
        userPlayerSprite.setScale(3);
        userPlayerSprite.setPosition(screenWidth - 50, (screenHeight / 2));
        userY = userPlayerSprite.getY();

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
        winnerFont.setColor(Color.BLACK);

        //setup sounds
        ballSound = Gdx.audio.newSound(Gdx.files.internal("kick.mp3"));
        cheer = Gdx.audio.newSound(Gdx.files.internal("Cheer.mp3"));

        state = GAME_STATE.PLAY;

	}

	@Override
	public void render () {

        float delta = Gdx.graphics.getDeltaTime();

        if (userScore < 3 && AIScore < 3) {
            update(delta);
            play();
        } else if (userScore == 3 && state != GAME_STATE.OVER){
            //Player Wins
            end("user");
            state = GAME_STATE.OVER;
        } else if (state != GAME_STATE.OVER) {
            //AI Wins
            end("ai");
            state = GAME_STATE.OVER;
        }
	}

    private void play(){
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.setProjectionMatrix(camera.combined);
        batch.draw(field, 0, 0, screenWidth, screenHeight);
        if (state == GAME_STATE.PLAY) {
            pauseSprite.draw(batch);
        } else {
            playSprite.draw(batch);
        }
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
        glyphLayout.setText(winnerFont, winnerString);

        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.setProjectionMatrix(camera.combined);
        batch.draw(field, 0, 0, screenWidth, screenHeight);
        winnerFont.draw(batch, glyphLayout, screenWidth/2 - glyphLayout.width/2, screenHeight/2 + glyphLayout.height/2);
        batch.end();
    }

    private void update(float time){

        if (state == GAME_STATE.PAUSED) {
            pauseBounds.set(playSprite.getBoundingRectangle());
        } else {
            pauseBounds.set(pauseSprite.getBoundingRectangle());
        }

        if (ballXSpeed != 0){
            stateTime += time;
        } else {
            stateTime = 0;
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
        float aiTop = AIPlayerBounds.getY();
        float aiBottom = aiTop + AIPlayerBounds.getHeight();

        //userPlayer Location
        userPlayerBounds.set(userPlayerSprite.getBoundingRectangle());
        float userBottom = userPlayerBounds.getY();
        float userTop = userBottom + userPlayerBounds.getHeight();

        if (userTop > screenTop){
            //contain player to top of screen
            userPlayerSprite.setY(screenTop - userPlayerSprite.getHeight()*2);
        }

        if (userBottom < 0){
            //contain player to bottom of screen
            userPlayerSprite.setY(0 + userPlayerSprite.getHeight());
        }

        if (aiTop > screenTop){
            AIPlayerSprite.setY(screenTop - AIPlayerSprite.getHeight() * 2);
            aiPlayerYSpeed = -aiPlayerYSpeed;
        }

        if (aiBottom < screenBottom){
            AIPlayerSprite.setY(0 + AIPlayerSprite.getHeight());
            aiPlayerYSpeed = -aiPlayerYSpeed;
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

        aiY += time* aiPlayerYSpeed;
        AIPlayerSprite.setPosition(AIPlayerSprite.getX(), aiY);
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

        Vector3 translatedCoordinates = new Vector3(screenX, screenY, 0);
        camera.unproject(translatedCoordinates);

        //check if ball or players touched
        if (ballRect.contains(translatedCoordinates.x, translatedCoordinates.y) && ballXSpeed == 0 && ballYSpeed == 0){
            //ball touched
            ballSound.stop();
            ballSound.play();
            ballXSpeed = -screenWidth/4;
            ballYSpeed = screenHeight/3;
            if (aiPlayerYSpeed == 0) {
                aiPlayerYSpeed = screenHeight / 5;
            }
        }

        if (userPlayerBounds.contains(translatedCoordinates.x, translatedCoordinates.y)){
            cheer.stop();
            cheer.play();
        }

        if (pauseBounds.contains(translatedCoordinates.x, translatedCoordinates.y) && state == GAME_STATE.PLAY){
            Gdx.app.log("Pause", "True");
            lastBallXSpeed = ballXSpeed;
            lastBallYSpeed = ballYSpeed;
            lastAIPlayerYSpeed = aiPlayerYSpeed;
            ballXSpeed = 0.0f;
            ballYSpeed = 0.0f;
            aiPlayerYSpeed = 0.0f;
            state = GAME_STATE.PAUSED;
        } else if (pauseBounds.contains(translatedCoordinates.x, translatedCoordinates.y) && state == GAME_STATE.PAUSED){
            ballXSpeed = lastBallXSpeed;
            ballYSpeed = lastBallYSpeed;
            aiPlayerYSpeed = lastAIPlayerYSpeed;
            state = GAME_STATE.PLAY;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (userScreenHalf.contains(screenX, screenY)){
            //Move user Sprite
            Vector2 newTouch = new Vector2(screenX, screenY);
            Vector2 delta = newTouch.cpy().sub(touchPoint);
            if (delta.y != 0) {
                userPlayerSprite.translateY(-delta.y);
            }

            touchPoint.set(newTouch);
            touchUp((int) newTouch.x, (int)newTouch.y, pointer, 0);
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
