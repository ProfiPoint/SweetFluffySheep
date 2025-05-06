package cz.cvut.copakond.sweetfluffysheep.model.world;

import cz.cvut.copakond.sweetfluffysheep.model.items.Coin;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.SoundListEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.files.SoundManager;
import cz.cvut.copakond.sweetfluffysheep.model.utils.game.GameObject;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.DirectionEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.RenderPriorityEnums;
import javafx.scene.image.Image;

import java.util.logging.Logger;

/**
 * Represents a goal object in the game.
 * The goal is initially locked and can be unlocked by collecting coins.
 * The goal has a direction and can be rotated.
 * The texture of the goal changes based on its state (locked/unlocked).
 */
public class Goal extends GameObject {
    private static final Logger logger = Logger.getLogger(Goal.class.getName());
    
    // 10x per second it will update the anim texture
    private static final int TEXTURE_CHANGE_FRAME_COEFFICIENT = (int) Math.ceil((double) GameObject.getFPS() / 10);
    private boolean locked = true;
    private boolean lockedTexture = true;
    private DirectionEnum direction;
    private static DirectionEnum globalDirection;

    /**
     * Constructor for the Goal class.
     *
     * @param position   The position of the goal in the game world.
     * @param orientation The initial direction of the goal.
     */
    public Goal(double[] position, DirectionEnum orientation) {
        super("goal", position, RenderPriorityEnums.ARROW.getValue());
        this.direction = orientation;
        globalDirection = orientation;
    }

    /**
     * Returns the correct direction of the goal, because visually it is rotated 90 degrees.
     *
     * @return The global direction of the goal.
     */
    public static DirectionEnum getGlobalDirection() {
        // because the texture is rotated 90 degrees
        switch (globalDirection) {
            case UP -> {
                return DirectionEnum.LEFT;
            }
            case LEFT -> {
                return DirectionEnum.DOWN;
            }
            case RIGHT -> {
                return DirectionEnum.RIGHT;
            }
            default -> {
                return DirectionEnum.UP;
            }
        }
    }

    public DirectionEnum getDirection() {
        return this.direction;
    }

    public boolean isLocked() {
        return locked;
    }

    /**
     * Rotates the goal character in the level editor.
     */
    public void rotateCharacterLE(){
        this.direction = this.direction.next();
        globalDirection = this.direction;
    }

    /**
     * Makes the goal visible and unlocked in the level editor.
     */
    public void unlockForLevelEditor() {
        this.lockedTexture = false;
    }

    @Override
    public void tick(boolean doesTimeFlow) {
        super.tick(doesTimeFlow);
        if (this.locked && Coin.getCoinsLeft() <= 0) {
            this.locked = false;
            this.lockedTexture = false;
            SoundManager.playSound(SoundListEnum.GOAL_UNLOCKED);
            logger.info("Goal unlocked!");
        }
    }

    @Override
    public Image getTexture() {
        int orientation = (this.direction.getValue() / 90);
        this.textureIdNow = (32 * ((orientation+2) % 4)) + (int)((Level.getCurrentCalculatedFrame()/TEXTURE_CHANGE_FRAME_COEFFICIENT) % 32);
        if (this.locked && this.lockedTexture) {
            this.textureIdNow += 32*4;

        }
        return this.textures.get(this.textureIdNow);
    }
}
