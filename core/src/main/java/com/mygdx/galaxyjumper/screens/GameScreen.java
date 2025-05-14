package com.mygdx.galaxyjumper.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
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
import com.mygdx.galaxyjumper.entities.Experience;
import com.mygdx.galaxyjumper.entities.Explosion;
import com.mygdx.galaxyjumper.entities.Joystick;
import com.mygdx.galaxyjumper.entities.Nave;
import com.mygdx.galaxyjumper.utils.InputHandler;

public class GameScreen implements Screen {
    private static final float VIRTUAL_WIDTH = 800;
    private static final float VIRTUAL_HEIGHT = 480;

    // font
    private BitmapFont customFont;
    private BitmapFont defaultFont;

    // vida
    private Texture lifeIcon;
    private int lives;

    // game utils
    private boolean isGameOver = false;
    private float gameOverTimer = 0f;
    private static final float GAME_OVER_DURATION = 2f;

    // experiencia
    // Textura y array para las monedas
    private Texture xpTexture;
    private Array<Experience> xpList;

    // Contador de experiencia
    private int xpCollected = 0;


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

    // Variables para controlar la progresión
    private float gameTimer = 0f;
    // Intervalo inicial de aparición
    private float initialSpawnInterval = 1f;
    // Mínimo tiempo entre oleadas
    private float minSpawnInterval = 0.5f;
    // Asteroides por oleada inicial
    private int baseAsteroidsPerWave = 1;
    // Máximo de asteroides por oleada
    private int maxAsteroidsActive = 3;
    // Cantidad actual de asteroides por oleada
    private int currentWaveSize = 1;
    // Puntos necesarios para subir dificultad
    private static final int XP_THRESHOLD = 10;

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
        parameter.size = 25;
        parameter.color = Color.WHITE;
        customFont = generator.generateFont(parameter);
        // tras generator.dispose();
        defaultFont = new BitmapFont();

        generator.dispose();

        // inicializar vida
        lives = 3;
        lifeIcon = new Texture("images/playerShip1_blue.png");

        // background
        backgroundTexture = new Texture("backgrounds/blue.png");
        backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        backgroundRegion = new TextureRegion(backgroundTexture);
        backgroundRegion.setRegion(0, 0,
            (int) VIRTUAL_WIDTH,
            (int) VIRTUAL_HEIGHT
        );

        // experience
        xpTexture = new Texture("images/Effects/star3.png");
        xpList = new Array<>();

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
        gameTimer += delta;

        if (isGameOver) {
            // Incrementa el temporizador
            gameOverTimer += delta;

            // Dibuja "Game Over" centrado
            String text = "GAME OVER";
            GlyphLayout layout = new GlyphLayout(customFont, text);
            float x = (viewport.getWorldWidth() - layout.width) / 2;
            float y = (viewport.getWorldHeight() + layout.height) / 2;
            batch.begin();
            customFont.draw(batch, layout, x, y);
            batch.end();

            // Tras la duración, reinicia la partida
            if (gameOverTimer >= GAME_OVER_DURATION) {
                resetGame();
                isGameOver = false;
            }
            return;
        }

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

        // Mostrar vidas: "x3"
        defaultFont.draw(batch, "x", 20, viewport.getWorldHeight() - 10);
        customFont.draw(batch, String.valueOf(lives),
            36, viewport.getWorldHeight() - 10);
        // Dibujar iconos de vida

        for (int i = 0; i < lives; i++) {
            batch.draw(lifeIcon,
                60 + i * (25 + 5),
                viewport.getWorldHeight() - 30,
                25,
                20);
        }

        // Mostrar Experiencia
        String s = "Stars: " + xpCollected;
        BitmapFont font = defaultFont;  // o customFont
        GlyphLayout layout = new GlyphLayout(font, s);

        // coordenadas: margen 20px desde borde derecho y superior
        float x = viewport.getWorldWidth() - layout.width - 20;
        float y = viewport.getWorldHeight() - 20;
        font.draw(batch, layout, x, y);


        // Dibujar disparos
        for (Bullet bullet : bullets) {
            bullet.draw(batch);
        }

        // Dibujar nave y joystick
        nave.draw(batch);
        joystick.draw(batch);

        // Dibujar asteroides

        asteroidTimer += delta;

        // Calcular intervalo dinámico (disminuye con el tiempo)
        float currentSpawnInterval = MathUtils.clamp(
            initialSpawnInterval - (gameTimer * 0.03f), // Ajusta 0.03 para velocidad de progresión
            minSpawnInterval,
            initialSpawnInterval
        );

        // Calcular cantidad de asteroides por oleada
        currentWaveSize = (xpCollected >= XP_THRESHOLD) ? 2 : 1;

