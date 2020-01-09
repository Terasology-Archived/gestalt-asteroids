package org.terasology.gestalt.example.asteroids.java;

import org.terasology.gestalt.assets.module.ModuleAwareAssetTypeManager;
import org.terasology.gestalt.example.asteroids.common.engine.Engine;
import org.terasology.gestalt.example.asteroids.common.engine.ModuleSubsystem;
import org.terasology.gestalt.module.Module;
import org.terasology.gestalt.module.ModuleEnvironment;
import org.terasology.gestalt.module.ModuleFactory;
import org.terasology.gestalt.module.ModuleMetadata;
import org.terasology.gestalt.module.sandbox.ModuleSecurityManager;
import org.terasology.gestalt.module.sandbox.ModuleSecurityPolicy;
import org.terasology.gestalt.module.sandbox.PermissionProviderFactory;
import org.terasology.gestalt.module.sandbox.StandardPermissionProviderFactory;
import org.terasology.gestalt.module.sandbox.WarnOnlyProviderFactory;
import org.terasology.gestalt.naming.Name;
import org.terasology.gestalt.naming.Version;

import java.security.Policy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class JavaModuleSubsystem implements ModuleSubsystem {

    private List<Module> coreModules = new ArrayList<>();
    private ModuleEnvironment environment;
    private ModuleFactory moduleFactory;
    private PermissionProviderFactory permissionProviderFactory;

    public JavaModuleSubsystem() {
        moduleFactory = new ModuleFactory();
        ModuleMetadata engineMetadata = new ModuleMetadata();
        engineMetadata.setId(new Name("Core"));
        engineMetadata.setVersion(new Version("1.0.0"));
        coreModules.add(moduleFactory.createPackageModule(engineMetadata, "org.terasology.gestalt.example.asteroids.modules.engine"));

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
    public void initialise(Engine engine) {
    }

    @Override
    public void registerAssetTypes(ModuleAwareAssetTypeManager assetTypeManager) {

    }

    @Override
    public void tick(int delta) {

    }

    @Override
    public void close() {
        if (environment != null) {
            environment.close();
            environment = null;
        }
    }

    @Override
    public void onEnvironmentChanged(ModuleEnvironment environment) {

    }
}
