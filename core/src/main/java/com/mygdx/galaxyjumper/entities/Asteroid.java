package com.mygdx.galaxyjumper.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Asteroid {
    private Texture texture;
    private Vector2 position;
    private Vector2 direction;
    private float speed;
    private float width, height;
    private Viewport viewport;

    public Asteroid(Texture texture, Vector2 spawnPosition, Vector2 targetPosition, float speed, Viewport viewport ) {
        this.texture = texture;
        this.position = new Vector2(spawnPosition);
        this.direction = new Vector2(targetPosition).sub(spawnPosition).nor();
        this.speed = speed;
        this.viewport = viewport;
        this.width = texture.getWidth();
        this.height = texture.getHeight();
    }

    public void update(float delta) {
        position.x += direction.x * speed * delta;
        position.y += direction.y * speed * delta;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y);
    }

    public boolean isOutOfScreen() {
        float margin = 200f;
        return position.x + width < -margin ||
            position.x > viewport.getWorldWidth() + margin ||
            position.y + height < -margin ||
            position.y > viewport.getWorldHeight() + margin;
    }

    public Rectangle getBounds() {
        return new Rectangle(position.x, position.y, width, height);
    }

    public void dispose() {
        texture.dispose();
    }
}
