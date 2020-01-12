package org.terasology.gestalt.example.asteroids.common.rendering;

import org.joml.Vector2ic;
import org.terasology.gestalt.assets.Asset;
import org.terasology.gestalt.assets.AssetType;
import org.terasology.gestalt.assets.DisposableResource;
import org.terasology.gestalt.assets.ResourceUrn;

public abstract class Texture extends Asset<TextureData> {

    protected Texture(ResourceUrn urn, AssetType<?, TextureData> type, DisposableResource resource) {
        super(urn, type, resource);
    }

    public abstract Vector2ic getSize();

    public abstract boolean isLoaded();

}
