package io.github.uoyteamsix.map;

/**
 * A class which represents a building placed on the map.
 */
public class Building {
    private final BuildingPrefab prefab;
    private final int x;
    private final int y;

    public Building(BuildingPrefab prefab, int x, int y) {
        this.prefab = prefab;
        this.x = x;
        this.y = y;
    }

    /**
     * @return the prefab this building was constructed from
     */
    public BuildingPrefab getPrefab() {
        return prefab;
    }

    /**
     * @return the x position of the building in tiles on the game map
     */
    public int getX() {
        return x;
    }

    /**
     * @return the y position of the building in tiles on the game map
     */
    public int getY() {
        return y;
    }
}
