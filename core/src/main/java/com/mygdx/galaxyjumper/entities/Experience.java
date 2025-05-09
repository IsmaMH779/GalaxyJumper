package com.mygdx.galaxyjumper.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Experience {
    private Sprite sprite;
    private Vector2 velocity = new Vector2(0, -100);

    public Experience(Texture tex, float x, float y) {
        sprite = new Sprite(tex);
        sprite.setSize(32, 32);
        sprite.setPosition(x, y);
    }

    public void update(float delta) {
        sprite.translate(velocity.x * delta, velocity.y * delta);
    }

    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public Rectangle getBounds() {
        return sprite.getBoundingRectangle();
    }
}
