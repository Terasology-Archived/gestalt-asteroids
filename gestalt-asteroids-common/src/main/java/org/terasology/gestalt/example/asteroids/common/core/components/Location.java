package org.terasology.gestalt.example.asteroids.common.core.components;

import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.terasology.gestalt.entitysystem.component.Component;

public class Location implements Component<Location> {

    private final Vector3f position = new Vector3f();
    private final Quaternionf rotation = new Quaternionf();
    private final Vector3f scale = new Vector3f();

    public Location() {}

    public Location(Vector3fc position, Quaternionfc rotation, Vector3fc scale) {
        this.position.set(position);
        this.rotation.set(rotation);
        this.scale.set(scale);
    }

    public Location(Location other) {
        this(other.position, other.rotation, other.scale);
    }

    public Vector3f getPosition() {
        return position;
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    public Vector3f getScale() {
        return scale;
    }

    public void setPosition(Vector3fc newPosition) {
        this.position.set(newPosition);
    }

    public void setRotation(Quaternionfc newRotation) {
        this.rotation.set(newRotation);
    }

    public void setScale(Vector3fc newScale) {
        this.scale.set(newScale);
    }

    @Override
    public void copy(Location location) {
        this.position.set(location.position);
        this.rotation.set(location.rotation);
        this.scale.set(location.scale);
    }

    @Override
    public String toString() {
        return "Location(Position: " + position + ", Rotation: " + rotation + ", Scale: " + scale + ")";
    }
}
