package io.github.uoyteamsix.lwjgl3;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import io.github.uoyteamsix.GameScreen;
import io.github.uoyteamsix.GameTimer;
import io.github.uoyteamsix.UniSimGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import java.util.concurrent.CountDownLatch;
import java.lang.Thread;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TimeTest {
    Lwjgl3Application app;
    GameTimer timer;

    private CountDownLatch latch;

    @BeforeEach
    public void setUp() throws InterruptedException {
        latch = new CountDownLatch(1);

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

        app = new Lwjgl3Application(new ApplicationAdapter() {
            @Override
            public void create() {
                UniSimGame game = new UniSimGame();
                game.create();
                GameScreen screen = game.getGameScreen();
                timer = screen.getGameTimer();

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

    /*
    public void TestTimeDepletes() throws InterruptedException {
        int start_time = (int) timer.getTimeLeft();
        Thread.sleep(1000);
        //TimeUnit.SECONDS.sleep(1);
        int current_time = (int) timer.getTimeLeft();
        assertEquals(start_time - 1, current_time, "Start time should decrease by 1 each second");
    }
     */

    @Test
    public void TestTimeStartsAt5Minutes() {
        assertEquals(timer.getTimeLeft(), 300, "The time should start at 5 minutes");
    }

    @Test
    public void TestTimeEndsAfter5Minutes() {
        timer.resumeTime();
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

    /*
    public void TestTimeDecreasesAfterGamePausedThenResumed() throws InterruptedException {
        timer.resumeTime();
        float start_time = timer.getTimeLeft();
        timer.pauseTime();
        timer.resumeTime();
        TimeUnit.SECONDS.sleep(1);
        assertEquals(start_time - 1, timer.getTimeLeft(), "When game is resumed, time should continue " +
                "to decrease");
    }
     */
}
