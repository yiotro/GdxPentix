package yio.tro.gdxpentix;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.gdxpentix.behaviors.ReactBehavior;
import yio.tro.gdxpentix.factor_yio.FactorYio;

import java.util.ArrayList;

/**
 * Created by ivan on 22.07.14.
 */
public class ButtonLighty {
    public MenuControllerLighty menuControllerLighty;
    public SimpleRectangle position, animPos;
    public TextureRegion textureRegion;
    public FactorYio factorModel, selectionFactor, selAlphaFactor;
    public int id; // must be unique for every menu button
    public boolean touchable;
    public boolean visible;
    public static final int ANIM_DEFAULT = 0;
    public static final int ANIM_UP = 1;
    public static final int ANIM_DOWN = 2;
    public static final int ANIM_COLLAPSE_UP = 3;
    public static final int ANIM_SOLID = 4;
    public static final int ANIM_FROM_CENTER = 5;
    ReactBehavior reactBehavior;
    long lastTimeTouched;
    boolean currentlyTouched;
    int touchDelay, animType;
    ArrayList<String> text;
    Color backColor;
    boolean needToPerformAction;
    long timeToPerformAction;
    Sound pressSound;
    float hor, ver, cx, cy, touchX, touchY, animR;
    float x1, x2, y1, y2;
    float deltaSizeArgument, deltaSize;
    boolean hasShadow, mandatoryShadow, rectangularMask, onlyShadow, touchAnimation, lockAction, deltaAnimationEnabled; // mandatory shadow - draw shadow right before button

    public ButtonLighty(SimpleRectangle position, int id, MenuControllerLighty menuControllerLighty) {
        this.menuControllerLighty = menuControllerLighty;
        this.position = position;
        this.id = id;
        touchable = false;
        visible = false;
        touchDelay = 1000;
        factorModel = new FactorYio();
        selectionFactor = new FactorYio();
        selAlphaFactor = new FactorYio();
        text = new ArrayList<String>();
        backColor = new Color(0.5f, 0.5f, 0.5f, 1);
        pressSound = menuControllerLighty.getDefaultSound();
        hasShadow = true;
        mandatoryShadow = false;
        animPos = new SimpleRectangle(0, 0, 0, 0);
        deltaAnimationEnabled = false;
    }

    public void move() {
        factorModel.move();
        selectionFactor.move();
        if (currentlyTouched) selAlphaFactor.move();
        if (deltaAnimationEnabled) {
            deltaSizeArgument += 0.1f;
            deltaSize = 0.98f + 0.04f * (float)Math.cos(deltaSizeArgument);
        }
        if (currentlyTouched && System.currentTimeMillis() - lastTimeTouched > touchDelay && selAlphaFactor.get() == 0) {
            currentlyTouched = false;
        }
        float factor = factorModel.get();
        switch (animType) {
            case ANIM_DEFAULT:
                hor = (float)(0.5 * factor * position.width);
                ver = (float)(0.5 * factor * position.height);
                cx = (float)position.x + 0.5f * (float)position.width;
                cy = (float)position.y + 0.5f * (float)position.height;
                x1 = cx - hor;
                x2 = cx + hor;
                y1 = cy - ver;
                y2 = cy + ver;
                break;
            case ANIM_UP:
                x1 = (float)position.x;
                x2 = x1 + (float)position.width;
                hor = 0.5f * (float)position.width;
                ver = 0.5f * (float)position.height;
                y1 = (float)position.y + (float)((1 - factor) * (menuControllerLighty.yioGdxGame.h - position.y));
                y2 = y1 + (float)position.height;
                break;
            case ANIM_DOWN:
                x1 = (float)position.x;
                x2 = x1 + (float)position.width;
                hor = 0.5f * (float)position.width;
                ver = 0.5f * (float)position.height;
                y1 = (float)(factor * (position.y + position.height)) - (float)position.height;
                y2 = y1 + (float)position.height;
                break;
            case ANIM_COLLAPSE_UP:
                x1 = (float)position.x;
                x2 = x1 + (float)position.width;
                hor = 0.5f * (float)position.width;
                ver = 0.5f * (float)(factor * position.height);
                y1 = (float)position.y + (float)((1 - factor) * (menuControllerLighty.yioGdxGame.h - position.y));
                y2 = y1 + (float)(factor * position.height);
                break;
            case ANIM_SOLID:
                x1 = (float)position.x;
                x2 = x1 + (float)position.width;
                hor = 0.5f * (float)position.width;
                ver = 0.5f * (float)position.height;
                y1 = (float)position.y;
                y2 = y1 + (float)position.height;
                break;
            case ANIM_FROM_CENTER:
                hor = (float)(0.5 * factor * position.width);
                ver = (float)(0.5 * factor * position.height);
                cx = (float)position.x + 0.5f * (float)position.width;
                cy = (float)position.y + 0.5f * (float)position.height;
                cx -= (1 - factor) * (cx - 0.5f * menuControllerLighty.yioGdxGame.w);
                cy -= (1 - factor) * (cy - 0.5f * menuControllerLighty.yioGdxGame.h);
                x1 = cx - hor;
                x2 = cx + hor;
                y1 = cy - ver;
                y2 = cy + ver;
                break;
        }
        if (deltaAnimationEnabled) {
            cx = x1 + hor;
            cy = y1 + ver;
            animPos.set(cx - deltaSize * hor, cy - deltaSize * ver, deltaSize * 2 * hor, deltaSize * 2 * ver);
        } else {
            animPos.set(x1, y1, 2 * hor, 2 * ver);
        }
    }

