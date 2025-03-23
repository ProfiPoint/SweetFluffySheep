package cz.cvut.copakond.pinkfluffyunicorn.model.entities;

import cz.cvut.copakond.pinkfluffyunicorn.model.enums.DirectionEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.world.IObject;

public interface ICharacter extends IObject {
    // moves the character by one step in the current direction
    void move();

    // changes the direction of the character
    void changeDirection(DirectionEnum direction);

    // manages the character's state
    boolean isAlive();
    boolean isEnemy();
    void kill();
}
