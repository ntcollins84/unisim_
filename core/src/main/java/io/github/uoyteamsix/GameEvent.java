package io.github.uoyteamsix;

import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A class representing an ingame event
 * Updated from the enum GameEvent
 */
public class GameEvent {

    // List of possible events, can be added to as needed
    private final ArrayList[] EVENTS = {
            new ArrayList<Object>(Arrays.asList("Rain", -1, false)),
            new ArrayList<Object>(Arrays.asList("Roses", 1, false)),
            new ArrayList<Object>(Arrays.asList("Strike", 0, true)),
    };

    private String name;
    private int satisfactionEffect;
    private boolean affectsStudy;
    public GameTimer timer;

    public GameEvent() {
        // Select random event
        List<Object> eventDetails = EVENTS[MathUtils.random(EVENTS.length - 1)];
        // Set event variables as needed
        name = (String) eventDetails.get(0);
        satisfactionEffect = (int) eventDetails.get(1);
        affectsStudy = (boolean) eventDetails.get(2);
        // Start timer (random duration, 15 - 45 seconds)
        timer = new GameTimer(MathUtils.random(15.0f, 45.0f), false);
    }

    public String getName() {
        return name;
    }

    public int getSatisfactionEffect() {
        return satisfactionEffect;
    }

    public boolean affectsStudy() {
        return affectsStudy;
    }

    /*NONE,
    RAIN,
    ROSES,
    STRIKE,*/
}