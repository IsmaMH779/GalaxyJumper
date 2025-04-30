package com.mygdx.galaxyjumper.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.galaxyjumper.entities.Asteroid;
import com.mygdx.galaxyjumper.entities.Bullet;
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
    private Array<Bullet> bullets;
    // tiempo entre disparos (en segundos)
    private float shootInterval = 0.8f;
    // Velocidad de la bala
    private float bulletSpeed = 300f;
    private float shootTimer = 0;

    // asteroide

    private Array<Asteroid> asteroids;
    private Texture asteroidTexture;
    private float asteroidTimer = 0;
    private float asteroidSpawnInterval = 2f;



    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
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
        nave = new Nave(new Texture("images/playerShip1_blue.png"), VIRTUAL_WIDTH/2, VIRTUAL_HEIGHT/2, 200, viewport);

        // joystick
        joystick = new Joystick(
            new Texture("controls/transparent-light/transparentLight05.png"),
            new Texture("controls/transparent-light/transparentLight09.png"),
            100 // Radio en unidades virtuales
        );

        // balas
        bulletTexture = new Texture("images/Lasers/laserBlue03.png"); // Usa tu sprite
        bullets = new Array<>();

        // asteroide
        asteroidTexture = new Texture("images/Meteors/meteorBrown_big1.png"); // ajusta la ruta
        asteroids = new Array<>();


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

            Vector2 bulletDirection = nave.getDirection();
            Vector2 bulletPosition = nave.getGunTip();

            bullets.add(new Bullet(
                bulletTexture,
                bulletPosition.x,
                bulletPosition.y,
                bulletDirection.cpy(),
                bulletSpeed
            ));
        }

        // Actualizar disparos existentes
        for (int i = bullets.size - 1; i >= 0; i--) {
            bullets.get(i).update(delta);
            if (bullets.get(i).isOutOfScreen(camera, viewport)) {
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
        for (Bullet bullet : bullets) {
            bullet.draw(batch);
        }

        // Dibujar nave y joystick
        nave.draw(batch);
        joystick.draw(batch);

        // Dibujar asteroides

        asteroidTimer += delta;
        if (asteroidTimer >= asteroidSpawnInterval) {
            asteroidTimer = 0;

            // Posición aleatoria fuera del borde
            Vector2 spawnPos = new Vector2();
            float margin = 50;
            int edge = MathUtils.random(3); // 0=top, 1=right, 2=bottom, 3=left

            switch (edge) {
                case 0: // Top
                    spawnPos.set(MathUtils.random(viewport.getWorldWidth()), viewport.getWorldHeight() + margin);
                    break;
                case 1: // Right
                    spawnPos.set(viewport.getWorldWidth() + margin, MathUtils.random(viewport.getWorldHeight()));
                    break;
                case 2: // Bottom
                    spawnPos.set(MathUtils.random(viewport.getWorldWidth()), -margin);
                    break;
                case 3: // Left
                    spawnPos.set(-margin, MathUtils.random(viewport.getWorldHeight()));
                    break;
            }

            // Posición de la nave actual
            Vector2 targetPos = new Vector2(nave.getCenterX(), nave.getCenterY());

            asteroids.add(new Asteroid(asteroidTexture, spawnPos, targetPos, 200, viewport));
        }

        // actualizar y dibujar asteroides
        for (int i = asteroids.size - 1; i >= 0; i--) {
            Asteroid asteroid = asteroids.get(i);
            asteroid.update(delta);
            asteroid.draw(batch);

            if (asteroid.isOutOfScreen()) {
                asteroids.removeIndex(i);
            }
        }

        // agregar la colision de balas
        for (int i = asteroids.size - 1; i >= 0; i--) {
            Asteroid asteroid = asteroids.get(i);

            for (int j = bullets.size - 1; j >= 0; j--) {
                if (asteroid.getBounds().overlaps(bullets.get(j).getBounds())) {
                    asteroids.removeIndex(i);
                    bullets.removeIndex(j);
                    break;
                }
            }
        }

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
