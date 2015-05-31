package com.adamcrawford.poncer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
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
 * File:    Menu
 * Purpose: TODO Minimum 2 sentence description
 */
public class Menu implements Screen {

    Poncer poncer;
    TextButton playButton;
    Stage stage;
    TextButton.TextButtonStyle style;
    BitmapFont font;
    BitmapFont titleFont;
    SpriteBatch batch;
    Texture fieldTexture;
    TextButton creditsButton;
    TextButton helpButton;
    String title = "Poncer";
    GlyphLayout titleLayout = new GlyphLayout();

    public Menu(final Poncer g){
        poncer = g;
        stage = new Stage();
        batch = new SpriteBatch();

        Gdx.input.setInputProcessor(stage);

        style = new TextButton.TextButtonStyle();
        FileHandle fontFile = Gdx.files.internal("Roboto.ttf");
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        FreeTypeFontGenerator.FreeTypeFontParameter titleParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 100;
        titleParam.size = 150;
        font = generator.generateFont(parameter);
        titleFont = generator.generateFont(titleParam);
        titleFont.setColor(Color.BLACK);

        style.font = font;
        style.fontColor = Color.BLUE;
        playButton = new TextButton("Play", style);
        playButton.setPosition(Gdx.graphics.getWidth() / 6 - playButton.getWidth() / 2, Gdx.graphics.getHeight() / 2 - playButton.getHeight() / 2);
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                g.startGame();
            }
        });

        creditsButton = new TextButton("Credits", style);
        creditsButton.setPosition(Gdx.graphics.getWidth() - Gdx.graphics.getWidth()/6 - creditsButton.getWidth()/2, Gdx.graphics.getHeight() / 2 - creditsButton.getHeight()/2);
        creditsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                g.showCredits();
            }
        });

        helpButton = new TextButton("Help", style);
        helpButton.setPosition(Gdx.graphics.getWidth()/2 - helpButton.getWidth()/2, helpButton.getHeight() + 25);
        helpButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                g.showHelp();
            }
        });

        fieldTexture = new Texture("soccerField.jpg");
        stage.addActor(playButton);
        stage.addActor(creditsButton);
        stage.addActor(helpButton);
    }
    @Override
    public void show() {
        render(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        titleLayout.setText(titleFont, title);
        batch.begin();
        batch.draw(fieldTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        titleFont.draw(batch, titleLayout, (Gdx.graphics.getWidth() / 2) - (titleLayout.width/2), (Gdx.graphics.getHeight()) - (titleLayout.height/2));
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
