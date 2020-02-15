package org.terasology.gestalt.example.asteroids.modules.core.components;

import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.terasology.gestalt.entitysystem.component.Component;

public class Drift implements Component<Drift> {
    private Vector3f velocity = new Vector3f();

    public Vector3f getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector3fc velocity) {
        this.velocity.set(velocity);
    }

    @Override
    public void copy(Drift other) {
        this.velocity.set(other.velocity);
    }
}
