package cz.cvut.copakond.pinkfluffyunicorn.model.items;

import cz.cvut.copakond.pinkfluffyunicorn.model.enums.ItemEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.world.IObject;

public interface IItem extends IObject {
    boolean isAlive();
    boolean isActive();
}
