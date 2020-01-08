package org.terasology.gestalt.example.asteroids.common.core.rendering;

import org.terasology.gestalt.assets.Asset;
import org.terasology.gestalt.assets.AssetType;
import org.terasology.gestalt.assets.ResourceUrn;

public abstract class Texture extends Asset<TextureData> {

    public Texture(ResourceUrn urn, AssetType<?, TextureData> type, TextureData data) {
        super(urn, type);
        reload(data);
    }

}
