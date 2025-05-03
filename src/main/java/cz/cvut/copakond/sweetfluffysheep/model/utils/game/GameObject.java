package cz.cvut.copakond.sweetfluffysheep.model.utils.game;

import cz.cvut.copakond.sweetfluffysheep.model.utils.files.TextureManager;
import cz.cvut.copakond.sweetfluffysheep.model.world.Level;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.GameStatusEnum;

import javafx.scene.image.Image;
import java.util.List;

/**
 * GameObject is the base class for all game objects.
 * It contains the position, texture, and render priority of the object.
 * It also contains methods for loading textures and getting the size of the object.
 * If the object wants to be rendered, it must inherit from this class.
 */
public class GameObject implements IGameObject {
    // Constants
    private static int FPS = 60; // 60 ticks per second
    private static final double COLLISION_HITBOX_PRECISION = 0.1; // 0.1/fps tiles precision

    protected boolean visible;
    protected final int renderPriority;
    protected double[] position;

    protected List<Image> textures;
    protected List<int[]> textureSizes;
    protected int textureIdNow;
    protected static GameStatusEnum gameStatus = GameStatusEnum.RUNNING;
    protected final static TextureManager textureManager = new TextureManager();

    /**
     * Constructor for GameObject.
     * All objects must call this constructor.
     * @param textureName the name of the texture
     * @param position the position of the object
     * @param renderPriority the render priority of the object
     */
    public GameObject(String textureName, double[] position, int renderPriority) {
        this.position = position;
        this.renderPriority = renderPriority;
        this.textureIdNow = 0;
        this.visible = true;
        this.loadTextures(textureName);
    }

    public static GameStatusEnum getGameStatus() {
        return gameStatus;
    }

    public static void setGameStatus(GameStatusEnum gameStatus) {
        GameObject.gameStatus = gameStatus;
    }

    public static int getFPS() {
        return FPS;
    }

    public static void setFPS(int fps) {
        FPS = fps;
    }

    public static double getCollisionLimit() {
        return COLLISION_HITBOX_PRECISION;
    }

    public static TextureManager getTextureManager() {return textureManager;}

    public boolean isVisible() {
        return this.visible;
    }

    public void tick(boolean doesTimeFlow) {}

    public void resetLevel() {}

    public int getRenderPriority() {
        return this.renderPriority;
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

    public Image getTexture() {
        return this.textures.get(this.textureIdNow);
    }

    public int[] getTextureSize() {
        return this.textureSizes.get(this.textureIdNow);
    }

    public void setPosition(double[] position) {
        this.position = position;
    }

    /**
     * Gets the size in percentage 0 to 1 of the texture size relative to the map size
     * @param level the level to get the map size from
     * @return the size in percentage of the texture size relative to the map size
     */
    public double[] getScaledTextureSizePercentage(Level level) {
        int[] textureSize = this.getTextureSize();
        int[] mapSize = level.getMapSize();

        /* for instance mapSize = 24, 12 that is x = 24, y = 12
         * texture size for instance 64, 22 that is x = 66, y = 22
         * calculate percentage for one tile for x, y so x = 1/24, y = 1/12
         * rescale the texture size to the map size, keep the aspect ratio, fit the texture to the tile, so does min,
         */
        double ratioTextureSize = ((double) textureSize[0] / (double) textureSize[1]);
        double[] mapTileRatio = {((double) 1 / (double) mapSize[0]), ((double) 1 / (double) mapSize[1])};
        double[] result = new double[2];
        result[0] = mapTileRatio[0];
        result[1] = mapTileRatio[1];

        if (ratioTextureSize >= 1) {
            result[1] *= 1 / ratioTextureSize;
        } else {
            result[0] *= ratioTextureSize;
        }
        return result;
    }

    /**
     * Gets the size in percentage 0 to 1 of the texture size relative to the map size
     * @param level the level to get the map size from
     * @return the size in percentage of the texture size relative to the map size
     */
    public double[] getScaledPositionSizePercentage(Level level) {
        double[] scaledTextureSize = this.getScaledTextureSizePercentage(level);
        double[] scaledPosition = new double[2];
        scaledPosition[0] = this.position[0] * scaledTextureSize[0];
        scaledPosition[1] = this.position[1] * scaledTextureSize[1];
        return scaledPosition;
    }

    /**
     * Loads the textures for the object.
     * @param textureName the name of the texture
     */
    protected void loadTextures(String textureName) {
        List<Image> textures = textureManager.getTexture(textureName);
        List<int[]> textureSizes = textureManager.getTextureSizes(textures, textureName);
        this.textures = textures;
        this.textureSizes = textureSizes;
    }
}