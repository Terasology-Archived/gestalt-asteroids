package org.terasology.gestalt.example.asteroids.common.engine;

import org.terasology.gestalt.assets.module.ModuleAwareAssetTypeManager;
import org.terasology.gestalt.module.ModuleEnvironment;

public interface Subsystem {
    void initialise(Engine engine);

    void registerAssetTypes(ModuleAwareAssetTypeManager assetTypeManager);

    void tick(int delta);

    void close();

    void onEnvironmentChanged(ModuleEnvironment environment);
}
