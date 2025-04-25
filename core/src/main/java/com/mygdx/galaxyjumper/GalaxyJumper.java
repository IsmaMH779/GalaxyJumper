package com.mygdx.galaxyjumper;

import com.badlogic.gdx.Game;
import com.mygdx.galaxyjumper.screens.GameScreen;

public class GalaxyJumper extends Game {
    @Override
    public void create() {
        setScreen(new GameScreen());
    }
}
