package org.terasology.gestalt.example.asteroids.java.lwjgl.opengl;

import org.terasology.gestalt.assets.DisposableResource;
import org.terasology.gestalt.example.asteroids.common.core.GameThread;

import static org.lwjgl.opengl.GL33.glDeleteTextures;

public class OpenGLTextureResource implements DisposableResource {
    private final int textureId;

    public OpenGLTextureResource(int id) {
        this.textureId = id;
    }

    int getId() {
        return textureId;
    }

    @Override
    public void close() {
        GameThread.async(() -> glDeleteTextures(textureId));
    }
}
