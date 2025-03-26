package cz.cvut.copakond.pinkfluffyunicorn.model.entities;

import cz.cvut.copakond.pinkfluffyunicorn.model.enums.DirectionEnum;

public interface ICharacter  {
    // moves the character by one step in the current direction
    void move(int pixels);

    // changes the direction of the character
    void changeDirection(DirectionEnum direction);

    // manages the character's state
    boolean isAlive();
    boolean isEnemy();
    void kill();
}
