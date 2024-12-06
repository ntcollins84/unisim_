package io.github.uoyteamsix;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.uoyteamsix.map.GameMap;
import io.github.uoyteamsix.map.GameMapInput;
import io.github.uoyteamsix.ui.UiStage;

/**
 * A class representing the main gameplay screen.
 */
public class GameScreen extends ScreenAdapter {
    private final UniSimGame game;
    private final AssetManager assetManager;
    private final CursorManager cursorManager;
    private SpriteBatch batch;
    private final CameraController cameraController;
    private final GameLogic gameLogic;
    private final UiStage uiStage;
    private GameMap map;
    private GameMapInput mapInput;
    private MapRenderer mapRenderer;
    private GameTimer gameTimer;

    public GameScreen(UniSimGame game, AssetManager assetManager, CursorManager cursorManager, SpriteBatch batch) {
        this.game = game;
        this.assetManager = assetManager;
        this.cursorManager = cursorManager;
        this.batch = batch;
        cameraController = new CameraController();
        gameTimer = new GameTimer(300f, true);
        gameLogic = new GameLogic(gameTimer);
        uiStage = new UiStage(assetManager, gameLogic, gameTimer);

        // Create an input multiplexer to chain together our input adapters.
        // Add the UI stage first, then the camera controller.
        var inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(uiStage);
        inputMultiplexer.addProcessor(cameraController);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void resize(int width, int height) {
        // Propagate resize to other systems.
        cameraController.setViewportDimensions(width, height);
        uiStage.getViewport().update(width, height, true);
    }

    @Override
    public void render(float deltaTime) {
        // Clear the screen with a solid color (black).
        ScreenUtils.clear(0.0f, 0.0f, 0.0f, 1.0f);

        // Create map and map renderer if first run.
        if (map == null) {
            initializeMap();
        }

        // Set cursor based on camera behavior.
        updateCursorState();

        // Update camera, game logic, and UI.
        cameraController.update(deltaTime);
        gameTimer.updateTime(deltaTime);
        gameLogic.update(deltaTime);
        uiStage.act(deltaTime);

        // Render the map.
        mapRenderer.setView(cameraController.getCamera());
        mapRenderer.render();

        // Render the building currently being placed.
        renderBuildingPlacement();

        // Render the UI last.
        uiStage.draw();

        // When game ends go to end screen
        if (gameTimer.isTimeEnded()) {
            float satisfaction = gameLogic.getSatisfaction();
            game.setScreen(new HighScoreScreen(game, satisfaction));
        }
    }

    /**
     * Initializes the map and map renderer and sets up the camera position.
     */
    private void initializeMap() {
        try {
            var tiledMap = assetManager.get("maps/map.tmx", TiledMap.class);
            map = new GameMap(tiledMap);
            mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, batch);

            // Center the camera on the map.
            cameraController.getCamera().position.set(map.getWidthPx() / 2.0f, map.getHeightPx() / 2.0f, 0.0f);
            cameraController.setMapDimensions(map.getWidthPx(), map.getHeightPx());

            gameLogic.setMap(map);

            // Add input handler for map.
            mapInput = new GameMapInput(map, gameLogic, gameTimer, cameraController);
            ((InputMultiplexer) Gdx.input.getInputProcessor()).addProcessor(mapInput);
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Failed to initialize the map renderer: " + e.getMessage());
        }
    }

    /**
     * Updates the cursor state based on camera actions.
     */
    private void updateCursorState() {
        if (cameraController.isZoomingIn()) {
            cursorManager.setCursor(GameCursor.ZOOM_IN);
        } else if (cameraController.isZoomingOut()) {
            cursorManager.setCursor(GameCursor.ZOOM_OUT);
        } else if (cameraController.isPanning()) {
            cursorManager.setCursor(GameCursor.HAND_OPEN);
        } else {
            cursorManager.setCursor(GameCursor.POINTER);
        }
    }

    /**
     * Renders a building on the mouse cursor if a building is currently being placed.
     */
    private void renderBuildingPlacement() {
        var prefab = gameLogic.getSelectedPrefab();
        if (prefab == null) {
            // Building not being placed.
            return;
        }

        int placementX = mapInput.getPlacementTileX();
        int placementY = mapInput.getPlacementTileY();

        // Select texture based on whether this placement is valid.
        boolean canPlace = map.canPlaceBuilding(prefab, placementX, placementY);
        var texture = canPlace ? prefab.getTransparentTexture() : prefab.getRedTexture();

        // Render texture.
        batch.begin();
        batch.draw(texture, placementX * map.getTileWidthPx(), placementY * map.getTileHeightPx());
        batch.end();
    }

    // Disposal of assets moved to hide method
    @Override
    public void hide() {
        batch.dispose();
        uiStage.dispose();
    }


    //----------Methods for Testing----------
    public GameTimer getGameTimer(){
        return gameTimer;
    }

    public MapRenderer getMapRenderer() {
        return mapRenderer;
    }

    public GameMapInput getMapInput() {
        return mapInput;
    }

    public GameMap getMap() {
        return map;
    }

    public UiStage getUiStage() {
        return uiStage;
    }

    public GameLogic getGameLogic() {
        return gameLogic;
    }

    public CameraController getCameraController() {
        return cameraController;
    }

    public UniSimGame getGame() {
        return game;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public CursorManager getCursorManager() {
        return cursorManager;
    }

    public SpriteBatch getSpriteBatch() {
        return batch;
    }
}
