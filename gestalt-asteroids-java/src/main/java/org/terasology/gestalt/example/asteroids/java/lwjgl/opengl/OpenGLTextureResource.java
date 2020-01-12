package org.terasology.gestalt.example.asteroids.java.lwjgl.opengl;

import org.terasology.gestalt.assets.DisposableResource;
import org.terasology.gestalt.example.asteroids.common.engine.NamedThread;

import static org.lwjgl.opengl.GL33.glDeleteTextures;

public class OpenGLTextureResource implements DisposableResource {
    private final int textureId;
    private final NamedThread displayThread;

    public OpenGLTextureResource(int id, NamedThread displayThread) {
        this.textureId = id;
        this.displayThread = displayThread;
    }

    int getId() {
        return textureId;
    }

    NamedThread getDisplayThread() {
        return displayThread;
    }

    @Override
    public void close() {
        displayThread.async(() -> glDeleteTextures(textureId));
    }
}
