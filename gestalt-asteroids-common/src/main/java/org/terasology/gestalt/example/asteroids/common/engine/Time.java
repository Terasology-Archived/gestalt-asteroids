package org.terasology.gestalt.example.asteroids.common.engine;

/**
 * Time information
 */
public interface Time {
    /**
     * @return Time that has passed this update
     */
    float globalDelta();

    /**
     * @return Time that has passed this update, in milliseconds
     */
    int globalDeltaMs();

    /**
     * @return Time that has passed this update, or 0 if paused
     */
    float gameDelta();

    /**
     * @return Time that has passed this update in milliseconds, or 0 if paused
     */
    int gameDeltaMs();

    /**
     * @return Is the game paused
     */
    boolean isPaused();

    /**
     * Toggle whether the game is paused
     */
    void togglePaused();
}
