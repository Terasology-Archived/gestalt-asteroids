package org.terasology.gestalt.example.asteroids.common.json;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import org.terasology.gestalt.assets.Asset;
import org.terasology.gestalt.assets.AssetData;
import org.terasology.gestalt.assets.management.AssetManager;

import java.io.IOException;

public class AssetTypeAdapterFactory implements TypeAdapterFactory {

    private final AssetManager assetManager;

    public AssetTypeAdapterFactory(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class<T> rawType = (Class<T>) type.getRawType();
        if (Asset.class.isAssignableFrom(rawType)) {
            return new AssetTypeAdapter(assetManager, rawType);
        }
        return null;
    }

    private static class AssetTypeAdapter<T extends Asset<U>, U extends AssetData> extends TypeAdapter<T> {

        private AssetManager assetManager;
        private Class<T> assetType;

        AssetTypeAdapter(AssetManager assetManager, Class<T> assetType) {
            this.assetManager = assetManager;
            this.assetType = assetType;
        }

        @Override
        public void write(JsonWriter jsonWriter, T value) throws IOException {
            if (value == null) {
                jsonWriter.nullValue();
            } else {
                jsonWriter.value(value.getUrn().toString());
            }
        }

        @Override
        public T read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                return null;
            } else {
                String value = jsonReader.nextString();
                return assetManager.getAsset(value, assetType).orElse(null);
            }
        }
    }
}