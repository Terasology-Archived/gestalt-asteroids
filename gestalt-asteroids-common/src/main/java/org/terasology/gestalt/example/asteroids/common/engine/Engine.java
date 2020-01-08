package org.terasology.gestalt.example.asteroids.common.engine;

import org.terasology.gestalt.module.Module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Engine {

    private TimeSubsystem timeSubsystem;
    private ModuleSubsystem moduleSubsystem;
    private List<Subsystem> subsystems;
    private volatile boolean shutdown;

    public Engine(TimeSubsystem timeSubsystem, ModuleSubsystem moduleSubsystem, Subsystem ... otherSubsystems) {
        this(timeSubsystem, moduleSubsystem, Arrays.asList(otherSubsystems));
    }

    public Engine(TimeSubsystem timeSubsystem, ModuleSubsystem moduleSubsystem, Collection<Subsystem> otherSubsystems) {
        this.timeSubsystem = timeSubsystem;
        this.moduleSubsystem = moduleSubsystem;
        this.subsystems = new ArrayList<>();
        this.subsystems.add(timeSubsystem);
        this.subsystems.add(moduleSubsystem);
        this.subsystems.addAll(otherSubsystems);
    }

    public void run() {
        start();
        loop();
        shutdown();
    }

    public void exit() {
        this.shutdown = true;
    }

    public void switchEnvironment(Collection<Module> modules) {
        moduleSubsystem.changeEnvironment(modules);
        for (Subsystem subsystem : subsystems) {
            subsystem.onEnvironmentChanged(moduleSubsystem.getEnvironment());
        }
    }

    private void start() {
        for (Subsystem subsystem : subsystems) {
            subsystem.initialise();
        }
    }

    private void loop() {
        while (!shutdown) {
            for (Subsystem subsystem : subsystems) {
                subsystem.tick(timeSubsystem.deltaMs());
            }
        }
    }

    private void shutdown() {
        for (Subsystem subsystem : subsystems) {
            subsystem.close();
        }
    }


}
