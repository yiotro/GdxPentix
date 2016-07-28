package yio.tro.gdxpentix;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.Random;

/**
 * Created by ivan on 08.08.2014.
 */
public class BackgroundMenuDecoration {

    double angles[];
    double speeds[];
    double x, y, r;
    YioGdxGame yioGdxGame;
    Random random;
    ShapeRenderer shapeRenderer;
    double thickness;

    public BackgroundMenuDecoration(double x, double y, YioGdxGame yioGdxGame) {
        this.x = x;
        this.y = y;
        this.yioGdxGame = yioGdxGame;
        r = 0.05 * Gdx.graphics.getHeight();
        angles = new double[4];
        speeds = new double[4];
        random = new Random();
        for (int i=0; i<4; i++) speeds[i] = 0.1 * (random.nextDouble() - 0.5);
        shapeRenderer = new ShapeRenderer();
        thickness = 0.01 * Gdx.graphics.getWidth();
    }

    public void move() {
        for (int i=0; i<4; i++) {
            angles[i] += speeds[i];
        }
    }

    public void render() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 1);
//        GameView.drawLine(x, y, x + r * Math.cos(angles[0]), y + r * Math.sin(angles[0]), thickness, shapeRenderer);
//        GameView.drawLine(x + r * Math.cos(angles[0]), y + r * Math.sin(angles[0]), x + r * Math.cos(angles[0]) + r * Math.cos(angles[1]), y + r * Math.sin(angles[0]) + r * Math.sin(angles[1]), thickness, shapeRenderer);
//        GameView.drawLine(x, y, x + r * Math.cos(angles[2]), y + r * Math.sin(angles[2]), thickness, shapeRenderer);
//        GameView.drawLine(x + r * Math.cos(angles[2]), y + r * Math.sin(angles[2]), x + r * Math.cos(angles[2]) + r * Math.cos(angles[3]), y + r * Math.sin(angles[2]) + r * Math.sin(angles[3]), thickness, shapeRenderer);
        shapeRenderer.circle((float)x, (float)y, 3f * (float)thickness);
        shapeRenderer.circle((float)(x + r * Math.cos(angles[0])), (float)(y + r * Math.sin(angles[0])), 2f * (float)thickness);
        shapeRenderer.circle((float)(x + r * Math.cos(angles[2])), (float)(y + r * Math.sin(angles[2])), 2f * (float)thickness);
        shapeRenderer.circle((float)(x + r * Math.cos(angles[0]) + r * Math.cos(angles[1])), (float)(y + r * Math.sin(angles[0]) + r * Math.sin(angles[1])), 2f * (float)thickness);
        shapeRenderer.circle((float)(x + r * Math.cos(angles[2]) + r * Math.cos(angles[3])), (float)(y + r * Math.sin(angles[2]) + r * Math.sin(angles[3])), 2f * (float)thickness);
        shapeRenderer.end();
    }
}
