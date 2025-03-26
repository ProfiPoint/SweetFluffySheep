package cz.cvut.copakond.pinkfluffyunicorn.model.items;

import cz.cvut.copakond.pinkfluffyunicorn.model.world.IGameObject;

public interface IItem extends IGameObject {
    boolean isAlive();
    boolean isActive();
}
