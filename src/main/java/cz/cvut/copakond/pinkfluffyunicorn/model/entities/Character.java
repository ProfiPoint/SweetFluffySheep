package cz.cvut.copakond.pinkfluffyunicorn.model.entities;

import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.DirectionEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.GameObject;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.RenderPriorityEnums;

public class Character extends GameObject implements ICharacter {
    DirectionEnum direction;
    boolean alive = true;
    boolean isEnemy;
    String name;

    public Character(String textureName, int[] position, DirectionEnum direction) {
        super(textureName, position, RenderPriorityEnums.CHARACTER.getValue());
        this.direction = direction;
        this.name = textureName;
    }

    public void move(int pixels) {
        switch (direction) {
            case UP:
                position[1] -= pixels;
                break;
            case DOWN:
                position[1] += pixels;
                break;
            case LEFT:
                position[0] -= pixels;
                break;
            case RIGHT:
                position[0] += pixels;
                break;
        }
    }

    public void changeDirection(DirectionEnum direction) {
        this.direction = direction;
    }

    public boolean isAlive() {
        return this.alive;
    }

    public boolean isEnemy() {
        return this.isEnemy;
    }

    public void kill() {
        this.alive = false;
    }

    protected void setEnemy(boolean isEnemy) {
        this.isEnemy = isEnemy;
    }
}
