package io.github.uoyteamsix.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import io.github.uoyteamsix.Achievement;
import io.github.uoyteamsix.GameLogic;

/**
 * NEW CLASS
 * A class which represents an achievement popup
 */
public class AchievementPopup extends Table {
    private final UiAssets uiAssets;
    private final GameLogic gameLogic;
    private Label titleLabel;
    private Label nameLabel;
    private Label descriptionLabel;
    private Image boxImage;
    private Achievement displayAchievement;
    private float timer = 5f;


    public AchievementPopup(UiAssets uiAssets, GameLogic gameLogic) {
        this.uiAssets = uiAssets;
        this.gameLogic = gameLogic;
        // Hidden by default
        this.setVisible(false);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        // Create labels once fonts have been loaded.
        if (nameLabel == null && uiAssets.hasFontsLoaded()) {
            var labelStyleLarge = new Label.LabelStyle(uiAssets.getLargeFont(), Color.BLACK);
            var labelStyleSmall = new Label.LabelStyle(uiAssets.getSmallFont(), Color.BLACK);
            titleLabel = new Label("Achievement get!", labelStyleLarge);
            nameLabel = new Label("", labelStyleSmall);
            descriptionLabel = new Label("", labelStyleSmall);
        }

        // Create image once spritesheet has been loaded.
        if (boxImage == null && uiAssets.hasSpritesheetLoaded()) {
            var textureRegion = new TextureRegion(uiAssets.getSpritesheet(), 224, 32, 96, 64);
            boxImage = new Image(textureRegion);
        }

        // Add children once they have been created.
        if (getChildren().isEmpty() && nameLabel != null && boxImage != null) {
            add(boxImage).size(64.0f * 6.5f, 32.0f * 6.5f);
            row();
            add(titleLabel).align(Align.center).padTop(-300.0f);
            row();
            add(nameLabel).align(Align.center).padTop(-160.0f);
            row();
            add(descriptionLabel).align(Align.center).padTop(-100.0f);
        }

        // Show obtained achievement
        if (nameLabel != null) {
            Achievement lastAchievement = gameLogic.getLastAchievement();
            // If show timer not running check for achievements
            if (timer == 5f || (lastAchievement != null && displayAchievement != lastAchievement)) {
                displayAchievement = lastAchievement;
            }
            // If achievement obtained show popup and start time
            if (displayAchievement != null) {
                this.setVisible(true);
                nameLabel.setText(displayAchievement.name);
                descriptionLabel.setText(displayAchievement.description);
                timer -= delta;
            }
            // When timer ends reset popup
            if (timer <= 0f) {
                this.setVisible(false);
                displayAchievement = null;
                nameLabel.setText("");
                descriptionLabel.setText("");
                timer = 5f;
            }
        }
    }
}
