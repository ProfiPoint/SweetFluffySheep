package cz.cvut.copakond.pinkfluffyunicorn.model.world;

import cz.cvut.copakond.pinkfluffyunicorn.model.utils.GameObject;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.RenderPriorityEnums;

public class Tile extends GameObject {
    public Tile(int[] position) {
        super("tile", position, RenderPriorityEnums.TILE.getValue());
    }
}
