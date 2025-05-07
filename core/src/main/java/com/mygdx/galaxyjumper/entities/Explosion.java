// Clase Explosion.java
package com.mygdx.galaxyjumper.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Explosion {
    private Texture explosionTexture;
    private Texture smokeTexture;
    private Vector2 position;
    private float stateTime;
    private boolean showSmoke;

    // Tiempos de animación
    private static final float EXPLOSION_DURATION = 0.2f;
    private static final float SMOKE_DURATION = 0.2f;

    public Explosion(Vector2 position) {
        this.explosionTexture = new Texture("Explosion/explosion00.png");
        this.smokeTexture = new Texture("BlackSmoke/blackSmoke00.png");
        this.position = new Vector2(position);
        this.stateTime = 0;
        this.showSmoke = false;
    }

    public void update(float delta) {
        stateTime += delta;

        // Cambiar a humo después de la explosión
        if(!showSmoke && stateTime >= EXPLOSION_DURATION) {
            showSmoke = true;
            stateTime = 0; // Reset timer para humo
        }
    }

    public void draw(SpriteBatch batch) {
        if(showSmoke) {
            batch.draw(smokeTexture, position.x - 32, position.y - 32, 64, 64);
        } else {
            batch.draw(explosionTexture, position.x - 32, position.y - 32, 64, 64);
        }
    }

    public boolean isComplete() {
        return showSmoke && stateTime >= SMOKE_DURATION;
    }

    public void dispose() {
        explosionTexture.dispose();
        smokeTexture.dispose();
    }
}
