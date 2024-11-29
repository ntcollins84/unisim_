package io.github.uoyteamsix.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import io.github.uoyteamsix.GameLogic;

import java.util.ArrayList;
import java.util.List;

/**
 * A class which represents the building toolbar and next building timer UI elements.
 */
public class BuildingToolbar extends Stack {
    private final UiAssets uiAssets;
    private final GameLogic gameLogic;
    private final Table toolbarTable;
    private final List<Image> backgroundImages;
    private Label nextBuildingTimeLabel;
    private TextureRegion selectionBoxTexture;

    public BuildingToolbar(UiAssets uiAssets, GameLogic gameLogic) {
        this.uiAssets = uiAssets;
        this.gameLogic = gameLogic;
        toolbarTable = new Table();
        backgroundImages = new ArrayList<>();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        // Create children when assets have loaded.
        if (backgroundImages.isEmpty() && uiAssets.hasSpritesheetLoaded() && uiAssets.hasFontsLoaded()) {
            // Create a drawable for the tooltip box and give it some padding as otherwise the text intersects the edge
            // of the box.
            var tooltipBoxTexture = new TextureRegion(uiAssets.getSpritesheet(), 320, 32, 64, 32);
            var tooltipBoxDrawable = new TextureRegionDrawable(tooltipBoxTexture);
            tooltipBoxDrawable.setPadding(5.0f, 8.0f, 5.0f, 8.0f);

            // Create a tooltip style.
            var tooltipLabelStyle = new Label.LabelStyle(uiAssets.getSmallFont(), Color.BLACK);
            var tooltipStyle = new TextTooltip.TextTooltipStyle(tooltipLabelStyle, tooltipBoxDrawable);

            var textureRegion = new TextureRegion(uiAssets.getSpritesheet(), 32, 160, 32, 32);
            for (var prefab : gameLogic.getGameMap().getAvailablePrefabs()) {
                final int index = gameLogic.getGameMap().getAvailablePrefabs().indexOf(prefab);
                var image = new Image(textureRegion);
                image.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        gameLogic.setSelectedPrefabIndex(index);
                    }
                });

                // Add tooltip to display on image hover.
                // Tooltip does not display numbers in prefab name
                var tooltip = new TextTooltip(prefab.getName().replaceAll("\\d",""), tooltipStyle);
                tooltip.setInstant(true);
                image.addListener(tooltip);

                backgroundImages.add(image);
            }

            // Create next building time label.
            var timeLabelStyle = new Label.LabelStyle(uiAssets.getLargeFont(), Color.BLACK);
            nextBuildingTimeLabel = new Label("", timeLabelStyle);
            nextBuildingTimeLabel.setAlignment(Align.center);
            selectionBoxTexture = new TextureRegion(uiAssets.getSpritesheet(), 384, 32, 32, 32);

            // Add toolbar table to the stack, and add the background images to the table.
            add(toolbarTable);
            for (int i = 0; i < backgroundImages.size(); i++) {
                var cell = toolbarTable.add(backgroundImages.get(i)).size(64.0f, 64.0f);
                if (i != 0) {
                    cell.padLeft(20.0f);
                }
            }
        }

        if (nextBuildingTimeLabel != null) {
            // Update label text.
            var text = String.format("Next building in %d", (int) gameLogic.getNextBuildingTime());
            nextBuildingTimeLabel.setText(text);

            // Show toolbar if player can place a building, otherwise show the next building timer.
            boolean canPlaceBuilding = gameLogic.canPlaceBuilding();
            toolbarTable.setVisible(canPlaceBuilding);

            // Remove and add label so it doesn't affect the layout of the toolbar.
            removeActor(nextBuildingTimeLabel);
            if (!canPlaceBuilding) {
                add(nextBuildingTimeLabel);
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        // Don't draw building icons or selection box if the toolbar is not supposed to be visible.
        if (!toolbarTable.isVisible()) {
            return;
        }

        // Draw building icons in each box.
        for (int i = 0; i < backgroundImages.size(); i++) {
            var image = backgroundImages.get(i);
            var coords = image.localToScreenCoordinates(new Vector2(0, 0));

            // Offset slightly.
            coords.add(8.0f, -10.0f);

            var prefab = gameLogic.getGameMap().getAvailablePrefabs().get(i);
            batch.draw(prefab.getNormalTexture(), coords.x, Gdx.graphics.getHeight() - coords.y, 48.0f, 48.0f);
        }

        var prefabIndex = gameLogic.getSelectedPrefabIndex();
        if (prefabIndex < 0 || selectionBoxTexture == null) {
            // No selected prefab.
            return;
        }

        // Draw selection image on top of clicked cell.
        var image = backgroundImages.get(prefabIndex);
        var coords = image.localToScreenCoordinates(new Vector2(0, 0));
        batch.draw(selectionBoxTexture, coords.x, Gdx.graphics.getHeight() - coords.y, 64.0f, 64.0f);
    }
}
