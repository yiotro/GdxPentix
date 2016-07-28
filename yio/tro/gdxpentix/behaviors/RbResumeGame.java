package yio.tro.gdxpentix.behaviors;

import yio.tro.gdxpentix.ButtonLighty;

/**
 * Created by ivan on 06.08.14.
 */
public class RbResumeGame extends ReactBehavior{

    @Override
    public void reactAction(ButtonLighty buttonLighty) {
        getYioGdxGame(buttonLighty).gameView.beginSpawnProcess();
        buttonLighty.menuControllerLighty.createGameOverlay();
        getYioGdxGame(buttonLighty).setGamePaused(false);
        getYioGdxGame(buttonLighty).setAnimToResumeButtonSpecial();
    }
}
