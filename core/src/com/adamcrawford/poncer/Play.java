package com.adamcrawford.poncer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
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

/**
 * Author:  Adam Crawford
 * Project: MGD1505
 * Package: com.adamcrawford.poncer
 * File:    Play
 * Purpose: TODO Minimum 2 sentence description
 */
public class Play implements InputProcessor, Screen {

    float screenHeight;
    float screenWidth;
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
    double ballMultiplier;
    double lastMultiplier = 0;

    Vector2 touchPoint = new Vector2();
    float userY;

    float aiY;
    float aiPlayerYSpeed;
    double aiPlayerSlow;
    float lastAIPlayerYSpeed;

    Texture pauseTexture;
    Sprite pauseSprite;
    Rectangle pauseBounds;
    Texture playTexture;
    Sprite playSprite;

    enum GAME_STATE{
        LOADING,
        READY,
        PLAY,
        PAUSED,
        OVER
    }
    public GAME_STATE state;

    OrthographicCamera camera;

    BitmapFont pauseFont;
    String pauseString = "Game Paused";

    Poncer poncer;

    int immerse = 0;


    public Play(final Poncer g){
        state = GAME_STATE.LOADING;

        poncer = g; // ** get Game parameter **//
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
        aiPlayerSlow = .5;

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
        ballMultiplier = 1;

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
        FreeTypeFontGenerator.FreeTypeFontParameter menuParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 150;

        menuParam.size = 75;

        userBitmapFont = generator.generateFont(parameter);
        AIBitmapFont = generator.generateFont(parameter);
        userBitmapFont.setColor(Color.WHITE);
        AIBitmapFont.setColor(Color.WHITE);
        userScoreString = String.valueOf(userScore);
        AIScoreString = String.valueOf(AIScore);

        pauseFont = generator.generateFont(parameter);
        pauseFont.setColor(Color.RED);

        //setup sounds
        ballSound = Gdx.audio.newSound(Gdx.files.internal("kick.mp3"));
        cheer = Gdx.audio.newSound(Gdx.files.internal("Cheer.mp3"));

        state = GAME_STATE.READY;
    }

