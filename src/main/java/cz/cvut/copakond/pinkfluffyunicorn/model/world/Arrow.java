package cz.cvut.copakond.pinkfluffyunicorn.model.world;

import cz.cvut.copakond.pinkfluffyunicorn.model.utils.GameObject;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.DirectionEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.ItemEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.RenderPriorityEnums;

public class Arrow extends GameObject {
    static int arrowCount = 0;
    int maxArrows = 10;
    DirectionEnum direction;

    public Arrow(double[] position) {
        super("arrow", position, RenderPriorityEnums.ARROW.getValue());
        arrowCount++;
        if (arrowCount > maxArrows) {
            destroy();
            throw new IllegalStateException("Too many arrows");
        }
    }

    // on click rotate arrow
    public void rotate(DirectionEnum direction) {
        this.direction = direction.next();
    }

    public DirectionEnum getDirection() {
        return direction;
    }

    public void destroy() {
        arrowCount--;
        super.visible = false;
    }
}
