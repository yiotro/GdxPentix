package yio.tro.gdxpentix;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.gdxpentix.factor_yio.FactorYio;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;

/**
 * Created by ivan on 05.08.14.
 */
public class GameController {

    YioGdxGame yioGdxGame;
    public static final int COLOR_NUMBER = 5;
    int w, h, screenX, screenY, touchDownX, touchDownY;
    int maxTouchCount, currentTouchCount, lastTouchCount;
    Random random, predictableRandom;
    LanguagesManager languagesManager;
    boolean tutorial, multiTouchDetected;
    long currentTime, lastTimeTouched;
    String key;
    int fHeight, fWidth, rotateX, rotateY;
    OrthographicCamera orthoCam;
    SimpleRectangle fieldRectangle;
    Block field[][];
    ArrayList<Block> fallingBlocks;
    long timeToMakeNextGravityMove, moveDelay, lastTimeKeyPressed, keyPressDelay, startedPressing, spawningMoveDelay, minMoveDelay;
    boolean keys[], currentlyPressed, gameEnded;
    int currentFigureIndex, nextFigureIndex, currentFigureColor, nextFigureColor, score, maxScore, stickFigureCountUp;
    ArrayList<Block> figures[], fadeOutBlocks;
    FactorYio transFactor, fallBlocksAlphaFactor;
    ArrayList<Splat> snowSplats;

    public GameController(YioGdxGame yioGdxGame) {
        this.yioGdxGame = yioGdxGame;
        w = Gdx.graphics.getWidth();
        h = Gdx.graphics.getHeight();
        random = new Random();
        fallingBlocks = new ArrayList<Block>();
        predictableRandom = new Random(0);
        keys = new boolean[100];
        keyPressDelay = 50;
        transFactor = new FactorYio();
        fallBlocksAlphaFactor = new FactorYio();
        minMoveDelay = 250;
        languagesManager = yioGdxGame.menuControllerLighty.languagesManager;
        fHeight = 19;
        fWidth = 10;
        fieldRectangle = new SimpleRectangle(0.25f * w, 0.025f * h, 0.5f * w, 0.95f * h);
        createSnowSplats();
        loadMaxScore();
        createFiguresArray();
    }

    private void createSnowSplats() {
        if (!yioGdxGame.newYearTime) return;
        TextureRegion snowTexture = GameView.loadTextureRegionByName("snow_ball.png", false);
        snowSplats = new ArrayList<Splat>();
        for (int i = 0; i < 300; i++) {
            Splat splat = new Splat(this, snowTexture, random.nextFloat() * w, random.nextFloat() * h);
            splat.setRadius(0.002f * w);
            splat.setSpeed(0, 0.005f * splat.r);
            splat.wind = 0.03f * random.nextFloat() * splat.dy;
            if (random.nextDouble() > 0.5) splat.wind *= -1;
            snowSplats.add(splat);
        }
    }

