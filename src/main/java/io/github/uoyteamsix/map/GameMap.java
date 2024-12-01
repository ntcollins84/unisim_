package io.github.uoyteamsix.map;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
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

    private final int mapWidth;
    private final int mapHeight;
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

        // map dimensions are in tiles
        mapWidth = buildingLayer.getWidth();
        mapHeight = buildingLayer.getHeight();
        tileWidthPx = buildingLayer.getTileWidth();
        tileHeightPx = buildingLayer.getTileHeight();
        widthPx = mapWidth * tileWidthPx;
        heightPx = mapHeight * tileHeightPx;

        // Compute which tiles are allowed to be placed on.
        usableTiles = new boolean[mapWidth][mapHeight];
        updateUsableTiles();

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
                if (mapX < 0 || mapY < 0 || mapX >= mapWidth || mapY >= mapHeight) {
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
        // CHANGED CODE
        placedBuildings.add(new Building(prefab, x, y));
    }

    /** NEW METHOD
     * Removes a building from the map.
     * @param tileX the x-coordinate in tiles
     * @param tileY the y-coordinate in tiles
     */
    public void deleteBuilding(int tileX, int tileY) {
        Building building = getSelectedBuilding(tileX, tileY);
        if (building == null) {
            return;
        }
        eraseBuildingFromMap(building);
        placedBuildings.remove(building);
        updateUsableTiles();
    }

    /** NEW METHOD
     * Checks whether the tile clicked is a building tile.
     * @param tileX the x-coordinate of the tile.
     * @param tileY the y-coordinate of the tile.
     * @return true if the tile is a building tile, false otherwise.
     */
    public boolean isBuildingClicked(int tileX, int tileY) {
        MapProperties properties = getMapProperties(tileX, tileY);
        return properties != null;
    }

    /** NEW METHOD
     * @param tileX the x-coordinate of the tile.
     * @param tileY the y-coordinate of the tile.
     * @return the name of the building e.g Canteen, Accommodation
     */
    public String getBuildingName(int tileX, int tileY) {
        MapProperties properties = getMapProperties(tileX, tileY);
        if (properties == null) {
            return null;
        }
        return properties.get("PrefabName").toString();
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
        return mapWidth;
    }

    /**
     * @return the height of the map in tiles
     */
    public int getHeight() {
        return mapHeight;
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

    /** NEW METHOD
     * @param tileX the x-coordinate of the selected tile
     * @param tileY the y-coordinate of the selected tile
     * @return key-value pairings of the property type and its name.
     */
    private MapProperties getMapProperties(int tileX, int tileY) {
        TiledMapTileLayer.Cell cell = buildingLayer.getCell(tileX, tileY);
        if (cell == null) {
            return null;
        }
        TiledMapTile tile = cell.getTile();
        return tile.getProperties();
    }

    /** NEW METHOD
     * Gets the Building object of the selected placed building
     * @param tileX x-coordinate of the tile.
     * @param tileY y-coordinate of the tile.
     */
    private Building getSelectedBuilding(int tileX, int tileY) {
        for (Building building : placedBuildings) {
            if (isClickContainedInBuilding(building, tileX, tileY)) {
                System.out.println("Building found.");
                return building;
            }
        }
        return null;
    }

    /** NEW METHOD
     * Checks which building the tile that has been clicked belongs to.
     * @param building the building object
     * @param tileX x-coordinate of the tile in tiles.
     * @param tileY y-coordinate of the tile in tiles.
     * @return true if the tile is contained within the current building, false
     *         otherwise.
     */
    private boolean isClickContainedInBuilding(Building building , int tileX, int tileY) {
        BuildingPrefab prefab = building.getPrefab();
        int width = prefab.getWidth();
        int height = prefab.getHeight();
        int tilePosX = building.getX();
        int tilePosY = building.getY();
        // Iterate through all tiles of the building
        for (int x = tilePosX; x < tilePosX + width; x++) {
            for (int y = tilePosY; y < tilePosY + height; y++) {
                if (x == tileX && y == tileY) {
                    return true;
                }
            }
        }
        return false;
    }

    /** NEW METHOD
     * Deletes all the clicked building's tiles from the buildingLayer.
     * @param building the building object to be erased.
     */
    private void eraseBuildingFromMap(Building building) {
        int buildingX = building.getX();
        int buildingY = building.getY();
        int buildingWidth = building.getPrefab().getWidth();
        int buildingHeight = building.getPrefab().getHeight();
        for (int x = buildingX; x < buildingX + buildingWidth; x++) {
            for (int y = 0; y < buildingY + buildingHeight; y++) {
                setTileToNull(x, y);
                usableTiles[x][y] = false;
            }
        }
    }

    /** NEW METHOD
     * Deletes the selected tile
     * @param tileX x-coordinate in tiles
     * @param tileY y-coordinate in tiles
     */
    private void setTileToNull(int tileX, int tileY) {
        buildingLayer.setCell(tileX, tileY, null);
    }

    /** NEW METHOD
     * Updates the 2D array of usable tiles to show which ones are free or not.
     */
    private void updateUsableTiles() {
        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
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
    }
}
