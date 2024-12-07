package io.github.uoyteamsix.lwjgl3;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import io.github.uoyteamsix.GameLogic;
import io.github.uoyteamsix.GameScreen;
import io.github.uoyteamsix.UniSimGame;
import io.github.uoyteamsix.map.Building;
import io.github.uoyteamsix.map.BuildingPrefab;
import io.github.uoyteamsix.map.GameMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BuildingPlacementTest {
    final int x = 20;
    final int y = 24;
    GameMap map;
    List<BuildingPrefab> prefabs;
    Method getBuilding;

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
                prefabs = map.getAvailablePrefabs();
                try {
                    getBuilding = GameMap.class.getDeclaredMethod("getSelectedBuilding", int.class, int.class);
                    getBuilding.setAccessible(true);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        }, config);
    }

    @Test
    public void TestPlaceAccommodationBuilding() throws InvocationTargetException, IllegalAccessException {
        BuildingPrefab prefab = prefabs.get(0);
        map.placeBuilding(prefab, x, y);
        Building placedBuilding = (Building) getBuilding.invoke(map, x, y);
        assertEquals(prefab, placedBuilding.getPrefab(), "Building placed should be an accommodation building");
    }

    @Test
    public void TestPlaceCanteenBuilding() throws InvocationTargetException, IllegalAccessException {
        BuildingPrefab prefab = prefabs.get(1);
        map.placeBuilding(prefab, x, y);
        Building placedBuilding = (Building) getBuilding.invoke(map, x, y);
        assertEquals(prefab, placedBuilding.getPrefab(), "Building placed should be a canteen building");
    }

    @Test
    public void TestPlaceStudyBuilding() throws InvocationTargetException, IllegalAccessException {
        BuildingPrefab prefab = prefabs.get(2);
        map.placeBuilding(prefab, x, y);
        Building placedBuilding = (Building) getBuilding.invoke(map, x, y);
        assertEquals(prefab, placedBuilding.getPrefab(), "Building placed should be a study building");
    }

    @Test
    public void TestPlaceRecreationBuilding1() throws InvocationTargetException, IllegalAccessException {
        BuildingPrefab prefab = prefabs.get(3);
        map.placeBuilding(prefab, x, y);
        Building placedBuilding = (Building) getBuilding.invoke(map, x, y);
        assertEquals(prefab, placedBuilding.getPrefab(), "Building placed should be the first recreation building");
    }

    @Test
    public void TestPlaceRecreationBuilding2() throws InvocationTargetException, IllegalAccessException {
        BuildingPrefab prefab = prefabs.get(4);
        map.placeBuilding(prefab, x, y);
        Building placedBuilding = (Building) getBuilding.invoke(map, x, y);
        assertEquals(prefab, placedBuilding.getPrefab(), "Building placed should be the second recreation building");
    }
}