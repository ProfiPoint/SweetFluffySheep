package cz.cvut.copakond.sweetfluffysheep.model.entities;

import cz.cvut.copakond.sweetfluffysheep.model.utils.levels.GamePhysics;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.DirectionEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.game.GameObject;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.PhisicsEventsEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.RenderPriorityEnums;
import cz.cvut.copakond.sweetfluffysheep.model.world.Level;
import javafx.scene.image.Image;

public class Character extends GameObject implements ICharacter {
    private static final int TEXTURE_ROTATION_SPEED = 10;
    private static final int TEXTURE_CHANGE_FRAME_COEFFICIENT = (int) Math.ceil((double) GameObject.getFPS() / 15);
    private static final int NUMBER_OF_TEXTURE_ROTATION = 9;
    private static final int NUMBER_OF_TEXTURE_ANIMATION = 16;
    private static final int NUMBER_OF_TEXTURES = NUMBER_OF_TEXTURE_ROTATION + NUMBER_OF_TEXTURE_ANIMATION;

    private boolean alive = true;
    private boolean isEnemy;
    private int textureRotation;

    private DirectionEnum direction;
    private PhisicsEventsEnum previousEvent = PhisicsEventsEnum.NO_COLLISION;
    private PhisicsEventsEnum previousSpeedEvent = PhisicsEventsEnum.NO_COLLISION;

    public Character(String textureName, double[] position, DirectionEnum direction) {
        super(textureName, position, RenderPriorityEnums.CHARACTER.getValue());
        this.direction = direction;
        this.textureRotation = direction.getValue();
    }

    public Character(String textureName, double[] position, DirectionEnum direction, PhisicsEventsEnum previousEvent) {
        super(textureName, position, RenderPriorityEnums.CHARACTER.getValue());
        this.direction = direction;
        this.textureRotation = direction.getValue();
        this.previousEvent = previousEvent;
    }

    public DirectionEnum getDirection() {
        return this.direction;
    }

    public PhisicsEventsEnum getPreviousEvent() {
        return this.previousEvent;
    }

    public boolean isAlive() {
        return this.alive;
    }

    public boolean isEnemy() {
        return this.isEnemy;
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
        this.textureIdNow = getTextureNumber();
        return this.textures.get(this.textureIdNow);
    }

    public void move(double tilesSpeed, boolean doesTimeFlow) {
        if (!this.alive || !this.visible) {
            return;
        }

        PhisicsEventsEnum event = GamePhysics.checkCollision(this);

        // continue rotating if it started rotating, thus is not in stable rotation rn.
        if (textureRotation != direction.getValue() && doesTimeFlow) {
            boolean clockwise = GamePhysics.decideClockwiseRotation(textureRotation, direction);
            if (!clockwise) {
                textureRotation = (textureRotation + TEXTURE_ROTATION_SPEED) % 360;
            } else {
                textureRotation = (textureRotation - TEXTURE_ROTATION_SPEED + 360) % 360;
            }
        }

        switch (event) {
            case NO_COLLISION, BEFORE_START, ROTATION_STUCK_4WALLS:
                // do not rotate or get affected by arrows, before start
                break;
            case SHEEP_KILLED:
                this.kill();
                previousEvent = PhisicsEventsEnum.SHEEP_KILLED;
                return;
            case IN_GOAL:
                // do not rotate or get affected by arrows, after goal
                if (previousEvent != PhisicsEventsEnum.IN_GOAL) {
                    Sheep.sheepEnteredGoal(true);
                }
                break;
            case ROTATION_OPPOSITE:
                this.direction = this.direction.getOppositeDirection();
                break;
            default:
                // arrow or wall collision will rotate the character
                this.direction = PhisicsEventsEnum.convertPhysicsEvent(event);
                break;
        }

        // if there is a character too closely before the character, it will slow down
        if (event.isNotRotation() && previousSpeedEvent != PhisicsEventsEnum.SLOWDOWN) {
            PhisicsEventsEnum speedEvent = GamePhysics.checkCharactersStruggle(this);
            if (speedEvent == PhisicsEventsEnum.SLOWDOWN) {
                tilesSpeed = tilesSpeed / 2;
                previousSpeedEvent = PhisicsEventsEnum.SLOWDOWN;
            }
        } else if (event.isNotRotation() && event != PhisicsEventsEnum.SLOWDOWN) {
            // the character must always end up in the same tileSpeed offset, so if the speed is halved, it will be
            // halved also in the next frame, to get to the same tileSpeed offset to prevent unique collisions behavior
            tilesSpeed = tilesSpeed / 2;
            previousSpeedEvent = PhisicsEventsEnum.NO_COLLISION;
        }

        // do not move if rotating in that frame
        if (event.isNotRotation()) {
            switch (direction) {
                case UP: position[1] -= tilesSpeed; break;
                case DOWN: position[1] += tilesSpeed; break;
                case LEFT: position[0] -= tilesSpeed; break;
                case RIGHT: position[0] += tilesSpeed; break;
            }
        }

        previousEvent = event;
    }

    // for level editor visualization purposes
    public void rotateCharacterLE(){
        DirectionEnum direction = this.direction.next();
        this.direction = direction;
        this.textureRotation = direction.getValue();
    }

    void kill() {
        this.visible = false;
        this.alive = false;
    }

    // get the correct texture for the character, based on the current rotation state
    protected int getTextureNumber() {
        int orientation = (this.textureRotation / 90);
        if (this.textureRotation % 90 != 0) {
            // in rotation animation
            return (NUMBER_OF_TEXTURES * ((orientation+2) % 4)) + NUMBER_OF_TEXTURE_ANIMATION +
                    ((this.textureRotation) / TEXTURE_ROTATION_SPEED + NUMBER_OF_TEXTURE_ROTATION) % NUMBER_OF_TEXTURE_ROTATION;
        } else {
            // in movement animation
            return (NUMBER_OF_TEXTURES * ((orientation+2) % 4)) + (int)((Level.getCurrentCalculatedFrame() /
                    TEXTURE_CHANGE_FRAME_COEFFICIENT) % NUMBER_OF_TEXTURE_ANIMATION) + 1;
        }
    }
}