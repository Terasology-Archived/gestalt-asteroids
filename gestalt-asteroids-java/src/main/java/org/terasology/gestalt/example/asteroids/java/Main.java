package org.terasology.gestalt.example.asteroids.java;

import org.lwjgl.Version;
import org.terasology.gestalt.example.asteroids.common.engine.AssetSubsystem;
import org.terasology.gestalt.example.asteroids.common.engine.BasicTimeSubsystem;
import org.terasology.gestalt.example.asteroids.common.engine.Engine;
import org.terasology.gestalt.example.asteroids.common.engine.GameLogicSubsystem;
import org.terasology.gestalt.example.asteroids.common.engine.entitysystem.EntitySubsystem;
import org.terasology.gestalt.example.asteroids.common.engine.StandardModuleSubsystem;
import org.terasology.gestalt.example.asteroids.java.lwjgl.opengl.OpenglSubsystem;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws Exception {
        new Main().run();
    }

    private void run() throws IOException {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        EntitySubsystem entitySubsystem = new EntitySubsystem();
        Engine engine = new Engine(new BasicTimeSubsystem(), new StandardModuleSubsystem(), new AssetSubsystem(), new GameLogicSubsystem(), new OpenglSubsystem(), new EntitySubsystem());
        engine.run();
    }


}

