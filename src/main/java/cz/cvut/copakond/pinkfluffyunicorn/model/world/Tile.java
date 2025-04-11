package cz.cvut.copakond.pinkfluffyunicorn.model.world;

import cz.cvut.copakond.pinkfluffyunicorn.model.utils.GameObject;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.RenderPriorityEnums;

public class Tile extends GameObject {
    int textureType;
    public Tile(double[] position, int textureType) {
        super("tile", position, RenderPriorityEnums.TILE.getValue());
        this.textureType = textureType;
        super.loadTextures("tile", new int[]{textureType});
    }

    public int getTextureType() {
        return textureType;
    }
}
