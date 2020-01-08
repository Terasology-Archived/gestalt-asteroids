package org.terasology.gestalt.example.asteroids.common.engine;

import org.terasology.gestalt.module.Module;
import org.terasology.gestalt.module.ModuleEnvironment;

import java.util.Collection;

public interface ModuleSubsystem extends Subsystem {

    ModuleEnvironment getEnvironment();

    void changeEnvironment(Collection<Module> modules);

    ModuleEnvironment loadEnvironment(Collection<Module> modules);
}
