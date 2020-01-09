package org.terasology.gestalt.example.asteroids.java;

import org.lwjgl.Version;
import org.terasology.gestalt.example.asteroids.common.engine.AssetSubsystem;
import org.terasology.gestalt.example.asteroids.common.engine.BasicTimeSubsystem;
import org.terasology.gestalt.example.asteroids.common.engine.Engine;
import org.terasology.gestalt.example.asteroids.java.lwjgl.opengl.OpenglSubsystem;

import java.io.IOException;

public class Main {

    private Engine engine;

    public static void main(String[] args) throws Exception {
        new Main().run();
    }

    private void run() throws IOException {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        engine = new Engine(new BasicTimeSubsystem(), new JavaModuleSubsystem(), new AssetSubsystem(), new OpenglSubsystem());
        engine.run();
    }


}

