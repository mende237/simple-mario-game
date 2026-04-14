package com.game.mario.util;

import java.util.concurrent.locks.ReentrantLock;

// This class is used to manage the state of Mario and how long it should be valid for (in frames).
public class StateValidityFrame {
    private final MarioState state;
    private boolean value;
    private final int validForFrames;
    private int framesCounter;
    private ReentrantLock statelocker;
    private boolean read;

    public StateValidityFrame(MarioState state, boolean value, int validForFrames) {
        this.state = state;
        this.value = value;
        this.validForFrames = validForFrames;
        this.framesCounter = 0;
        this.statelocker = new ReentrantLock();
        this.read = false;
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

    public ReentrantLock getStateLocker() {
        return this.statelocker;
    }

    public void setValue(boolean value) {
        this.statelocker.lock();
        if (this.read && value) {
            this.value = value;
            this.read = false;
            this.framesCounter = 0; // Reset the counter when the state becomes valid
        }
        this.statelocker.unlock();
    }

    public void setRead() {
        this.statelocker.lock();
        this.read = true;
        this.statelocker.unlock();
    }

    public void update() {
        this.statelocker.lock();
        if (value) {
            if (framesCounter < validForFrames) {
                framesCounter++;
            } else if (this.read) {
                value = false; // Invalidate the state after the specified number of frames
                framesCounter = 0; // Reset the counter for the next time the state becomes valid
            }
        } else {
            framesCounter = 0; // Reset the counter if the state is not valid
        }
        this.statelocker.unlock();
    }

    @Override
    public String toString() {
        return "StateValidityFrame{" +
                "state=" + state +
                ", value=" + value +
                ", validForFrames=" + validForFrames +
                ", framesCounter=" + framesCounter +
                ", read=" + read +
                '}';
    }
}
