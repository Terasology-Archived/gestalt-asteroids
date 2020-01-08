package org.terasology.gestalt.example.asteroids.common.core.components;

import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.gestalt.example.asteroids.common.core.rendering.Texture;

public class Sprite implements Component<Sprite> {

    private Texture texture;

    @Override
    public void copy(Sprite sprite) {
        this.texture = sprite.texture;
    }
}
