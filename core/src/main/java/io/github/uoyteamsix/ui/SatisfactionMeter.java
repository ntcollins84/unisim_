package io.github.uoyteamsix.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import io.github.uoyteamsix.GameLogic;

/**
 * A class which represents the student satisfaction UI element.
 */
public class SatisfactionMeter extends Table {
    private final UiAssets uiAssets;
    private final GameLogic gameLogic;
    private Image backgroundImage;
    private TextureRegion solidColour;

    public SatisfactionMeter(UiAssets uiAssets, GameLogic gameLogic) {
        this.uiAssets = uiAssets;
        this.gameLogic = gameLogic;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (backgroundImage == null && uiAssets.hasSpritesheetLoaded()) {
            var textureRegion = new TextureRegion(uiAssets.getSpritesheet(), 544, 204, 64, 8);
            backgroundImage = new Image(textureRegion);
            solidColour = new TextureRegion(uiAssets.getSpritesheet(), 512, 176, 1, 1);

            add(backgroundImage).size(192.0f, 32.0f);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (backgroundImage != null) {
            var coords = backgroundImage.localToScreenCoordinates(new Vector2(0, 0));
            float width = MathUtils.lerp(0.0f, 176.0f, gameLogic.getSatisfaction());
            batch.draw(solidColour, coords.x + 9, Gdx.graphics.getHeight() - coords.y + 13, width, 8.0f);
        }
    }
}