    boolean checkToPerformAction() {
        if (needToPerformAction && System.currentTimeMillis() > timeToPerformAction && !lockAction) {
            needToPerformAction = false;
            menuControllerLighty.yioGdxGame.setBackAnimation(false);
            reactBehavior.reactAction(this);
            return true;
        }
        return false;
    }

    public void setPressSound(Sound pressSound) {
        this.pressSound = pressSound;
    }

    public void setTouchable(boolean touchable) {
        this.touchable = touchable;
    }

    public void setAnimType(int animType) {
        this.animType = animType;
    }

    public boolean checkTouch(int screenX, int screenY, int pointer, int button) {
        if (!touchable) return false;
        if (screenX > position.x && screenX < position.x + position.width && screenY > position.y && screenY < position.y + position.height) {
            press(screenX, screenY);
            return true;
        }
        return false;
    }

    public void press() {
        press((int)(position.x + 0.5f * position.width), (int)(position.y + 0.5f * position.height));
    }

    void press(int screenX, int screenY) {
        currentlyTouched = true;
        lastTimeTouched = System.currentTimeMillis();
//        pressSound.play();
        selectionFactor.setValues(0.2, 0.02);
        selectionFactor.beginSpawning(0, 1);
        selAlphaFactor.setValues(1, 0);
        selAlphaFactor.beginDestroying(1, 0.5);
        touchX = screenX;
        touchY = screenY;
        animR = Math.max(touchX - (float)animPos.x, (float)(animPos.x + animPos.width - touchX));
        if (touchAnimation) lockAction = true;
        menuControllerLighty.yioGdxGame.render();
        if (reactBehavior != null) {
            needToPerformAction = true;
            timeToPerformAction = System.currentTimeMillis() + 50;
        }
    }

    public void loadTexture(String path) {
        Texture texture = new Texture(Gdx.files.internal(path));
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        textureRegion = new TextureRegion(texture);
        hasShadow = false;
    }

    public void destroy() {
        setTouchable(false);
        factorModel.setDy(0);
        factorModel.beginDestroying(0, 1);
    }

    public void cleatText() {
        text.clear();
    }

    public void setTextLine(String line) {
        cleatText();
        addTextLine(line);
    }

    public void enableDeltaAnimation() {
        deltaAnimationEnabled = true;
    }

    public void disableTouchAnimation() {
        touchAnimation = false;
    }

    public void addTextLine(String textLine) {
        text.add(textLine);
    }

    public void addManyLines(ArrayList<String> lines) {
        text.addAll(lines);
    }

    public void setBackgroundColor(float r, float g, float b) {
        backColor.set(r, g, b, 1);
    }

    public void setReactBehavior(ReactBehavior reactBehavior) {
        this.reactBehavior = reactBehavior;
    }

    public boolean isVisible() {
        if (factorModel.get() > 0 && visible) return true;
        return false;
    }

    public void setTouchDelay(int touchDelay) {
        this.touchDelay = touchDelay;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isTouchable() {
        return touchable;
    }

    public MenuControllerLighty getMenuControllerLighty() {
        return menuControllerLighty;
    }

    public boolean isCurrentlyTouched() {
        return currentlyTouched;
    }

    public boolean notRendered() {
        return textureRegion == null;
    }

    public void setShadow(boolean hasShadow) {
        this.hasShadow = hasShadow;
    }

    public boolean isShadowMandatory() {
        return mandatoryShadow;
    }

    public void setMandatoryShadow(boolean mandatoryShadow) {
        this.mandatoryShadow = mandatoryShadow;
    }

    public void setPosition(SimpleRectangle position) {
        this.position = position;
    }
}
