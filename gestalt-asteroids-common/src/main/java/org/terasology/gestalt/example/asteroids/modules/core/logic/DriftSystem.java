package org.terasology.gestalt.example.asteroids.modules.core.logic;

import org.joml.Vector3f;
import org.terasology.gestalt.entitysystem.entity.EntityRef;
import org.terasology.gestalt.example.asteroids.common.engine.Time;
import org.terasology.gestalt.example.asteroids.common.engine.gamelogic.TickEntities;
import org.terasology.gestalt.example.asteroids.modules.core.components.Drift;
import org.terasology.gestalt.example.asteroids.modules.core.components.Location;

import javax.inject.Inject;

public class DriftSystem {

    private Time time;

    @Inject
    public DriftSystem(Time time) {
        this.time = time;
    }

    @TickEntities
    public void tick(EntityRef entity, Location location, Drift drift) {
        Vector3f velocity = new Vector3f(drift.getVelocity());
        velocity.mul(time.gameDelta());

        location.getPosition().add(velocity);
        entity.setComponent(location);
    }

}
