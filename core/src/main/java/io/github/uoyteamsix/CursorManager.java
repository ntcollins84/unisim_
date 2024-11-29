package io.github.uoyteamsix;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Disposable;

import java.util.HashMap;
import java.util.Map;

/**
 * A class to manage all the cursors used in the game. It queues the loading of the bitmaps and automatically
 * creates the cursor objects when the bitmap has loaded.
 */
public class CursorManager implements Disposable {
    private final AssetManager assetManager;

    // A map of our cursor enum to GDX cursors.
    private final Map<GameCursor, Cursor> cursorMap;

    public CursorManager(AssetManager assetManager) {
        this.assetManager = assetManager;
        cursorMap = new HashMap<>();

        // Queue loading of all cursors.
        for (var cursor : GameCursor.values()) {
            assetManager.load(cursor.getPath(), Pixmap.class);
        }

        // Finish loading standard pointer straight away so it can be used as a fallback.
        assetManager.finishLoadingAsset(GameCursor.POINTER.getPath());

        // Call update to load pointer.
        update();
    }

    /**
     * Tries to set the current cursor to the given cursor. If the given cursor is not loaded, fallback to the
     * pointer cursor.
     *
     * @param cursor the desired cursor
     */
    public void setCursor(GameCursor cursor) {
        // Cursors.POINTER is guaranteed to be loaded. Use it as a fallback in case the requested cursor isn't loaded.
        var gdxCursor = cursorMap.getOrDefault(cursor, cursorMap.get(GameCursor.POINTER));
        Gdx.graphics.setCursor(gdxCursor);
    }

    /**
     * Creates cursor objects for any loaded pixmaps. Should be called every frame.
     */
    public void update() {
        // See if any pixmaps have been loaded.
        for (var cursor : GameCursor.values()) {
            if (!cursorMap.containsKey(cursor) && assetManager.isLoaded(cursor.getPath())) {
                var pixmap = assetManager.get(cursor.getPath(), Pixmap.class);
                cursorMap.put(cursor, Gdx.graphics.newCursor(pixmap, 0, 0));
            }
        }
    }

    @Override
    public void dispose() {
        for (var gdxCursor : cursorMap.values()) {
            gdxCursor.dispose();
        }
    }
}
