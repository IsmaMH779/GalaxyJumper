package com.mygdx.galaxyjumper.entities;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Bullet {
    private Sprite sprite;
    private Vector2 direction;
    private float speed;

    public Bullet(Texture texture, float x, float y, Vector2 direction, float speed) {
        this.sprite = new Sprite(texture);
        this.direction = direction.nor();
        this.speed = speed;

        sprite.setOriginCenter();
        sprite.setPosition(x - sprite.getWidth() / 2, y - sprite.getHeight() / 2);
        sprite.setRotation(direction.angleDeg() + 90);
    }

    public void update(float delta) {
        sprite.translate(direction.x * speed * delta, direction.y * speed * delta);
    }

    public Vector2 getPosition() {
        return new Vector2(sprite.getX(), sprite.getY());
    }
    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public boolean isOutOfScreen(OrthographicCamera camera, Viewport viewport) {
        float x = sprite.getX();
        float y = sprite.getY();

        float left = camera.position.x - viewport.getWorldWidth() / 2;
        float right = camera.position.x + viewport.getWorldWidth() / 2;
        float bottom = camera.position.y - viewport.getWorldHeight() / 2;
        float top = camera.position.y + viewport.getWorldHeight() / 2;

        return (x + sprite.getWidth() < left || x > right ||
            y + sprite.getHeight() < bottom || y > top);
    }

    public Rectangle getBounds() {
        return sprite.getBoundingRectangle();
    }

    public void dispose() {
        sprite.getTexture().dispose();
    }
}
