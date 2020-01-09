package org.terasology.gestalt.example.asteroids.common.engine;

import org.terasology.gestalt.example.asteroids.common.core.GameThread;
import org.terasology.gestalt.module.Module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Engine {

    private TimeSubsystem timeSubsystem;
    private ModuleSubsystem moduleSubsystem;
    private AssetSubsystem assetSubsystem;
    private List<Subsystem> subsystems;
    private volatile boolean shutdown;

    public Engine(TimeSubsystem timeSubsystem, ModuleSubsystem moduleSubsystem, AssetSubsystem assetSubsystem, Subsystem ... otherSubsystems) {
        this(timeSubsystem, moduleSubsystem, assetSubsystem, Arrays.asList(otherSubsystems));
    }

    public Engine(TimeSubsystem timeSubsystem, ModuleSubsystem moduleSubsystem, AssetSubsystem assetSubsystem, Collection<Subsystem> otherSubsystems) {
        this.timeSubsystem = timeSubsystem;
        this.moduleSubsystem = moduleSubsystem;
        this.assetSubsystem = assetSubsystem;
        this.subsystems = new ArrayList<>();
        this.subsystems.add(timeSubsystem);
        this.subsystems.add(moduleSubsystem);
        this.subsystems.add(assetSubsystem);
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
        GameThread.setToCurrentThread();
        for (Subsystem subsystem : subsystems) {
            subsystem.initialise(this);
        }
        for (Subsystem subsystem : subsystems) {
            subsystem.registerAssetTypes(assetSubsystem.getAssetTypeManager());
        }
        switchEnvironment(Collections.emptyList());
    }

    private void loop() {
        while (!shutdown) {
            for (Subsystem subsystem : subsystems) {
                GameThread.processWaitingProcesses();
                subsystem.tick(timeSubsystem.deltaMs());
            }
        }
    }

    private void shutdown() {
        for (Subsystem subsystem : subsystems) {
            GameThread.processWaitingProcesses();
            subsystem.close();
        }
    }


}
