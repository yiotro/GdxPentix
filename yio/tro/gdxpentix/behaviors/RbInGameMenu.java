package yio.tro.gdxpentix.behaviors;

import yio.tro.gdxpentix.ButtonLighty;

/**
 * Created by ivan on 06.08.14.
 */
public class RbInGameMenu extends ReactBehavior{

    @Override
    public void reactAction(ButtonLighty buttonLighty) {
//        getYioGdxGame(buttonLighty).setBackAnimation(true);
        buttonLighty.menuControllerLighty.createInGameMenu();
        getYioGdxGame(buttonLighty).setGamePaused(true);
    }
}
