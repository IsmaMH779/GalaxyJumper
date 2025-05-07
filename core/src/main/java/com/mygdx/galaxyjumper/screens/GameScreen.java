package com.mygdx.galaxyjumper.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.galaxyjumper.entities.Asteroid;
import com.mygdx.galaxyjumper.entities.Bullet;
import com.mygdx.galaxyjumper.entities.Explosion;
import com.mygdx.galaxyjumper.entities.Joystick;
import com.mygdx.galaxyjumper.entities.Nave;
import com.mygdx.galaxyjumper.utils.InputHandler;

public class GameScreen implements Screen {
    private static final float VIRTUAL_WIDTH = 800;
    private static final float VIRTUAL_HEIGHT = 480;

    // font
    private BitmapFont customFont;

    // vida
    private Texture lifeTexture;
    private int lives = 3;

    // utils del juego
    private float gameOverTimer = 0;
    private boolean isGameOver = false;

    // gameplay
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

    // explosiones
    private Array<Explosion> explosions;

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        batch = new SpriteBatch();

        // cargar la fuente personalizada
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font/kenvector_future_thin.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 28; // Tamaño de fuente
        parameter.color = Color.WHITE;
        customFont = generator.generateFont(parameter);
        generator.dispose();

        // textura de la vida

        lifeTexture = new Texture("images/playerShip1_blue.png");

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
        bulletTexture = new Texture("images/Lasers/laserBlue03.png");
        bullets = new Array<>();

        // asteroide
        asteroidTexture = new Texture("images/Meteors/meteorBrown_big1.png"); // ajusta la ruta
        asteroids = new Array<>();


        inputHandler = new InputHandler(joystick, viewport);
        Gdx.input.setInputProcessor(inputHandler);

        // explosiones
        explosions = new Array<>();
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

        // terminar el juego
        if (isGameOver) {
            customFont.draw(batch, "GAME OVER", viewport.getWorldWidth()/2 - 100, viewport.getWorldHeight()/2);

            gameOverTimer -= delta;
            if (gameOverTimer <= 0) {
                resetGame();
            }
        }

        // Dibujar las vidas (x3 sprite sprite sprite)
        customFont.draw(batch, "x" + lives, 10, viewport.getWorldHeight() - 10);

        for (int i = 0; i < lives; i++) {
            batch.draw(lifeTexture, 40 + i * 30, viewport.getWorldHeight() - 35, 25, 25);
        }

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


        // Actualizar explosiones
        for(int i = explosions.size - 1; i >= 0; i--) {
            explosions.get(i).update(delta);
            if(explosions.get(i).isComplete()) {
                explosions.removeIndex(i);
            }
        }

        for(int i = asteroids.size - 1; i >= 0; i--) {
            Asteroid asteroid = asteroids.get(i);

            for(int j = bullets.size - 1; j >= 0; j--) {
                if(asteroid.getBounds().overlaps(bullets.get(j).getBounds())) {
                    lives--;
                    // Crear explosión en la posición del asteroide
                    explosions.add(new Explosion(
                        new Vector2(asteroid.getBounds().x + asteroid.getBounds().width/2,
                            asteroid.getBounds().y + asteroid.getBounds().height/2)
                    ));

                    asteroids.removeIndex(i);
                    bullets.removeIndex(j);

                    if (lives <= 0) {
                        isGameOver = true;
                        gameOverTimer = 3; // 3 segundos para reiniciar
                    }
                    break;
                }
            }
        }

        for(Explosion explosion : explosions) {
            explosion.draw(batch);
        }

        batch.end();
    }

    private void resetGame() {
        lives = 3;
        isGameOver = false;
        bullets.clear();
        asteroids.clear();
        explosions.clear();
        nave = new Nave(new Texture("images/playerShip1_blue.png"), VIRTUAL_WIDTH/2, VIRTUAL_HEIGHT/2, 200, viewport);
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
