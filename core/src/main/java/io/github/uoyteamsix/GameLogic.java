package io.github.uoyteamsix;

import com.badlogic.gdx.math.MathUtils;
import io.github.uoyteamsix.map.BuildingPrefab;
import io.github.uoyteamsix.map.GameMap;

/**
 * A class which manages the gameplay logic, including the remaining game time, placing buildings, and calculating
 * satisfaction and score.
 */
public class GameLogic {
    //private static final float TOTAL_GAME_TIME = 5.0f * 60.0f;
    private static final float BUILDING_TIME = 20.0f;

    private GameMap gameMap;
    private int maximumAllowedBuildings;
    private int selectedPrefabIndex = -1;

    // Timers.
    //private float remainingTime;
    private float nextBuildingTime;
    //private boolean gameOver;
    private final GameTimer gameTimer;

    // Satisfaction.
    private float satisfaction;
    private float newBuildingSatisfaction;
    private int previousBuildingCount;

    // Events.
    private GameEvent currentEvent;
    private float nextEventProbability;
    private float checkEventTimer;
    private float eventDurationTimer;

    public GameLogic(GameTimer gameTimer) {
        this.gameTimer = gameTimer;
        nextBuildingTime = 0.0f;
        currentEvent = null;
    }

    public void setMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    /**
     * Finds a building prefab given its name.
     *
     * @param name the prefab name
     * @return a {@link BuildingPrefab}
     */
    private BuildingPrefab findPrefab(String name) {
        for (var prefab : gameMap.getAvailablePrefabs()) {
            if (prefab.getName().equals(name)) {
                return prefab;
            }
        }
        return null;
    }

    /**
     * Continuously updates the student satisfaction.
     *
     * @param deltaTime the time between the last call of this method
     */
    private void updateSatisfaction(float deltaTime) {
        // Get all building types.
        var accommodationPrefab = findPrefab("Accommodation");
        var canteenPrefab = findPrefab("Canteen");
        var recreationPrefab = findPrefab("Recreation");
        var recreation2Prefab = findPrefab("Recreation2");
        var studyPrefab = findPrefab("Study");

        // Work out the number of students based on how many accommodation buildings there are.
        int studentCount = gameMap.getBuildingCount(accommodationPrefab) * 25;

        // Store satisfaction to add for new buildings.
        int newBuildingCount = gameMap.getTotalBuildingCount() - previousBuildingCount;
        if (newBuildingCount > 0) {
            newBuildingSatisfaction += 0.5f * newBuildingCount;
        }
        previousBuildingCount = gameMap.getTotalBuildingCount();

        // Slowly apply new building satisfaction.
        float newBuildingFactor = newBuildingSatisfaction * 2.0f * deltaTime;
        satisfaction += newBuildingFactor;
        newBuildingSatisfaction -= newBuildingFactor;
        newBuildingSatisfaction = Math.max(newBuildingSatisfaction, 0.0f);

        // Apply some satisfaction based on student count.
        satisfaction += Math.min(studentCount / 25000.0f, 0.01f) * deltaTime;

        // Decrease satisfaction if there isn't enough canteen or study buildings for all the students. Each canteen
        // can support 100 students and each study building can support 75 students. Use exponential formulas so a
        // deficit can not just be offset by placing lots of recreation buildings.
        var canteenDeficit = studentCount - gameMap.getBuildingCount(canteenPrefab) * 100;
        var studyDeficit = studentCount - gameMap.getBuildingCount(studyPrefab) * 75;
        if (canteenDeficit > 0) {
            satisfaction -= ((float) Math.pow(2.0f, canteenDeficit / 12.0f) / 175.0f) * deltaTime * 0.5f;
        }
        if (studyDeficit > 0) {
            float factor = currentEvent != null && currentEvent.affectsStudy() ? 1.0f : 0.5f;
            satisfaction -= ((float) Math.pow(2.0f, studyDeficit / 15.0f) / 75.0f) * deltaTime * factor;
        }

        // Decay satisfaction based on a rate determined by the amount of recreation buildings.
        // Calculation now accounts for second recreation building type
        float decayRate = 0.035f;
        decayRate -= (gameMap.getBuildingCount(recreationPrefab) + gameMap.getBuildingCount(recreation2Prefab)) / 500.0f;
        satisfaction -= Math.max(decayRate, 0.015f) * deltaTime;

        // Handle event effects
        if (currentEvent != null) {
            satisfaction += 0.02f * currentEvent.getSatisfactionEffect() * deltaTime;
        }

        // Clamp satisfaction between 0 and 1.
        satisfaction = MathUtils.clamp(satisfaction, 0.0f, 1.0f);
    }

    /**
     * Updates the game logic.
     *
     * @param deltaTime the delta time between the last call of update
     */
    public void update(float deltaTime) {
        if (gameTimer.isTimeEnded()) {
            return;
        }

        /*
        // Update timers.
        remainingTime -= deltaTime;
        if (remainingTime < 0.0f) {
            gameOver = true;
        }
        */

        // Other ingame timers now rely on GameTimer
        if (!gameTimer.isPaused()) {
            nextBuildingTime -= deltaTime;
            if (nextBuildingTime < 0.0f) {
                // User can place another building.
                maximumAllowedBuildings++;
                nextBuildingTime = BUILDING_TIME;
            }

            // Update satisfaction.
            updateSatisfaction(deltaTime);

            // Update event timer
            if (currentEvent != null) {
                currentEvent.timer.updateTime(deltaTime);
                // Check if event has ended
                if (currentEvent.timer.isTimeEnded()) {
                    currentEvent = null;
                }
            }
            // If no event active
            else {
                // Generate a random number every 2 seconds to see if we should start an event. Bias the random number slightly
                // to prevent events from happening to close to each other.
                nextEventProbability += deltaTime * 0.01f;
                checkEventTimer += deltaTime;
                if (checkEventTimer > 2.0f) {
                    checkEventTimer = 0.0f;
                    if (Math.min(MathUtils.random() + 0.1f, 1.0f) < nextEventProbability) {
                        nextEventProbability = 0;
                        currentEvent = new GameEvent();
                    }
                };
            }
        }
    }

    /**
     * Sets the selected building placement prefab to the given index.
     *
     * @param prefabIndex the prefab index
     */
    public void setSelectedPrefabIndex(int prefabIndex) {
        if (gameMap != null && prefabIndex < gameMap.getAvailablePrefabs().size()) {
            selectedPrefabIndex = prefabIndex;
        } else {
            selectedPrefabIndex = -1;
        }
    }

    /**
     * @return true if the player is allowed to place another building
     */
    public boolean canPlaceBuilding() {
        return !gameTimer.isTimeEnded() && gameMap.getTotalBuildingCount() < maximumAllowedBuildings;
    }

    /**
     * @return the selected prefab if there is one, otherwise null
     */
    public BuildingPrefab getSelectedPrefab() {
        if (gameMap == null || selectedPrefabIndex < 0) {
            return null;
        }
        return gameMap.getAvailablePrefabs().get(selectedPrefabIndex);
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public int getSelectedPrefabIndex() {
        return selectedPrefabIndex;
    }

    /*public float getRemainingTime() { return remainingTime; }*/

    public float getNextBuildingTime() { return nextBuildingTime; }

    public float getSatisfaction() { return satisfaction; }

    public GameEvent getCurrentEvent() { return currentEvent; }

    public float getEventDurationTimer() { return eventDurationTimer;}
}
