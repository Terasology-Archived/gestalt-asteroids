package org.terasology.gestalt.example.asteroids.common.engine;

import org.terasology.gestalt.assets.module.ModuleAwareAssetTypeManager;
import org.terasology.gestalt.module.ModuleEnvironment;

public class BasicTimeSubsystem implements TimeSubsystem {

    private final int maxFrameTime;
    private long lastTime;
    private int delta = 1;

    public BasicTimeSubsystem() {
        this(1500);
    }

    public BasicTimeSubsystem(int maxFrameTime) {
        this.maxFrameTime = maxFrameTime;
    }

    @Override
    public void initialise(Engine engine) {
       lastTime = getTime();
    }

    @Override
    public void registerAssetTypes(ModuleAwareAssetTypeManager assetTypeManager) {
    }

    @Override
    public void tick(int lastDelta) {
        long newTime = getTime();
        delta = Math.min((int) (newTime - lastTime), maxFrameTime);
        lastTime = newTime;
    }

    @Override
    public void close() {

    }

    @Override
    public void onEnvironmentChanged(ModuleEnvironment environment) {
    }

    @Override
    public int deltaMs() {
        return delta;
    }

    @Override
    public float delta() {
        return 1.0f / delta;
    }

    private long getTime() {
        return System.nanoTime() / 1000000;
    }




}
