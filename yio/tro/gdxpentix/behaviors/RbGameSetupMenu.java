package yio.tro.gdxpentix.behaviors;

import yio.tro.gdxpentix.ButtonLighty;

/**
 * Created by ivan on 05.08.14.
 */
public class RbGameSetupMenu extends ReactBehavior{

    @Override
    public void reactAction(ButtonLighty buttonLighty) {
        getYioGdxGame(buttonLighty).setGamePaused(true);
        buttonLighty.menuControllerLighty.createGameSetupMenu();
        getYioGdxGame(buttonLighty).setAnimToPlayButtonSpecial();
    }
}
