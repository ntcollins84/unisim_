package io.github.uoyteamsix;

/**
 * A class to track the time remaining in the game or in an event
 * Replaces timer variable in GameLogic
 */
public class GameTimer {

    private float timeLeft;
    private boolean isPaused;

    public GameTimer (float timeLeft, boolean startsPaused) {
        this.timeLeft = timeLeft;
        // Main game starts paused, events do not
        this.isPaused = startsPaused;
    }

    /**
     * Runs down timer as game progresses
     * @param deltaTime time since game last rendered
     */
    public void updateTime(float deltaTime) {
        if (!isPaused && timeLeft > 0) {
            timeLeft -= deltaTime;
        }
        if (timeLeft <= 0) {
            isPaused = true;
        }
    }

    public void pauseTime() { isPaused = true; }

    public void resumeTime() { isPaused = false; }

    /**
     *
     * @return whether game is paused or not
     */
    public boolean isPaused() { return isPaused; }

    /**
     *
     * @return whether game has ended
     */
    public boolean isTimeEnded() {
        return timeLeft <= 0;
    }

    /**
     *
     * @return amount of time left
     */
    public float getTimeLeft() { return timeLeft; }
}
