package cz.cvut.copakond.pinkfluffyunicorn.model.entities;

import cz.cvut.copakond.pinkfluffyunicorn.model.utils.GamePhysics;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.DirectionEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.GameObject;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.PhisicsEventsEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.RenderPriorityEnums;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Character extends GameObject implements ICharacter {
    public static final int textureRotationSpeed = 5;

    DirectionEnum direction;
    DirectionEnum previousDirection;
    PhisicsEventsEnum previousEvent = PhisicsEventsEnum.NO_COLLISION;
    boolean alive = true;
    boolean clockwiseRotation = true;
    boolean isEnemy;
    String name;
    int textureRotation = 0;

    public Character(String textureName, double[] position, DirectionEnum direction) {
        super(textureName, position, RenderPriorityEnums.CHARACTER.getValue());
        this.direction = direction;
        this.name = textureName;
    }

    public Character(String textureName, double[] position, DirectionEnum direction, PhisicsEventsEnum previousEvent) {
        super(textureName, position, RenderPriorityEnums.CHARACTER.getValue());
        this.direction = direction;
        this.name = textureName;
        this.previousEvent = previousEvent;
    }

    public void move(int pixels) {
        PhisicsEventsEnum event = GamePhysics.checkCollision(this);

        // continue rotating if it started rotating, thus is not in stable rotation rn.
        if (textureRotation % 90 != 0) {
            if (clockwiseRotation) {
                textureRotation = (textureRotation + textureRotationSpeed) % 360;
            } else {
                textureRotation = (textureRotation - textureRotationSpeed + 360) % 360;
            }
        }

        switch (event) {
            case NO_COLLISION:
                break;
            case SHEEP_KILLED:
                this.kill();
                previousEvent = PhisicsEventsEnum.SHEEP_KILLED;
                return;
            case IN_GOAL:
                // CAN NOT DIE + CAN NOT ROTATE, AND IT WILL

                if (previousEvent != PhisicsEventsEnum.IN_GOAL) {
                    // will happen only once it arrives to the goal
                }
                previousEvent = PhisicsEventsEnum.IN_GOAL;
                break;
            case BEFORE_START:

                break;
            case ROTATION_OPPOSITE:
                this.direction = this.direction.getOppositeDirection();
                break;
            case ROTATION_STUCK_4WALLS:
                break;
            default:
                this.direction = PhisicsEventsEnum.convertPhisicsEvent(event);
                break;
        }

        if (event.isRotation()) {
            // do not move this tick, to avoid issues when stuck in 4-way collision
            clockwiseRotation = GamePhysics.decideClockwiseRotation(direction, previousDirection);
            if (clockwiseRotation) {
                textureRotation = (textureRotation + textureRotationSpeed) % 360;
            } else {
                textureRotation = (textureRotation - textureRotationSpeed + 360) % 360;
            }
        } else {
            switch (direction) {
                case UP: position[1] -= pixels; break;
                case DOWN: position[1] += pixels; break;
                case LEFT: position[0] -= pixels; break;
                case RIGHT: position[0] += pixels; break;
            }
        }

        previousEvent = event;
        previousDirection = direction;
    }

    public DirectionEnum getDirection() {
        return this.direction;
    }

    public PhisicsEventsEnum getPreviousEvent() {return this.previousEvent;}

    public void changeDirection(DirectionEnum direction) {
        this.direction = direction;
    }

    public boolean isAlive() {
        return this.alive;
    }

    public boolean isEnemy() {
        return this.isEnemy;
    }

    void kill() {
        this.alive = false;
    }

    protected void setEnemy(boolean isEnemy) {
        this.isEnemy = isEnemy;
    }

    protected static void moveCharacter(Character character, int speed) {}
}
