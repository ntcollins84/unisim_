package io.github.uoyteamsix.headless;

import org.junit.jupiter.api.Test;

public class TimeTest {
    @BeforeEach
    public void setUp() {
        UniSimGame game = new UniSimGame();
        game.create();
        GameScreen screen = game.getGameScreen();
        timer = screen.getGameTimer();
    }

    @Test
    public void TestTimeDepletes() throws InterruptedException {
        int start_time = (int) timer.getTimeLeft();
        TimeUnit.SECONDS.sleep(1);
        int current_time = (int) timer.getTimeLeft();
        assertEquals(current_time, start_time -1, "Start time should decrease by 1 each second");
    }

    @Test
    public void TestTimeStartsAt5Minutes() {
        assertEquals(timer.getTimeLeft(), 300, "The time should start at 5 minutes");
    }

    @Test
    public void TestTimeEndsAfter5Minutes() {
        timer.decreaseTimeLeft(300);
        assertTrue(timer.isTimeEnded(), "The time should end after 5 minutes");
    }

    @Test
    public void TestTimeDoesNotDecreaseWhenGamePaused() throws InterruptedException {
        timer.resumeTime();
        float start_time = timer.getTimeLeft();
        timer.pauseTime();
        TimeUnit.SECONDS.sleep(1);
        assertEquals(start_time, timer.getTimeLeft(), "When paused time should not decrease");
    }

    @Test
    public void TestTimeDecreasesAfterGamePausedThenResumed() throws InterruptedException {
        timer.resumeTime();
        float start_time = timer.getTimeLeft();
        timer.pauseTime();
        timer.resumeTime();
        TimeUnit.SECONDS.sleep(1);
        assertEquals(start_time - 1, timer.getTimeLeft(), "When game is resumed, time should continue " +
                "to decrease");
    }
}
