package com.mygdx.galaxyjumper.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Nave {
    private Sprite sprite;
    private Texture texture;
    private float speed;
    private Viewport viewport;

    public Nave(Texture texture, float startX, float startY, float speed, Viewport viewport) {
        this.texture = texture;
        this.sprite = new Sprite(texture);
        this.speed = speed;
        this.viewport = viewport;

        sprite.setSize(sprite.getWidth() * 0.5f, sprite.getHeight() * 0.5f);
        sprite.setOriginCenter(); // Para rotar correctamente
        sprite.setPosition(startX - sprite.getWidth()/2, startY - sprite.getHeight()/2);
    }


    public void update(float delta, Vector2 direction) {
        if (direction.isZero()) return;

        float moveX = direction.x * speed * delta;
        float moveY = direction.y * speed * delta;

        float newX = sprite.getX() + moveX;
        float newY = sprite.getY() + moveY;

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        // Limitar para que no salga de la pantalla
        newX = Math.max(0, Math.min(newX, worldWidth - sprite.getWidth()));
        newY = Math.max(0, Math.min(newY, worldHeight - sprite.getHeight()));

        sprite.setPosition(newX, newY);

        // Rotar hacia dirección
        float angle = direction.angleDeg();
        sprite.setRotation(angle - 90);
    }

    public Vector2 getDirection() {
        // La dirección en la que está mirando la nave calculada en update
        // rotación visual
        float angle = sprite.getRotation() + 90;
        return new Vector2(1, 0).setAngleDeg(angle).nor();
    }

    public Vector2 getGunTip() {
        Vector2 direction = getDirection();
        float x = sprite.getX() + sprite.getWidth() / 2 + direction.x * sprite.getHeight() / 2;
        float y = sprite.getY() + sprite.getHeight() / 2 + direction.y * sprite.getHeight() / 2;
        return new Vector2(x, y);
    }


    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public float getCenterX() {
        return sprite.getX() + sprite.getWidth() / 2;
    }

    public float getCenterY() {
        return sprite.getY() + sprite.getHeight() / 2;
    }
    public Sprite getSprite() {
        return sprite;
    }

    public Rectangle getBounds() {
        return sprite.getBoundingRectangle();
    }

    public void dispose() {
        texture.dispose();
    }

    public float getX() {
        return sprite.getX();
    }

    public float getY() {
        return sprite.getY();
    }

    public float getWidth() {
        return sprite.getWidth();
    }

    public float getHeight() {
        return sprite.getHeight();
    }

    public float getRotation() {
        return sprite.getRotation();
    }
}
