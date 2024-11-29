package io.github.uoyteamsix.map;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

import java.util.ArrayList;
import java.util.List;

/**
 * A class which represents the playable game map. Holds the underlying tiled map and keeps track
 * of placed buildings.
 */
public class GameMap {
    private final TiledMap tiledMap;
    private final TiledMapTileLayer buildingLayer;

    private final int width;
    private final int height;
    private final int widthPx;
    private final int heightPx;
    private final int tileWidthPx;
    private final int tileHeightPx;

    private final boolean[][] usableTiles;
    private final List<BuildingPrefab> availablePrefabs;
    private final List<Building> placedBuildings;

    public GameMap(TiledMap tiledMap) {
        this.tiledMap = tiledMap;
        buildingLayer = (TiledMapTileLayer) tiledMap.getLayers().get("Buildings");

        width = buildingLayer.getWidth();
        height = buildingLayer.getHeight();
        tileWidthPx = buildingLayer.getTileWidth();
        tileHeightPx = buildingLayer.getTileHeight();
        widthPx = width * tileWidthPx;
        heightPx = height * tileHeightPx;

        // Compute which tiles are allowed to be placed on.
        usableTiles = new boolean[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                usableTiles[x][y] = true;
                for (var layer : tiledMap.getLayers()) {
                    if (layer.getName().equals("Terrain")) {
                        continue;
                    }
                    if (((TiledMapTileLayer) layer).getCell(x, y) != null) {
                        usableTiles[x][y] = false;
                    }
                }
            }
        }

        // Create building types for each prefab layer in the map.
        availablePrefabs = new ArrayList<>();
        for (var layer : tiledMap.getLayers()) {
            if (layer.getName().startsWith("Prefab: ")) {
                // Extract prefab name, e.g. Accomodation.
                var prefabName = layer.getName().substring("Prefab: ".length());
                availablePrefabs.add(new BuildingPrefab(prefabName, (TiledMapTileLayer) layer));
            }
        }

        // Generate textures for each building prefab.
        var offscreenBuildingRenderer = new OffscreenBuildingRenderer(this);
        for (var prefab : availablePrefabs) {
            prefab.generateTextures(offscreenBuildingRenderer);
        }

        placedBuildings = new ArrayList<>();
    }

    /**
     * Checks whether a building prefab can be placed at the given coordinates.
     *
     * @param prefab the building prefab
     * @param x      the x coordinate in world space
     * @param y      the y coordinate in world space
     * @return true if the building can be placed, false otherwise
     */
    public boolean canPlaceBuilding(BuildingPrefab prefab, int x, int y) {
        for (int prefabX = 0; prefabX < prefab.getWidth(); prefabX++) {
            for (int prefabY = 0; prefabY < prefab.getHeight(); prefabY++) {
                int mapX = x + prefabX;
                int mapY = y + prefabY;

                // Check out of bounds.
                if (mapX < 0 || mapY < 0 || mapX >= width || mapY >= height) {
                    return false;
                }

                // Check if on top of a disallowed tile.
                if (!usableTiles[mapX][mapY]) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Creates an instance of the given building prefab and places it at the given coordinates. Doesn't check for
     * validity of placement.
     *
     * @param prefab the building prefab
     * @param x      the x coordinate in world space
     * @param y      the y coordinate in world space
     * @see #canPlaceBuilding
     */
    public void placeBuilding(BuildingPrefab prefab, int x, int y) {
        for (int prefabX = 0; prefabX < prefab.getWidth(); prefabX++) {
            for (int prefabY = 0; prefabY < prefab.getHeight(); prefabY++) {
                int mapX = x + prefabX;
                int mapY = y + prefabY;
                buildingLayer.setCell(mapX, mapY, prefab.getTiledLayer().getCell(prefabX, prefabY));
                usableTiles[mapX][mapY] = false;
            }
        }
        placedBuildings.add(new Building(prefab, x, y));
    }

    /**
     * Counts the number of existing buildings of the given prefab.
     *
     * @param prefab the building prefab
     * @return the building count
     */
    public int getBuildingCount(BuildingPrefab prefab) {
        int count = 0;
        for (var building : placedBuildings) {
            if (building.getPrefab() == prefab) {
                count++;
            }
        }
        return count;
    }

    /**
     * @return the total building count of all prefabs
     */
    public int getTotalBuildingCount() {
        return placedBuildings.size();
    }

    /**
     * @return the underlying tiled map of this map
     */
    public TiledMap getTiledMap() {
        return tiledMap;
    }

    /**
     * @return the width of the map in tiles
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return the height of the map in tiles
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return the width of the map in pixels
     */
    public int getWidthPx() {
        return widthPx;
    }

    /**
     * @return the height of the map in pixels
     */
    public int getHeightPx() {
        return heightPx;
    }

    /**
     * @return the width of a single map tile in pixels
     */
    public int getTileWidthPx() {
        return tileWidthPx;
    }

    /**
     * @return the height of a single map tile in pixels
     */
    public int getTileHeightPx() {
        return tileHeightPx;
    }

    /**
     * @return a list of all available prefabs in this map
     */
    public List<BuildingPrefab> getAvailablePrefabs() {
        return availablePrefabs;
    }
}
