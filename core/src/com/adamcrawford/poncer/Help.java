package com.adamcrawford.poncer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
public class Help implements Screen {

    Poncer poncer;

    float screenWidth = Gdx.graphics.getWidth();
    float screenHeight = Gdx.graphics.getHeight();
    Texture fieldTexture;
    BitmapFont titleFont;
    GlyphLayout titleLayout = new GlyphLayout();
    SpriteBatch batch;
    TextButton backButton;
    Stage stage;
    TextButton.TextButtonStyle style;
    BitmapFont buttonFont;
    String title = "Help";
    String ball = "Click the ball to begin.";
    String move = "Move finger anywhere this side of screen to move player.";
    String win = "Score as many times as you can before your opponent scores.";
    GlyphLayout ballGlyph = new GlyphLayout();
    GlyphLayout moveLayout = new GlyphLayout();
    GlyphLayout winLayout = new GlyphLayout();
    BitmapFont textFont;
    Sprite ballSprite;
    float ballX;
    float ballY;
    private static final int FRAME_COLS = 2;
    private static final int FRAME_ROWS = 4;
    Texture rollSheet;
    TextureRegion[] rollFrames;

    public Help(final Poncer g) {
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
        FreeTypeFontGenerator.FreeTypeFontParameter textParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 150;
        buttonParam.size = 100;
        textParam.size = 35;

        titleFont = generator.generateFont(parameter);
        titleFont.setColor(Color.BLACK);

        textFont = generator.generateFont(textParam);
        textFont.setColor(Color.BLACK);

        buttonFont = generator.generateFont(buttonParam);
        style.font = buttonFont;
        style.fontColor = Color.BLUE;
        backButton = new TextButton("Back", style);
        backButton.setPosition(Gdx.graphics.getWidth() / 2 - backButton.getWidth() / 2, 0 + backButton.getHeight() * 2);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                g.showMenu();
            }
        });
        stage.addActor(backButton);

        rollSheet = new Texture("ui_ball.png");
        TextureRegion[][] tmp = TextureRegion.split(rollSheet, rollSheet.getWidth() / FRAME_COLS, rollSheet.getHeight() / FRAME_ROWS);
        rollFrames = new TextureRegion[FRAME_COLS * (FRAME_ROWS - 1)];
        int index = 0;
        for (int i = 0, j = FRAME_ROWS - 1; i < j; i++) {
            for (int k = 0, l = FRAME_COLS; k < l; k++) {
                rollFrames[index++] = tmp[i][k];
            }
        }
        ballSprite = new Sprite(rollFrames[0]);
        ballX = screenWidth / 2 - ballSprite.getWidth() / 2;
        ballY = screenHeight / 2 - ballSprite.getHeight() / 2;
        ballSprite.setPosition(ballX, ballY);
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
        ballGlyph.setText(textFont, ball);
        moveLayout.setText(textFont, move);
        winLayout.setText(textFont, win);
        batch.begin();
        batch.draw(fieldTexture, 0, 0, screenWidth, screenHeight);
        titleFont.draw(batch, titleLayout, (screenWidth / 2) - (titleLayout.width / 2), (screenHeight - titleLayout.height));
        textFont.draw(batch, ballGlyph, (screenWidth / 2) - (ballGlyph.width / 2), screenHeight / 2 - ballGlyph.height * 2);
        textFont.draw(batch, moveLayout, screenWidth - moveLayout.width - 25, moveLayout.height + 15);
        textFont.draw(batch, winLayout, 15, screenHeight - winLayout.height - 15);
        ballSprite.draw(batch);
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
