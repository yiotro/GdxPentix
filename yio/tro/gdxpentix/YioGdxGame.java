package yio.tro.gdxpentix;

import com.badlogic.gdx.*;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.TimeUtils;
import yio.tro.gdxpentix.factor_yio.FactorYio;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.*;

public class YioGdxGame extends ApplicationAdapter implements InputProcessor{
	SpriteBatch batch;
    ShapeRenderer shapeRenderer;
    int w, h;
    MenuControllerLighty menuControllerLighty;
    MenuViewLighty menuViewLighty;
    public static BitmapFont font, scoreFont, listFont;
    public static final String FONT_CHARACTERS = "йцукенгшщзхъёфывапролджэячсмитьбюЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮabcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][_!$%#@|\\/?-+=()*&.;:,{}\"´`'<>";
    public static int FONT_SIZE;
    public static final int INDEX_OF_LAST_LEVEL = 50; // with tutorial
    TextureRegion mainBackground, infoBackground, settingsBackground, pauseBackground;
    TextureRegion currentBackground, lastBackground, splatTexture;
    public static float screenRatio;
    public GameSettings gameSettings;
    public GameController gameController;
    public GameView gameView;
    boolean gamePaused, readyToUnPause;
    long timeToUnPause;
    int frameSkipCount;
    FrameBuffer frameBuffer, screenshotBuffer;
    FactorYio transitionFactor, splatTransparencyFactor;
    ArrayList<Splat> splats;
    long timeToSpawnNextSplat;
    float splatSize;
    int currentSplatIndex;
    public static final Random random = new Random();
    long lastTimeButtonPressed;
    boolean alreadyShownErrorMessageOnce, showFpsInfo;
    int fps, currentFrameCount;
    long timeToUpdateFpsInfo;
    int currentBackgroundIndex;
    long timeWhenPauseStarted, timeForFireworkExplosion, timeToHideSplats;
    boolean backAnimation;
    int selectedLevelIndex, splashCount;
    float pressX;
    float pressY;
    float animX;
    float animY;
    float animRadius;
    boolean ignoreNextTimeCorrection;
    boolean loadedResources;
    boolean ignoreDrag;
    boolean needToHideSplats;
    boolean simpleTransitionAnimation;
    TextureRegion splash;
    ArrayList<Float> debugValues;
    boolean debugFactorModel;
    boolean newYearTime;

