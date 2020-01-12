package org.terasology.gestalt.example.asteroids.modules.core.components;

import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.gestalt.example.asteroids.common.rendering.Texture;

public class Sprite implements Component<Sprite> {

    private Texture texture;

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    @Override
    public void copy(Sprite sprite) {
        this.texture = sprite.texture;
    }
}
