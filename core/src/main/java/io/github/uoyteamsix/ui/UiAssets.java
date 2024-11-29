package io.github.uoyteamsix.ui;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

/**
 * A class which handles the asynchronous loading of the UI assets.
 */
public class UiAssets {
    private final AssetManager assetManager;
    private final AssetDescriptor<FreeTypeFontGenerator> fontDescriptor;
    private final AssetDescriptor<Texture> spritesheetDescriptor;
    private BitmapFont largeFont;
    private BitmapFont smallFont;
    private Texture spritesheet;

    public UiAssets(AssetManager assetManager) {
        this.assetManager = assetManager;

        // Queue the font loading.
        fontDescriptor = new AssetDescriptor<>("ui/font.ttf", FreeTypeFontGenerator.class);
        assetManager.load(fontDescriptor);

        // Queue the spritesheet loading.
        spritesheetDescriptor = new AssetDescriptor<>("ui/spritesheet.png", Texture.class);
        assetManager.load(spritesheetDescriptor);
    }

    /**
     * Creates a bitmap font from a given freetype font and size.
     *
     * @param generator the freetype font
     * @param size      the desired size
     * @return a {@link BitmapFont}
     */
    private BitmapFont createFont(FreeTypeFontGenerator generator, int size) {
        var parameters = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameters.size = size;
        return generator.generateFont(parameters);
    }

    /**
     * Polls and stores and newly loaded assets.
     */
    public void update() {
        // Check if the font file has been loaded.
        if (largeFont == null && assetManager.isLoaded(fontDescriptor)) {
            // Generate bitmap fonts from the truetype font.
            var generator = assetManager.get(fontDescriptor);
            largeFont = createFont(generator, 72);
            smallFont = createFont(generator, 32);
        }

        // Check if the spritesheet has been loaded.
        if (spritesheet == null && assetManager.isLoaded(spritesheetDescriptor)) {
            spritesheet = assetManager.get(spritesheetDescriptor);
        }
    }

    public boolean hasFontsLoaded() {
        return largeFont != null;
    }

    public boolean hasSpritesheetLoaded() {
        return spritesheet != null;
    }

    public BitmapFont getLargeFont() {
        return largeFont;
    }

    public BitmapFont getSmallFont() {
        return smallFont;
    }

    public Texture getSpritesheet() {
        return spritesheet;
    }
}
