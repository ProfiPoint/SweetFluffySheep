package cz.cvut.copakond.pinkfluffyunicorn.model.world;

import cz.cvut.copakond.pinkfluffyunicorn.model.utils.GameObject;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.DirectionEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.RenderPriorityEnums;

public class Start extends GameObject {
    DirectionEnum orientation;

    public Start(int[] position, DirectionEnum orientation) {
        super("start", position, RenderPriorityEnums.ARROW.getValue());
        this.orientation = orientation;
    }

    public DirectionEnum getDirection() {
        return this.orientation;
    }
}
