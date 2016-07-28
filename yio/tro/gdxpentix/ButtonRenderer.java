package yio.tro.gdxpentix;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by ivan on 22.07.14.
 */
public class ButtonRenderer {
    FrameBuffer frameBuffer;
    ShapeRenderer shapeRenderer;
    SpriteBatch batch;
    SimpleRectangle pos;
    BitmapFont font;
    TextureRegion buttonBackground1, buttonBackground2, buttonBackground3, bigButtonBackground;

    protected ButtonRenderer() {
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        buttonBackground1 = GameView.loadTextureRegionByName("button_background_1.png", true);
        buttonBackground1.flip(false, true);
        buttonBackground2 = GameView.loadTextureRegionByName("button_background_2.png", true);
        buttonBackground2.flip(false, true);
        buttonBackground3 = GameView.loadTextureRegionByName("button_background_3.png", true);
        buttonBackground3.flip(false, true);
        bigButtonBackground = GameView.loadTextureRegionByName("big_button_background.png", true);
        bigButtonBackground.flip(false, true);
    }

    TextureRegion getButtonBackground(ButtonLighty buttonLighty) {
        switch (buttonLighty.id % 3) {
            case 0 : return buttonBackground1;
            case 1 : return buttonBackground2;
            case 2 : return buttonBackground3;
            default: return buttonBackground1;
        }
    }

    void beginRender(ButtonLighty buttonLighty) {
        frameBuffer = new FrameBuffer(Pixmap.Format.RGB565, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        frameBuffer.begin();
        Gdx.gl.glClearColor(buttonLighty.backColor.r, buttonLighty.backColor.g, buttonLighty.backColor.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        if (buttonLighty.position.height < 0.2 * Gdx.graphics.getHeight())
            batch.draw(getButtonBackground(buttonLighty), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        else
            batch.draw(bigButtonBackground, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
        pos = new SimpleRectangle(buttonLighty.position);
        font = YioGdxGame.font;
    }

    void endRender(ButtonLighty buttonLighty) {
        Texture texture = frameBuffer.getColorBufferTexture();
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        buttonLighty.textureRegion = new TextureRegion(texture, (int)pos.width, (int)pos.height);
        frameBuffer.end();
    }

    public void renderButton(ButtonLighty buttonLighty) {
        beginRender(buttonLighty);
        BitmapFont font = YioGdxGame.font;
        float ratio = (float)(pos.width / pos.height);
        int lineHeight = (int)(1.2f * YioGdxGame.FONT_SIZE);
        int horizontalOffset = (int)(0.3f * YioGdxGame.FONT_SIZE);
        if (buttonLighty.text.size() == 1) {
            //if button has single line of text then center it
            float textWidth = font.getBounds(buttonLighty.text.get(0)).width;
            horizontalOffset = (int)(0.5 * (1.35f * YioGdxGame.FONT_SIZE * ratio - textWidth));
            if (horizontalOffset < 0) {
                horizontalOffset = (int)(0.3f * YioGdxGame.FONT_SIZE);
            }
        }
        int verticalOffset = (int)(0.3f * YioGdxGame.FONT_SIZE);
        int lineNumber = 0;
        float longestLineLength = 0, currentLineLength;
        batch.begin();
        font.setColor(0, 0, 0, 1);
        for (String line : buttonLighty.text) {
            font.draw(batch, line, horizontalOffset, verticalOffset + lineNumber * lineHeight);
            currentLineLength = font.getBounds(line).width;
            if (currentLineLength > longestLineLength) longestLineLength = currentLineLength;
            lineNumber++;
        }
        batch.end();
        pos.height = buttonLighty.text.size() * lineHeight + verticalOffset / 2;
        pos.width = pos.height * ratio;
        if (longestLineLength > pos.width - 0.3f * (float)lineHeight) {
            pos.width = longestLineLength + 2 * horizontalOffset;
        }
        endRender(buttonLighty);
    }
}
