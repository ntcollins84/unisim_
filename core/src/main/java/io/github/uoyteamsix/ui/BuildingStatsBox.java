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
import java.util.Objects;

/**
 * A class which represents the building counter UI element.
 */
public class BuildingStatsBox extends Table {
    private final UiAssets uiAssets;
    private final GameLogic gameLogic;
    private final List<Label> labels;
    private Image boxImage;

    public BuildingStatsBox(UiAssets uiAssets, GameLogic gameLogic) {
        this.uiAssets = uiAssets;
        this.gameLogic = gameLogic;
        labels = new ArrayList<>();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        // Create labels once fonts have been loaded.
        if (labels.isEmpty() && uiAssets.hasFontsLoaded()) {
            var labelStyle = new Label.LabelStyle(uiAssets.getSmallFont(), Color.BLACK);
            for (int i = 0; i < gameLogic.getGameMap().getAvailablePrefabs().size(); i++) {
                labels.add(new Label("", labelStyle));
            }
        }

        // Create image once spritesheet has been loaded.
        if (boxImage == null && uiAssets.hasSpritesheetLoaded()) {
            var textureRegion = new TextureRegion(uiAssets.getSpritesheet(), 320, 64, 64, 32);
            boxImage = new Image(textureRegion);
        }

        // TODO: This code assumes 4 labels (building types).
        if (getChildren().isEmpty() && !labels.isEmpty() && boxImage != null) {
            add(boxImage).size(64.0f * 3.0f, 32.0f * 3.0f);
            row();
            add(labels.get(0)).align(Align.left).padLeft(12.0f).padTop(-165.0f);
            row();
            add(labels.get(1)).align(Align.left).padLeft(12.0f).padTop(-116.0f);
            row();
            add(labels.get(2)).align(Align.left).padLeft(12.0f).padTop(-68.0f);
            row();
            add(labels.get(3)).align(Align.left).padLeft(12.0f).padTop(-23.0f);
        }

        // Update label text.
        // Recreation buildings only have one label for two types of building
        if (!labels.isEmpty()) {
            var map = gameLogic.getGameMap();
            for (int i = 0; i < map.getAvailablePrefabs().size() - 1; i++) {
                var prefab = map.getAvailablePrefabs().get(i);
                var count = map.getBuildingCount(prefab);
                if (Objects.equals(prefab.getName(), "Recreation")) {
                    var recPrefab = map.getAvailablePrefabs().get(i + 1);
                    count += map.getBuildingCount(recPrefab);
                }
                labels.get(i).setText(String.format("%s: %d", prefab.getName(), count));
            }
        }
    }
}
