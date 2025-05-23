package com.mygdx.galaxyjumper.utils;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.galaxyjumper.entities.Joystick;

public class InputHandler implements InputProcessor {
    private Joystick joystick;
    private Viewport viewport;
    private Vector3 touchPos = new Vector3();

    public InputHandler(Joystick joystick, Viewport viewport) {
        this.joystick = joystick;
        this.viewport = viewport;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // Convertir coordenadas reales a virtuales
        touchPos.set(screenX, screenY, 0);
        viewport.unproject(touchPos);

        if (!joystick.isVisible()) {
            joystick.setPosition(touchPos.x, touchPos.y, pointer);
            return true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        joystick.hide(pointer);
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        touchPos.set(screenX, screenY, 0);
        viewport.unproject(touchPos);

        if (joystick.isVisible() && joystick.getActivePointer() == pointer) {
            joystick.update(touchPos.x, touchPos.y);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }


}
