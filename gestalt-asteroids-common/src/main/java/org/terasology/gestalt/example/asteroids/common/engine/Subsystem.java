package org.terasology.gestalt.example.asteroids.common.engine;

import org.terasology.gestalt.assets.management.AssetManager;
import org.terasology.gestalt.assets.module.ModuleAwareAssetTypeManager;
import org.terasology.gestalt.module.ModuleEnvironment;

import java.util.Set;

public interface Subsystem {
    default void initialise(Engine engine) {
    }

    default void registerAssetTypes(ModuleAwareAssetTypeManager assetTypeManager) {
    }

    default void registerProviders(GameLogicSubsystem gameLogicSubsystem) {
    }

    default void onSystemsAvailable(Set<Object> systems) {

    }

    default void tick() {
    }

    default void close() {
    }

    default void onEnvironmentChanged(ModuleEnvironment newEnvironment) {
    }

    default void onAssetsAvailable(AssetManager assetManager) {

    }
}
