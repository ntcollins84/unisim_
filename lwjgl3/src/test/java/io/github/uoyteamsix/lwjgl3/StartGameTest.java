package io.github.uoyteamsix.lwjgl3;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import io.github.uoyteamsix.CursorManager;
import io.github.uoyteamsix.GameScreen;
import io.github.uoyteamsix.UniSimGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StartGameTest {
    UniSimGame game;

    @BeforeEach
    public void setUp() {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

        new Lwjgl3Application(new ApplicationAdapter() {
            @Override
            public void create() {
                game = new UniSimGame();
                game.create();
            }
        }, config);
    }

    @Test
    public void TestVariablesSet(){
        assertNotNull(game.getAssetManager(), "Game should have an asset manager");
        assertNotNull(game.getGameScreen(), "Game should have a game screen");
        assertNotNull(game.getCursorManager(), "Game should have a cursor manager");
    }

    @Test
    public void TestGameStartsOnGameScreen(){
        GameScreen gameScreen = game.getGameScreen();
        assertEquals(game.getScreen(), gameScreen, "Game should start on Game Screen");
    }

    @Test
    public void TestGameScreenSetUpCorrectly(){
        GameScreen gameScreen = game.getGameScreen();
        assertEquals(game, gameScreen.getGame(), "Game Screen should assign UniSimGame to it's game");
        AssetManager assetManager = game.getAssetManager();
        assertEquals(assetManager, gameScreen.getAssetManager(), "Game Screen should have the same asset " +
                "manager as the game");
        CursorManager cursorManager = game.getCursorManager();
        assertEquals(cursorManager, gameScreen.getCursorManager(), "Game Screen should have the same cursor " +
                "manager as the game");
        assertNotNull(gameScreen.getSpriteBatch(), "Game Screen should have a SpriteBatch");
        assertNotNull(gameScreen.getCameraController(), "Game screen should have a Camera Controller");
        assertNotNull(gameScreen.getGameTimer(), "Game Screen should have a GameTimer");
        assertNotNull(gameScreen.getGameLogic(), "Game Screen should have a GameLogic");
        assertNotNull(gameScreen.getUiStage(), "Game Screen should have a UiStage");

    }
}

