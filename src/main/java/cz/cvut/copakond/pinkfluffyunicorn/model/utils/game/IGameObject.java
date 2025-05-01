package cz.cvut.copakond.pinkfluffyunicorn.model.utils.game;

import javafx.scene.image.Image;

public interface IGameObject {
    // Visibility {get, set}
    boolean isVisible();
    void tick(boolean doesTimeFlow);
    void resetLevel();

    // Render Priority {get, set}
    int getRenderPriority(); // 0 - background layer ... infinity - top layer

    // 2D Position {get, set}
    double[] getPosition();
    void setPosition(double[] position);

    // Textures {get, set}
    Image getTexture();
    int[] getTextureSize();
}
