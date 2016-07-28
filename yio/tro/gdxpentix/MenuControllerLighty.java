package yio.tro.gdxpentix;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.gdxpentix.behaviors.ReactBehavior;
import yio.tro.gdxpentix.factor_yio.FactorYio;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.StringTokenizer;

/**
 * Created by ivan on 22.07.14.
 */
public class MenuControllerLighty {
    public YioGdxGame yioGdxGame;
    ArrayList<ButtonLighty> buttons;
    ButtonFactory buttonFactory;
    SimpleRectangle biggerBlockPosition;
    ButtonRenderer buttonRenderer;
    LanguagesManager languagesManager;
    Sound soundMenuButton;
    TextureRegion unlockedLevelIcon, lockedLevelIcon, openedLevelIcon;
    FactorYio infoPanelFactor;

    public MenuControllerLighty(YioGdxGame yioGdxGame) {
        this.yioGdxGame = yioGdxGame;
        buttonFactory = new ButtonFactory(this);
        buttons = new ArrayList<ButtonLighty>();
        biggerBlockPosition = new SimpleRectangle(0.1 * Gdx.graphics.getWidth(), 0.1 * Gdx.graphics.getHeight(), 0.8 * Gdx.graphics.getWidth(), 0.8 * Gdx.graphics.getHeight());
        buttonRenderer = new ButtonRenderer();
        infoPanelFactor = new FactorYio();
        languagesManager = LanguagesManager.getInstance();
        soundMenuButton = Gdx.audio.newSound(Gdx.files.internal("sound/menu_button.ogg"));
        unlockedLevelIcon = GameView.loadTextureRegionByName("unlocked_level_icon.png", true);
        lockedLevelIcon = GameView.loadTextureRegionByName("locked_level_icon.png", true);
        openedLevelIcon = GameView.loadTextureRegionByName("opened_level_icon.png", true);
//        createMainMenu();
    }

    Sound getDefaultSound() {
        return soundMenuButton;
    }

    public void move() {
        infoPanelFactor.move();
        for (ButtonLighty buttonLighty : buttons) {
            buttonLighty.move();
        }
        for (int i=buttons.size()-1; i>=0; i--) {
            if (buttons.get(i).checkToPerformAction()) break;
        }
    }

    public void addMenuBlockToArray(ButtonLighty buttonLighty) {
        // considered that menu block is not in array at this moment
        ListIterator iterator = buttons.listIterator();
        while (iterator.hasNext()) {
            iterator.next();
        }
        iterator.add(buttonLighty);
    }

    public void removeMenuBlockFromArray(ButtonLighty buttonLighty) {
        ListIterator iterator = buttons.listIterator();
        ButtonLighty currentBlock;
        while (iterator.hasNext()) {
            currentBlock = (ButtonLighty) iterator.next();
            if (currentBlock == buttonLighty) {
                iterator.remove();
                return;
            }
        }
    }

    public ButtonLighty getButtonById(int id) { // can return null
        for (ButtonLighty buttonLighty : buttons) {
            if (buttonLighty.id == id) return buttonLighty;
        }
        return null;
    }

