package io.github.uoyteamsix.lwjgl3;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import io.github.uoyteamsix.GameCursor;
import io.github.uoyteamsix.UniSimGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AssetTests {
    UniSimGame game;

    private CountDownLatch latch;
    @BeforeEach
    public void setUp() throws InterruptedException {
        latch = new CountDownLatch(1);
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

        new Lwjgl3Application(new ApplicationAdapter() {
            @Override
            public void create() {
                game = new UniSimGame();
                game.create();

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
    public void testCursors() {
        assertTrue(Gdx.files.internal(GameCursor.POINTER.getPath()).exists(), "The asset for the pointer should" +
                " exist");
        assertTrue(Gdx.files.internal(GameCursor.HAND_OPEN.getPath()).exists(), "The asset for the open hand " +
                "should exist");
        assertTrue(Gdx.files.internal(GameCursor.ZOOM_IN.getPath()).exists(), "The asset for zoom in should " +
                "exist");
        assertTrue(Gdx.files.internal(GameCursor.ZOOM_OUT.getPath()).exists(), "The asset for the zoom out " +
                "should exist");
    }
}
