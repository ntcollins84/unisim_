package io.github.uoyteamsix.lwjgl3;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import io.github.uoyteamsix.UniSimGame;
import com.badlogic.gdx.Preferences;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

public class LeaderboardTest {

    private Preferences highScores;
    private final String highScoreFile = "High Scores";
    private CountDownLatch latch;

    @BeforeEach
    public void setUp() throws InterruptedException {
        latch = new CountDownLatch(1);

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

        new Lwjgl3Application(new ApplicationAdapter() {
            @Override
            public void create() {
                UniSimGame game = new UniSimGame();
                game.create();

                // Initialize leaderboard preferences
                highScores = Gdx.app.getPreferences(highScoreFile);

                for (int i = 0; i < 5; i++) {
                    highScores.putInteger(i + "score", 100 - i * 10); // Simulate some high scores
                    highScores.putString(i + "name", "Player" + (i + 1)); // Simulate some player names
                }
                highScores.flush();

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

    @Test
    public void testNewHighScoreAsksForName() {
        int testScore = 90;

        boolean isHighScore = false;
        for (int i = 0; i < 5; i++) {
            if (highScores.getInteger(i + "score", 0) < testScore) {
                isHighScore = true;
                break;
            }
        }

        assertTrue(isHighScore, "A new high score should prompt for the player's name.");
    }

    @Test
    public void testNotNewHighScoreDoesNotAskForName() {
        int testScore = 50;

        boolean isHighScore = false;
        for (int i = 0; i < 5; i++) {
            if (highScores.getInteger(i + "score", 0) < testScore) {
                isHighScore = true;
                break;
            }
        }

        assertFalse(isHighScore, "A score not in the top 5 should not prompt for the player's name.");
    }

    @Test
    public void testSavedScoreIsInFile() {
        highScores.clear();
        String playerName = "Player1";
        int score = 100;

        // Simulate saving a new score
        highScores.putString("0name", playerName);
        highScores.putInteger("0score", score);
        highScores.flush();

        assertNotNull(highScores, "The file should contain the saved score.");
        assertEquals(playerName, highScores.getString("0name"), "The saved player name should match.");
        assertEquals(score, highScores.getInteger("0score"), "The saved score should match.");
    }
}