    void loadButtonOnce(ButtonLighty buttonLighty, String fileName) {
        if (buttonLighty.notRendered()) {
            buttonLighty.loadTexture(fileName);
        }
    }

    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        for (ButtonLighty buttonLighty : buttons) {
            if (buttonLighty.isTouchable()) {
                if (buttonLighty.checkTouch(screenX, screenY, pointer, button)) return true;
            }
        }
        return false;
    }

    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    public void touchDragged(int screenX, int screenY, int pointer) {

    }

    void beginMenuCreation() {
        infoPanelFactor.setValues(1, 0);
        infoPanelFactor.beginDestroying(1, 3);
        for (ButtonLighty buttonLighty : buttons) {
            buttonLighty.destroy();
            if (buttonLighty.id == 11 && buttonLighty.isVisible()) {
                buttonLighty.factorModel.stopMoving();
                buttonLighty.factorModel.beginDestroying(0, 1);
            }
            if (buttonLighty.id == 3 && buttonLighty.isVisible()) {
                buttonLighty.factorModel.setValues(1, 0);
                buttonLighty.factorModel.beginDestroying(1, 2);
            }
            if (buttonLighty.id >= 22 && buttonLighty.id <= 29 && buttonLighty.isVisible()) {
                buttonLighty.factorModel.beginDestroying(1, 2.1);
            }
            if (buttonLighty.id == 30) {
                buttonLighty.factorModel.setValues(1, 0);
                buttonLighty.factorModel.beginDestroying(1, 1);
            }
        }
        if (yioGdxGame.gameView != null) yioGdxGame.gameView.beginDestroyProcess();
        yioGdxGame.hideSplats();
    }

    void endMenuCreation() {
        if (yioGdxGame.backAnimation) {
            forceSpawningButtonsToTheEnd();
        }
    }

    void forceSpawningButtonsToTheEnd() {
        for (ButtonLighty buttonLighty : buttons) {
            if (buttonLighty.factorModel.getGravity() > 0) {
                buttonLighty.factorModel.setValues(1, 0);
            }
        }
    }

    ArrayList<String> getArrayListFromString(String src) {
        ArrayList<String> list = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(src, "#");
        while (tokenizer.hasMoreTokens()) {
            list.add(tokenizer.nextToken());
        }
        return list;
    }

    SimpleRectangle generateRectangle(double x, double y, double width, double height) {
        return new SimpleRectangle(x * Gdx.graphics.getWidth(), y * Gdx.graphics.getHeight(), width * Gdx.graphics.getWidth(), height * Gdx.graphics.getHeight());
    }

    SimpleRectangle generateSquare(double x, double y, double size) {
        return generateRectangle(x, y, size, size * YioGdxGame.screenRatio);
    }

    public void createMainMenu() {
        beginMenuCreation();

        yioGdxGame.beginBackgroundChange(0, false, false);

        ButtonLighty exitButton = buttonFactory.getButton(generateSquare(0.8, 0.87, 0.15), 1, null);
        loadButtonOnce(exitButton, "shut_down.png");
        exitButton.setShadow(true);
        exitButton.setAnimType(ButtonLighty.ANIM_UP);
        exitButton.setReactBehavior(ReactBehavior.rbExit);
        exitButton.disableTouchAnimation();

        ButtonLighty infoButton = buttonFactory.getButton(generateSquare(0.05, 0.87, 0.15), 2, null);
        loadButtonOnce(infoButton, "info_icon.png");
        infoButton.setShadow(true);
        infoButton.setAnimType(ButtonLighty.ANIM_UP);
        infoButton.setReactBehavior(ReactBehavior.rbInfo);
        infoButton.disableTouchAnimation();

        ButtonLighty playButton = buttonFactory.getButton(generateSquare(0.3, 0.35, 0.4), 3, null);
        loadButtonOnce(playButton, "play_button.png");
        playButton.setReactBehavior(ReactBehavior.rbGameSetupMenu);
        playButton.enableDeltaAnimation();
        playButton.disableTouchAnimation();
        playButton.selectionFactor.setValues(1, 0);

        endMenuCreation();
    }

    public void createInfoMenu() {
        beginMenuCreation();

        yioGdxGame.beginBackgroundChange(1, true, false);

        ButtonLighty backButton = buttonFactory.getButton(generateRectangle(0.05, 0.9, 0.4, 0.07), 10, null);
        loadButtonOnce(backButton, "back_icon.png");
        backButton.setShadow(true);
        backButton.setAnimType(ButtonLighty.ANIM_UP);
        backButton.setReactBehavior(ReactBehavior.rbMainMenu);

        ButtonLighty infoPanel = buttonFactory.getButton(generateRectangle(0.05, 0.1, 0.9, 0.7), 11, null);
        if (infoPanel.notRendered()) {
            infoPanel.addManyLines(getArrayListFromString(languagesManager.getString("info_array")));
            buttonRenderer.renderButton(infoPanel);
        }
        infoPanel.setTouchable(false);
        infoPanel.setAnimType(ButtonLighty.ANIM_SOLID);
        infoPanel.setShadow(false);
        infoPanel.factorModel.setValues(-0.3, 0);
        infoPanel.factorModel.beginSpawning(1, 0.3);
        infoPanelFactor.setValues(-0.3, 0);
        infoPanelFactor.beginSpawning(1, 0.36);

        endMenuCreation();
    }

    public void createGameSetupMenu() {
        beginMenuCreation();

        yioGdxGame.beginBackgroundChange(2, true, true);

        ButtonLighty backButton = buttonFactory.getButton(generateRectangle(0.05, 0.9, 0.4, 0.07), 20, null);
        loadButtonOnce(backButton, "back_icon.png");
        backButton.setShadow(true);
        backButton.setAnimType(ButtonLighty.ANIM_UP);
        backButton.setReactBehavior(ReactBehavior.rbMainMenu);

        ButtonLighty startButton = buttonFactory.getButton(generateRectangle(0.55, 0.9, 0.4, 0.07), 21, languagesManager.getString("game_settings_start"));
        startButton.setReactBehavior(ReactBehavior.rbStartGame);
        startButton.setAnimType(ButtonLighty.ANIM_UP);

        endMenuCreation();
    }

    public void createGameOverlay() {
        beginMenuCreation();

        ButtonLighty inGameMenuButton = buttonFactory.getButton(generateSquare(1 - 0.07 / YioGdxGame.screenRatio, 0.93, 0.07 / YioGdxGame.screenRatio), 30, null);
        loadButtonOnce(inGameMenuButton, "menu_icon.png");
        inGameMenuButton.setReactBehavior(ReactBehavior.rbInGameMenu);
        inGameMenuButton.setAnimType(ButtonLighty.ANIM_UP);
        inGameMenuButton.rectangularMask = true;
        inGameMenuButton.disableTouchAnimation();

//        ButtonLighty updCacheButton = buttonFactory.getButton(generateSquare(1 - 2.5f * 0.07 / YioGdxGame.screenRatio, 0, 0.07 / YioGdxGame.screenRatio), 32, "C");
//        updCacheButton.setReactBehavior(ReactBehavior.rbUpdateCache);
//        updCacheButton.setAnimType(ButtonLighty.ANIM_DOWN);
//        updCacheButton.disableTouchAnimation();

        endMenuCreation();
    }

    public void createInGameMenu() {
        beginMenuCreation();

        yioGdxGame.beginBackgroundChange(3, true, true);

        ButtonLighty exitButton = buttonFactory.getButton(generateSquare(0.87, 0.87, 0.1), 1, null);
        loadButtonOnce(exitButton, "shut_down.png");
        exitButton.setShadow(true);
        exitButton.setAnimType(ButtonLighty.ANIM_UP);
        exitButton.setReactBehavior(ReactBehavior.rbExit);
        exitButton.disableTouchAnimation();
        exitButton.factorModel.beginSpawning(3, 1);

        ButtonLighty restartButton = buttonFactory.getButton(generateRectangle(0.05, 0.9, 0.5, 0.08), 44, languagesManager.getString("in_game_menu_restart"));
        restartButton.setReactBehavior(ReactBehavior.rbStartGame);
        restartButton.setAnimType(ButtonLighty.ANIM_UP);
        restartButton.disableTouchAnimation();
        restartButton.factorModel.beginSpawning(3, 1);

        ButtonLighty resumeButton = buttonFactory.getButton(generateRectangle(0.05, 0.8, 0.5, 0.08), 45, languagesManager.getString("in_game_menu_resume"));
        resumeButton.setReactBehavior(ReactBehavior.rbResumeGame);
        resumeButton.setAnimType(ButtonLighty.ANIM_UP);
        resumeButton.disableTouchAnimation();
        resumeButton.factorModel.beginSpawning(3, 1);

        endMenuCreation();
    }

    public void createExceptionReport(Exception exception) {
        beginMenuCreation();
        yioGdxGame.setGamePaused(true);

        ArrayList<String> text = new ArrayList<String>();
        text.add("Error : " + exception.toString());
        String temp;
        int start, end;
        boolean go;
        for (int i=0; i<exception.getStackTrace().length; i++) {
            temp = exception.getStackTrace()[i].toString();
            start = 0;
            go = true;
            while (go) {
                end = start + 40;
                if (end > temp.length() - 1) {
                    go = false;
                    end = temp.length() - 1;
                }
                try {
                    text.add(temp.substring(start, end));
                } catch (ArrayIndexOutOfBoundsException e) {}
                start = end + 1;
            }
        }
        ButtonLighty textPanel = buttonFactory.getButton(generateRectangle(0.1, 0.2, 0.8, 0.7), 6731267, null);
        if (textPanel.notRendered()) {
            textPanel.addManyLines(text);
            for (int i=0; i<10; i++) textPanel.addTextLine(" ");
            buttonRenderer.renderButton(textPanel);
        }
        textPanel.setTouchable(false);

        ButtonLighty okButton = buttonFactory.getButton(generateRectangle(0.1, 0.1, 0.8, 0.1), 73612321, "Ok");
        okButton.setReactBehavior(ReactBehavior.rbInGameMenu);

        endMenuCreation();
    }
}
