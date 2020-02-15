package org.terasology.gestalt.example.asteroids.common.engine;

import org.terasology.gestalt.module.Module;
import org.terasology.gestalt.module.ModuleEnvironment;
import org.terasology.gestalt.module.ModuleFactory;
import org.terasology.gestalt.module.ModuleMetadataJsonAdapter;
import org.terasology.gestalt.module.sandbox.ModuleSecurityManager;
import org.terasology.gestalt.module.sandbox.ModuleSecurityPolicy;
import org.terasology.gestalt.module.sandbox.PermissionProviderFactory;
import org.terasology.gestalt.module.sandbox.StandardPermissionProviderFactory;
import org.terasology.gestalt.module.sandbox.WarnOnlyProviderFactory;

import java.security.Policy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class StandardModuleSubsystem implements org.terasology.gestalt.example.asteroids.common.engine.ModuleSubsystem {

    private List<Module> coreModules = new ArrayList<>();
    private ModuleEnvironment environment;
    private ModuleFactory moduleFactory;
    private PermissionProviderFactory permissionProviderFactory;

    public StandardModuleSubsystem() {
        moduleFactory = new ModuleFactory();
        coreModules.add(moduleFactory.createPackageModule("org.terasology.gestalt.example.asteroids.modules.core"));

        permissionProviderFactory = new WarnOnlyProviderFactory(new StandardPermissionProviderFactory());
        Policy.setPolicy(new ModuleSecurityPolicy());
        System.setSecurityManager(new ModuleSecurityManager());
    }

    @Override
    public ModuleEnvironment getEnvironment() {
        return environment;
    }

    @Override
    public void changeEnvironment(Collection<Module> modules) {
        if (environment != null) {
            environment.close();
        }
        environment = loadEnvironment(modules);
    }

    @Override
    public ModuleEnvironment loadEnvironment(Collection<Module> modules) {
        Set<Module> allModules = new LinkedHashSet<>(coreModules);
        allModules.addAll(modules);
        return new ModuleEnvironment(allModules, permissionProviderFactory);
    }

    @Override
    public void close() {
        if (environment != null) {
            environment.close();
            environment = null;
        }
    }
}
