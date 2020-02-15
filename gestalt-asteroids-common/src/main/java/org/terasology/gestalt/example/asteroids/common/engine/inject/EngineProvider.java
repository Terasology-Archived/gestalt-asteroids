package org.terasology.gestalt.example.asteroids.common.engine.inject;

import org.terasology.gestalt.dependencyinjection.DoNotGenerate;
import org.terasology.gestalt.dependencyinjection.GameLogicProvider;
import org.terasology.gestalt.example.asteroids.common.engine.Time;

@GameLogicProvider
@DoNotGenerate
public interface EngineProvider {
    Time time();
}
