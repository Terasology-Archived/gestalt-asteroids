package org.terasology.gestalt.example.asteroids.common.engine;

import org.terasology.gestalt.module.ModuleEnvironment;

public interface Subsystem {
    void initialise();

    void tick(int delta);

    void close();

    void onEnvironmentChanged(ModuleEnvironment environment);
}
