package cz.cvut.copakond.pinkfluffyunicorn.model.world;

import cz.cvut.copakond.pinkfluffyunicorn.model.utils.GameObject;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.DirectionEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.RenderPriorityEnums;

public class Goal extends GameObject {
    boolean locked = true;
    DirectionEnum orientation;

    public Goal(double[] position, DirectionEnum orientation) {
        super("goal", position, RenderPriorityEnums.ARROW.getValue());
        this.orientation = orientation;
    }

    public DirectionEnum getDirection() {
        return this.orientation;
    }
}
