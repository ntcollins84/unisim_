package io.github.uoyteamsix.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import io.github.uoyteamsix.GameEvent;
import io.github.uoyteamsix.GameLogic;

/**
 * A class which represents the event information box UI element.
 */
public class CurrentEventBox extends Table {
    private final UiAssets uiAssets;
    private final GameLogic gameLogic;
    private Label eventLabel;
    private Label descriptionLabel;
    private Label timeLabel;
    private Image boxImage;

    public CurrentEventBox(UiAssets uiAssets, GameLogic gameLogic) {
        this.uiAssets = uiAssets;
        this.gameLogic = gameLogic;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        // Create label once fonts have been loaded.
        if (eventLabel == null && uiAssets.hasFontsLoaded()) {
            var labelStyle = new Label.LabelStyle(uiAssets.getSmallFont(), Color.BLACK);
            eventLabel = new Label("", labelStyle);
            descriptionLabel = new Label("", labelStyle);
            timeLabel = new Label("", labelStyle);
        }

        // Create image once spritesheet has been loaded.
        if (boxImage == null && uiAssets.hasSpritesheetLoaded()) {
            var textureRegion = new TextureRegion(uiAssets.getSpritesheet(), 320, 32, 64, 32);
            boxImage = new Image(textureRegion);
        }

        // Add children once they have been created.
        if (getChildren().isEmpty() && eventLabel != null && boxImage != null) {
            add(boxImage).size(64.0f * 3.0f, 32.0f * 3.0f);
            row();
            add(eventLabel).align(Align.left).padLeft(12.0f).padTop(-156.0f);
            row();
            add(descriptionLabel).align(Align.left).padLeft(12.0f).padTop(-100.0f);
            row();
            add(timeLabel).align(Align.left).padLeft(12.0f).padTop(-44.0f);
        }

        if (eventLabel != null) {
            // Get current event
            GameEvent currentEvent = gameLogic.getCurrentEvent();
            if (currentEvent != null) {
                // Display event name and time left
                timeLabel.setText(String.format("%d", (int) currentEvent.timer.getTimeLeft()));
                eventLabel.setText(String.format("Event: %s", currentEvent.getName()));
                // If event affects study deficit
                if (currentEvent.affectsStudy()) {
                    descriptionLabel.setText("Study -");
                }
                // Trick event
                else if (currentEvent.getSatisfactionEffect() == 0) {
                    descriptionLabel.setText("");
                } else {
                    // Display if event is positive or negative
                    descriptionLabel.setText(String.format("Satisfaction %s",
                            currentEvent.getSatisfactionEffect() < 0 ? "-" : "+"));
                }
            } else { // No event active
                eventLabel.setText("Event: None");
                descriptionLabel.setText("");
                timeLabel.setText("");
            }
            /*switch (gameLogic.getCurrentEvent()) {
                case NONE:
                    eventLabel.setText("Event: None");
                    descriptionLabel.setText("");
                    timeLabel.setText("");
                    break;
                case RAIN:
                    eventLabel.setText("Event: Rain");
                    descriptionLabel.setText("Satisfaction -");
                    break;
                case ROSES:
                    eventLabel.setText("Event: Roses");
                    descriptionLabel.setText("Satisfaction +");
                    break;
                case STRIKE:
                    eventLabel.setText("Event: Strike");
                    descriptionLabel.setText("Study -");
                    break;
            }*/

        }
    }
}
