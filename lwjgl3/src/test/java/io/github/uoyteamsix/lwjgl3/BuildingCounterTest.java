package io.github.uoyteamsix.lwjgl3;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import io.github.uoyteamsix.GameLogic;
import io.github.uoyteamsix.GameScreen;
import io.github.uoyteamsix.UniSimGame;
import io.github.uoyteamsix.map.BuildingPrefab;
import io.github.uoyteamsix.map.GameMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BuildingCounterTest {

    GameMap map;
    List<BuildingPrefab> prefabs;
    Method getBuilding;
    private CountDownLatch latch;

    @BeforeEach
    public void setUp() throws InterruptedException {
        latch = new CountDownLatch(1);

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

                new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                        Gdx.app.exit();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }).start();

                latch.countDown();
            }
        }, config);

        latch.await();
    }

    @Test
    public void testAllCountersStartAtZero() {
        for (BuildingPrefab prefab : prefabs) {
            assertEquals(0, map.getBuildingCount(prefab), "Counter for " + prefab.getName() + " should start at 0.");
        }
    }

    @Test
    public void testPlaceBuildingIncreasesCorrectCounter() throws InvocationTargetException, IllegalAccessException {
        BuildingPrefab prefab = prefabs.get(0);
        map.placeBuilding(prefab, 20, 24);
        assertEquals(1, map.getBuildingCount(prefab), "Counter for " + prefab.getName() + " should increase by 1.");
    }

    @Test
    public void testPlaceBuildingDoesNotAffectOtherCounters() throws InvocationTargetException, IllegalAccessException {
        BuildingPrefab prefabPlaced = prefabs.get(0);
        BuildingPrefab prefabNotPlaced = prefabs.get(1);

        map.placeBuilding(prefabPlaced, 20, 24);

        assertEquals(1, map.getBuildingCount(prefabPlaced), "Counter for " + prefabPlaced.getName() + " should increase by 1.");
        assertEquals(0, map.getBuildingCount(prefabNotPlaced), "Counter for " + prefabNotPlaced.getName() + " should not change.");
    }

    @Test
    public void testPlaceMultipleBuildings() {
        BuildingPrefab prefab = prefabs.get(0);
        int numberOfBuildings = 5;

        for (int i = 0; i < numberOfBuildings; i++) {
            map.placeBuilding(prefab, 20 + i, 24);
        }

        assertEquals(numberOfBuildings, map.getBuildingCount(prefab), "Counter for " + prefab.getName() + " should be " + numberOfBuildings + ".");
    }
}
