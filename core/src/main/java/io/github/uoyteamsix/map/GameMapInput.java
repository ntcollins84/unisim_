package io.github.uoyteamsix.map;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector3;
import io.github.uoyteamsix.CameraController;
import io.github.uoyteamsix.GameLogic;
import io.github.uoyteamsix.GameTimer;

/**
 * A class which handles user input events on the game map.
 */
public class GameMapInput extends InputAdapter {
    private final GameMap map;
    private final GameLogic gameLogic;
    private final CameraController cameraController;
    private final GameTimer gameTimer;
    private int selectedTileX = -1;
    private int selectedTileY = -1;

    public GameMapInput(GameMap map, GameLogic gameLogic, GameTimer gameTimer, CameraController cameraController) {
        this.map = map;
        this.gameLogic = gameLogic;
        this.gameTimer = gameTimer;
        this.cameraController = cameraController;
    }

    @Override
    public boolean keyDown(int keycode) {
        // New function: pause
        // Pause/unpause game with P key
        if (keycode == Input.Keys.P) {
            if (gameTimer.isPaused()) {
                gameTimer.resumeTime();
            }
            else {
                gameTimer.pauseTime();
            }
            return true;
        }
        // No other input allowed if game is paused
        if (!gameTimer.isPaused()) {
            // Allow deselecting the current prefab either by pressing escape, pressing a number out of range, or pressing
            // the same key again.
            if (keycode == Input.Keys.ESCAPE) {
                gameLogic.setSelectedPrefabIndex(-1);
                return true;
            }
            if (keycode >= Input.Keys.NUM_1 && keycode <= Input.Keys.NUM_9) {
                int index = keycode - Input.Keys.NUM_1;
                if (index == gameLogic.getSelectedPrefabIndex()) {
                    gameLogic.setSelectedPrefabIndex(-1);
                } else if (gameLogic.canPlaceBuilding()) {
                    gameLogic.setSelectedPrefabIndex(index);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        Vector3 worldCoordinates = cameraController.getCamera().unproject(new Vector3(screenX, screenY, 0));
        selectedTileX = (int) (worldCoordinates.x / map.getTileWidthPx());
        selectedTileY = (int) (worldCoordinates.y / map.getTileHeightPx());

        // Set value to -1 if outside the map bounds.
        if (selectedTileX < 0 || selectedTileX >= map.getWidth()) {
            selectedTileX = -1;
        }
        if (selectedTileY < 0 || selectedTileY >= map.getHeight()) {
            selectedTileY = -1;
        }
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        var prefab = gameLogic.getSelectedPrefab();
        if (button == Input.Buttons.LEFT && selectedTileX >= 0 && selectedTileY >= 0 && prefab != null) {
            int placementX = getPlacementTileX();
            int placementY = getPlacementTileY();
            if (gameLogic.canPlaceBuilding() && map.canPlaceBuilding(prefab, placementX, placementY)) {
                map.placeBuilding(prefab, placementX, placementY);

                // Deselect prefab after successfully placing a building.
                gameLogic.setSelectedPrefabIndex(-1);
            }
        }
        return true;
    }

    /**
     * @return the x coordinate of the currently hovered tile
     */
    public int getSelectedTileX() {
        return selectedTileX;
    }

    /**
     * @return the y coordinate of the currently hovered tile
     */
    public int getSelectedTileY() {
        return selectedTileY;
    }

    /**
     * @return the x coordinate of the centre of the currently selected prefab in world space
     */
    public int getPlacementTileX() {
        var prefab = gameLogic.getSelectedPrefab();
        if (prefab == null) {
            return selectedTileX;
        }
        return selectedTileX - prefab.getWidth() / 2;
    }

    /**
     * @return the y coordinate of the centre of the currently selected prefab in world space
     */
    public int getPlacementTileY() {
        var prefab = gameLogic.getSelectedPrefab();
        if (prefab == null) {
            return selectedTileY;
        }
        return selectedTileY - prefab.getHeight() / 2;
    }
}
