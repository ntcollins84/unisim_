package io.github.uoyteamsix;

/**
 * NEW CLASS
 * A class representing an achievement
 */
public class Achievement {

    String name;
    String description;
    boolean isAchieved;
    GameTimer timer;
    int trackedVariable;
    boolean unachievable;

    public Achievement (String name, String description) {
        this.name = name;
        this.description = description;
        this.isAchieved = false;
    }

    public Achievement (String name, String description, int trackedVariable) {
        this.name = name;
        this.description = description;
        this.trackedVariable = trackedVariable;
        this.isAchieved = false;
    }

    /**
     * Tracks the required conditions for the achievement to be obtained
     * @param logic the GameLogic instance of the main game
     * @param deltaTime time since last render
     * @return whether an achievement has been obtained
     */
    public boolean achievementGet(GameLogic logic, float deltaTime) {
        return false;
    }

    /**
     * Updates achievement timer
     * @param deltaTime time since last render
     * @return whether timer has ended or not
     */
    public boolean runTimer(float deltaTime) {
        timer.updateTime(deltaTime);
        return timer.isTimeEnded();
    }
}
