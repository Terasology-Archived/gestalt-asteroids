package org.terasology.gestalt.example.asteroids.common.engine;

public class BasicTimeSubsystem implements TimeSubsystem {

    private final int maxFrameTime;
    private long lastTime;
    private int deltaMs = 1;
    private float delta = 0.001f;
    private Time timeControl = new TimeImpl();

    public BasicTimeSubsystem() {
        this(1500);
    }

    public BasicTimeSubsystem(int maxFrameTime) {
        this.maxFrameTime = maxFrameTime;
    }

    @Override
    public void initialise(Engine engine) {
        lastTime = getCurrentTime();
    }


    @Override
    public void tick() {
        long newTime = getCurrentTime();
        deltaMs = Math.min((int) (newTime - lastTime), maxFrameTime);
        delta = deltaMs / 1000f;
        lastTime = newTime;
    }

    @Override
    public Time getTime() {
        return timeControl;
    }

    private long getCurrentTime() {
        return System.nanoTime() / 1000000;
    }

    private class TimeImpl implements Time {


        @Override
        public float globalDelta() {
            return delta;
        }

        @Override
        public int globalDeltaMs() {
            return deltaMs;
        }

        @Override
        public float gameDelta() {
            return delta;
        }

        @Override
        public int gameDeltaMs() {
            return deltaMs;
        }

        @Override
        public boolean isPaused() {
            // TODO
            return false;
        }

        @Override
        public void togglePaused() {
            // TODO
        }

        // TODO: Time dialation
        // TODO: Current time
    }
}
