package io.github.uoyteamsix.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

/**
 * A class which represents the information about a building.
 */
public class BuildingPrefab {
    private final String name;
    private final TiledMapTileLayer tiledLayer;
    private int width;
    private int height;
    private TextureRegion normalTexture;
    private TextureRegion transparentTexture;
    private TextureRegion redTexture;

    public BuildingPrefab(String name, TiledMapTileLayer tiledLayer) {
        this.name = name;
        this.tiledLayer = tiledLayer;

        // Compute width and height. Assumes building is rectangular.
        for (width = 0; width < tiledLayer.getWidth(); width++) {
            if (tiledLayer.getCell(width, 0) == null) {
                break;
            }
        }
        for (height = 0; height < tiledLayer.getHeight(); height++) {
            if (tiledLayer.getCell(0, height) == null) {
                break;
            }
        }
    }

    /**
     * Generate transparent and red textures for this building.
     *
     * @param renderer an instance of an OfflineBuildingRenderer
     */
    public void generateTextures(OffscreenBuildingRenderer renderer) {
        normalTexture = renderer.render(this, new Color(1.0f, 1.0f, 1.0f, 1.0f));
        transparentTexture = renderer.render(this, new Color(1.0f, 1.0f, 1.0f, 0.8f));
        redTexture = renderer.render(this, new Color(1.0f, 0.1f, 0.1f, 0.8f));
    }

    /**
     * @return the name of the building
     */
    public String getName() {
        return name;
    }

    /**
     * @return the tiled layer corresponding to this prefab
     */
    public TiledMapTileLayer getTiledLayer() {
        return tiledLayer;
    }

    /**
     * @return the width of the building in tiles
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return the height of the building in tiles
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return an exact fit rendered texture of this building
     */
    public TextureRegion getNormalTexture() {
        return normalTexture;
    }

    /**
     * @return an exact fit rendered texture of this building which is slightly transparent
     */
    public TextureRegion getTransparentTexture() {
        return transparentTexture;
    }

    /**
     * @return an exact fit rendered texture of this building which is slightly transparent and with a red tint
     */
    public TextureRegion getRedTexture() {
        return redTexture;
    }
}
