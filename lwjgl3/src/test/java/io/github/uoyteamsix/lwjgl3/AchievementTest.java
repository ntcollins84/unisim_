package io.github.uoyteamsix.lwjgl3;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import io.github.uoyteamsix.*;
import io.github.uoyteamsix.map.BuildingPrefab;
import io.github.uoyteamsix.map.GameMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

public class AchievementTest {
    UniSimGame game;
    AchievementTracker achievements;
    GameScreen screen;
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
                game = new UniSimGame();
                game.create();
                screen = game.getGameScreen();
                achievements = screen.getGameLogic().getAchievementTracker();
                map = screen.getGameLogic().getGameMap();
                prefabs = map.getAvailablePrefabs();
                screen.getGameLogic().setMaximumAllowedBuildings(10); // Make sure the tests can place more than one building
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

    /**
     * Checks that the game starts off with none of the achievements achieved
     */
    @Test
    public void testNoAchievementsToStart(){
        for (int i = 0; i < 6; i++) {
            assertFalse(achievements.getAchivement(i).getIsAchieved(),
                    String.format("Achievement %d starts off achieved", i));
        }
    }

    /**
     * Places one building on the map, and then checks that the campus starter achievement is achieved
     */
    @Test
    public void TestCampusStarter(){
        BuildingPrefab prefab = prefabs.get(0);
        map.placeBuilding(prefab, 20, 40);
        Achievement campusStarter = achievements.getAchivement(0);
        assertTrue(campusStarter.getIsAchieved(), "Should have the campus starter achievement after placing one" +
                " building");
    }

    /**
     * Places one of each type of building, and then checks that the Master of Diversity achievement is achieved
     */
    @Test
    public void TestMasterOfDiversity(){
        for (int i = 0; i < 5; i++) {
            BuildingPrefab prefab = prefabs.get(i);
            map.placeBuilding(prefab, i * 5, 40);
        }
        Achievement masterOfDiversity = achievements.getAchivement(1);
        assertTrue(masterOfDiversity.getIsAchieved(), "Should have the master of diversity achievement after " +
                "placing one of each type of building");
    }

    /**
     * Waits 1 minute in order to collect three buildings, and then places them in quick succession in order to get the
     * Quick Hands achievement
     */
    @Test
    public void TestQuickHands(){
        BuildingPrefab prefab = prefabs.get(0);
        for (int i = 0; i < 3; i++) {
            map.placeBuilding(prefab, i * 5, 40);
        }
        Achievement quickHands = achievements.getAchivement(2);
        assertTrue(quickHands.getIsAchieved(), "Should have the quick hands achievement after 3 buildings in" +
                " under 10 seconds");
    }

    /**
     * Places seven buildings onto the map in order to get the Rising Architect Achievement
     */
    @Test
    public void TestRisingArchitect(){
        BuildingPrefab prefab = prefabs.get(0);
        for (int i = 0; i < 7; i++) {
            map.placeBuilding(prefab, i * 5, 40);
        }
        Achievement risingArchitect = achievements.getAchivement(3);
        assertTrue(risingArchitect.getIsAchieved(), "Should have the rising architect achievement after placing " +
                "7 buildings on the map");
    }

    /**
     * Ends the game with satisfaction at 100% in order to get the tryHard achievement
     */
    @Test
    public void TestTryHard(){
        GameLogic logic = screen.getGameLogic();
        logic.setSatisfaction(1.0f);
        GameTimer timer = screen.getGameTimer();
        timer.updateTime(300f);
        timer.isTimeEnded();
        Achievement tryHard = achievements.getAchivement(4);
        assertTrue(tryHard.getIsAchieved(), "Should have the try hard achievement if you end the game with 100%" +
                "satisfaction");
    }

    /**
     * Keeps satisfaction above 50% for 2 minutes in order to get the consistency achievement
     */
    @Test
    public void TestConsistency(){
        GameLogic logic = screen.getGameLogic();
        logic.setSatisfaction(1.0f);
        GameTimer timer = logic.getGameTimer();
        while (timer.getTimeLeft() > 170.0f){
            if (logic.getSatisfaction() <= 0.6f){
                logic.setSatisfaction(1.0f);
            }
        }
        Achievement consistency = achievements.getAchivement(5);
        assertTrue(consistency.getIsAchieved(), "Should have the consistency achievement when having satisfaction" +
                "above 50% for over 2 minutes");
    }
}
