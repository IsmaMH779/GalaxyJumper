package com.mygdx.galaxyjumper.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Experience {
    private Sprite sprite;
    private float timeAlive;
    private boolean visible = true;
    private float blinkTimer = 0;
    private int value;

    public Experience(Texture tex, float x, float y) {
        sprite = new Sprite(tex);
        sprite.setSize(32, 32);
        sprite.setPosition(x, y);
        timeAlive = 0f;
        this.value = 1;
    }
    public Experience(Texture tex, float x, float y, int value) {
        sprite = new Sprite(tex);
        sprite.setSize(32, 32);
        sprite.setPosition(x, y);
        timeAlive = 0f;
        this.value = value;
    }

    public void update(float delta) {
        timeAlive += delta;

        // Lógica de parpadeo
        if (timeAlive >= 4f) {
            blinkTimer += delta;
            // Intercambiar visibilidad cada 0.166 segundos (3 parpadeos en 1 segundo)
            if (blinkTimer >= 0.166f) {
                visible = !visible;
                blinkTimer = 0;
            }
        }
    }

    public void draw(SpriteBatch batch) {
        if (visible) {
            sprite.draw(batch);
        }
    }

    public Rectangle getBounds() {
        return sprite.getBoundingRectangle();
    }

    public boolean isExpired() {
        return timeAlive >= 5f;
    }

    public Sprite getSprite() {
        return sprite;
    }
    public int getValue() {
        return value;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public float getTimeAlive() {
        return timeAlive;
    }

    public void setTimeAlive(float timeAlive) {
        this.timeAlive = timeAlive;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public float getBlinkTimer() {
        return blinkTimer;
    }

    public void setBlinkTimer(float blinkTimer) {
        this.blinkTimer = blinkTimer;
    }
}
