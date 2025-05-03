package cz.cvut.copakond.sweetfluffysheep.view.interfaces;

import javafx.scene.canvas.GraphicsContext;

/**
 * Interface representing responsive frames
 * This interfaces is used for handling events of screen resize or rendering the content.
 */
public interface IInteractableFrame {

    /**
     * Draws the frame on the given GraphicsContext.
     * Usually used to render the UI elements.
     *
     * @param gc The GraphicsContext to draw on.
     */
    void draw(GraphicsContext gc);

    /**
     * Handles the updating the UI aspect of the frame when the canvas is resized.
     * Redraws all the elements on the canvas.
     */
    void onResizeCanvas(double width, double height);
}