package cz.cvut.copakond.sweetfluffysheep.model.world;

import cz.cvut.copakond.sweetfluffysheep.model.utils.game.GameObject;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.RenderPriorityEnums;

public class Tile extends GameObject {
    private final int textureType;
    private final boolean isWalkable;

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

    public String getTileName() {
        double[] dPosition = this.getPosition();
        int[] position = new int[]{(int) dPosition[0], (int) dPosition[1]};
        return position[0] + "-" + position[1];
    }
}
