package com.mygdx.galaxyjumper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameScreen implements Screen {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Texture naveTexture;
    private Sprite naveSprite;

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480); // Tamaño de la vista
        batch = new SpriteBatch();

        // Carga la textura de la nave (¡asegúrate de añadirla en assets/images/)
        naveTexture = new Texture(Gdx.files.internal("images/playerShip1_blue.png"));
        naveSprite = new Sprite(naveTexture);
        naveSprite.setPosition(400, 240); // Posición inicial
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Actualiza la cámara y el batch
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        naveSprite.draw(batch);
        batch.end();
    }

    // ... (otros métodos de Screen como resize, pause, etc.)

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
