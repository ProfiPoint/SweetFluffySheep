package cz.cvut.copakond.sweetfluffysheep.model.utils.game;

import javafx.scene.image.Image;

/**
 * Interface representing any game object.
 * A game object can be rendered, updated, and has a position in the game world.
 */
public interface IGameObject {

    /**
     * Checks if the game object is visible.
     *
     * @return true if the object is visible, false otherwise.
     */
    boolean isVisible();

    /**
     * Updates the game object, by specified behavior.
     *
     * @param doesTimeFlow Whether time is currently flowing (true if the scene is rendering, and only during that
     *                     the time limit clock flows).
     */
    void tick(boolean doesTimeFlow);

    /**
     * Resets the game object to its initial state.
     * Used when loading / resting the level.
     * It will clear all the static variables if the class uses them.
     * (Like the total number of coins collected, etc.)
     */
    void resetLevel();

    /**
     * Returns the render priority of the game object.
     * The lower the number, the more in the background it is rendered.
     * The higher the number, the more in the foreground it is rendered.
     *
     * @return The render priority of the game object.
     */
    int getRenderPriority();

    /**
     * Returns the tile-converted position of the game object in the game world.
     *
     * @return The position of the game object as an array of doubles.
     */
    double[] getPosition();

    /**
     * Sets the tile-converted position of the game object in the game world.
     *
     * @param position The new position of the game object as an array of doubles.
     */
    void setPosition(double[] position);

    /**
     * Returns the texture of the game object.
     *
     * @return The texture of the game object as an Image.
     */
    Image getTexture();

    /**
     * Returns the size of the texture of the game object in pixels.
     *
     * @return The size of the texture as an array of integers.
     */
    int[] getTextureSize();
}
