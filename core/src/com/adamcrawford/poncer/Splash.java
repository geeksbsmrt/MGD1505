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


/**
 * Author:  Adam Crawford
 * Project: MGD1505
 * Package: com.adamcrawford.poncer
 * File:    Splash
 * Purpose: TODO Minimum 2 sentence description
 */
public class Splash implements Screen {

    String loading = "Poncer";
    Texture fieldTexture;
    BitmapFont loadingFont;
    GlyphLayout loadingLayout = new GlyphLayout();
    SpriteBatch batch;

    Poncer poncer;

    public Splash(Poncer g){
        batch = new SpriteBatch();
        poncer = g;
        fieldTexture = new Texture("soccerField.jpg");
        FileHandle fontFile = Gdx.files.internal("Roboto.ttf");
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 150;

        loadingFont = generator.generateFont(parameter);
        loadingFont.setColor(Color.BLACK);
    }
    @Override
    public void show() {
        render(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        loadingLayout.setText(loadingFont, loading);
        batch.begin();
        batch.draw(fieldTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        loadingFont.draw(batch, loadingLayout, (Gdx.graphics.getWidth() / 2) - (loadingLayout.width/2), (Gdx.graphics.getHeight()) - (loadingLayout.height/2));
        batch.end();
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
