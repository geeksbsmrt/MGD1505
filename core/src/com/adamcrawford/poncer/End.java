package com.adamcrawford.poncer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;


/**
 * Author:  Adam Crawford
 * Project: MGD1505
 * Package: com.adamcrawford.poncer
 * File:    Splash
 * Purpose: TODO Minimum 2 sentence description
 */
public class End implements Screen {

    Texture fieldTexture;
    BitmapFont font;
    GlyphLayout resultLayout = new GlyphLayout();
    SpriteBatch batch;
    Stage stage;
    TextButton.TextButtonStyle style;
    TextButton menuButton;
    String winnerString = "Game Over";
    OrthographicCamera camera;
    TextButton fbShareButton;

    Poncer poncer;

    public End(final Poncer g, final int score) {

        stage = new Stage();
        style = new TextButton.TextButtonStyle();
        batch = new SpriteBatch();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();

        Gdx.input.setInputProcessor(stage);

        poncer = g;
        fieldTexture = new Texture("soccerField.jpg");
        FileHandle fontFile = Gdx.files.internal("Roboto.ttf");
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 150;

        font = generator.generateFont(parameter);
        font.setColor(Color.BLACK);

        style.font = font;
        style.fontColor = Color.BLUE;
        menuButton = new TextButton("Main Menu", style);
        menuButton.setPosition((Gdx.graphics.getWidth() / 6), menuButton.getHeight());
        menuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Gdx.app.log("Play", "Menu Pressed");
                g.showMenu();
            }
        });

        fbShareButton = new TextButton("Share", style);
        fbShareButton.setPosition((Gdx.graphics.getWidth() - Gdx.graphics.getWidth() / 6) - fbShareButton.getWidth() / 2, fbShareButton.getHeight());
        fbShareButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                g.actionResolver.shareFB(score);
            }
        });

        stage.addActor(menuButton);
        stage.addActor(fbShareButton);

        winnerString = winnerString + "\nPoints: " + score;

        if (poncer.actionResolver.getSignedInGPGS()){
            poncer.actionResolver.submitScoreGPGS(score);
            if (score == 0){
                //Negative Achievement
                poncer.actionResolver.unlockAchievementGPGS("CgkIst6Fo-kUEAIQBg");
            }
            if (score >= 10){
                //Measurement Achievement
                poncer.actionResolver.unlockAchievementGPGS("CgkIst6Fo-kUEAIQCQ");
            }
        }
    }

    @Override
    public void show() {
        render(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void render(float delta) {
        batch.setProjectionMatrix(camera.combined);

        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        resultLayout.setText(font, winnerString);
        batch.begin();
        batch.draw(fieldTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        font.draw(batch, resultLayout, (Gdx.graphics.getWidth() / 2) - resultLayout.width / 2, Gdx.graphics.getHeight() - resultLayout.height / 2);
        batch.end();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }
}
