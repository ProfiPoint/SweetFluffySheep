package cz.cvut.copakond.pinkfluffyunicorn.model.entities;

import cz.cvut.copakond.pinkfluffyunicorn.model.utils.levels.GamePhysics;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.DirectionEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.game.GameObject;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.PhisicsEventsEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.RenderPriorityEnums;
import cz.cvut.copakond.pinkfluffyunicorn.model.world.Level;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class Character extends GameObject implements ICharacter {
    private static final int textureRotationSpeed = 10;

    private DirectionEnum direction;
    private PhisicsEventsEnum previousEvent = PhisicsEventsEnum.NO_COLLISION;
    private boolean alive = true;
    private boolean isEnemy;
    private String name;
    private int textureRotation = 0;

    public Character(String textureName, double[] position, DirectionEnum direction) {
        super(textureName, position, RenderPriorityEnums.CHARACTER.getValue());
        this.direction = direction;
        this.textureRotation = direction.getValue();
        this.name = textureName;
    }

    public Character(String textureName, double[] position, DirectionEnum direction, PhisicsEventsEnum previousEvent) {
        super(textureName, position, RenderPriorityEnums.CHARACTER.getValue());
        this.direction = direction;
        this.textureRotation = direction.getValue();
        this.name = textureName;
        this.previousEvent = previousEvent;
    }

    public void move(double tiles, boolean doesTimeFlow) {
        PhisicsEventsEnum event = GamePhysics.checkCollision(this);

        // continue rotating if it started rotating, thus is not in stable rotation rn.
        if (textureRotation != direction.getValue() && doesTimeFlow) {
            boolean clockwise = GamePhysics.decideClockwiseRotation(textureRotation, direction);
            if (!clockwise) {
                textureRotation = (int)(textureRotation + textureRotationSpeed) % 360;
            } else {
                textureRotation = (int)(textureRotation - textureRotationSpeed + 360) % 360;
            }
        }

        //System.out.println("Moving character " + this.name + " in direction " + this.direction + " with event " +
        // event + " and position " + this.position[0] + ", " + this.position[1]);

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
                    Unicorn.unicornEnteredGoal(true);
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

        if (!event.isRotation()) {
            switch (direction) {
                case UP: position[1] -= tiles; break;
                case DOWN: position[1] += tiles; break;
                case LEFT: position[0] -= tiles; break;
                case RIGHT: position[0] += tiles; break;
            }
        }

        previousEvent = event;
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
        this.visible = false;
        this.alive = false;
    }

    // for level editor visualization purposes
    public void rotateCharacterLE(){
        DirectionEnum direction = this.direction.next();
        this.direction = direction;
        this.textureRotation = direction.getValue();
    }

    protected void setEnemy(boolean isEnemy) {
        this.isEnemy = isEnemy;
    }

    @Override
    public void tick(boolean doesTimeFlow) {
        super.tick(doesTimeFlow);
        move((double)1/GameObject.getFPS(), doesTimeFlow);
    }

    @Override
    public Image getTexture() {
        int orientation = (this.textureRotation / 90);
        if (this.textureRotation % 90 != 0) {
            // in rotation animation
            this.textureIdNow = (25 * ((orientation+2) % 4)) + 16 + ((this.textureRotation)/textureRotationSpeed + 9) % 9;
            return this.textures.get(this.textureIdNow);
        } else {
            // in movement animation
            this.textureIdNow = (25 * ((orientation+2) % 4)) + (int)((Level.getCurrentCalculatedFrame()/4) % 16) + 1;
            return this.textures.get(this.textureIdNow);
        }
    }
}
