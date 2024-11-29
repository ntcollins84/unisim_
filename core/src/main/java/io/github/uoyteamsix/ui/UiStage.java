package io.github.uoyteamsix.ui;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.uoyteamsix.GameLogic;
import io.github.uoyteamsix.GameTimer;

/**
 * A class which holds all the UI elements.
 */
public class UiStage extends Stage {
    private final UiAssets assets;

    public UiStage(AssetManager assetManager, GameLogic gameLogic, GameTimer gameTimer) {
        // The UI spans the whole screen.
        super(new ScreenViewport());
        assets = new UiAssets(assetManager);

        // Create a table to fill the whole screen.
        var mainTable = new Table();
        mainTable.setFillParent(true);
        addActor(mainTable);

        // Create a table anchored to the top left for the timer and stats.
        var topLeftTable = new Table();
        topLeftTable.add(new TimerDisplay(assets, gameTimer));
        topLeftTable.row();
        topLeftTable.add(new BuildingStatsBox(assets, gameLogic));
        topLeftTable.row();
        topLeftTable.add(new SatisfactionMeter(assets, gameLogic)).align(Align.left).padTop(16.0f);
        topLeftTable.row();
        topLeftTable.add(new CurrentEventBox(assets, gameLogic)).padTop(16.0f);

        // Create the building toolbar anchored to the bottom center.
        var buildingToolbar = new BuildingToolbar(assets, gameLogic);
        mainTable.add(topLeftTable).expand().top().left().padLeft(25.0f);
        mainTable.row();
        mainTable.add(buildingToolbar).bottom().center().padBottom(5.0f);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        assets.update();
    }
}
