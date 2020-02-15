package org.terasology.gestalt.example.asteroids.common.engine;

import org.terasology.gestalt.example.asteroids.common.engine.inject.EngineProvider;
import org.terasology.gestalt.module.Module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Engine {

    private final TimeSubsystem timeSubsystem;
    private final ModuleSubsystem moduleSubsystem;
    private final GameLogicSubsystem gameLogicSubsystem;
    private final AssetSubsystem assetSubsystem;
    private final List<Subsystem> subsystems;
    private volatile boolean shutdown;

    public Engine(TimeSubsystem timeSubsystem, ModuleSubsystem moduleSubsystem, AssetSubsystem assetSubsystem, GameLogicSubsystem gameLogicSubsystem, Subsystem... otherSubsystems) {
        this(timeSubsystem, moduleSubsystem, assetSubsystem, gameLogicSubsystem, Arrays.asList(otherSubsystems));
    }

    public Engine(TimeSubsystem timeSubsystem, ModuleSubsystem moduleSubsystem, AssetSubsystem assetSubsystem, GameLogicSubsystem gameLogicSubsystem, Collection<Subsystem> otherSubsystems) {
        this.timeSubsystem = timeSubsystem;
        this.moduleSubsystem = moduleSubsystem;
        this.assetSubsystem = assetSubsystem;
        this.gameLogicSubsystem = gameLogicSubsystem;
        this.subsystems = new ArrayList<>();
        this.subsystems.add(timeSubsystem);
        this.subsystems.add(moduleSubsystem);
        this.subsystems.add(assetSubsystem);
        this.subsystems.add(gameLogicSubsystem);
        this.subsystems.addAll(otherSubsystems);
    }

    public void run() {
        start();
        loop();
        terminate();
    }

    public void exit() {
        this.shutdown = true;
    }

    public void switchEnvironment(Collection<Module> modules) {
        moduleSubsystem.changeEnvironment(modules);
        for (Subsystem subsystem : subsystems) {
            subsystem.onEnvironmentChanged(moduleSubsystem.getEnvironment());
        }
        for (Subsystem subsystem : subsystems) {
            subsystem.onSystemsAvailable(gameLogicSubsystem.getSystems());
        }
        for (Subsystem subsystem : subsystems) {
            subsystem.onAssetsAvailable(assetSubsystem.getAssetManager());
        }
    }

    private void start() {
        for (Subsystem subsystem : subsystems) {
            subsystem.initialise(this);
        }
        for (Subsystem subsystem : subsystems) {
            subsystem.registerAssetTypes(assetSubsystem.getAssetTypeManager());
        }
        gameLogicSubsystem.addProvider(EngineProvider.class, new EngineProviderImpl());
        for (Subsystem subsystem : subsystems) {
            subsystem.registerProviders(gameLogicSubsystem);
        }
        switchEnvironment(Collections.emptyList());
    }

    private void loop() {
        while (!shutdown) {
            for (Subsystem subsystem : subsystems) {
                subsystem.tick();
            }
        }
    }

    private void terminate() {
        for (Subsystem subsystem : subsystems) {
            subsystem.close();
        }
    }

    public <T extends Subsystem> T getSubsystemOfType(Class<T> type) {
        for (Subsystem subsystem : subsystems) {
            if (type.isInstance(subsystem)) {
                return type.cast(subsystem);
            }
        }
        return null;
    }

    private class EngineProviderImpl implements EngineProvider {

        @Override
        public Time time() {
            return timeSubsystem.getTime();
        }
    }
}
