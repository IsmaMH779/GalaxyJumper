package com.mygdx.galaxyjumper.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.galaxyjumper.GalaxyJumper;

public class MainMenuScreen implements Screen {
    private final GalaxyJumper game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private Texture backgroundTexture;
    private BitmapFont titleFont;
    private BitmapFont scoreFont;
    private Stage stage;
    private int highScore;

    private static final float VIRTUAL_WIDTH = 800;
    private static final float VIRTUAL_HEIGHT = 480;

    public MainMenuScreen(GalaxyJumper game) {
        this.game = game;
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        batch = new SpriteBatch();

        // Cargar preferencias para el high score
        Preferences prefs = Gdx.app.getPreferences("GalaxyJumper");
        highScore = prefs.getInteger("highScore", 0);

        // Fuentes
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font/kenvector_future_thin.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter titleParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        titleParams.size = 60;
        titleParams.color = Color.WHITE;
        titleFont = generator.generateFont(titleParams);

        // Fuente para el score
        FreeTypeFontGenerator.FreeTypeFontParameter scoreParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        scoreParams.size = 30;
        scoreFont = generator.generateFont(scoreParams);

        generator.dispose();

        // Fondo
        backgroundTexture = new Texture("backgrounds/blue.png");

        createButtons();
    }

    private void createButtons() {
        stage = new Stage(viewport, batch);
        Gdx.input.setInputProcessor(stage);

        // Texturas de botones
        Texture startTexture = new Texture("UI/menu/Start_BTN.png");
        Texture startPressedTexture = new Texture("UI/menu/Start_BTN.png");
        Texture exitTexture = new Texture("UI/menu/Exit_BTN.png");
        Texture exitPressedTexture = new Texture("UI/menu/Exit_BTN.png");

        // Crear botones
        ImageButton startButton = new ImageButton(
            new TextureRegionDrawable(startTexture),
            new TextureRegionDrawable(startPressedTexture)
        );

        ImageButton exitButton = new ImageButton(
            new TextureRegionDrawable(exitTexture),
            new TextureRegionDrawable(exitPressedTexture)
        );

        // Posicionar botones
        float buttonWidth = 200f;
        float buttonHeight = 80f;
        startButton.setSize(buttonWidth, buttonHeight);
        exitButton.setSize(buttonWidth, buttonHeight);

        startButton.setPosition(
            VIRTUAL_WIDTH/2 - buttonWidth/2,
            VIRTUAL_HEIGHT/2 - buttonHeight/2 + 50
        );

        exitButton.setPosition(
            VIRTUAL_WIDTH/2 - buttonWidth/2,
            VIRTUAL_HEIGHT/2 - buttonHeight/2 - 50
        );

        // Listeners
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game));
                dispose();
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        stage.addActor(startButton);
        stage.addActor(exitButton);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Dibujar fondo
        batch.draw(backgroundTexture, 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);

        // Dibujar título (centrado correctamente)
        String title = "GALAXY JUMPER";
        GlyphLayout titleLayout = new GlyphLayout(titleFont, title);
        float titleX = (VIRTUAL_WIDTH - titleLayout.width) / 2;
        float titleY = VIRTUAL_HEIGHT - 100;
        titleFont.draw(batch, titleLayout, titleX, titleY);

        // Dibujar high score (también mejorado)
        String scoreText = "High Score: " + highScore;
        GlyphLayout scoreLayout = new GlyphLayout(scoreFont, scoreText);
        float scoreX = VIRTUAL_WIDTH - scoreLayout.width - 20; // 20px de margen
        float scoreY = VIRTUAL_HEIGHT - 30;
        scoreFont.draw(batch, scoreLayout, scoreX, scoreY);

        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.position.set(VIRTUAL_WIDTH/2, VIRTUAL_HEIGHT/2, 0);
    }

    @Override
    public void dispose() {
        batch.dispose();
        backgroundTexture.dispose();
        titleFont.dispose();
        scoreFont.dispose();
        stage.dispose();
    }

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
