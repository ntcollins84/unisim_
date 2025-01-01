package io.github.uoyteamsix.lwjgl3;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.math.MathUtils;
import io.github.uoyteamsix.GameLogic;
import io.github.uoyteamsix.GameScreen;
import io.github.uoyteamsix.UniSimGame;
import io.github.uoyteamsix.map.BuildingPrefab;
import io.github.uoyteamsix.map.GameMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SatisfactionTests {

    GameMap map;
    GameLogic logic;
    private CountDownLatch latch;
    List<BuildingPrefab> prefabs;

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
                logic = screen.getGameLogic();

                try {
                    var method = GameScreen.class.getDeclaredMethod("initializeMap");
                    method.setAccessible(true);
                    method.invoke(screen);
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }

                map = logic.getGameMap();

                prefabs = map.getAvailablePrefabs();

                logic.getGameTimer().resumeTime();

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
    public void testSatisfactionStartsAtZero() {
        assertEquals(0.0f, logic.getSatisfaction(), 0.0001f,
                "Satisfaction should start at 0.");
    }

    @Test
    public void testSatisfactionDecaysOverTime() {
        // Start at maximum satisfaction
        float initialSatisfaction = 1.0f;
        logic.setSatisfaction(1.0f);

        // Progress game 10 seconds
        logic.update(10.0f);

        // Expected decay calculations
        float decayRate = Math.max(0.035f - (0 / 500.0f), 0.015f); // Decay rate with no recreational buildings
        float expectedSatisfaction = MathUtils.clamp(initialSatisfaction - (decayRate * 10.0f), 0.0f, 1.0f);

        assertEquals(expectedSatisfaction, logic.getSatisfaction(), 0.0001f,
                "Satisfaction should decay over time.");
    }

    @Test
    public void testSatisfactionIsZeroWhenNoBuildingsPlaced() {
        // Progress full game
        logic.update(300.0f);

        assertEquals(0.0f, logic.getSatisfaction(),
                "Satisfaction should be zero if no buildings are placed");
    }

    @Test
    public void testSatisfactionIncreaseWhenBuildingPlaced() {
        logic.setSatisfaction(0.0f);

        // Place new building
        BuildingPrefab prefab = prefabs.get(0);
        map.placeBuilding(prefab, 20, 24);

        logic.update(1.0f); // Progress 1 second

        assertTrue(0.0f < logic.getSatisfaction(),
                "Satisfaction should increase after building placement");

    }


}
