package cz.cvut.copakond.sweetfluffysheep.view.interfaces;

/**
 * Interface representing a level frame in the game.
 * This interface is responsible for rendering the level objects and checking the game status.
 */
public interface ILevelFrame {

    /**
     * Renders the level objects on the screen.
     * This method is responsible for drawing all the objects in the current level.
     */
    void drawLevelObjects();

    /**
     * Checks the game status to determine if the game is over or if the player has won.
     * This method should be called periodically to update the game state.
     */
    void checkGameStatus();
}
