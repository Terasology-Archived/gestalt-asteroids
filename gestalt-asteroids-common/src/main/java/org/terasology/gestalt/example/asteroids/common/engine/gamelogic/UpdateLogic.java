package org.terasology.gestalt.example.asteroids.common.engine.gamelogic;

/**
 * Interface for GameLogic that should be called each from to do some processing
 */
public interface UpdateLogic {

    /**
     * Called every tick
     */
    void update();
}
