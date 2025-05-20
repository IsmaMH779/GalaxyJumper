package com.mygdx.galaxyjumper;

import com.badlogic.gdx.Game;
import com.mygdx.galaxyjumper.screens.MainMenuScreen;

public class GalaxyJumper extends Game {
    @Override
    public void create() {
        setScreen(new MainMenuScreen(this));
    }
}
