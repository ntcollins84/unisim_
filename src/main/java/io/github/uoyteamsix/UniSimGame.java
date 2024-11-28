package io.github.uoyteamsix;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

/**
 * Class implementing the main game loop by extending from {@link Game}.
 */
public class UniSimGame extends Game {
    // Use LibGDX's AssetManager class which handles asynchronous loading and unloading of assets for us.
    private AssetManager assetManager;
    private CursorManager cursorManager;
    private GameScreen gameScreen;

    @Override
    public void create() {
        // Create the asset manager and register the loader for TTF fonts and tiled maps.
        assetManager = new AssetManager();
        assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(new InternalFileHandleResolver()));
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));

        // Load tiled map.
        assetManager.load("maps/map.tmx", TiledMap.class);

        cursorManager = new CursorManager(assetManager);

        // Create all of our screens.
        gameScreen = new GameScreen(this, assetManager, cursorManager);

        // Go straight to the main game screen.
        setScreen(gameScreen);
    }

    @Override
    public void render() {
        // Continuously load in assets. Block for a maximum of 17 milliseconds which should maintain roughly 60 frames
        // per second.
        if (!assetManager.update(17)) {
            // Returned false so we are still loading assets.
            // TODO: Display some kind of loading screen?
            return;
        }

        // Create any cursors for pixmaps which have been loaded.
        cursorManager.update();

        // Otherwise delegate to the current screen via the Game class.
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();

        // Dispose is not called automatically for screens, Game.dispose() only calls Screen.hide().
        gameScreen.dispose();
        cursorManager.dispose();
        assetManager.dispose();
    }

    public static void main(String[] args) {
        var config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("UniSim");

        // Limit frame rate to display refresh rate.
        config.useVsync(true);

        // Create an application using the LWJGL3 (desktop) backend.
        new Lwjgl3Application(new UniSimGame(), config);
    }
}
