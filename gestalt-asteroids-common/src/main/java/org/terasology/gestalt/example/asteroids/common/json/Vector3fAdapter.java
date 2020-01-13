package org.terasology.gestalt.example.asteroids.common.json;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.io.IOException;
import java.lang.annotation.Annotation;

public class Vector3fAdapter extends TypeAdapter<Vector3fc> {
    @Override
    public void write(JsonWriter jsonWriter, Vector3fc vector3fc) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("x");
        jsonWriter.value(vector3fc.x());
        jsonWriter.name("y");
        jsonWriter.value(vector3fc.y());
        jsonWriter.name("z");
        jsonWriter.value(vector3fc.z());
        jsonWriter.endObject();
    }

    @Override
    public Vector3fc read(JsonReader jsonReader) throws IOException {
        Vector3f value = new Vector3f();
        if (jsonReader.peek() == JsonToken.NUMBER) {
            float uniform = (float) jsonReader.nextDouble();
            value.x = uniform;
            value.y = uniform;
            value.z = uniform;
        } else {
            jsonReader.beginObject();
            while (jsonReader.peek() != JsonToken.END_OBJECT) {
                String name = jsonReader.nextName();
                switch (name) {
                    case "x":
                        value.x = (float) jsonReader.nextDouble();
                        break;
                    case "y":
                        value.y = (float) jsonReader.nextDouble();
                        break;
                    case "z":
                        value.z = (float) jsonReader.nextDouble();
                        break;
                    default:
                        throw new IOException("Unexpected element '" + name + "'");
                }
            }
            jsonReader.endObject();
        }
        return value;
    }
}
