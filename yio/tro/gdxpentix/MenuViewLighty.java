package yio.tro.gdxpentix;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;

/**
 * Created by ivan on 22.07.14.
 */
public class MenuViewLighty {
    YioGdxGame yioGdxGame;
    MenuControllerLighty menuControllerLighty;
    TextureRegion buttonPixel, shadowCorner, shadowSide, blackCircle, scrollerCircle, grayTransCircle;
    ShapeRenderer shapeRenderer;
    SpriteBatch batch;
    int cornerSize;
    float x1, y1, x2, y2; // local variables for rendering
    Color c; // local variable for rendering

    public MenuViewLighty(YioGdxGame yioGdxGame) {
        this.yioGdxGame = yioGdxGame;
        menuControllerLighty = yioGdxGame.menuControllerLighty;
        shapeRenderer = new ShapeRenderer();
        cornerSize = (int)(0.02 * Gdx.graphics.getHeight());
        buttonPixel = GameView.loadTextureRegionByName("button_pixel.png", false);
        shadowCorner = GameView.loadTextureRegionByName("corner_shadow.png", true);
        shadowSide = GameView.loadTextureRegionByName("side_shadow.png", true);
        blackCircle = GameView.loadTextureRegionByName("anim_circle_high_res.png", false);
        scrollerCircle = GameView.loadTextureRegionByName("scroller_circle.png", false);
        grayTransCircle = GameView.loadTextureRegionByName("gray_transition_circle.png", false);
    }

    private void drawRoundRect(SimpleRectangle pos) {
        shapeRenderer.rect(pos.x + cornerSize, pos.y, pos.width - 2 * cornerSize, pos.height);
        shapeRenderer.rect(pos.x, pos.y + cornerSize, pos.width, pos.height - 2 * cornerSize);
        shapeRenderer.circle(pos.x + cornerSize, pos.y + cornerSize, cornerSize, 16);
        shapeRenderer.circle(pos.x + pos.width - cornerSize, pos.y + cornerSize, cornerSize, 16);
        shapeRenderer.circle(pos.x + cornerSize, pos.y + pos.height - cornerSize, cornerSize, 16);
        shapeRenderer.circle(pos.x + pos.width - cornerSize, pos.y + pos.height - cornerSize, cornerSize, 16);
    }

    private void drawRect(SimpleRectangle pos) {
        shapeRenderer.rect((float)pos.x, (float)pos.y, (float)pos.width, (float)pos.height);
    }

    private void renderShadow(ButtonLighty buttonLighty, SpriteBatch batch) {
        x1 = buttonLighty.x1;
        x2 = buttonLighty.x2;
        y1 = buttonLighty.y1;
        y2 = buttonLighty.y2;
        if (buttonLighty.factorModel.get() <= 1)
            batch.setColor(c.r, c.g, c.b, buttonLighty.factorModel.get());
        else batch.setColor(c.r, c.g, c.b, 1);
        batch.draw(shadowSide, x1 + cornerSize, y2 - cornerSize, 2 * (buttonLighty.hor - cornerSize), 2 * cornerSize);
        batch.draw(shadowSide, x1 + cornerSize, y1 + cornerSize, 0, 0, 2 * (buttonLighty.ver - cornerSize), 2 * cornerSize, 1, 1, 90);
        batch.draw(shadowSide, x2 - cornerSize, y1 + cornerSize, 0, 0, 2 * (buttonLighty.hor - cornerSize), 2 * cornerSize, 1, 1, 180);
        batch.draw(shadowSide, x2 - cornerSize, y2 - cornerSize, 0, 0, 2 * (buttonLighty.ver - cornerSize), 2 * cornerSize, 1, 1, 270);
        batch.draw(shadowCorner, x1 - cornerSize, y2 - cornerSize, 2 * cornerSize, 2 * cornerSize);
        batch.draw(shadowCorner, x1 + cornerSize, y1 - cornerSize, 0, 0, 2 * cornerSize, 2 * cornerSize, 1, 1, 90);
        batch.draw(shadowCorner, x2 + cornerSize, y1 + cornerSize, 0, 0, 2 * cornerSize, 2 * cornerSize, 1, 1, 180);
        batch.draw(shadowCorner, x2 - cornerSize, y2 + cornerSize, 0, 0, 2 * cornerSize, 2 * cornerSize, 1, 1, 270);
    }

