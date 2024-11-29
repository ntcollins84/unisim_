package io.github.uoyteamsix.headless;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import io.github.uoyteamsix.GameScreen;
import io.github.uoyteamsix.GameTimer;
import io.github.uoyteamsix.UniSimGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GamePauseTest {
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
    public void Test(){}
}
