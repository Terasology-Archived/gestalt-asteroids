package org.terasology.gestalt.example.asteroids.common.engine;

import org.terasology.gestalt.assets.management.AssetManager;
import org.terasology.gestalt.assets.module.ModuleAwareAssetTypeManager;
import org.terasology.gestalt.assets.module.ModuleAwareAssetTypeManagerImpl;
import org.terasology.gestalt.module.ModuleEnvironment;

public class AssetSubsystem implements Subsystem {

    private ModuleAwareAssetTypeManager assetTypeManager;
    private AssetManager assetManager;

    public ModuleAwareAssetTypeManager getAssetTypeManager() {
        return assetTypeManager;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    @Override
    public void initialise(Engine engine) {
        assetTypeManager = new ModuleAwareAssetTypeManagerImpl();
        assetManager = new AssetManager(assetTypeManager);
    }

    @Override
    public void onEnvironmentChanged(ModuleEnvironment environment) {
        assetTypeManager.switchEnvironment(environment);
    }
}