        if (asteroidTimer >= currentSpawnInterval) {
            asteroidTimer = 0;

            // Calcula cuántos podemos spawnear sin superar el máximo
            int remainingSlots = maxAsteroidsActive - asteroids.size;
            if(remainingSlots > 0) {
                int asteroidsToSpawn = Math.min(currentWaveSize, remainingSlots);

                for (int i = 0; i < asteroidsToSpawn; i++) {
                    Vector2 spawnPos = getRandomSpawnPosition();
                    Vector2 targetPos = new Vector2(
                        nave.getCenterX() + MathUtils.random(-50f, 50f),
                        nave.getCenterY() + MathUtils.random(-50f, 50f)
                    );

                    asteroids.add(new Asteroid(
                        asteroidTexture,
                        spawnPos,
                        targetPos,
                        200f,
                        viewport
                    ));
                }
            }
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
                    // Crear explosión en la posición del asteroide
                    explosions.add(new Explosion(
                        new Vector2(asteroid.getBounds().x + asteroid.getBounds().width/2,
                            asteroid.getBounds().y + asteroid.getBounds().height/2)
                    ));
                    asteroids.removeIndex(i);
                    bullets.removeIndex(j);

                    // posición de la moneda: centro del asteroide
                    float coinX = asteroid.getBounds().x + asteroid.getBounds().width / 2 - 16;
                    float coinY = asteroid.getBounds().y + asteroid.getBounds().height / 2 - 16;


                    Experience xp = new Experience(xpTexture, coinX, coinY);
                    xp.getSprite().setSize(20, 20);
                    xpList.add(xp);


                    break;
                }
            }
            // si el asteroide choca con la nave
            if (asteroid.getBounds().overlaps(nave.getSprite().getBoundingRectangle())) {
                lives = Math.max(0, lives - 1);
                // mostrar la explosion
                explosions.add(new Explosion(
                    new Vector2(asteroid.getBounds().x + asteroid.getBounds().width/2,
                        asteroid.getBounds().y + asteroid.getBounds().height/2)
                ));

                asteroids.removeIndex(i);
                if (lives == 0 && !isGameOver) {
                    isGameOver = true;
                    gameOverTimer = 0f;
                }
            }
        }

        for(Explosion explosion : explosions) {
            explosion.draw(batch);
        }

        for (int i = xpList.size - 1; i >= 0; i--) {
            Experience xp = xpList.get(i);
            xp.update(delta);

            // Verificar colisión con la nave
            if (xp.getBounds().overlaps(nave.getBounds())) {
                xpCollected++;
                xpList.removeIndex(i);
                continue;
            }

            // Eliminar si expira
            if (xp.isExpired()) {
                xpList.removeIndex(i);
            }
        }

        // renderizar todas las monedas
        for (Experience c : xpList) {
            c.draw(batch);
        }

        // Añade en el HUD para mostrar el progreso
        String progressText = (xpCollected < XP_THRESHOLD) ?
            "Siguiente nivel: " + (XP_THRESHOLD - xpCollected) :
            "¡Nivel máximo!";
        GlyphLayout progressLayout = new GlyphLayout(customFont, progressText);
        float progressX = viewport.getWorldWidth() - progressLayout.width - 20;
        float progressY = viewport.getWorldHeight() - 90;
        customFont.draw(batch, progressText, progressX, progressY);

        batch.end();
    }

    // método para generar posiciones aleatorias
    private Vector2 getRandomSpawnPosition() {
        float margin = 100f;
        int edge = MathUtils.random(3); // 0=top, 1=right, 2=bottom, 3=left

        switch (edge) {
            case 0: // Top
                return new Vector2(
                    MathUtils.random(-margin, viewport.getWorldWidth() + margin),
                    viewport.getWorldHeight() + margin
                );
            case 1: // Right
                return new Vector2(
                    viewport.getWorldWidth() + margin,
                    MathUtils.random(-margin, viewport.getWorldHeight() + margin)
                );
            case 2: // Bottom
                return new Vector2(
                    MathUtils.random(-margin, viewport.getWorldWidth() + margin),
                    -margin
                );
            default: // Left
                return new Vector2(
                    -margin,
                    MathUtils.random(-margin, viewport.getWorldHeight() + margin)
                );
        }
    }

    private void resetGame() {
        lives = 3;
        // Limpiar experiencia al reiniciar
        xpList.clear();
        // Resetear contador
        xpCollected = 0;
        bullets.clear();
        asteroids.clear();
        explosions.clear();
        nave = new Nave(new Texture("images/playerShip1_blue.png"), VIRTUAL_WIDTH/2, VIRTUAL_HEIGHT/2, 200, viewport);
        gameTimer = 0f;
        currentWaveSize = 1;
        xpCollected = 0;
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
