package yio.tro.gdxpentix.behaviors;

import yio.tro.gdxpentix.ButtonLighty;
import yio.tro.gdxpentix.GameController;
import yio.tro.gdxpentix.YioGdxGame;

/**
 * Created by ivan on 05.08.14.
 */
public abstract class ReactBehavior {

    public abstract void reactAction(ButtonLighty buttonLighty);

    YioGdxGame getYioGdxGame(ButtonLighty buttonLighty) {
        return buttonLighty.menuControllerLighty.yioGdxGame;
    }

    GameController getGameController(ButtonLighty buttonLighty) {
        return buttonLighty.menuControllerLighty.yioGdxGame.gameController;
    }

    public static RbExit rbExit = new RbExit();
    public static RbInfo rbInfo = new RbInfo();
    public static RbMainMenu rbMainMenu = new RbMainMenu();
    public static RbGameSetupMenu rbGameSetupMenu = new RbGameSetupMenu();
    public static RbStartGame rbStartGame = new RbStartGame();
    public static RbInGameMenu rbInGameMenu = new RbInGameMenu();
    public static RbResumeGame rbResumeGame = new RbResumeGame();
    public static RbNothing rbNothing = new RbNothing();
}
