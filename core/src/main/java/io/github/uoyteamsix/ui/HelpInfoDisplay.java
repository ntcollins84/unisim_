package io.github.uoyteamsix.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import io.github.uoyteamsix.GameLogic;

import java.util.ArrayList;
import java.util.List;

/** NEW CLASS
 * This class displays the welcome message and help text when the game is paused.
 */
public class HelpInfoDisplay extends Table {
    private final UiAssets uiAssets;
    private final GameLogic gameLogic;
    private final String[] labelStrings;
    private Image boxImage;
    private final List<Label> helpText;

    public HelpInfoDisplay(UiAssets uiAssets, GameLogic gameLogic) {
        this.uiAssets = uiAssets;
        this.gameLogic = gameLogic;
        this.helpText = new ArrayList<>();
        this.labelStrings = generateTextLines();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        // Create labels for each line of text once fonts have loaded.
        if (uiAssets.hasFontsLoaded()) {
            var labelStyle = new Label.LabelStyle(uiAssets.getSmallFont(), Color.BLACK);
            for (String line : labelStrings) {
                helpText.add(new Label(line, labelStyle));
            }
        }

        // Create image once spritesheet has loaded.
        if (boxImage == null && uiAssets.hasSpritesheetLoaded()) {
            var textureRegion = new TextureRegion(uiAssets.getSpritesheet(), 32, 32, 96, 64);
            boxImage = new Image(textureRegion);
        }

        // Populate table with image and labels.
        if (getChildren().isEmpty() && helpText != null && boxImage != null) {
            add(boxImage).size(64.0f * 12.0f, 32.0f * 12.0f);
            addLabelsToTable(this);
        }

        // Show or hide the table accordingly
        if (gameLogic.getGameTimer().isPaused()) {
            show();
        } else {
            hide();
        }
    }

    // Display the table
    public void show() {
        this.setVisible(true);
    }

    // Hide the table
    public void hide() {
        this.setVisible(false);
    }

    /**
     * Create the text to be displayed in the help display box.
     * @return an array of String objects corresponding to each line of text.
     */
    private String[] generateTextLines() {
        String line1 = "Welcome to UniSim, where you must place your buildings strategically";
        String line2 = "and react to campus events accordingly to get the highest student";
        String line3 = "satisfaction possible.";
        String line4 = "";
        String line5 = "Think carefully when placing your buildings as you cannot";
        String line6 = "change your mind once placed. Best of luck!";
        String line7 = "";
        String line8 = "How to play:";
        String line9 = "    - Press 'P' to pause/unpause game";
        String line10 = "    - Press keys '1' to '5' to select/deselect buildings";
        String line11 = "    - Scroll to zoom in/out";
        String line12 = "    - 'WASD' or hold 'left mouse click' to navigate the map";
        return new String[]{line1, line2, line3, line4, line5, line6,
                            line7, line8, line9, line10, line11, line12};
    }

    /**
     * Populate the table with the labels for each line of text.
     * @param table the table that stores the text.
     */
    private void addLabelsToTable(Table table) {
        float topPadding = -655.0f;
        for (Label label : helpText) {
            table.row();
            table.add(label).align(Align.left).padLeft(32.0f).padTop(topPadding);
            // Increment padding to position each line below the previous one.
            topPadding += 50.0f;
        }
    }
}
