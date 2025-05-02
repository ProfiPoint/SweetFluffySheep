package cz.cvut.copakond.sweetfluffysheep.view.interfaces;

import javafx.scene.canvas.GraphicsContext;

public interface IInteractableFrame {
    void draw(GraphicsContext gc);
    void onResizeCanvas(double width, double height);
}