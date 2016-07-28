package yio.tro.gdxpentix;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by ivan on 13.08.2014.
 */
public class Splat {

    GameController gameController;
    TextureRegion textureRegion;
    float x, y, dx, dy, wind, r, speedMultiplier;

    public Splat(GameController gameController, TextureRegion textureRegion, float x, float y) {
        this.gameController = gameController;
        this.textureRegion = textureRegion;
        this.x = x;
        this.y = y;
    }

    void move() {
        x += dx;
        y -= dy * speedMultiplier;
        dx += wind;
        if (Math.abs(dx) > 0.0002f * Gdx.graphics.getWidth()) wind = -wind;
        if (gameController != null) {
            if (y < 0) y = gameController.h;
            if (x < 0) x = gameController.w;
            if (x > gameController.w) x = 0;
        }
    }

    void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    void setSpeed(float sdx, float sdy) {
        dx = sdx;
        dy = sdy;
        wind = -0.01f * dx;
    }

    public void setRadius(float r) {
        this.r = r;
        speedMultiplier = (0.05f * Gdx.graphics.getHeight()) / r;
//        speedMultiplier = (float)Math.sqrt(speedMultiplier);
    }

    boolean isVisible() {
        return y < Gdx.graphics.getHeight() + r;
    }
}
