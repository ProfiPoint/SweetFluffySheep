package cz.cvut.copakond.sweetfluffysheep.model.world;

import cz.cvut.copakond.sweetfluffysheep.model.utils.game.GameObject;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.RenderPriorityEnums;

/**
 * Represents a tile in the game world.
 * Each tile has a position, a texture type, and a walkable property.
 */
public class Tile extends GameObject {
    private final int textureType;
    private final boolean isWalkable;

    /**
     * Creates a new Tile object.
     *
     * @param position    The position of the tile in the game world.
     * @param textureType The texture type of the tile.
     * @param walkable    Indicates whether the tile is walkable or not.
     */
    public Tile(double[] position, int textureType, boolean walkable) {
        super("tile", position, RenderPriorityEnums.TILE.getValue());
        this.isWalkable = walkable;
        this.textureType = textureType / 16;
        this.textureIdNow = textureType - 1;
    }

    public boolean isWalkable() {
        return isWalkable;
    }

    public int getTextureType() {
        return textureType;
    }

    /**
     * Returns the name of the tile based on its position.
     * The name is a string representation of the tile's coordinates.
     * Example: "0-0", "1-2", etc.
     *
     * @return The name of the tile.
     */
    public String getTileName() {
        double[] dPosition = this.getPosition();
        int[] position = new int[]{(int) dPosition[0], (int) dPosition[1]};
        return position[0] + "-" + position[1];
    }
}