	@Override
	public void create () {
        loadedResources = false;
        splashCount = 0;
		batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        splash = GameView.loadTextureRegionByName("splash.png", true);
        w = Gdx.graphics.getWidth();
        h = Gdx.graphics.getHeight();
        pressX = 0.5f * w;
        pressY = 0.5f * h;
        screenRatio = (float)w / (float)h;
        frameBuffer = new FrameBuffer(Pixmap.Format.RGB565, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        screenshotBuffer = new FrameBuffer(Pixmap.Format.RGB565, w, h, true);
        debugFactorModel = false;
        debugValues = new ArrayList<Float>();
        if (debugFactorModel) {
            FactorYio factorYio = new FactorYio();
            factorYio.setValues(0, 0);
            factorYio.beginSpawning(3, 0.5);
            int c = 100;
            while (factorYio.needsToMove() && c > 0) {
                debugValues.add(new Float(factorYio.get()));
                factorYio.move();
                c--;
            }
        }
	}

    void loadResourcesAndInitEverything() {
        try {
            loadedResources = true;
            gameSettings = new GameSettings(this);
            FileHandle fontFile = Gdx.files.internal("font.otf");
            mainBackground = GameView.loadTextureRegionByName("main_menu_background.png", true);
            infoBackground = GameView.loadTextureRegionByName("info_background.png", true);
            settingsBackground = GameView.loadTextureRegionByName("settings_background.png", true);
            pauseBackground = GameView.loadTextureRegionByName("pause_background.png", true);
            splatTexture = GameView.loadTextureRegionByName("splat.png", true);
            checkForNewYear();
            transitionFactor = new FactorYio();
            splatTransparencyFactor = new FactorYio();
            splats = new ArrayList<Splat>();
            splatSize = 0.15f * Gdx.graphics.getWidth();
            ListIterator iterator = splats.listIterator();
            for (int i=0; i<100; i++) {
                float sx, sy, sr;
                sx = random.nextFloat() * w;
                sr = 0.03f * random.nextFloat() * h + 0.02f * h;
                sy = random.nextFloat() * h;
                float dx, dy;
                dx = 0.02f * splatSize * random.nextFloat() - 0.01f * splatSize;
                dy = 0.01f * splatSize;
                Splat splat = new Splat(gameController, null, sx, sy);
                if (random.nextDouble() < 0.6 || distance(w/2, h/2, sx, sy) > 0.6f * w) splat.y = 2 * h; // hide splat
                splat.setSpeed(dx, dy);
                splat.setRadius(sr);
                iterator.add(splat);
//            iterator.add(new Splat(null, 0, 2 * h));
            }
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            FONT_SIZE = (int)(0.041 * Gdx.graphics.getHeight());
            parameter.size = FONT_SIZE;
            parameter.characters = FONT_CHARACTERS;
            parameter.flip = true;
            font = generator.generateFont(parameter);
            parameter.size = FONT_SIZE;
            parameter.flip = false;
            scoreFont = generator.generateFont(parameter);
            scoreFont.setColor(1, 1, 1, 1);
            generator.dispose();
            gamePaused = true;
            alreadyShownErrorMessageOnce = false;
            showFpsInfo = false;
            fps = 0;
            selectedLevelIndex = 0;
            timeToUpdateFpsInfo = System.currentTimeMillis() + 1000;
//        decorations = new ArrayList<BackgroundMenuDecoration>();
//        initDecorations();

            Preferences preferences = Gdx.app.getPreferences("main");
            selectedLevelIndex = preferences.getInteger("progress", 0); // 0 - default value
            menuControllerLighty = new MenuControllerLighty(this);
            menuViewLighty = new MenuViewLighty(this);
            gameController = new GameController(this); // must be called after menu controller is created. because of languages manager and other stuff
            gameView = new GameView(this);
            gameView.factorModel.beginDestroying(1, 1);
            currentBackgroundIndex = -1;
            currentBackground = gameView.blackPixel; // call this after game view is created
            beginBackgroundChange(3, true, true);
            revealSplats();
            Gdx.input.setInputProcessor(this);
            Gdx.gl.glClearColor(0, 0, 0, 1);

            startGame();
        } catch (Exception e) {
            writeToFile(e);
            e.printStackTrace();
        }
    }

    private void checkForNewYear() {
        newYearTime = false;
        if (true) return;
        Date date = new Date(TimeUtils.millis());
        int novStartIndex = date.toString().indexOf("Dec");
        if (novStartIndex < 0) return;
        StringTokenizer tokenizer = new StringTokenizer(date.toString());
        int countDown = 2;
        String token = "";
        while (tokenizer.hasMoreTokens() && countDown >= 0) {
            token = tokenizer.nextToken();
            countDown--;
        }
        int day = Integer.valueOf(token);
        if (day > 20) newYearTime = true;
    }

    public void setBackAnimation(boolean backAnimation) {
        this.backAnimation = backAnimation;
    }

    public void setGamePaused(boolean gamePaused) {
        if (gamePaused) {
            this.gamePaused = true;
            timeWhenPauseStarted = System.currentTimeMillis();
        } else {
            unPauseAfterSomeTime();
//            beginBackgroundChange(4, true, true);
        }
    }

    public void beginBackgroundChange(int index, boolean updateAnimPos, boolean simpleTransition) {
        if (currentBackgroundIndex == index) return;
        this.simpleTransitionAnimation = simpleTransition;
        currentBackgroundIndex = index;
        lastBackground = currentBackground;
        if (updateAnimPos) {
            animX = pressX;
            animY = pressY;
            if (backAnimation && animY < 0.9 * h) {
                animY = 0.9f * h;
            }
            float r1, r2, r3, r4;
            r1 = (float)distance(animX, animY, 0, 0);
            r2 = (float)distance(animX, animY, w, 0);
            r3 = (float)distance(animX, animY, 0, h);
            r4 = (float)distance(animX, animY, w, h);
            animRadius = r1;
            if (r2 > animRadius) animRadius = r2;
            if (r3 > animRadius) animRadius = r3;
            if (r4 > animRadius) animRadius = r4;
        }
        switch (index) {
            case 0: currentBackground = mainBackground; break;
            case 1: currentBackground = infoBackground; break;
            case 2: currentBackground = settingsBackground; break;
            case 3: currentBackground = pauseBackground; break;
            case 4: currentBackground = gameView.blackPixel; break;
        }
        transitionFactor.setValues(0.02, 0.01);
        transitionFactor.beginSpawning(0, 0.8);
    }

    void timeCorrection(long correction) {
        if (ignoreNextTimeCorrection) {
            ignoreNextTimeCorrection = false;
            return;
        }
        gameController.timeCorrection(correction);
    }

    void letsIgnoreNextTimeCorrection() {
        ignoreNextTimeCorrection = true;
    }

    public void move() {
        if (!loadedResources) return;
        transitionFactor.move();
        splatTransparencyFactor.move();
        if (readyToUnPause && System.currentTimeMillis() > timeToUnPause && gameView.factorModel.get() == 1) {
            gamePaused = false;
            readyToUnPause = false;
            gameController.currentTouchCount = 0;
            timeCorrection(System.currentTimeMillis() - timeWhenPauseStarted);
        }
        if (needToHideSplats && System.currentTimeMillis() > timeToHideSplats) {
            needToHideSplats = false;
        }
        gameView.moveFactors();
        menuControllerLighty.move();
        if (!gamePaused) {
            gameView.moveInsideStuff();
            gameController.move();
            if (gameView.factorModel.get() < 0.95) System.out.println("what the fucking fuck?");
        }
        if (!gameView.coversAllScreen()) {
            if (System.currentTimeMillis() > timeToSpawnNextSplat) {
                timeToSpawnNextSplat = System.currentTimeMillis() + 300 + random.nextInt(100);
                float sx, sy, sr;
                sx = random.nextFloat() * w;
                sr = 0.03f * random.nextFloat() * h + 0.02f * h;
                sy = -sr;
                int c = 0, size = splats.size();
                Splat splat = null;
                while (c < size) {
                    c++;
                    splat = splats.get(currentSplatIndex);
                    currentSplatIndex++;
                    if (currentSplatIndex >= size) currentSplatIndex = 0;
                    if (!splat.isVisible()) {
                        float dx, dy;
                        dx = 0.02f * splatSize * random.nextFloat() - 0.01f * splatSize;
                        dy = 0.01f * splatSize;
                        splat.set(sx, sy);
                        splat.setSpeed(dx, dy);
                        splat.setRadius(sr);
                        break;
                    }
                }
            }
            for (Splat splat : splats) {
                splat.move();
            }
        }
    }

    void renderDebugValues() {

    }

    void renderInternals() {
        currentFrameCount++;
        if (showFpsInfo && System.currentTimeMillis() > timeToUpdateFpsInfo) {
            timeToUpdateFpsInfo = System.currentTimeMillis() + 1000;
            fps = currentFrameCount;
            currentFrameCount = 0;
        }
        if (debugFactorModel) {
            renderDebugValues();
            return;
        }
        if (!gameView.coversAllScreen()) {
            Color c = batch.getColor();
            batch.setColor(c.r, c.g, c.b, 1);
            if (transitionFactor.get() < 1) {
                if (backAnimation) {
                    float f = (1 - transitionFactor.get());
                    batch.begin();
                    batch.draw(currentBackground, 0, 0, w, h);
                    batch.end();
                } else {
                    batch.begin();
                    batch.draw(lastBackground, 0, 0, w, h);
                    batch.end();
                }
            } else {
                batch.begin();
                batch.draw(currentBackground, 0, 0, w, h);
                batch.end();
            }

            batch.begin();
            renderSplats(c);
            batch.end();

            if (gamePaused) {
                if (!backAnimation) menuViewLighty.render(false, true);
                else menuViewLighty.render(true, false);
            }

            if (transitionFactor.get() < 1) {
                if (!simpleTransitionAnimation) {
                    if (backAnimation) {
                        float f = (1 - transitionFactor.get());
                        maskingBegin();
                        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                        shapeRenderer.circle(animX, animY, f * animRadius, 32);
                        shapeRenderer.end();
                        maskingContinue();
                        batch.begin();
                        batch.draw(lastBackground, 0, 0, w, h);
                        batch.end();
                        maskingEnd();
                    } else {
                        float f = (0 + transitionFactor.get());
                        maskingBegin();
                        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                        shapeRenderer.circle(animX, animY, f * animRadius, 32);
                        shapeRenderer.end();
                        maskingContinue();
                        batch.begin();
                        batch.draw(currentBackground, 0, 0, w, h);
                        batch.end();
                        maskingEnd();
                    }
                } else {
                    if (backAnimation) {
                        float f = (1 - transitionFactor.get());
                        batch.setColor(c.r, c.g, c.b, f);
                        batch.begin();
                        batch.draw(lastBackground, 0, 0, w, h);
                        batch.end();
                    } else {
                        float f = (0 + transitionFactor.get());
                        batch.setColor(c.r, c.g, c.b, f);
                        batch.begin();
                        batch.draw(currentBackground, 0, 0, w, h);
                        batch.end();
                    }
                }
            }
        }
        gameView.render();
        if (gamePaused) {
            if (backAnimation) menuViewLighty.render(false, true);
            else menuViewLighty.render(true, false);
        } else {
            menuViewLighty.render(true, true);
        }
        if (showFpsInfo) {
            batch.begin();
            scoreFont.draw(batch, "" + fps, 0.2f * w, Gdx.graphics.getHeight() - 10);
            batch.end();
        }
    }

    private void renderSplats(Color c) {
        if (splatTransparencyFactor.get() > 0.01) {
            batch.setColor(c.r, c.g, c.b, splatTransparencyFactor.get());
            for (Splat splat : splats) {
                batch.draw(splatTexture, splat.x - splat.r, splat.y - splat.r, 2 * splat.r, 2 * splat.r);
            }
        }
    }

    public static final void maskingBegin() {
        Gdx.gl.glClearDepthf(1f);
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glDepthFunc(GL20.GL_LESS);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthMask(true);
        Gdx.gl.glColorMask(false, false, false, false);
    }

    public static final void maskingContinue() {
        Gdx.gl.glColorMask(true, true, true, true);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthFunc(GL20.GL_EQUAL);
    }

    public static final void maskingEnd() {
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
    }

    @Override
	public void render () {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!loadedResources) {
            batch.begin();
            batch.draw(splash, 0, 0, w, h);
            batch.end();
            if (splashCount == 2) loadResourcesAndInitEverything();
            splashCount++;
            return;
        }

        try {
            move();
        } catch (Exception exception) {
            if (!alreadyShownErrorMessageOnce) {
                exception.printStackTrace();
                alreadyShownErrorMessageOnce = true;
                menuControllerLighty.createExceptionReport(exception);
            }
        }

        if (gamePaused) {
            renderInternals();
        } else {
            if (Gdx.graphics.getDeltaTime() < 0.025 || frameSkipCount >= 2) {
                frameSkipCount = 0;
                frameBuffer.begin();
                renderInternals();
                frameBuffer.end();
            } else {
                frameSkipCount++;
            }
            batch.begin();
            batch.draw(frameBuffer.getColorBufferTexture(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false, true);
            batch.end();
        }
	}

    TextureRegion takeScreenshot() {
        screenshotBuffer.begin();
        renderInternals();
        Texture texture = screenshotBuffer.getColorBufferTexture();
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        TextureRegion screenshot = new TextureRegion(texture);
        screenshotBuffer.end();
        screenshot.flip(false, true);
        return screenshot;
    }

    void unPauseAfterSomeTime() {
        readyToUnPause = true;
        timeToUnPause = System.currentTimeMillis() + 150; // время анимации - около 420мс
    }

    public void setAnimToPlayButtonSpecial() {
        ButtonLighty buttonLighty = menuControllerLighty.getButtonById(3);
        animX = buttonLighty.cx;
        animY = buttonLighty.cy;
        transitionFactor.setValues(0.15, 0);
    }

    public void setAnimToResumeButtonSpecial() {
        animX = w;
        animY = h;
        animRadius = (float)distance(0, 0, w, h);
    }

    public void setAnimToStartButtonSpecial() {
        animX = 0.5f * w;
        animY = 0.65f * h;
        animRadius = animY;
    }

    public void forceBackgroundChange() {
        transitionFactor.setValues(1, 0);
        simpleTransitionAnimation = true;
    }

    public static void say(String text) {
        System.out.println(text);
    }

    public void startGame() {
        gameController.prepareForNewGame(selectedLevelIndex);
        gameView.beginSpawnProcess();
        menuControllerLighty.createGameOverlay();
        setGamePaused(false);
        letsIgnoreNextTimeCorrection();
        gameController.tutorial = false;
    }

    static double angle(double x1, double y1, double x2, double y2) {
        if (x1 == x2) {
            if (y2 > y1) return 0.5 * Math.PI;
            if (y2 < y1) return 1.5 * Math.PI;
            return 0;
        }
        if (x2 >= x1) return Math.atan((y2 - y1) / (x2 - x1));
        else return Math.PI + Math.atan((y2 - y1) / (x2 - x1));
    }

    static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
    }

