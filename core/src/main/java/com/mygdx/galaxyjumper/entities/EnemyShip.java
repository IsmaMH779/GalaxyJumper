package com.mygdx.galaxyjumper.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

public class EnemyShip {
    private Sprite sprite;
    private Vector2 position;
    private Vector2 targetPosition;
    private float speed;
    private float shootTimer;
    private float shootInterval;
    private Viewport viewport;

    public EnemyShip(Texture texture, Viewport viewport, Vector2 playerPosition) {
        this.sprite = new Sprite(texture);
        this.viewport = viewport;
        this.speed = 150f;
        this.shootInterval = 3f;
        this.shootTimer = 0f;

        sprite.setSize(40, 40);
        sprite.setPosition(
            viewport.getWorldWidth() + 100,
            MathUtils.random(100, viewport.getWorldHeight() - 100)
        );

        targetPosition = new Vector2(
            viewport.getWorldWidth() * 0.8f,
            sprite.getY()
        );
    }

    public Vector2 update(float delta, Vector2 playerPosition) {
        // Movimiento
        if (sprite.getX() > targetPosition.x) {
            sprite.translateX(-speed * delta);
        }

        // Rotación hacia el jugador
        Vector2 direction = new Vector2(
            playerPosition.x - sprite.getX(),
            playerPosition.y - sprite.getY()
        );
        sprite.setRotation(direction.angleDeg() - 90);  // -90 para corrección de ángulo

        // Disparo
        shootTimer += delta;
        if (shootTimer >= shootInterval) {
            shootTimer = 0;
            return playerPosition;
        }
        return null;
    }

    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public Vector2 getPosition() {
        return new Vector2(sprite.getX(), sprite.getY());
    }

    public Sprite getSprite() {
        return sprite;
    }
}
