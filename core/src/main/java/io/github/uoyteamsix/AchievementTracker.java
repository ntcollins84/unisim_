package io.github.uoyteamsix;

import io.github.uoyteamsix.map.Building;
import io.github.uoyteamsix.map.BuildingPrefab;
import io.github.uoyteamsix.map.GameMap;

import java.util.List;

/**
 * NEW CLASS
 * A class to track achievements a player has obtained throughout the game
 */
public class AchievementTracker {

    // List of all achievements and their respective tracking methods
    private final Achievement[] ACHIEVEMENTS = {
        new Achievement("Campus Starter", "Place your first building") {
            @Override
            public boolean achievementGet(GameLogic logic, float deltaTime) {
                GameMap map = logic.getGameMap();

                // Has building been placed
                return map.getTotalBuildingCount() == 1;
            }
        },
        new Achievement("Master of Diversity", "Place at least one of each type of building") {
            @Override
            public boolean achievementGet(GameLogic logic, float deltaTime) {
                GameMap map = logic.getGameMap();

                // Get all building types
                List<BuildingPrefab> prefabs = map.getAvailablePrefabs();
                for (int i = 0; i < prefabs.size() - 1; i++) {
                    // If a type of building is yet to be placed then achievement not complete
                    if (map.getBuildingCount(prefabs.get(i)) < 1) {
                        return false;
                    }
                }
                return true;
            }
        },
        new Achievement("Quick Hands", "Place 3 buildings in 10 seconds", 0) {
            @Override
            public boolean achievementGet(GameLogic logic, float deltaTime) {
                GameMap map = logic.getGameMap();

                // If a building has been placed then start timer
                if (trackedVariable - map.getTotalBuildingCount() < 0 && timer == null) {
                    timer = new GameTimer(10, false);
                    runTimer(deltaTime);
                }
                // While timer running
                else if (timer != null) {
                    // Get amount of buildings placed during timer
                    int difference = map.getTotalBuildingCount() - trackedVariable;

                    // At end of timer
                    if (runTimer(deltaTime)) {
                        // Reset timer
                        timer = null;
                        // Set comparison variable to current building count
                        trackedVariable = map.getTotalBuildingCount();
                    }

                    // If 3 or more buildings placed within 10 seconds
                    return difference >= 3;
                }
                return false;
            }
        },
        new Achievement("Rising Architect", "Place 7 buildings without deleting any", 0) {
            @Override
            public boolean achievementGet(GameLogic logic, float deltaTime) {
                GameMap map = logic.getGameMap();

                // If building has already been deleted
                if (unachievable) {
                    return false;
                }
                // If building count decreases
                else if (trackedVariable - map.getTotalBuildingCount() > 0) {
                    // Building has been deleted, achievement cannot be obtained
                    unachievable = true;
                    return false;
                }
                // Update tracked variable
                trackedVariable = map.getTotalBuildingCount();

                // If 7 buildings placed without deleting any
                return trackedVariable == 7;
            }
        },
        new Achievement("Tryhard", "Score 100%") {
            @Override
            public boolean achievementGet(GameLogic logic, float deltaTime) {
                // Get timer and satisfaction
                GameTimer gameTimer = logic.getGameTimer();
                float satisfaction = logic.getSatisfaction();

                // If score at end of game is 100%
                return gameTimer.isTimeEnded() && Math.abs(1 - satisfaction) < 0.001;
            }
        },
        new Achievement("Consistency", "Keep satisfaction above 50% for 2 minutes") {
            @Override
            public boolean achievementGet(GameLogic logic, float deltaTime) {
                //
                float satisfaction = logic.getSatisfaction();

                if (satisfaction >= 0.5f) {
                    if (timer == null) {
                        timer = new GameTimer(120, false);
                        runTimer(deltaTime);
                    }
                    else if (runTimer(deltaTime)) {
                        return true;
                    }
                }
                else {
                    timer = null;
                }

                return false;
            }
        }
    };

    private final GameLogic logic;

    public AchievementTracker(GameLogic logic) {
        this.logic = logic;
    }

    /**
     * Updates achievement status and displays relevant popups
     * @param deltaTime time since last render
     */
    public void update(float deltaTime) {
        // Reset last achievement
        logic.setLastAchievement(null);
        for (Achievement achievement : ACHIEVEMENTS) {
            // Only check unobtained achievements
            if (!achievement.isAchieved) {
                // If achievement obtained
                if (achievement.achievementGet(logic, deltaTime)) {
                    achievement.isAchieved = true;
                    logic.setLastAchievement(achievement);
                }
            }
        }
    }
}
