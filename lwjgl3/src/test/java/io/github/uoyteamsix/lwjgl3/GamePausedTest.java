package io.github.uoyteamsix.lwjgl3;

import com.badlogic.gdx.ApplicationAdapter;
import io.github.uoyteamsix.GameScreen;
import io.github.uoyteamsix.GameTimer;
import io.github.uoyteamsix.UniSimGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GamePausedTest {
    GameTimer timer;

    @BeforeEach
    public void setUp() {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        //config.setTitle("Test"); // Optional window configuration
        //config.setWindowedMode(1, 1); // Minimal window size for testing

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
}
