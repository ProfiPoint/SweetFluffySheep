package cz.cvut.copakond.pinkfluffyunicorn.model.utils;

import javafx.scene.image.Image;

public interface IGameObject {
    // Visibility {get, set}
    boolean isVisible();
    void setVisible(boolean visible);
    void tick(boolean doesTimeFlow);

    // Render Priority {get, set}
    int getRenderPriority(); // 0 - background layer ... infinity - top layer
    void setRenderPriority(int renderPriority);

    // 2D Position {get, set}
    double[] getPosition();
    void setPosition(double[] position);

    // Textures {get, set}
    Image getTexture();
    int[] getTextureSize();
    void nextTexture();
}
