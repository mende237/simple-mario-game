package com.game.mario.util;

// This class is used to manage the state of Mario and how long it should be valid for (in frames).
public class StateValidityFrame {
    private final MarioState state;
    private boolean value;
    private final int validForFrames;
    private int framesCounter;

    public StateValidityFrame(MarioState state, boolean value, int validForFrames) {
        this.state = state;
        this.value = value;
        this.validForFrames = validForFrames;
        this.framesCounter = 0;
    }

    public MarioState getState() {
        return state;
    }

    public boolean getValue() {
        return value;
    }

    public int getValidForFrames() {
        return validForFrames;
    }

    public void setValue(boolean value) {
        if (value) {
            this.framesCounter = 0; // Reset the counter when the state becomes valid
        }
        this.value = value;
    }

    public void update() {
        if (value) {
            if (framesCounter < validForFrames) {
                framesCounter++;
            } else {
                value = false; // Invalidate the state after the specified number of frames
                framesCounter = 0; // Reset the counter for the next time the state becomes valid
            }
        } else {
            framesCounter = 0; // Reset the counter if the state is not valid
        }
    }

    @Override
    public String toString() {
        return "StateValidityFrame{" +
                "state=" + state +
                ", value=" + value +
                ", validForFrames=" + validForFrames +
                ", framesCounter=" + framesCounter +
                '}';
    }
}
