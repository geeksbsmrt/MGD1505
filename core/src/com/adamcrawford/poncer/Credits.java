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
 * File:    Credits
 * Purpose: TODO Minimum 2 sentence description
 */
public class Credits implements Screen {

    Poncer poncer;

    String title = "Credits";
    String author = "Author: Adam Crawford";
    String instructor = "Instructor: Joseph Sheckles";
    String assets = "Assets: nr.edu, opengameart.org, http://www.helio.org/openkickoff, iconfinder.com, iconarchive.com, sourceaudio.com";
    Texture fieldTexture;
    BitmapFont titleFont;
    BitmapFont assetFont;
    GlyphLayout titleLayout = new GlyphLayout();
    GlyphLayout authorLayout = new GlyphLayout();
    GlyphLayout instructorLayout = new GlyphLayout();
    GlyphLayout assetLayout = new GlyphLayout();
    SpriteBatch batch;
    TextButton backButton;
    Stage stage;
    TextButton.TextButtonStyle style;
    BitmapFont buttonFont;

    public Credits(final Poncer g){
        poncer = g;

        batch = new SpriteBatch();
        poncer = g;
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        style = new TextButton.TextButtonStyle();

        fieldTexture = new Texture("soccerField.jpg");
        FileHandle fontFile = Gdx.files.internal("Roboto.ttf");
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        FreeTypeFontGenerator.FreeTypeFontParameter buttonParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        FreeTypeFontGenerator.FreeTypeFontParameter assetParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 75;
        buttonParam.size = 50;
        assetParam.size = 25;

        titleFont = generator.generateFont(parameter);
        titleFont.setColor(Color.BLACK);

        assetFont = generator.generateFont(assetParam);
        assetFont.setColor(Color.BLACK);

        buttonFont = generator.generateFont(buttonParam);
        style.font = buttonFont;
        style.fontColor = Color.BLUE;
        backButton = new TextButton("Back", style);
        backButton.setPosition(Gdx.graphics.getWidth()/2 - backButton.getWidth()/2, backButton.getHeight()*2);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                g.showMenu();
            }
        });
        stage.addActor(backButton);
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
        authorLayout.setText(titleFont, author);
        instructorLayout.setText(titleFont, instructor);
        assetLayout.setText(assetFont, assets);
        batch.begin();
        batch.draw(fieldTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        titleFont.draw(batch, titleLayout, (Gdx.graphics.getWidth() / 2) - (titleLayout.width / 2), (Gdx.graphics.getHeight() - titleLayout.height));
        titleFont.draw(batch, authorLayout, (Gdx.graphics.getWidth() / 2) - (authorLayout.width / 2), (Gdx.graphics.getHeight() - titleLayout.height * 3));
        titleFont.draw(batch, instructorLayout, (Gdx.graphics.getWidth() / 2) - (instructorLayout.width / 2), (Gdx.graphics.getHeight() - titleLayout.height * 5));
        assetFont.draw(batch, assetLayout, (Gdx.graphics.getWidth() / 2) - (assetLayout.width / 2), (backButton.getHeight()*4) + assetLayout.height*2);

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