    void createFiguresArray() {
        figures = new ArrayList[25];
        createFigure(0, new int[][] {{0, 0}, {-1, 0}, {-1, -1}, {0, -1}});
        createFigure(1, new int[][] {{-1, -1}, {-1, 0}, {0, 0}, {1, 0}});
        createFigure(2, new int[][] {{-1, 0}, {0, 0}, {1, 0}, {1, -1}});
        createFigure(3, new int[][] {{0, 0}, {-1, -1}, {0, -1}, {1, -1}});
        createFigure(4, new int[][] {{-1, 0}, {0, 0}, {0, -1}, {1, -1}});
        createFigure(5, new int[][] {{-1, -1}, {0, -1}, {0, 0}, {1, 0}});
        createFigure(6, new int[][] {{-2, 0}, {-1, 0}, {0, 0}, {1, 0}});
        createFigure(7, new int[][] {{0, 0}, {-1, -1}, {0, -1}, {1, -1}, {0, -2}});
        createFigure(8, new int[][] {{0, 0}, {-1, -1}, {0, -1}, {1, -1}, {1, -2}});
        createFigure(9, new int[][] {{0, 0}, {-1, -1}, {0, -1}, {1, -1}, {-1, -2}});
        createFigure(10, new int[][] {{-1, 0}, {0, 0}, {1, 0}, {-1, -1}, {1, -1}});
        createFigure(11, new int[][] {{-2, 0}, {-1, 0}, {0, 0}, {1, 0}, {1, -1}});
        createFigure(12, new int[][] {{-2, -1}, {-2, 0}, {-1, 0}, {0, 0}, {1, 0}});
        createFigure(13, new int[][] {{-2, 0}, {-1, 0}, {0, 0}, {1, 0}, {2, 0}});
        createFigure(14, new int[][] {{-1, 0}, {0, 0}, {0, -1}, {0, -2}, {1, -2}});
        createFigure(15, new int[][] {{-1, -2}, {0, -2}, {0, -1}, {0, 0}, {1, 0}});
        createFigure(16, new int[][] {{-1, -2}, {-1, -1}, {-1, 0}, {0, 0}, {1, 0}});
        createFigure(17, new int[][] {{-1, 0}, {0, 0}, {1, 0}, {0, -1}, {0, -2}});
        createFigure(18, new int[][] {{-2, 0}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}});
        createFigure(19, new int[][] {{-2, -1}, {-1, -1}, {0, -1}, {0, 0}, {1, 0}});
        createFigure(20, new int[][] {{-2, -1}, {-1, -1}, {0, -1}, {1, -1}, {0, 0}});
        createFigure(21, new int[][] {{-2, -1}, {-1, -1}, {0, -1}, {1, -1}, {-1, 0}});
        createFigure(22, new int[][] {{-1, 0}, {0, 0}, {-1, -1}, {0, -1}, {1, -1}});
        createFigure(23, new int[][] {{-1, -1}, {0, 0}, {1, 0}, {1, -1}, {0, -1}});
        createFigure(24, new int[][] {{-1, -2}, {0, -2}, {0, -1}, {1, -1}, {1, 0}});
    }

    private void printFigure(int k) {
        YioGdxGame.say("" + k);
        boolean b[][] = new boolean[5][5];
        for (Block block : figures[k]) b[2 + block.getX()][4 + block.getY()] = true;
        for (int j=4; j>=0; j--) {
            for (int i=0; i<=4; i++) {
                if (b[i][j]) System.out.print("#");
                else System.out.print(" ");
            }
            YioGdxGame.say("");
        }
        YioGdxGame.say("");
    }

    private void createFigure(int k, int a[][]) {
        figures[k] = new ArrayList<Block>();
        for (int i=0; i < a.length; i++) {
            figures[k].add(new Block(0, a[i][0], a[i][1]));
        }
    }

    public void move() {
        currentTime = System.currentTimeMillis();
        transFactor.move();
        fallBlocksAlphaFactor.move();
        for (int i=0; i<fWidth; i++) {
            for (int j=0; j<fHeight; j++) {
                if (field[i][j].color > 0 || field[i][j].upDeltaFactor.get() > 0) {
                    field[i][j].move();
                }
            }
        }
        for (Block fadeOutBlock : fadeOutBlocks) {
            fadeOutBlock.move();
        }
        if (gameEnded) return; // following stuff is executing only when game has not ended
        if (currentlyPressed && currentTime > startedPressing + 350 && currentTime > lastTimeKeyPressed + keyPressDelay) {
            checkKeys();
        }
        if (currentTime > timeToMakeNextGravityMove) {
            timeToMakeNextGravityMove = currentTime + moveDelay;
            if (canFallingBlocksMoveDown()) makeGravityMove();
            else landDownFallingBlocks();
        }
        if (yioGdxGame.newYearTime) {
            for (Splat snowSplat : snowSplats) {
                snowSplat.move();
            }
        }
    }

    void spawnNextFigure() {
        transFactor.setValues(0, 0);
        transFactor.beginSpawning(3, 1);
        currentFigureIndex = nextFigureIndex;
        nextFigureIndex = getRandomFigureIndex();
        currentFigureColor = nextFigureColor;
        nextFigureColor = getRandomBlockColor();
        spawnFallingFigure(currentFigureIndex, currentFigureColor);
    }

    ArrayList<Block> getNextFigure() {
        return figures[nextFigureIndex];
    }

    ArrayList<Block> getCurrentFigure() {
        return figures[currentFigureIndex];
    }

    void landDownFallingBlocks() {
        placeFallingBlocksToField();
        fallingBlocks.clear();
        checkBlockLines();
        spawnNextFigure();
    }

    void placeFallingBlocksToField() {
        boolean giveImpulse = false;
        if (fallingBlocksLowestHeight() > 0 && moveDelay < 50) giveImpulse = true;
        for (Block block : fallingBlocks) {
            field[block.getX()][block.getY()].color = block.color;
            if (giveImpulse) field[block.getX()][block.getY()].giveImpulse();
            if (giveImpulse && block.getY() > 1) field[block.getX()][block.getY() - 1].giveImpulse();
        }
    }

    int fallingBlocksLowestHeight() {
        int min = fHeight;
        for (Block block : fallingBlocks) {
            if (block.getY() < min) min = block.getY();
        }
        return min;
    }

    void makeGravityMove() {
        for (Block block : fallingBlocks) {
            block.pos.y--;
        }
    }

    boolean canFallingBlocksMoveDown() {
        for (Block block : fallingBlocks) {
            if (!canBlockMoveDown(block)) return false;
        }
        return true;
    }

    boolean isCellEmpty(SimplePoint pos) {
        return isCellEmpty(pos.x, pos.y);
    }

    boolean isCellEmpty(int x, int y) {
        if (x < 0 || x > fWidth-1 || y < 0 || y > fHeight - 1) return false;
        return field[x][y].color == 0;
    }

    boolean canBlockMoveDown(Block block) {
        if (block.getY() == 0) return false;
        return isCellEmpty(block.getX(), block.getY() - 1);
    }

    boolean canBlockMoveRight(Block block) {
        if (block.getX() == fWidth - 1) return false;
        return isCellEmpty(block.getX() + 1, block.getY());
    }

    boolean canBlockMoveLeft(Block block) {
        if (block.getX() == 0) return false;
        return isCellEmpty(block.getX() - 1, block.getY());
    }

    void loadMaxScore() {
        try {
            Preferences preferences = Gdx.app.getPreferences("main");
            maxScore = preferences.getInteger("max_score", 0); // 0 - default value
        } catch (Exception e) {
            maxScore = -1;
        }
    }

    void saveMaxScore() {
        Preferences preferences = Gdx.app.getPreferences("main");
        preferences.putInteger("max_score", maxScore);
        preferences.flush();
    }

    void increaseScore(int howMuch) {
        score += howMuch;
        for (int i=0; i<howMuch; i++) speedUp();
    }

    void checkBlockLines() {
        int linesDestroyed = 0;
        for (int k=fHeight-1; k>=0; k--) {
            if (isLineFull(k)) {
                linesDestroyed++;
                for (int i = 0; i < fWidth; i++) {
                    addFadeOutBlock(i, k);
                }
                for (int j=k; j<fHeight; j++) {
                    copyLineFromUpper(j);
                }
            }
        }
        if (linesDestroyed > 0) {
            increaseScore(getBonusForLinesDestroyed(linesDestroyed));
        }
    }

    void addFadeOutBlock(int x, int y) {
        Block fadeOutBlock = new Block(field[x][y].color, x, y);
        fadeOutBlock.upDeltaFactor.setValues(1, 0);
        fadeOutBlock.upDeltaFactor.beginDestroying(3, 5);
        fadeOutBlocks.add(fadeOutBlock);
    }

    int getBonusForLinesDestroyed(int linesDestroyed) {
        switch (linesDestroyed) {
            case 1: return 1;
            case 2: return 3;
            case 3: return 6;
            case 4: return 10;
            case 5: return 15;
            default: return 0;
        }
    }

    void copyLineFromUpper(int y) {
        if (y == fHeight - 1) {
            for (int x=0; x<fWidth; x++) {
                field[x][y].color = 0;
            }
            return;
        }

        for (int x=0; x<fWidth; x++) {
            field[x][y].setColorAndImpulse(field[x][y+1]);
            if (!isCellEmpty(x, y)) {
                field[x][y].upDeltaFactor.setValues(1, 0);
                field[x][y].upDeltaFactor.beginDestroying(3, 5);
            }
        }
    }

    boolean isLineFull(int y) {
        for (int x=0; x<fWidth; x++)
            if (isCellEmpty(x, y)) return false;
        return true;
    }

    public void checkForMaxScore() {
        if (score > maxScore) {
            maxScore = score;
            saveMaxScore();
        }
    }

    void speedUp() {
        int speedUpTime = (int)(0.1 * (spawningMoveDelay - minMoveDelay));
        if (speedUpTime > 3) speedUpTime = 3;
        spawningMoveDelay -= speedUpTime;
    }

    void prepareForNewGame(int index) {
        checkForMaxScore();
        score = 0;
        spawningMoveDelay = 450;
        stickFigureCountUp = 0;
        gameEnded = false;
        yioGdxGame.gameView.createOrthoCam();
        orthoCam = yioGdxGame.gameView.orthoCam;
        predictableRandom = new Random(index);
        fallingBlocks.clear();
        field = new Block[fWidth][fHeight];
        for (int i=0; i<fWidth; i++) {
            for (int j=0; j<fHeight; j++) {
                field[i][j] = new Block(0, i, j);
            }
        }
        fadeOutBlocks = new ArrayList<Block>();
        currentFigureIndex = getStartingFigureIndex();
        nextFigureIndex = getRandomFigureIndex();
        currentFigureColor = getRandomBlockColor();
        nextFigureColor = getRandomBlockColor();
        spawnFallingFigure(currentFigureIndex, currentFigureColor);
        fallBlocksAlphaFactor.setValues(1, 0);
        transFactor.setValues(0, 0);
        transFactor.beginSpawning(3, 1);
        maxTouchCount = 0;
        currentTouchCount = 0;
    }

    int getStartingFigureIndex() {
        int list[] = new int[] {1, 2, 3, 10, 6, 11, 12, 16, 17, 20, 21, 22, 23};
        return list[random.nextInt(list.length)];
    }

    boolean isFallingBlock(int x, int y) {
        for (Block block : fallingBlocks) {
            if (block.getY() == y && block.getX() == x) return true;
        }
        return false;
    }

    boolean areFallingBlocksCollidingWithFieldBlocks() {
        for (Block block : fallingBlocks) {
            if (field[block.getX()][block.getY()].color > 0) return true;
        }
        return false;
    }

    void checkToEndGame() {
        if (areFallingBlocksCollidingWithFieldBlocks()) {
            gameEnded = true;
            checkForMaxScore();
            yioGdxGame.menuControllerLighty.getButtonById(30).press();
        }
    }

    void spawnFallingFigure(int figureIndex, int color) {
        if (figureIndex == 6) stickFigureCountUp = 0;
        else stickFigureCountUp++;
        moveDelay = spawningMoveDelay;
        int x = fWidth / 2;
        int y = fHeight - 1;
        fallBlocksAlphaFactor.setValues(0, 0);
        fallBlocksAlphaFactor.beginSpawning(1, 5);
        ArrayList<Block> fBlocks = figures[figureIndex];
        for (Block block : fBlocks) {
            addFallingBlock(color, x + block.getX(), y + block.getY());
        }
        checkToEndGame();
    }

    boolean canFallingBlocksMoveLeft() {
        for (Block block : fallingBlocks) {
            if (!canBlockMoveLeft(block)) return false;
        }
        return true;
    }

    boolean canFallingBlocksMoveRight() {
        for (Block block : fallingBlocks) {
            if (!canBlockMoveRight(block)) return false;
        }
        return true;
    }

    void moveFallingBlocksLeft() {
        if (!canFallingBlocksMoveLeft()) return;
        for (Block block : fallingBlocks) {
            block.pos.x--;
        }
    }

    void moveFallingBlocksRight() {
        if (!canFallingBlocksMoveRight()) return;
        for (Block block : fallingBlocks) {
            block.pos.x++;
        }
    }

    int getRandomBlockColor() {
        return random.nextInt(COLOR_NUMBER) + 1;
    }

    int getRandomFigureIndex() {
        if (stickFigureCountUp > 10) {
            stickFigureCountUp = 0;
            return 6;
        }
        int rnd = random.nextInt(figures.length + 1);
        if (rnd == figures.length) rnd = 6; // stick drops more frequently
        return rnd;
    }

    void addFallingBlock(int color, int x, int y) {
        addFallingBlock(new Block(color, x, y));
    }

    void addFallingBlock(Block block) {
        ListIterator iterator = fallingBlocks.listIterator();
        iterator.add(block);
    }

    void detectRotateCell() {
        float cx = 0, cy = 0;
        for (Block block : fallingBlocks) {
            cx += block.getX() + 0.5f;
            cy += block.getY() + 0.5f;
        }
        cx /= fallingBlocks.size();
        cy /= fallingBlocks.size();
        rotateX = (int) cx;
        rotateY = (int) cy;
    }

    boolean canRotateFallingBlocks() {
        if (currentFigureIndex == 0) return false;
        for (Block block : fallingBlocks) {
            if (!isCellEmpty(getRotatedBlockPos(block))) return false;
        }
        return true;
    }

    int leftMostPoint(ArrayList<SimplePoint> points) {
        int res = points.get(0).x;
        for (SimplePoint point : points) {
            if (point.x < res) {
                res = point.x;
            }
        }
        return res;
    }

    int rightMostPoint(ArrayList<SimplePoint> points) {
        int res = points.get(0).x;
        for (SimplePoint point : points) {
            if (point.x > res) {
                res = point.x;
            }
        }
        return res;
    }

    boolean isListCollidingWithFieldBlocks(ArrayList<SimplePoint> list) {
        for (SimplePoint point : list) {
            if (field[point.x][point.y].color > 0) return true;
        }
        return false;
    }

    void dragFallingBlocksHorizontally(int offset) {
        for (Block block : fallingBlocks) {
            block.setPos(block.getX() + offset, block.getY());
        }
    }

    void tryToRotateWithMoving() {
        ArrayList<SimplePoint> list = new ArrayList<SimplePoint>();
        for (Block block : fallingBlocks) {
            list.add(getRotatedBlockPos(block));
        }
        if (leftMostPoint(list) < 0) {
            int offsetToTheRight = 0 - leftMostPoint(list);
            for (SimplePoint point : list) {
                point.x += offsetToTheRight;
            }
            if (isListCollidingWithFieldBlocks(list)) return;
            rotateFallingBlocks();
            dragFallingBlocksHorizontally(offsetToTheRight);
        }
        if (rightMostPoint(list) > fWidth - 1) {
            int offsetToTheLeft = rightMostPoint(list) - (fWidth - 1);
            for (SimplePoint point : list) {
                point.x -= offsetToTheLeft;
            }
            if (isListCollidingWithFieldBlocks(list)) return;
            rotateFallingBlocks();
            dragFallingBlocksHorizontally(-offsetToTheLeft);
        }
    }

    void rotateFallingBlocks() {
        for (Block block : fallingBlocks) {
            block.setPos(getRotatedBlockPos(block));
        }
    }

    SimplePoint getRotatedBlockPos(Block block) {
        int bas1, bas2;
        bas1 = block.getX() - rotateX;
        bas2 = block.getY() - rotateY;
        return new SimplePoint(rotateX - bas2, rotateY + bas1);
    }

    void timeCorrection(long correction) {

    }

    void immediateReactionToKeys(int keyCode) {
        if (keyCode == Input.Keys.UP) {
            detectRotateCell();
            if (canRotateFallingBlocks()) rotateFallingBlocks();
            else tryToRotateWithMoving();
        }
        if (keyCode == Input.Keys.SPACE) {
            moveDelay = 10;
            timeToMakeNextGravityMove = currentTime;
        }
        if (keyCode == Input.Keys.LEFT) {
            moveFallingBlocksLeft();
            lastTimeKeyPressed = currentTime;
        }
        if (keyCode == Input.Keys.RIGHT) {
            moveFallingBlocksRight();
            lastTimeKeyPressed = currentTime;
        }
        if (keyCode == Input.Keys.DOWN) {
            if (canFallingBlocksMoveDown()) makeGravityMove();
            lastTimeKeyPressed = currentTime;
        }
    }

    void checkKeys() {
        if (keys[Input.Keys.LEFT]) {
            moveFallingBlocksLeft();
            lastTimeKeyPressed = currentTime;
        }
        if (keys[Input.Keys.RIGHT]) {
            moveFallingBlocksRight();
            lastTimeKeyPressed = currentTime;
        }
        if (keys[Input.Keys.DOWN]) {
            if (canFallingBlocksMoveDown()) makeGravityMove();
            lastTimeKeyPressed = currentTime;
        }
    }

    void keyDown(int keyCode) {
        startedPressing = currentTime;
        currentlyPressed = true;
        if (keyCode < keys.length) keys[keyCode] = true;
        immediateReactionToKeys(keyCode);
    }

    void keyUp(int keyCode) {
        currentlyPressed = false;
        if (keyCode < keys.length) keys[keyCode] = false;
    }

    void touchDown(int screenX, int screenY, int pointer, int button) {
        currentTouchCount++;
        this.screenX = screenX;
        this.screenY = screenY;
        touchDownX = screenX;
        touchDownY = screenY;
        if (currentTouchCount == 1) {
            maxTouchCount = 1;
            multiTouchDetected = false;
            // touched down
        } else {
            multiTouchDetected = true;
        }

        if (currentTouchCount > maxTouchCount) maxTouchCount = currentTouchCount;
        lastTouchCount = currentTouchCount;
    }

    void touchUp(int screenX, int screenY, int pointer, int button) {
        lastTimeTouched = System.currentTimeMillis();
        currentTouchCount--;
        if (currentTouchCount == maxTouchCount - 1) {

        }
        if (currentTouchCount == 0) {

            multiTouchDetected = false;
        }
        lastTouchCount = currentTouchCount;
        // some stuff here
    }

    void touchDragged(int screenX, int screenY, int pointer) {

    }

    void scrolled(int amount) {

    }

}
