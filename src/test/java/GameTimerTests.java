import io.github.uoyteamsix.GameScreen;
import io.github.uoyteamsix.GameTimer;
import io.github.uoyteamsix.UniSimGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class GameTimerTests {
    GameTimer timer;
    @BeforeEach
    public void setUp() {
        UniSimGame game = new UniSimGame();
        game.create();
        GameScreen screen = game.getGameScreen();
        timer = screen.getGameTimer();
    }

    @Test
    public void TestStartPaused(){
        assertTrue(timer.isPaused(), "The game should start paused");
    }

    @Test
    public void TestPauseAndResume(){
        timer.resumeTime();
        assertFalse(timer.isPaused(), "The resumeTime method should unpause the game");
        timer.resumeTime();
        assertFalse(timer.isPaused(), "Reusing the resume method should not pause the game");
        timer.pauseTime();
        assertTrue(timer.isPaused(), "The pauseTime method should pause the game");
        timer.pauseTime();
        assertTrue(timer.isPaused(), "Reusing the pause method should not resume the game");
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