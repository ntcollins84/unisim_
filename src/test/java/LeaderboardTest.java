import io.github.uoyteamsix.GameScreen;
import io.github.uoyteamsix.GameTimer;
import io.github.uoyteamsix.HighScoreScreen;
import io.github.uoyteamsix.UniSimGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LeaderboardTest {
    UniSimGame game;
    GameTimer timer;
    @BeforeEach
    public void setUp() {
        game = new UniSimGame();
        game.create();
        GameScreen gameScreen = game.getGameScreen();
        timer = gameScreen.getGameTimer();
    }

    @Test
    public void TestLeaderboardDisplayedAtEndOfGame() {
        timer.decreaseTimeLeft(300);
        assertEquals("HighScoreScreen", game.getScreen().getClass().getName());
    }
}
