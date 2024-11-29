package io.github.uoyteamsix.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

/**
 * A class which renders each building type to textures. This is used for the transparent green and red building
 * textures when buildings are being placed, as well as for the preview on the building toolbar.
 */
public class OffscreenBuildingRenderer {
    private final GameMap map;
    private final SpriteBatch spriteBatch;
    private final MapRenderer mapRenderer;

    public OffscreenBuildingRenderer(GameMap map) {
        this.map = map;

        // Create our own sprite batch so we can control the tint colour on it.
        spriteBatch = new SpriteBatch();
        mapRenderer = new OrthogonalTiledMapRenderer(map.getTiledMap(), spriteBatch);
    }

    /**
     * Renders the given building to an offscreen texture.
     *
     * @param buildingPrefab the building to render
     * @param tintColour     a colour to tint the rendering
     * @return a texture region containing the rendered building
     */
    public TextureRegion render(BuildingPrefab buildingPrefab, Color tintColour) {
        // Fit the framebuffer perfectly to the building's size.
        int fboWidth = buildingPrefab.getWidth() * map.getTileWidthPx();
        int fboHeight = buildingPrefab.getHeight() * map.getTileHeightPx();

        // Create the framebuffer.
        var fbo = new FrameBuffer(Pixmap.Format.RGBA8888, fboWidth, fboHeight, false);
        var fboRegion = new TextureRegion(fbo.getColorBufferTexture());
        fboRegion.flip(false, true);

        // Create a camera centered on the building.
        var camera = new OrthographicCamera();
        camera.viewportWidth = fboWidth;
        camera.viewportHeight = fboHeight;
        camera.position.set(fboWidth / 2.0f, fboHeight / 2.0f, 0.0f);
        camera.update();
        mapRenderer.setView(camera);

        // Set the tint colour and set the render target to the FBO.
        spriteBatch.setColor(tintColour);
        fbo.begin();

        // Make sure the layer is visible.
        boolean layerOriginallyVisible = buildingPrefab.getTiledLayer().isVisible();
        buildingPrefab.getTiledLayer().setVisible(true);

        // Render the single layer corresponding to the building into the FBO texture.
        int layerIndex = map.getTiledMap().getLayers().getIndex(buildingPrefab.getTiledLayer());
        mapRenderer.render(new int[]{layerIndex});

        // Unbind the FBO.
        fbo.end();

        // Restore layer's original visibility.
        buildingPrefab.getTiledLayer().setVisible(layerOriginallyVisible);
        return fboRegion;
    }
}
