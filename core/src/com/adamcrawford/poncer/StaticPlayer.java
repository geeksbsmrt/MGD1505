package com.adamcrawford.poncer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * Author:  Adam Crawford
 * Project: MGD1505
 * Package: com.adamcrawford.poncer
 * File:    StaticPlayer
 * Purpose: TODO Minimum 2 sentence description
 */
public class StaticPlayer {

    private float x,y;
    private Sprite sprite;
    private Texture texture;

    enum TYPE {
        OFFENSE,
        DEFENSE
    }

    public void init(TYPE type){
        switch (type){
            case OFFENSE:
            {
                texture = new Texture("player2.gif");
                sprite = new Sprite(texture);
                sprite.setScale(3);
                break;
            }
            case DEFENSE:{
                texture = new Texture("player1.gif");
                sprite = new Sprite(texture);
                sprite.setScale(3);
                break;
            }
            default:{
                break;
            }
        }
    }

    public float getX(){
        return x;
    }
    public void setX(float x){
        this.x = x;
    }

    public float getY(){
        return y;
    }
    public void setY(float y){
        this.y = y;
    }

    public float getHeight() {
        return this.sprite.getHeight();
    }


    public float getWidth() {
        return this.sprite.getWidth();
    }


    public void draw(SpriteBatch batch){
        batch.draw(sprite, x, y);
    }

    public void dispose(){
        texture.dispose();
    }

    public Rectangle getBounds(){
        return sprite.getBoundingRectangle();
    }

}
