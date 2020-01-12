package org.terasology.gestalt.example.asteroids.common.engine;

import org.terasology.gestalt.assets.management.AssetManager;
import org.terasology.gestalt.assets.module.ModuleAwareAssetTypeManager;
import org.terasology.gestalt.module.ModuleEnvironment;

public interface Subsystem {
    default void initialise(Engine engine) {
    }

    default void registerAssetTypes(ModuleAwareAssetTypeManager assetTypeManager) {
    }

    default void tick(int delta) {
    }

    default void close() {
    }

    default void onEnvironmentChanged(ModuleEnvironment newEnvironment) {
    }

    default void onAssetsAvailable(AssetManager assetManager) {

    }
}
