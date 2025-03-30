package cz.cvut.copakond.pinkfluffyunicorn.model.utils;

import javafx.scene.image.Image;

import java.util.List;

public class GameObject implements IGameObject {
    // Constants
    public final static int ticksPerSecond = 60; // 60 ticks per second
    public final static double maxSpeedPerSecond = 4; // 4 tiles per second

    protected boolean visible;
    protected int renderPriority;
    protected double[] position;

    protected List<Image> textures;
    protected List<int[]> textureSizes;
    protected int textureIdNow;

    // static to share the same texture manager (avoid loading the same textures multiple times)
    protected static TextureManager textureManager = new TextureManager();

    public GameObject(String textureName, double[] position, int renderPriority) {
        this.position = position;
        this.renderPriority = renderPriority;
        this.textureIdNow = 0;
        this.visible = true;
        this.loadTextures(textureName);
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void tick() {
        // updates the game object
    }

    public int getRenderPriority() {
        return this.renderPriority;
    }

    public void setRenderPriority(int renderPriority) {
        this.renderPriority = renderPriority;
    }

    public double[] getPosition() {
        return this.position;
    }

    public double getX() {
        return this.position[0];
    }
    public double getY() {
        return this.position[1];
    }

    public void setPosition(double[] position) {
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

    protected void loadTextures(String textureName) {
        List<Image> textures = textureManager.getTexture(textureName);
        List<int[]> textureSizes = textureManager.getTextureSizes(textures);
        this.textures = textures;
        this.textureSizes = textureSizes;
    }

    protected void loadTextures(String textureName, int[] textureSelection) {
        List<Image> textures = textureManager.getTexture(textureName, textureSelection);
        List<int[]> textureSizes = textureManager.getTextureSizes(textures);
        this.textures = textures;
        this.textureSizes = textureSizes;
    }
}