    private void renderShadow(SimpleRectangle rectangle, float factor, SpriteBatch batch) {
        float hor = 0.5f * factor * rectangle.width;
        float ver = 0.5f * factor * rectangle.height;
        float cx = rectangle.x + 0.5f * rectangle.width;
        float cy = rectangle.y + 0.5f * rectangle.height;
        x1 = cx - hor;
        x2 = cx + hor;
        y1 = cy - ver;
        y2 = cy + ver;
        if (factor <= 1)
            batch.setColor(c.r, c.g, c.b, factor);
        else batch.setColor(c.r, c.g, c.b, 1);
        batch.draw(shadowSide, x1 + cornerSize, y2 - cornerSize, 2 * (hor - cornerSize), 2 * cornerSize);
        batch.draw(shadowSide, x1 + cornerSize, y1 + cornerSize, 0, 0, 2 * (ver - cornerSize), 2 * cornerSize, 1, 1, 90);
        batch.draw(shadowSide, x2 - cornerSize, y1 + cornerSize, 0, 0, 2 * (hor - cornerSize), 2 * cornerSize, 1, 1, 180);
        batch.draw(shadowSide, x2 - cornerSize, y2 - cornerSize, 0, 0, 2 * (ver - cornerSize), 2 * cornerSize, 1, 1, 270);
        batch.draw(shadowCorner, x1 - cornerSize, y2 - cornerSize, 2 * cornerSize, 2 * cornerSize);
        batch.draw(shadowCorner, x1 + cornerSize, y1 - cornerSize, 0, 0, 2 * cornerSize, 2 * cornerSize, 1, 1, 90);
        batch.draw(shadowCorner, x2 + cornerSize, y1 + cornerSize, 0, 0, 2 * cornerSize, 2 * cornerSize, 1, 1, 180);
        batch.draw(shadowCorner, x2 - cornerSize, y2 + cornerSize, 0, 0, 2 * cornerSize, 2 * cornerSize, 1, 1, 270);
    }

    boolean checkForSpecialMask(ButtonLighty buttonLighty) {
        switch (buttonLighty.id) {
            case 3:
                if (buttonLighty.factorModel.get() > 0.1) shapeRenderer.circle(buttonLighty.cx, buttonLighty.cy, (float)(0.8 + 0.2 * buttonLighty.selectionFactor.get()) * buttonLighty.hor);
                return true;
        }
        return false;
    }

    boolean checkForSpecialAnimationMask(ButtonLighty buttonLighty) { // mask when circle fill animation on press
        SimpleRectangle pos = buttonLighty.animPos;
        switch (buttonLighty.id) {
            case 41: // main menu button
                shapeRenderer.rect(pos.x, (pos.y + 0.5f * pos.height), pos.width, 0.5f * pos.height);
                return true;
            case 42: // resume button
                shapeRenderer.rect(pos.x, pos.y, pos.width, 0.5f * pos.height);
                return true;
            case 43: // new game button
                shapeRenderer.rect(pos.x, pos.y, pos.width, pos.height);
                return true;
            case 44: // restart button
                shapeRenderer.rect(pos.x, pos.y, pos.width, pos.height);
                return true;
        }
        return false;
    }

    boolean checkForSpecialAlpha(ButtonLighty buttonLighty) {
        switch (buttonLighty.id) {
            case 11:
                if (buttonLighty.factorModel.get() < 0.5) batch.setColor(c.r, c.g, c.b, 0);
                else batch.setColor(c.r, c.g, c.b, 1);
                return true;
            default: return false;
        }
    }

