package org.terasology.gestalt.example.asteroids.java.lwjgl.opengl;

import org.joml.Vector2i;
import org.joml.Vector2ic;
import static org.lwjgl.opengl.GL33.*;
import org.terasology.gestalt.assets.AssetType;
import org.terasology.gestalt.assets.ResourceUrn;
import org.terasology.gestalt.example.asteroids.common.rendering.FilterMode;
import org.terasology.gestalt.example.asteroids.common.rendering.Texture;
import org.terasology.gestalt.example.asteroids.common.rendering.TextureData;

public class OpenGLTexture extends Texture {

    private final Vector2i size = new Vector2i();
    private boolean loaded = false;
    private OpenGLTextureResource textureResource;

    public OpenGLTexture(ResourceUrn urn, AssetType<?, TextureData> type, TextureData data, OpenGLTextureResource resource) {
        super(urn, type, resource);
        this.textureResource = resource;
        reload(data);
    }

    @Override
    public Vector2ic getSize() {
        return size;
    }

    int getTextureId() {
        return textureResource.getId();
    }

    @Override
    protected void doReload(TextureData textureData) {
        loaded = false;
        size.set(textureData.getSize());
        textureResource.getDisplayThread().async(() -> {
            glBindTexture(GL_TEXTURE_2D, textureResource.getId());
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, textureData.getSize().x, textureData.getSize().y, 0, GL_RGBA, GL_UNSIGNED_BYTE, textureData.getPixelData());
            if (textureData.isMipmap())
            {
                // ATI bug fix
                glEnable(GL_TEXTURE_2D);
                glGenerateMipmap(GL_TEXTURE_2D);
                glDisable(GL_TEXTURE_2D);
            }
            if (textureData.getFilterMode() == FilterMode.LINEAR) {
                glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
                glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            } else {
                glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_LINEAR);
                glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            }
            switch (textureData.getWrapMode()) {
                case CLAMP:
                    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
                    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
                    break;
                case REPEAT:
                    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
                    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
                    break;
            }
            glBindTexture(GL_TEXTURE_2D,0);
            loaded = true;
        });
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }
}
