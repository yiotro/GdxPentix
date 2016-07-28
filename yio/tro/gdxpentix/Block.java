package yio.tro.gdxpentix;

import yio.tro.gdxpentix.factor_yio.FactorYio;

/**
 * Created by ivan on 29.06.2015.
 */
public class Block {

    int color;
    SimplePoint pos;
    FactorYio impulseFactor, upDeltaFactor;

    public Block(int color, int x, int y) {
        this.color = color;
        pos = new SimplePoint(x, y);
        impulseFactor = new FactorYio();
        upDeltaFactor = new FactorYio();
    }

    public void setPos(int x, int y) {
        pos.x = x;
        pos.y = y;
    }

    public void setPos(SimplePoint p) {
        pos.x = p.x;
        pos.y = p.y;
    }

    void setColorAndImpulse(Block src) {
        color = src.color;
        if (src.impulseFactor.get() > 0) {
            giveImpulse();
        } else {
            impulseFactor.setValues(0, 0);
        }
    }

    void giveImpulse() {
        impulseFactor.setValues(1, 0);
        impulseFactor.beginDestroying(1, 9);
    }

    void move() {
        impulseFactor.move();
        if (upDeltaFactor.get() > 0) upDeltaFactor.move();
    }

    public int getX() {
        return pos.x;
    }

    public int getY() {
        return pos.y;
    }

    public float getViewY() {
        return pos.y + upDeltaFactor.get();
    }
}