    public static ArrayList<String> decodeStringToArrayList(String string, String delimiters) {
        ArrayList<String> res = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(string, delimiters);
        while (tokenizer.hasMoreTokens()) {
            res.add(tokenizer.nextToken());
        }
        return res;
    }

    public void writeToFile(Exception exception) {
        BufferedWriter writer = null;
        try {
            File logFile = new File("error.txt");
            writer = new BufferedWriter(new FileWriter(logFile));
            writer.write(exception.getLocalizedMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the writer regardless of what happens...
                writer.close();
            } catch (Exception e) {
            }
        }
    }

    public void setSelectedLevelIndex(int selectedLevelIndex) {
        if (selectedLevelIndex >= 0 && selectedLevelIndex <= INDEX_OF_LAST_LEVEL)
            this.selectedLevelIndex = selectedLevelIndex;
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE){
            if (!gamePaused) {
                if (gameView.factorModel.get() == 1) menuControllerLighty.getButtonById(30).press();
            } else {
                if (gameView.factorModel.get() == 0) menuControllerLighty.getButtonById(45).press();
            }
        }
        if ((keycode == Input.Keys.R || keycode == Input.Keys.SPACE) && gamePaused && gameView.factorModel.get() == 0) menuControllerLighty.getButtonById(44).press();
        if (keycode == Input.Keys.Q) {
            Gdx.app.exit();
        }
        gameController.keyDown(keycode);
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        gameController.keyUp(keycode);
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        ignoreDrag = true;
        pressX = screenX;
        pressY = h - screenY;
        try {
            if (!gameView.isInMotion() && transitionFactor.get() > 0.99 && menuControllerLighty.touchDown(screenX, Gdx.graphics.getHeight() - screenY, pointer, button)) {
                lastTimeButtonPressed = System.currentTimeMillis();
                return false;
            } else {
                ignoreDrag = false;
            }
            if (!gamePaused) gameController.touchDown(screenX, Gdx.graphics.getHeight() - screenY, pointer, button);
        } catch (Exception exception) {
            if (!alreadyShownErrorMessageOnce) {
                exception.printStackTrace();
                alreadyShownErrorMessageOnce = true;
                menuControllerLighty.createExceptionReport(exception);
            }
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        try {
            menuControllerLighty.touchUp(screenX, Gdx.graphics.getHeight() - screenY, pointer, button);
            if (!gamePaused && gameView.coversAllScreen() && System.currentTimeMillis() > lastTimeButtonPressed + 300) gameController.touchUp(screenX, Gdx.graphics.getHeight() - screenY, pointer, button);
        } catch (Exception exception) {
            if (!alreadyShownErrorMessageOnce) {
                exception.printStackTrace();
                alreadyShownErrorMessageOnce = true;
                menuControllerLighty.createExceptionReport(exception);
            }
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        menuControllerLighty.touchDragged(screenX, Gdx.graphics.getHeight() - screenY, pointer);
        if (!ignoreDrag && !gamePaused && gameView.coversAllScreen()) gameController.touchDragged(screenX, Gdx.graphics.getHeight() - screenY, pointer);
        return false;
    }

    public void hideSplats() {
        needToHideSplats = true;
        timeToHideSplats = System.currentTimeMillis() + 350;
        splatTransparencyFactor.setDy(0);
        splatTransparencyFactor.beginDestroying(0, 1);
    }

    public void revealSplats() {
        needToHideSplats = false;
        splatTransparencyFactor.beginSpawning(0, 0.3);
    }

    static float maxElement(ArrayList<Float> list) {
        if (list.size() == 0) return 0;
        float max = list.get(0);
        for (int i=1; i<list.size(); i++) {
            if (list.get(i) > max) max = list.get(i);
        }
        return max;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        if (gameView.factorModel.get() > 0.1) gameController.scrolled(amount);
        return true;
    }
}
