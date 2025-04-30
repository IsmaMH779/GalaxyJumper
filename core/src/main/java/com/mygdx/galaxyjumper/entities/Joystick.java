package com.mygdx.galaxyjumper.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Joystick {
    private Sprite baseSprite, knobSprite;
    private Texture baseTexture, knobTexture;
    private Vector2 position = new Vector2();
    private float radius;
    private boolean isVisible = false;
    private int activePointer = -1;

    public Joystick(Texture baseTexture, Texture knobTexture, float radius) {
        this.baseTexture = baseTexture;
        this.knobTexture = knobTexture;
        this.radius = radius;

        // Configurar sprites
        baseSprite = new Sprite(baseTexture);
        knobSprite = new Sprite(knobTexture);
        baseSprite.setSize(radius * 2, radius * 2);
        knobSprite.setSize(radius, radius);
    }

    public void update(float touchX, float touchY) {
        if (!isVisible) return;

        // Convertir a coordenadas de la cámara
        Vector2 direction = new Vector2(touchX - position.x, touchY - position.y);
        float distance = direction.len();

        if (distance > radius) direction.nor().scl(radius);
        knobSprite.setCenter(position.x + direction.x, position.y + direction.y);
    }

    public Vector2 getDirection() {
        if (!isVisible) return new Vector2();
        return new Vector2(
            (knobSprite.getX() + radius/2 - position.x) / radius,
            (knobSprite.getY() + radius/2 - position.y) / radius
        );
    }

    public void setPosition(float x, float y, int pointer) {
        position.set(x, y);
        activePointer = pointer;
        isVisible = true;
        baseSprite.setCenter(x, y);
        knobSprite.setCenter(x, y);
    }

    public void hide(int pointer) {
        if (activePointer == pointer) {
            isVisible = false;
            activePointer = -1;
        }
    }

    public void draw(SpriteBatch batch) {
        if (isVisible) {
            baseSprite.draw(batch);
            knobSprite.draw(batch);
        }
    }

    public void dispose() {
        baseTexture.dispose();
        knobTexture.dispose();
    }

    public int getActivePointer() {
        return activePointer;
    }

    public boolean isVisible() {
        return isVisible;
    }
}
