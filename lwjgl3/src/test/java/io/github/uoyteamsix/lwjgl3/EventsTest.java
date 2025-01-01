package io.github.uoyteamsix.lwjgl3;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.math.MathUtils;
import io.github.uoyteamsix.GameLogic;
import io.github.uoyteamsix.GameScreen;
import io.github.uoyteamsix.UniSimGame;
import io.github.uoyteamsix.GameEvent;
import io.github.uoyteamsix.map.GameMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EventsTest {

    GameMap map;
    GameLogic logic;
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
                logic = screen.getGameLogic();

                try {
                    var method = GameScreen.class.getDeclaredMethod("initializeMap");
                    method.setAccessible(true);
                    method.invoke(screen);
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }

                map = logic.getGameMap();

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
    public void testTriggerEvent() {
        GameEvent event;

        event = new GameEvent();

        float initialSatisfaction = logic.getSatisfaction();

        logic.setCurrentEvent(event); // Set event in the logic
        logic.update(1.0f); // Call update to apply event effect for 1 second

        // Calculate what satisfaction should be based on the intended logic
        float expectedSatisfaction = initialSatisfaction + (0.02f * event.getSatisfactionEffect());
        expectedSatisfaction -= Math.max(0.035f - (0 / 500.0f), 0.015f) * 1.0f; // decay
        expectedSatisfaction = MathUtils.clamp(expectedSatisfaction, 0.0f, 1.0f);

        float updatedSatisfaction = logic.getSatisfaction();
        assertEquals(expectedSatisfaction, updatedSatisfaction, 0.0001f,
                "Satisfaction should update based on the event's effect.");
    }


    @Test
    public void testEventsHappenDuringGame() {
        // Unpause the game timer to ensure game progression
        logic.getGameTimer().resumeTime();

        boolean eventTriggered = false;

        // Simulate game progression and check for events
        for (int i = 0; i < 300; i++) { // Simulate 300 seconds of game time
            logic.update(1.0f); // Advance by 1 second per iteration

            if (logic.getCurrentEvent() != null) {
                eventTriggered = true;
                break; // Exit the loop if an event is triggered
            }
        }

        assertTrue(eventTriggered, "An event should have been triggered during the game progression.");
    }
}
