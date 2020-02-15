package org.terasology.gestalt.example.asteroids.common.engine.entitysystem;

import com.google.gson.GsonBuilder;

import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.gestalt.assets.AssetType;
import org.terasology.gestalt.assets.format.producer.AssetFileDataProducer;
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
import org.terasology.gestalt.entitysystem.entity.EntityIterator;
import org.terasology.gestalt.entitysystem.entity.EntityManager;
import org.terasology.gestalt.entitysystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.entity.manager.CoreEntityManager;
import org.terasology.gestalt.entitysystem.prefab.GeneratedFromRecipeComponent;
import org.terasology.gestalt.entitysystem.prefab.Prefab;
import org.terasology.gestalt.entitysystem.prefab.PrefabData;
import org.terasology.gestalt.entitysystem.prefab.PrefabJsonFormat;
import org.terasology.gestalt.example.asteroids.common.engine.AssetSubsystem;
import org.terasology.gestalt.example.asteroids.common.engine.Engine;
import org.terasology.gestalt.example.asteroids.common.engine.Subsystem;
import org.terasology.gestalt.example.asteroids.common.engine.gamelogic.TickEntities;
import org.terasology.gestalt.example.asteroids.common.json.AssetTypeAdapterFactory;
import org.terasology.gestalt.example.asteroids.common.json.Vector3fAdapter;
import org.terasology.gestalt.module.ModuleEnvironment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EntitySubsystem implements Subsystem {

    private static final Logger logger = LoggerFactory.getLogger(EntitySubsystem.class);

    private AssetSubsystem assetSubsystem;

    private GsonBuilder gsonBuilder = new GsonBuilder();
    private CoreEntityManager entityManager;
    private ModuleAwareAssetTypeManager moduleAwareAssetTypeManager;
    private AssetType<Prefab, PrefabData> prefabAssetType;
    private PrefabJsonFormat prefabJsonFormat;
    private ComponentTypeIndex componentTypeIndex;
    private ComponentManager componentManager;
    private ModuleEnvironmentDependencyProvider resolutionStrategyProvider = new ModuleEnvironmentDependencyProvider();

    private List<Runnable> entityTickers = new ArrayList<>();


    public EntityManager getEntityManager() {
        return entityManager;
    }

    public ComponentManager getComponentManager() {
        return componentManager;
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
        gsonBuilder.registerTypeAdapter(Vector3f.class, new Vector3fAdapter());
        gsonBuilder.registerTypeAdapter(Vector3fc.class, new Vector3fAdapter());
        prefabJsonFormat = new PrefabJsonFormat(componentTypeIndex, componentManager, assetSubsystem.getAssetManager(), gsonBuilder);
        assetFileDataProducer.addAssetFormat(prefabJsonFormat);
    }

    @Override
    public void onEnvironmentChanged(ModuleEnvironment environment) {
        entityTickers.clear();
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
    public void onSystemsAvailable(Set<Object> systems) {
        entityTickers = new ArrayList<>();
        for (Object providedSystem : systems) {
            for (Method method : providedSystem.getClass().getMethods()) {
                TickEntities tickEntitiesAnnotation = method.getAnnotation(TickEntities.class);
                if (tickEntitiesAnnotation != null) {
                    // Generate caller
                    List<Class<? extends Component>> componentTypes = new ArrayList<>();
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    if (!EntityRef.class.isAssignableFrom(parameterTypes[0])) {
                        logger.error("@TickEntity method " + method + " malformed, first parameter must be an EntityRef.");
                        continue;
                    }
                    for (int i = 1; i < parameterTypes.length; i++) {
                        if (!Component.class.isAssignableFrom(parameterTypes[i])) {
                            logger.error("@TickEntity method " + method + " malformed, first parameter must be an EntityRef.");
                            continue; // Wrong scope, pull out method
                        }
                        componentTypes.add((Class<? extends Component>) parameterTypes[i]);
                    }


                    Component[] components = componentTypes.stream().map(x -> componentManager.getType(x).create()).toArray(Component[]::new);
                    Object[] params = new Object[parameterTypes.length];
                    for (int i = 0; i < components.length; i++) {
                        params[i + 1] = components[i];
                    }

                    entityTickers.add(() -> {
                        EntityIterator iterator = entityManager.iterate(components);
                        if (iterator.next()) {
                            params[0] = iterator.getEntity();
                            try {
                                method.invoke(providedSystem, params);
                            } catch (IllegalAccessException|InvocationTargetException e) {
                                logger.error("Failed to call entity ticker", e);
                            }
                        }
                    });
                }
            }

        }
    }

    @Override
    public void tick() {
        for (Runnable r : entityTickers) {
            r.run();
        }
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
