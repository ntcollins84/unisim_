package io.github.uoyteamsix.lwjgl3;

import com.badlogic.gdx.ApplicationAdapter;
import io.github.uoyteamsix.GameLogic;
import io.github.uoyteamsix.GameScreen;
import io.github.uoyteamsix.UniSimGame;
import io.github.uoyteamsix.map.BuildingPrefab;
import io.github.uoyteamsix.map.GameMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class BuildingRestrictionsTest {
    GameMap map;
    BuildingPrefab prefabRegular;
    BuildingPrefab prefabLarge;

    @BeforeEach
    public void setUp() {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

        new Lwjgl3Application(new ApplicationAdapter() {
            @Override
            public void create() {
                UniSimGame game = new UniSimGame();
                game.create();
                GameScreen screen = game.getGameScreen();
                GameLogic logic = screen.getGameLogic();
                try {
                    Method method = GameScreen.class.getDeclaredMethod("initializeMap");
                    method.setAccessible(true);
                    method.invoke(screen);
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                map = logic.getGameMap();
                List<BuildingPrefab> prefabs = map.getAvailablePrefabs();
                prefabRegular = prefabs.get(0);
                prefabLarge = prefabs.get(3);
            }
        }, config);
    }

    @Test
    public void TestBuildingPlacedOnBuilding() {
        map.placeBuilding(prefabRegular, 19, 14);
        for (int x = 19; x < 19 + prefabRegular.getWidth(); x++) {
            for (int y = 14; y < 14 + prefabRegular.getHeight(); y++) {
                assertFalse(map.canPlaceBuilding(prefabRegular, x, y), "Buildings should not overlap");
                assertFalse(map.canPlaceBuilding(prefabLarge, x, y), "Buildings should not overlap");
            }
        }
    }

    @Test
    public void TestBuildingPlacedOutOfBounds() {
        assertFalse(map.canPlaceBuilding(prefabRegular, -1, -1), "Buildings cannot be placed out of bounds");
        assertFalse(map.canPlaceBuilding(prefabLarge, -1, -1), "Buildings cannot be placed out of bounds");
    }

    @Test
    public void TestBuildingPlacedOnMapFeature() {
        assertFalse(map.canPlaceBuilding(prefabRegular, 1, 1), "Buildings cannot be placed on trees");
        assertFalse(map.canPlaceBuilding(prefabLarge, 1, 1), "Buildings cannot be placed on trees");
        assertFalse(map.canPlaceBuilding(prefabRegular, 23, 8), "Buildings cannot be placed on trees");
        assertFalse(map.canPlaceBuilding(prefabLarge, 23, 8), "Buildings cannot be placed on trees");
        assertFalse(map.canPlaceBuilding(prefabRegular, 45, 12), "Buildings cannot be placed on water");
        assertFalse(map.canPlaceBuilding(prefabLarge, 45, 12), "Buildings cannot be placed on water");

    }
}
