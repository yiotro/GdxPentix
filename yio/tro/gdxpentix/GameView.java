package yio.tro.gdxpentix;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import yio.tro.gdxpentix.factor_yio.FactorYio;

import java.util.ArrayList;

/**
 * Created by ivan on 05.08.14.
 */
public class GameView {

    YioGdxGame yioGdxGame;
    GameController gameController;
    TextureRegion backgroundRegion;
    public FactorYio factorModel;
    FrameBuffer frameBuffer;
    SpriteBatch batchSolid, batchField;
    ShapeRenderer shapeRenderer;
    float cx, cy, cellSize, cellOffset, selectionOffset;
    TextureRegion blackCircleTexture, gameBackground;
    TextureRegion animationTextureRegion;
    float linkLineThickness, figureFrameCellSize;
    TextureRegion blackPixel, grayPixel, transCircle1, transCircle2;
    int w, h;
    OrthographicCamera orthoCam;
    boolean initialAnimation;
    TextureRegion blocks[];
    SimpleRectangle nextFigureFrame;

    public GameView(YioGdxGame yioGdxGame) { //must be called after creation of GameController and MenuView
        this.yioGdxGame = yioGdxGame;
        gameController = yioGdxGame.gameController;
        factorModel = new FactorYio();
        frameBuffer = new FrameBuffer(Pixmap.Format.RGB565, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        batchSolid = yioGdxGame.batch;
        batchField = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        createOrthoCam();
        cx = yioGdxGame.w / 2;
        cy = yioGdxGame.h / 2;
        linkLineThickness = 0.01f * Gdx.graphics.getWidth();
        w = Gdx.graphics.getWidth();
        h = Gdx.graphics.getHeight();
        initialAnimation = true;
        cellSize = gameController.fieldRectangle.width / gameController.fWidth;
        selectionOffset = 0.05f * cellSize;
        cellOffset = 0;
        nextFigureFrame = new SimpleRectangle(0.775 * w, 0.7 * h, 0.2 * w, 0.2 * h);
        figureFrameCellSize = nextFigureFrame.height / 5;
        loadTextures();
    }

    void createOrthoCam() {
        orthoCam = new OrthographicCamera(yioGdxGame.w, yioGdxGame.h);
        orthoCam.position.set(orthoCam.viewportWidth / 2 - gameController.fieldRectangle.x, orthoCam.viewportHeight / 2 - gameController.fieldRectangle.y, 0);
        updateCam();
    }

    void loadTextures() {
        backgroundRegion = loadTextureRegionByName("game_background.png", true);
        blackCircleTexture = loadTextureRegionByName("black_circle.png", false);
        blackPixel = loadTextureRegionByName("black_pixel.png", false);
        transCircle1 = loadTextureRegionByName("transition_circle_1.png", false);
        transCircle2 = loadTextureRegionByName("transition_circle_2.png", false);
        grayPixel = loadTextureRegionByName("gray_pixel.png", false);
        gameBackground = loadTextureRegionByName("game_background.png", false);
        blocks = new TextureRegion[GameController.COLOR_NUMBER];
        for (int i=0; i<blocks.length; i++)
            blocks[i] = loadTextureRegionByName("block" + (i+1) + ".png", false);
    }

    public static TextureRegion loadTextureRegionByName(String name, boolean antialias) {
        Texture texture = new Texture(Gdx.files.internal(name));
        if (antialias) texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        TextureRegion region = new TextureRegion(texture);
        return region;
    }

    void updateCam() {
        orthoCam.update();
        batchField.setProjectionMatrix(orthoCam.combined);
    }

    public void beginSpawnProcess() {
        factorModel.setValues(0, 0);
        factorModel.beginSpawning(3, 1.2); // 3, 1
        updateAnimationTexture();
    }

    public void beginDestroyProcess() {
        if (yioGdxGame.gamePaused) return;
        if (factorModel.get() >= 1) {
            factorModel.setValues(1, 0);
            factorModel.beginDestroying(3, 5);
        }
        updateAnimationTexture();
        initialAnimation = false;
    }

    void updateAnimationTexture() {
        frameBuffer.begin();
        batchSolid.begin();
        batchSolid.draw(blackPixel, 0, 0, w, h);
        batchSolid.end();
        renderInternals();
        frameBuffer.end();
        Texture texture = frameBuffer.getColorBufferTexture();
        animationTextureRegion = new TextureRegion(texture);
        animationTextureRegion.flip(false, true);
    }

    void renderDebug() {

    }

    void renderShadowBelowFallingBlocks() {
        Color c = batchField.getColor();
        batchField.setColor(c.r, c.g, c.b, 0.05f);
        for (Block fallingBlock : gameController.fallingBlocks) {
            for (int y = fallingBlock.getY() - 1; y >= 0; y--) {
                if (gameController.isFallingBlock(fallingBlock.getX(), y)) break;
                if (!gameController.isCellEmpty(fallingBlock.getX(), y)) break;
                batchField.draw(blackPixel, fallingBlock.getX() * cellSize, y * cellSize, cellSize, cellSize);
            }
        }
        batchField.setColor(c.r, c.g, c.b, c.a);
    }

    void renderFieldBlocks() {
        Block b;

        for (int i=0; i<gameController.fWidth; i++) {
            for (int j=0; j<gameController.fHeight; j++) {
                b = gameController.field[i][j];
                if (b.color > 0) renderBlockShadow(b);
            }
        }

        for (int i=0; i<gameController.fWidth; i++) {
            for (int j=0; j<gameController.fHeight; j++) {
                b = gameController.field[i][j];
                if (b.color > 0) renderBlock(b);
            }
        }
    }

    void renderBlock(Block block) {
        batchField.draw(blocks[block.color - 1], block.getX() * cellSize - cellOffset, block.getViewY() * cellSize - cellOffset - 0.1f * cellSize * block.impulseFactor.get(), cellSize + 2 * cellOffset, cellSize + 2 * cellOffset);
    }

    void renderBlockShadow(Block block) {
        batchField.draw(blackPixel, block.getX() * cellSize - selectionOffset, block.getViewY() * cellSize - selectionOffset - 0.1f * cellSize * block.impulseFactor.get(), cellSize + 2 * selectionOffset, cellSize + 2 * selectionOffset);
    }

    void renderFallingBlocks() {
        Color c = batchField.getColor();
        batchField.setColor(c.r, c.g, c.b, gameController.fallBlocksAlphaFactor.get());
        for (Block block : gameController.fallingBlocks) {
            renderBlockShadow(block);
        }
        for (Block block : gameController.fallingBlocks) {
            renderBlock(block);
        }
        batchField.setColor(c.r, c.g, c.b, c.a);
    }

    void renderLittleFigure(ArrayList<Block> figure, int color, FactorYio transFactor, boolean next) {
        float px = nextFigureFrame.x + 2 * figureFrameCellSize;
        float py = nextFigureFrame.y + 3 * figureFrameCellSize;
        float transOffset = 0;
        if (next) {
            transOffset = - (1 - transFactor.get()) * nextFigureFrame.height;
        } else {
            transOffset = transFactor.get() * nextFigureFrame.height;
        }
        for (Block block : figure) {
            batchSolid.draw(blackPixel, px + block.getX() * figureFrameCellSize - selectionOffset, py + block.getY() * figureFrameCellSize - selectionOffset + transOffset, figureFrameCellSize + 2 * selectionOffset, figureFrameCellSize + 2 * selectionOffset);
        }

        for (Block block : figure) {
            batchSolid.draw(blocks[color - 1], px + block.getX() * figureFrameCellSize, py + block.getY() * figureFrameCellSize + transOffset, figureFrameCellSize, figureFrameCellSize);
        }
    }

    public static void drawFromCenter(Batch batch, TextureRegion textureRegion, double cx, double cy, double r) {
        batch.draw(textureRegion, (float)(cx - r), (float)(cy - r), (float)(2d * r), (float)(2d * r));
    }

    private void renderNewYearSplats() {
        if (!yioGdxGame.newYearTime) return;
        for (Splat snowSplat : gameController.snowSplats) {
            drawFromCenter(batchSolid, snowSplat.textureRegion, snowSplat.x, snowSplat.y, snowSplat.r);
        }
    }

    void renderFadeOutBlocks() {
        for (Block fadeOutBlock : gameController.fadeOutBlocks) {
            Color c = batchField.getColor();
            batchField.setColor(c.r, c.g, c.b, fadeOutBlock.upDeltaFactor.get());
            float vy = fadeOutBlock.getY() + fadeOutBlock.upDeltaFactor.get() - 1;
            if (vy < 0) vy = 0;
            batchField.draw(blocks[fadeOutBlock.color - 1], fadeOutBlock.getX() * cellSize - cellOffset, vy * cellSize - cellOffset - 0.1f * cellSize * fadeOutBlock.impulseFactor.get(), cellSize + 2 * cellOffset, cellSize + 2 * cellOffset);
            batchField.setColor(c.r, c.g, c.b, c.a);
        }
    }

    void renderInternals() {
        batchSolid.begin();
        batchSolid.draw(gameBackground, 0, 0, w, h);
        batchSolid.draw(blackPixel, gameController.fieldRectangle.x - selectionOffset, gameController.fieldRectangle.y - selectionOffset, gameController.fieldRectangle.width + 2 * selectionOffset, gameController.fieldRectangle.height + 2 * selectionOffset);
        renderNewYearSplats();
        batchSolid.draw(grayPixel, gameController.fieldRectangle.x, gameController.fieldRectangle.y, gameController.fieldRectangle.width, gameController.fieldRectangle.height);
        batchSolid.end();

        batchField.begin();
        renderFadeOutBlocks();
        renderShadowBelowFallingBlocks();
        renderFieldBlocks();
        renderFallingBlocks();
        batchField.end();

        batchSolid.begin();
        batchSolid.draw(grayPixel, nextFigureFrame.x - 2 * selectionOffset, nextFigureFrame.y - 2 * selectionOffset, nextFigureFrame.width + 4 * selectionOffset, nextFigureFrame.height + 4 * selectionOffset);
        batchSolid.end();
        YioGdxGame.maskingBegin();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(nextFigureFrame.x - 2 * selectionOffset, nextFigureFrame.y - 2 * selectionOffset, nextFigureFrame.width + 4 * selectionOffset, nextFigureFrame.height + 4 * selectionOffset);
        shapeRenderer.end();
        batchSolid.begin();
        YioGdxGame.maskingContinue();
        renderLittleFigure(gameController.getNextFigure(), gameController.nextFigureColor, gameController.transFactor, true);
        renderLittleFigure(gameController.getCurrentFigure(), gameController.currentFigureColor, gameController.transFactor, false);
        batchSolid.end();
        YioGdxGame.maskingEnd();

        batchSolid.begin();
        renderScore();
        renderDebug();
        batchSolid.end();
    }

    void renderScore() {
        yioGdxGame.scoreFont.draw(batchSolid, gameController.score + " / " + gameController.maxScore, 0.01f * w, 0.99f * h);
    }

    public void render() {
        if (factorModel.get() < 1) {
            renderTransitionFrame();
        } else {
            batchSolid.begin();
            batchSolid.draw(blackPixel, 0, 0, w, h);
            batchSolid.end();
            renderInternals();
        }
    }

    void renderTransitionFrame() {
        batchSolid.begin();
        Color c = batchSolid.getColor();
//        float cx = w / 2;
//        float cy = h / 2;
//        float fw = factorModel.get() * cx;
//        float fh = factorModel.get() * cy;
        if (initialAnimation) batchSolid.setColor(c.r, c.g, c.b, factorModel.get() * factorModel.get());
        else batchSolid.setColor(c.r, c.g, c.b, 1);
        batchSolid.draw(animationTextureRegion, 0, (factorModel.get() - 1) * 0.22f * w, w, h);
        batchSolid.setColor(c.r, c.g, c.b, c.a);
        batchSolid.end();
    }

    void moveInsideStuff() {

    }

    void moveFactors() {
        factorModel.move();
    }

    public static void drawLine(double x1, double y1, double x2, double y2, double thickness, SpriteBatch spriteBatch, TextureRegion blackPixel) {
        spriteBatch.draw(blackPixel, (float)x1, (float)(y1 - thickness * 0.5), 0f, (float)thickness * 0.5f, (float)YioGdxGame.distance(x1, y1, x2, y2), (float)thickness, 1f, 1f, (float)(180 / Math.PI *YioGdxGame.angle(x1, y1, x2, y2)));
    }

    public boolean coversAllScreen() {
        return factorModel.get() > 0.99;
    }

    boolean isInMotion() {
        return factorModel.get() > 0 && factorModel.get() < 1;
    }
}