    @Override
    public void show () {
        render(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void render (float delta) {

        if (AIScore < 1) {
            update(delta);
            play();
        } else if (state != GAME_STATE.OVER) {
            //AI Wins
            end();
            state = GAME_STATE.OVER;
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {
        Gdx.app.log("Pause", "True");
        lastBallXSpeed = ballXSpeed;
        lastBallYSpeed = ballYSpeed;
        lastAIPlayerYSpeed = aiPlayerYSpeed;
        ballXSpeed = 0.0f;
        ballYSpeed = 0.0f;
        aiPlayerYSpeed = 0.0f;
        state = GAME_STATE.PAUSED;
    }

    @Override
    public void resume() {
        ballXSpeed = lastBallXSpeed;
        ballYSpeed = lastBallYSpeed;
        aiPlayerYSpeed = lastAIPlayerYSpeed;
        state = GAME_STATE.PLAY;
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        pauseTexture.dispose();
        playTexture.dispose();
        AIPlayerTexture.dispose();
        userPlayerTexture.dispose();
        field.dispose();
        cheer.dispose();
        ballSound.dispose();
        rollSheet.dispose();
    }

    private void play(){
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.setProjectionMatrix(camera.combined);
        batch.draw(field, 0, 0, screenWidth, screenHeight);
        AIPlayerSprite.draw(batch);
        userPlayerSprite.draw(batch);
        userBitmapFont.draw(batch, userScoreString, (screenWidth / 2) + (screenWidth / 4) + 100, (screenHeight / 2) + 75);
        AIBitmapFont.draw(batch, AIScoreString, (screenWidth / 2) - (screenWidth / 4) - 200, (screenHeight / 2) + 75);
        batch.draw(currentFrame, ballX, ballY);
        if (state == GAME_STATE.PLAY) {
            pauseSprite.draw(batch);
        } else if (state == GAME_STATE.PAUSED){
            glyphLayout.setText(pauseFont, pauseString);
            pauseFont.draw(batch, glyphLayout, screenWidth/2 - glyphLayout.width/2, screenHeight/2 + glyphLayout.height/2);
            playSprite.draw(batch);
        }
        batch.end();
    }

    private void end(){
        poncer.showEnd(userScore);
    }

    private void update(float time) {

        if (state == GAME_STATE.PAUSED) {
            pauseBounds.set(playSprite.getBoundingRectangle());
        } else {
            pauseBounds.set(pauseSprite.getBoundingRectangle());
        }

        if (ballXSpeed != 0) {
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
        float aiBottom = AIPlayerBounds.getY();
        float aiTop = aiBottom + AIPlayerBounds.getHeight();

        //userPlayer Location
        userPlayerBounds.set(userPlayerSprite.getBoundingRectangle());
        float userBottom = userPlayerBounds.getY();
        float userTop = userBottom + userPlayerBounds.getHeight();

        if (userTop > screenTop) {
            //contain player to top of screen
            userPlayerSprite.setY(screenTop - userPlayerSprite.getHeight() * 2);
        }

        if (userBottom < 0) {
            //contain player to bottom of screen
            userPlayerSprite.setY(0 + userPlayerSprite.getHeight());
        }

        if (aiTop > screenTop) {
            AIPlayerSprite.setY(screenTop - AIPlayerSprite.getHeight() * 2);
            aiPlayerYSpeed = -aiPlayerYSpeed;
        }

        if (aiBottom < 0) {
            AIPlayerSprite.setY(0 + AIPlayerSprite.getHeight());
            aiPlayerYSpeed = -aiPlayerYSpeed;
        }

        //TODO: FIX THIS!
        if (aiY + AIPlayerSprite.getHeight() / 2 > ballY + ballSprite.getHeight() / 2) {
            //ai down
            aiPlayerYSpeed = (float) (ballYSpeed * aiPlayerSlow);
        } else if (aiY + AIPlayerSprite.getHeight()/2 < ballY + ballSprite.getHeight()/2){
            //ai up
        }else {
            aiPlayerYSpeed = 0;
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
            lastMultiplier = ballMultiplier;
            ballX = screenWidth/2 - ballSprite.getWidth()/2;
            ballY = screenHeight/2 - ballSprite.getHeight()/2;
            playerScored();
        }

        if(ballBottom < screenBottom || ballTop > screenTop)
        {
            ballSound.stop();
            ballSound.play();
            ballYSpeed = -ballYSpeed;
        }

        ballX += time * (ballXSpeed * ballMultiplier);
        ballY += time * (ballYSpeed * ballMultiplier);
        ballSprite.setPosition(ballX, ballY);

        aiY += time * aiPlayerYSpeed;
        AIPlayerSprite.setPosition(AIPlayerSprite.getX(), aiY);
    }

    private void playerScored() {
        if (userScore%3==0){
            switch (immerse){
                case 0:{
                    //Speed up ball
                    ballMultiplier = ballMultiplier + 0.05;
                    Gdx.app.log("Multiplier", String.valueOf(ballMultiplier));
                    immerse = 1;
                    break;
                }
                case 1:{
                    //Add players

                    immerse = 2;
                    break;
                }
                case 2:{
                    //Increase AI

                    immerse = 0;
                    break;
                }
                default:{
                    break;
                }
            }
        }
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
        if (ballRect.contains(translatedCoordinates.x, translatedCoordinates.y) && ballXSpeed == 0 && ballYSpeed == 0 && state != GAME_STATE.PAUSED){
            //ball touched
            state = GAME_STATE.PLAY;
            ballSound.stop();
            ballSound.play();
            ballXSpeed = -screenWidth/4;
            ballYSpeed = screenHeight/3;
            if (aiPlayerYSpeed == 0) {
                aiPlayerYSpeed = ballYSpeed ;
            }
        }

        if (pauseBounds.contains(translatedCoordinates.x, translatedCoordinates.y) && state == GAME_STATE.PLAY){
            pause();
        } else if (pauseBounds.contains(translatedCoordinates.x, translatedCoordinates.y) && state == GAME_STATE.PAUSED){
            resume();
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (userScreenHalf.contains(screenX, screenY)){
            //Move user Sprite
            Vector2 newTouch = new Vector2(screenX, screenY);
            Vector2 touchDelta = newTouch.cpy().sub(touchPoint);
            if (touchDelta.y != 0) {
                userPlayerSprite.translateY(-touchDelta.y);
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
