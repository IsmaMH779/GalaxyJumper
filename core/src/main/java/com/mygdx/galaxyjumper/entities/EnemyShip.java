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
    private boolean inPosition = false;

    public EnemyShip(Texture texture, Viewport viewport, Vector2 playerPosition) {
        this.sprite = new Sprite(texture);
        this.viewport = viewport;
        this.speed = 150f;
        this.shootInterval = 3f;
        this.shootTimer = 0f;

        sprite.setSize(40, 40);

        sprite.setOriginCenter();

        int spawnSide = MathUtils.random(3);
        float margin = 100f;

        switch(spawnSide) {
            case 0: // Arriba
                sprite.setPosition(
                    MathUtils.random(0, viewport.getWorldWidth()),
                    viewport.getWorldHeight() + margin
                );
                targetPosition = new Vector2(
                    sprite.getX(),
                    viewport.getWorldHeight() * 0.8f
                );
                break;

            case 1: // Derecha
                sprite.setPosition(
                    viewport.getWorldWidth() + margin,
                    MathUtils.random(0, viewport.getWorldHeight())
                );
                targetPosition = new Vector2(
                    viewport.getWorldWidth() * 0.8f,
                    sprite.getY()
                );
                break;

            case 2: // Abajo
                sprite.setPosition(
                    MathUtils.random(0, viewport.getWorldWidth()),
                    -margin
                );
                targetPosition = new Vector2(
                    sprite.getX(),
                    viewport.getWorldHeight() * 0.2f
                );
                break;

            case 3: // Izquierda
                sprite.setPosition(
                    -margin,
                    MathUtils.random(0, viewport.getWorldHeight())
                );
                targetPosition = new Vector2(
                    viewport.getWorldWidth() * 0.2f,
                    sprite.getY()
                );
                break;
        }
    }

    public Vector2 update(float delta, Vector2 playerPosition) {


        // Movimiento hacia la posición objetivo
        if (!inPosition) {
            Vector2 direction = new Vector2(
                targetPosition.x - sprite.getX(),
                targetPosition.y - sprite.getY()
            ).nor();

            float distance = direction.len();
            sprite.translate(direction.x * speed * delta, direction.y * speed * delta);

            // Comprobar si ha llegado a la posición objetivo
            if (Vector2.dst(sprite.getX(), sprite.getY(),
                targetPosition.x, targetPosition.y) < 5f) {
                inPosition = true;
            }
        }
        else {
            // Solo rotar cuando está en posición +  CALCULAR USANDO EL CENTRO DEL SPRITE
            Vector2 enemyCenter = new Vector2(
                sprite.getX() + sprite.getWidth()/2,
                sprite.getY() + sprite.getHeight()/2
            );

            Vector2 direction = new Vector2(
                playerPosition.x - enemyCenter.x,
                playerPosition.y - enemyCenter.y
            );

            // Suavizar la rotación
            float targetRotation = direction.angleDeg() - 90;
            float currentRotation = sprite.getRotation();

            // Interpolación suave (20% por frame)
            sprite.setRotation(MathUtils.lerp(currentRotation, targetRotation, 0.2f));
        }

        // Disparo
        shootTimer += delta;
        if (shootTimer >= shootInterval && inPosition) {
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
