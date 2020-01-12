package org.terasology.gestalt.example.asteroids.common.engine.entitysystem;

import com.google.gson.GsonBuilder;

import org.terasology.gestalt.assets.AssetType;
import org.terasology.gestalt.assets.ResolutionStrategy;
import org.terasology.gestalt.assets.format.producer.AssetFileDataProducer;
import org.terasology.gestalt.assets.format.producer.ModuleDependencyProvider;
import org.terasology.gestalt.assets.management.AssetManager;
import org.terasology.gestalt.assets.module.ModuleAwareAssetTypeManager;
import org.terasology.gestalt.assets.module.ModuleDependencyResolutionStrategy;
import org.terasology.gestalt.assets.module.ModuleEnvironmentDependencyProvider;
import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.gestalt.entitysystem.component.management.ComponentManager;
import org.terasology.gestalt.entitysystem.component.management.ComponentType;
import org.terasology.gestalt.entitysystem.component.management.ComponentTypeIndex;
import org.terasology.gestalt.entitysystem.component.store.ArrayComponentStore;
import org.terasology.gestalt.entitysystem.component.store.ComponentStore;
import org.terasology.gestalt.entitysystem.component.store.ConcurrentComponentStore;
import org.terasology.gestalt.entitysystem.component.store.SparseComponentStore;
import org.terasology.gestalt.entitysystem.entity.EntityManager;
import org.terasology.gestalt.entitysystem.entity.manager.CoreEntityManager;
import org.terasology.gestalt.entitysystem.prefab.GeneratedFromRecipeComponent;
import org.terasology.gestalt.entitysystem.prefab.Prefab;
import org.terasology.gestalt.entitysystem.prefab.PrefabData;
import org.terasology.gestalt.entitysystem.prefab.PrefabJsonFormat;
import org.terasology.gestalt.example.asteroids.common.engine.AssetSubsystem;
import org.terasology.gestalt.example.asteroids.common.engine.Engine;
import org.terasology.gestalt.example.asteroids.common.engine.Subsystem;
import org.terasology.gestalt.example.asteroids.common.engine.entitysystem.HighUsage;
import org.terasology.gestalt.example.asteroids.common.json.AssetTypeAdapterFactory;
import org.terasology.gestalt.module.ModuleEnvironment;

import java.util.ArrayList;
import java.util.List;

public class EntitySubsystem implements Subsystem {

    private AssetSubsystem assetSubsystem;

    private GsonBuilder gsonBuilder = new GsonBuilder();
    private CoreEntityManager entityManager;
    private ModuleAwareAssetTypeManager moduleAwareAssetTypeManager;
    private AssetType<Prefab, PrefabData> prefabAssetType;
    private PrefabJsonFormat prefabJsonFormat;
    private ComponentTypeIndex componentTypeIndex;
    private ComponentManager componentManager;
    private ModuleEnvironmentDependencyProvider resolutionStrategyProvider = new ModuleEnvironmentDependencyProvider();


    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public void initialise(Engine engine) {
        this.assetSubsystem = engine.getSubsystemOfType(AssetSubsystem.class);
        this.componentTypeIndex = new ComponentTypeIndex(new ModuleDependencyResolutionStrategy(resolutionStrategyProvider));
        this.componentManager = new ComponentManager();

    }

    @Override
    public void registerAssetTypes(ModuleAwareAssetTypeManager assetTypeManager) {
        moduleAwareAssetTypeManager = assetTypeManager;
        prefabAssetType = assetTypeManager.createAssetType(Prefab.class, Prefab::new, "prefabs");
        AssetFileDataProducer<PrefabData> assetFileDataProducer = moduleAwareAssetTypeManager.getAssetFileDataProducer(prefabAssetType);
        gsonBuilder.registerTypeAdapterFactory(new AssetTypeAdapterFactory(assetTypeManager.getAssetManager()));
        prefabJsonFormat = new PrefabJsonFormat(componentTypeIndex, componentManager, assetSubsystem.getAssetManager(), gsonBuilder);
        assetFileDataProducer.addAssetFormat(prefabJsonFormat);
    }

    @Override
    public void onEnvironmentChanged(ModuleEnvironment environment) {
        componentManager.clearCache();
        resolutionStrategyProvider.setModuleEnvironment(environment);
        componentTypeIndex.changeEnvironment(environment);
        List<ComponentStore<?>> stores = new ArrayList<>();
        for (Class<? extends Component> componentType : environment.getSubtypesOf(Component.class)) {
            stores.add(createComponentStore(componentManager.getType(componentType)));
        }
        stores.add(createComponentStore(componentManager.getType(GeneratedFromRecipeComponent.class)));
        entityManager = new CoreEntityManager(stores);
    }

    @Override
    public void onAssetsAvailable(AssetManager assetManager) {
        entityManager.createEntities(assetManager.getAsset("core:scene", Prefab.class).get());
    }

    private <T extends Component<T>> ComponentStore<T> createComponentStore(ComponentType<T> componentType) {
        ComponentStore<T> result;
        if (componentType.getComponentClass().isAnnotationPresent(HighUsage.class)) {
            result = new ArrayComponentStore<>(componentType);
        } else {
            result = new SparseComponentStore<>(componentType);
        }
        result = new ConcurrentComponentStore<>(result);
        return result;
    }
}
