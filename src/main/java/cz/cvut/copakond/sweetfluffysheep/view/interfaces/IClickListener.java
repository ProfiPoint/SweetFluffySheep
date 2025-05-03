package cz.cvut.copakond.sweetfluffysheep.view.interfaces;

import javafx.scene.input.MouseEvent;

/**
 * Interface for handling click events.
 */
public interface IClickListener {

    /**
     * Handles a mouse-click event.
     * Like placing a tile or object on the game board.
     *
     * @param event The mouse event to handle.
     */
    void handleClick(MouseEvent event);
}
