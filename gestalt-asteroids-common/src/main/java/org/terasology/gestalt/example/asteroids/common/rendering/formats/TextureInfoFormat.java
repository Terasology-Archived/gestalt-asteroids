package org.terasology.gestalt.example.asteroids.common.rendering.formats;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.terasology.gestalt.assets.format.AbstractAssetAlterationFileFormat;
import org.terasology.gestalt.assets.format.AssetDataFile;
import org.terasology.gestalt.example.asteroids.common.json.CaseInsensitiveEnumTypeAdapterFactory;
import org.terasology.gestalt.example.asteroids.common.rendering.FilterMode;
import org.terasology.gestalt.example.asteroids.common.rendering.TextureData;
import org.terasology.gestalt.example.asteroids.common.rendering.WrapMode;

import java.io.IOException;
import java.io.InputStreamReader;

public class TextureInfoFormat extends AbstractAssetAlterationFileFormat<TextureData> {

    private Gson gson = new GsonBuilder().registerTypeAdapterFactory(new CaseInsensitiveEnumTypeAdapterFactory()).create();

    public TextureInfoFormat() {
        super("json");
    }

    @Override
    public void apply(AssetDataFile input, TextureData assetData) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(input.openStream(), Charsets.UTF_8)) {
            TextureMetadata metadata = gson.fromJson(reader, TextureMetadata.class);
            if (metadata.filterMode != null) {
                assetData.setFilterMode(metadata.filterMode);
            }
            if (metadata.wrapMode != null) {
                assetData.setWrapMode(metadata.wrapMode);
            }
        }
    }

    private static class TextureMetadata {
        FilterMode filterMode;
        WrapMode wrapMode;
    }
}
