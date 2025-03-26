package cz.cvut.copakond.pinkfluffyunicorn.model.world;

import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

public class GameObject implements IGameObject {
    protected boolean visible;
    protected int renderPriority;
    protected int[] position;

    protected List<Image> textures;
    protected List<int[]> textureSizes;
    protected int textureIdNow;

    public GameObject(int[] position, int renderPriority, List<Image> textures, List<int[]> textureSizes) {
        this.position = position;
        this.renderPriority = renderPriority;
        this.textures = textures;
        this.textureSizes = textureSizes;
        this.textureIdNow = 0;
        this.visible = true;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int getRenderPriority() {
        return this.renderPriority;
    }

    public void setRenderPriority(int renderPriority) {
        this.renderPriority = renderPriority;
    }

    public int[] getPosition() {
        return this.position;
    }

    public void setPosition(int[] position) {
        this.position = position;
    }

    public Image getTexture() {
        return this.textures.get(this.textureIdNow);
    }

    public int[] getTextureSize() {
        return this.textureSizes.get(this.textureIdNow);
    }

    public void setTexture(int textureId) {
        this.textureIdNow = textureId;
    }

    public void nextTexture() {
        this.textureIdNow = (this.textureIdNow + 1) % this.textures.size();
    }
}
