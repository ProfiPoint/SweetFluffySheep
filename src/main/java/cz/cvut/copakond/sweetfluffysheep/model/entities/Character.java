package cz.cvut.copakond.sweetfluffysheep.model.entities;

import cz.cvut.copakond.sweetfluffysheep.model.utils.levels.GamePhysics;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.DirectionEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.game.GameObject;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.PhysicsEventsEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.RenderPriorityEnums;
import cz.cvut.copakond.sweetfluffysheep.model.world.Goal;
import cz.cvut.copakond.sweetfluffysheep.model.world.Level;
import javafx.scene.image.Image;

/**
 * Character class represents a character in the game.
 * It handles the movement, rotation, and collision detection of the character.
 * The character can be either a sheep or an enemy (wolf).
 * <p>
 * All the characters must inherit from this class.
 */
public class Character extends GameObject implements ICharacter {
    private static final int TEXTURE_ROTATION_SPEED = 10;
    private static final int NUMBER_OF_TEXTURE_ROTATION = 9;
    private static final int NUMBER_OF_TEXTURE_ANIMATION = 16;
    private static final int NUMBER_OF_TEXTURES = NUMBER_OF_TEXTURE_ROTATION + NUMBER_OF_TEXTURE_ANIMATION;

    // the texture will update once every "this number" of frames
    private static final int TEXTURE_CHANGE_FRAME_COEFFICIENT = (int) Math.ceil((double) GameObject.getFPS() / 15);

    private boolean alive = true;
    private boolean isEnemy;
    private int textureRotation;

    private DirectionEnum direction;
    private PhysicsEventsEnum previousEvent = PhysicsEventsEnum.NO_COLLISION;
    private PhysicsEventsEnum previousSpeedEvent = PhysicsEventsEnum.NO_COLLISION;

    /**
     * Constructor for the Character class.
     *
     * @param textureName the name of the texture
     * @param position    the position of the character
     * @param direction   the direction of the character
     */
    public Character(String textureName, double[] position, DirectionEnum direction) {
        super(textureName, position, RenderPriorityEnums.CHARACTER.getValue());
        this.direction = direction;
        this.textureRotation = direction.getValue();
    }

    /**
     * Constructor for the Character class with the previous event.
     *
     * @param textureName   the name of the texture
     * @param position      the position of the character
     * @param direction     the direction of the character
     * @param previousEvent the previous event of the character
     */
    public Character(String textureName, double[] position, DirectionEnum direction, PhysicsEventsEnum previousEvent) {
        super(textureName, position, RenderPriorityEnums.CHARACTER.getValue());
        this.direction = direction;
        this.textureRotation = direction.getValue();
        this.previousEvent = previousEvent;
    }

    public DirectionEnum getDirection() {
        return this.direction;
    }

    public PhysicsEventsEnum getPreviousEvent() {
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

    /**
     * Moves the character in the direction it is facing.
     * The character can be rotated by walls or arrows.
     * The character can also be slowed down by other characters in front of it.
     *
     * @param tilesSpeed the speed of the character in tiles per second
     * @param doesTimeFlow if true, the character will visually rotate
     */
    public void move(double tilesSpeed, boolean doesTimeFlow) {
        if (!this.alive || !this.visible) {
            return;
        }

        PhysicsEventsEnum event = GamePhysics.checkCollision(this);

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
                previousEvent = PhysicsEventsEnum.SHEEP_KILLED;
                return;
            case IN_GOAL:
                // do not rotate or get affected by arrows, after goal
                if (previousEvent != PhysicsEventsEnum.IN_GOAL) {
                    Sheep.sheepEnteredGoal(true);
                    this.direction = Goal.getGlobalDirection();
                }
                break;
            case ROTATION_OPPOSITE:
                this.direction = this.direction.getOppositeDirection();
                break;
            default:
                // arrow or wall collision will rotate the character
                this.direction = PhysicsEventsEnum.convertPhysicsEvent(event);
                break;
        }

        // if there is a character too closely before the character, it will slow down
        if (event.isNotRotation() && previousSpeedEvent != PhysicsEventsEnum.SLOWDOWN) {
            PhysicsEventsEnum speedEvent = GamePhysics.checkCharactersStruggle(this);
            if (speedEvent == PhysicsEventsEnum.SLOWDOWN) {
                tilesSpeed = tilesSpeed / 2;
                previousSpeedEvent = PhysicsEventsEnum.SLOWDOWN;
            }
        } else if (event.isNotRotation() && event != PhysicsEventsEnum.SLOWDOWN) {
            // the character must always end up in the same tileSpeed offset, so if the speed is halved, it will be
            // halved also in the next frame, to get to the same tileSpeed offset to prevent unique collisions behavior
            tilesSpeed = tilesSpeed / 2;
            previousSpeedEvent = PhysicsEventsEnum.NO_COLLISION;
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

    /**
     * Rotates the character to the right.
     * The character will rotate clockwise.
     */
    public void rotateCharacterLE(){
        DirectionEnum direction = this.direction.next();
        this.direction = direction;
        this.textureRotation = direction.getValue();
    }

    /**
     * Kills the character, making it invisible and not alive.
     */
    void kill() {
        this.visible = false;
        this.alive = false;
    }

    /**
     * Returns the correct texture for the character, based on the current rotation state.
     *
     * @return the texture number of the character
     */
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