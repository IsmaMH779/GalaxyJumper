package com.mygdx.galaxyjumper.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.galaxyjumper.entities.Laser;
import com.mygdx.galaxyjumper.entities.Joystick;
import com.mygdx.galaxyjumper.entities.Nave;
import com.mygdx.galaxyjumper.utils.InputHandler;

public class GameScreen implements Screen {
    private static final float VIRTUAL_WIDTH = 800;
    private static final float VIRTUAL_HEIGHT = 480;

    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private Nave nave;
    private Joystick joystick;
    private InputHandler inputHandler;
    private Texture backgroundTexture;
    private TextureRegion backgroundRegion;

    // balas
    private Texture bulletTexture;
    private Array<Laser> bullets;
    private float shootTimer = 0f;
    private float shootInterval = 0.2f;

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera); // Adapta manteniendo relación de aspecto
        batch = new SpriteBatch();

        // background

        backgroundTexture = new Texture("backgrounds/blue.png");
        backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        backgroundRegion = new TextureRegion(backgroundTexture);
        backgroundRegion.setRegion(0, 0,
            (int) VIRTUAL_WIDTH,
            (int) VIRTUAL_HEIGHT
        );

        //nave
        nave = new Nave(new Texture("images/playerShip1_blue.png"), VIRTUAL_WIDTH/2, VIRTUAL_HEIGHT/2, 350, viewport);

        joystick = new Joystick(
            new Texture("controls/transparent-light/transparentLight05.png"),
            new Texture("controls/transparent-light/transparentLight09.png"),
            100 // Radio en unidades virtuales
        );

        // balas
        bulletTexture = new Texture("images/Lasers/laserBlue03.png"); // Usa tu sprite
        bullets = new Array<>();


        inputHandler = new InputHandler(joystick, viewport);
        Gdx.input.setInputProcessor(inputHandler);
    }

    @Override
    public void render(float delta) {
        // Limpiar pantalla
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Actualizar lógica
        nave.update(delta, joystick.getDirection());

        // Controlar disparos
        shootTimer += delta;
        if (shootTimer >= shootInterval) {
            shootTimer = 0;
            bullets.add(new Laser(
                bulletTexture,
                nave.getX() + nave.getWidth() / 2 - bulletTexture.getWidth() / 2,
                nave.getY() + nave.getHeight() / 2 - bulletTexture.getHeight() / 2,
                nave.getRotation() + 90, // Ajuste porque el sprite apunta hacia arriba
                500 // velocidad
            ));
        }

        // Actualizar disparos existentes
        for (int i = bullets.size - 1; i >= 0; i--) {
            bullets.get(i).update(delta);
            if (bullets.get(i).isOutOfScreen(viewport.getWorldHeight())) {
                bullets.removeIndex(i);
            }
        }

        // Actualizar cámara
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        // Dibujar todo
        batch.begin();

        // Fondo en mosaico
        float textureWidth = backgroundTexture.getWidth();
        float textureHeight = backgroundTexture.getHeight();

        for (float x = 0; x < viewport.getWorldWidth(); x += textureWidth) {
            for (float y = 0; y < viewport.getWorldHeight(); y += textureHeight) {
                batch.draw(backgroundTexture, x, y);
            }
        }

        // Dibujar disparos
        for (Laser laser : bullets) {
            laser.draw(batch);
        }

        // Dibujar nave y joystick
        nave.draw(batch);
        joystick.draw(batch);

        batch.end();
    }


    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
        camera.update();
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
