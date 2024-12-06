package io.github.uoyteamsix.lwjgl3;

import com.badlogic.gdx.ApplicationAdapter;
import io.github.uoyteamsix.GameScreen;
import io.github.uoyteamsix.GameTimer;
import io.github.uoyteamsix.UniSimGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TimeTest {
    GameTimer timer;

    @BeforeEach
    public void setUp() {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

        new Lwjgl3Application(new ApplicationAdapter() {
            @Override
            public void create() {
                UniSimGame game = new UniSimGame();
                game.create();
                GameScreen screen = game.getGameScreen();
                timer = screen.getGameTimer();
            }
        }, config);
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
        timer.updateTime(300);
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
