package yio.tro.gdxpentix.behaviors;

import yio.tro.gdxpentix.ButtonLighty;

/**
 * Created by ivan on 05.08.14.
 */
public class RbMainMenu extends ReactBehavior{

    @Override
    public void reactAction(ButtonLighty buttonLighty) {
        getYioGdxGame(buttonLighty).setGamePaused(true);
        getYioGdxGame(buttonLighty).setBackAnimation(true);
        buttonLighty.menuControllerLighty.createMainMenu();
        getYioGdxGame(buttonLighty).revealSplats();
    }
}