    public void render(boolean renderAliveButtons, boolean renderDyingButtons) {
        ArrayList<ButtonLighty> buttons = menuControllerLighty.buttons;
        batch = yioGdxGame.batch;
        c = batch.getColor();

        //shadows
        batch.begin();
        for (ButtonLighty buttonLighty : buttons) {
            if (buttonLighty.isVisible() &&
                    buttonLighty.hasShadow &&
                    !buttonLighty.mandatoryShadow &&
                    ((renderAliveButtons && buttonLighty.factorModel.getGravity() >= 0) || (renderDyingButtons && buttonLighty.factorModel.getGravity() <= 0))) {
                renderShadow(buttonLighty, batch);
            }
        }
        batch.end();

        // Drawing masks
        YioGdxGame.maskingBegin();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (ButtonLighty buttonLighty : buttons) {
            if (buttonLighty.isVisible()) {
                if (checkForSpecialMask(buttonLighty)) continue;
                if (buttonLighty.rectangularMask &&
                        !buttonLighty.currentlyTouched &&
                        ((renderAliveButtons && buttonLighty.factorModel.getGravity() >= 0) || (renderDyingButtons && buttonLighty.factorModel.getGravity() <= 0))) {
                    drawRect(buttonLighty.position);
                    continue;
                }
                drawRoundRect(buttonLighty.animPos);
            }
        }
        shapeRenderer.end();


        // Drawing buttons
        batch.begin();
        YioGdxGame.maskingContinue();
        SimpleRectangle ap;
        for (ButtonLighty buttonLighty : buttons) {
            if (buttonLighty.isVisible() &&
                    !buttonLighty.onlyShadow &&
                    ((renderAliveButtons && buttonLighty.factorModel.getGravity() >= 0) || (renderDyingButtons && buttonLighty.factorModel.getGravity() <= 0))) {
                if (buttonLighty.mandatoryShadow) renderShadow(buttonLighty, batch);
                if (!checkForSpecialAlpha(buttonLighty)) {
                    batch.setColor(c.r, c.g, c.b, 1);
                }
                ap = buttonLighty.animPos;
                batch.draw(buttonLighty.textureRegion, ap.x, ap.y, ap.width, ap.height);
                if (buttonLighty.isCurrentlyTouched() && (!buttonLighty.touchAnimation || buttonLighty.selectionFactor.get() > 0.99)) {
                    batch.setColor(c.r, c.g, c.b, 0.7f * buttonLighty.selAlphaFactor.get());
                    batch.draw(buttonPixel, ap.x, ap.y, ap.width, ap.height);
                    if (buttonLighty.touchAnimation && buttonLighty.lockAction) buttonLighty.lockAction = false;
                }
            }
        }
        batch.setColor(c.r, c.g, c.b, 1);
        batch.end();
        YioGdxGame.maskingEnd();

        specialInfoPanelRenderPiece();

        for (ButtonLighty buttonLighty : buttons) {
            if (buttonLighty.isVisible() &&
                    buttonLighty.isCurrentlyTouched() &&
                    buttonLighty.touchAnimation &&
                    buttonLighty.selectionFactor.get() < 1 &&
                    ((renderAliveButtons && buttonLighty.factorModel.getDy() >= 0) || (renderDyingButtons && buttonLighty.factorModel.getDy() < 0))) {
                YioGdxGame.maskingBegin();
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                checkForSpecialAnimationMask(buttonLighty);
                drawRoundRect(buttonLighty.animPos);
                shapeRenderer.end();

                batch.begin();
                YioGdxGame.maskingContinue();
                batch.setColor(c.r, c.g, c.b, 0.7f * buttonLighty.selAlphaFactor.get());
                float r = buttonLighty.selectionFactor.get() * buttonLighty.animR;
                batch.draw(blackCircle, buttonLighty.touchX - r, buttonLighty.touchY - r, 2 * r, 2 * r);
                batch.end();
                batch.setColor(c.r, c.g, c.b, 1);
                YioGdxGame.maskingEnd();
            }
        }
    }

    void specialInfoPanelRenderPiece() {
        ButtonLighty infoPanel = menuControllerLighty.getButtonById(11);
        if (infoPanel != null && menuControllerLighty.getButtonById(11).isVisible()) {
            YioGdxGame.maskingBegin();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            drawRoundRect(infoPanel.animPos);
            shapeRenderer.end();
            YioGdxGame.maskingContinue();
            renderTransitionCircle(grayTransCircle,
                    menuControllerLighty.infoPanelFactor.get() * menuControllerLighty.infoPanelFactor.get(),
                    infoPanel.animPos,
                    batch,
                    (float)(infoPanel.animPos.x + 0.05 * infoPanel.animPos.width + 0.65 * Math.sqrt(infoPanel.factorModel.get()) * infoPanel.animPos.width),
                    (float)(infoPanel.animPos.y + 0.95 * infoPanel.animPos.height - 0.65 * Math.sqrt(infoPanel.factorModel.get()) * infoPanel.animPos.height));
            YioGdxGame.maskingEnd();
        }
    }

    public static void renderTransitionCircle(TextureRegion circleTexture, float factor, SimpleRectangle frame, SpriteBatch batch, float x, float y) {
        Color c = batch.getColor();
        if (factor < 0.5) batch.setColor(c.r, c.g, c.b, 1);
        else batch.setColor(c.r, c.g, c.b, 1 - 2f * factor);
        float r = 0.5f * (float)Math.sqrt(2f * factor) * (float) YioGdxGame.distance(0, 0, frame.width, frame.height);
        batch.begin();
        batch.draw(circleTexture, x - r, y - r, 2 * r, 2 * r);
        batch.end();
        batch.setColor(c.r, c.g, c.b, 1);
    }
}
