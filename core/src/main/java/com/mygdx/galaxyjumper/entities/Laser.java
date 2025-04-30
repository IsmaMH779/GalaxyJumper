package com.mygdx.galaxyjumper.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Laser {
    private Sprite sprite;
    private Vector2 velocity;

    public Laser(Texture texture, float x, float y, float angleDegrees, float speed) {
        sprite = new Sprite(texture);
        sprite.setSize(10, 10); // Ajusta el tamaño si hace falta
        sprite.setOriginCenter();
        sprite.setPosition(x, y);
        sprite.setRotation(angleDegrees);

        // Calcular dirección con trigonometría
        float radians = (float) Math.toRadians(angleDegrees);
        velocity = new Vector2((float) Math.cos(radians), (float) Math.sin(radians)).scl(speed);
    }

    public void update(float delta) {
        sprite.translate(velocity.x * delta, velocity.y * delta);
    }

    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public boolean isOutOfScreen(float screenHeight) {
        return sprite.getY() > screenHeight || sprite.getY() < 0 || sprite.getX() < 0 || sprite.getX() > screenHeight; // Puedes ajustar si usas worldWidth
    }

    public void dispose() {
        sprite.getTexture().dispose();
    }
}
