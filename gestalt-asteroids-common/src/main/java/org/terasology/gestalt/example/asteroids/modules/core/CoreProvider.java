package org.terasology.gestalt.example.asteroids.modules.core;

import org.terasology.gestalt.dependencyinjection.GameLogicProvider;
import org.terasology.gestalt.example.asteroids.common.engine.inject.EngineProvider;
import org.terasology.gestalt.example.asteroids.modules.core.logic.DriftSystem;

@GameLogicProvider(dependsOn = EngineProvider.class)
public interface CoreProvider {
    DriftSystem driftSystem();
}
