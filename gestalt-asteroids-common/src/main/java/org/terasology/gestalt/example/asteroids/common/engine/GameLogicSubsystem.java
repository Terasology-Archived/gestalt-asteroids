package org.terasology.gestalt.example.asteroids.common.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.gestalt.dependencyinjection.GameLogicProvider;
import org.terasology.gestalt.dependencyinjection.Provider;
import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.gestalt.entitysystem.entity.EntityIterator;
import org.terasology.gestalt.entitysystem.entity.EntityRef;
import org.terasology.gestalt.example.asteroids.common.engine.entitysystem.EntitySubsystem;
import org.terasology.gestalt.example.asteroids.common.engine.gamelogic.TickEntities;
import org.terasology.gestalt.example.asteroids.common.engine.gamelogic.UpdateLogic;
import org.terasology.gestalt.module.ModuleEnvironment;
import org.terasology.gestalt.util.collection.TypeKeyedMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class GameLogicSubsystem implements Subsystem {

    private static final Logger logger = LoggerFactory.getLogger(GameLogicSubsystem.class);

    private List<UpdateLogic> updateLogic;
    private TypeKeyedMap<Object> globalProviders = new TypeKeyedMap<>();
    private Set<Object> systems;

    public <T> void addProvider(Class<T> interfaceType, T provider) {
        globalProviders.put(interfaceType, provider);
    }

    public Set<Object> getSystems() {
        return systems;
    }

    @Override
    public void onEnvironmentChanged(ModuleEnvironment newEnvironment) {
        TypeKeyedMap<Object> providerMap = new TypeKeyedMap<>();
        for (Class<? extends Provider> providerType : newEnvironment.getSubtypesOf(Provider.class)) {
            try {
                Provider provider = providerType.newInstance();
                Class implementedInterface = provider.providerFor();
                providerMap.put(implementedInterface, provider);
            } catch (InstantiationException | IllegalAccessException e) {
                logger.error("Failed to generate system {}", providerType, e);
            }
        }
        providerMap.putAll(globalProviders);
        providerMap.values().stream().filter(x -> x instanceof Provider).map(x -> (Provider) x).forEach(
                x -> x.init(providerMap)
        );

        systems = providerMap.values().stream().filter(x -> x instanceof Provider).map(x -> (Provider) x).flatMap(x -> x.getAllSystems().stream()).collect(Collectors.toSet());
        updateLogic = systems.stream().filter(x -> x instanceof UpdateLogic).map(x -> (UpdateLogic) x).collect(Collectors.toList());
    }

    @Override
    public void tick() {
        for (UpdateLogic updateSystem : updateLogic) {
            updateSystem.update();
        }
    }
}
