package yio.tro.gdxpentix.behaviors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import yio.tro.gdxpentix.ButtonLighty;

/**
 * Created by ivan on 06.10.2014.
 */
public class RbReset extends ReactBehavior {

    @Override
    public void reactAction(ButtonLighty buttonLighty) {
        Preferences preferences = Gdx.app.getPreferences("main");
        preferences.putInteger("progress", 0);
        preferences.flush();
        getYioGdxGame(buttonLighty).setSelectedLevelIndex(0);
    }
}
