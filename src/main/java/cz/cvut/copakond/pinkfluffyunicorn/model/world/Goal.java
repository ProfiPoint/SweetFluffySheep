package cz.cvut.copakond.pinkfluffyunicorn.model.world;

import cz.cvut.copakond.pinkfluffyunicorn.model.items.Coin;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.game.GameObject;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.DirectionEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.RenderPriorityEnums;

public class Goal extends GameObject {
    private boolean locked = true;
    private DirectionEnum orientation;

    public Goal(double[] position, DirectionEnum orientation) {
        super("goal", position, RenderPriorityEnums.ARROW.getValue());
        this.orientation = orientation;
    }

    public DirectionEnum getDirection() {
        return this.orientation;
    }

    @Override
    public void tick(boolean doesTimeFlow) {
        super.tick(doesTimeFlow);
        if (this.locked && Coin.getCoinsLeft() <= 0) {
            this.locked = false;
            this.setTexture(1); // set the texture to the unlocked one
        }
    }

    public boolean isLocked() {
        return locked;
    }

}
