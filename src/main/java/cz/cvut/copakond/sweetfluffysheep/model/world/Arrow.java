package cz.cvut.copakond.sweetfluffysheep.model.world;

import cz.cvut.copakond.sweetfluffysheep.model.utils.game.GameObject;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.DirectionEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.RenderPriorityEnums;
import javafx.scene.image.Image;

import java.util.logging.Logger;

/**
 * Arrow class representing an arrow object in the game.
 * The arrow can be rotated and has a limited number of instances.
 */
public class Arrow extends GameObject {
    private static final Logger logger = Logger.getLogger(Arrow.class.getName());

    private static final int NUMBER_OF_TEXTURE_ROTATION = 9;
    private static final int TEXTURE_ROTATION_SPEED = 10;

    private static int arrowCount = 0;
    private int textureRotation = 0;
    private DirectionEnum direction;

    /**
     * Constructor for the Arrow class.
     *
     * @param position   The position of the arrow in the game world.
     * @param maxArrows  The maximum number of arrows allowed in the game.
     */
    public Arrow(double[] position, int maxArrows) {
        super("arrow", position, RenderPriorityEnums.ARROW.getValue());
        arrowCount++;

        if (arrowCount > maxArrows) {
            destroy();
            logger.info("Too many arrows! Limit is " + maxArrows);
        }

        this.direction = DirectionEnum.UP; // default direction
        this.textureRotation = this.direction.getValue();
    }

    public DirectionEnum getDirection() {
        return direction;
    }

    /**
     * On click the direction of the arrow.
     */
    public void rotate() {
        this.textureRotation = (textureRotation + TEXTURE_ROTATION_SPEED) % 360;
        direction = direction.next();
    }

    /**
     * Get the current number of arrows.
     */
    public void destroy() {
        arrowCount--;
        super.visible = false;
    }

    @Override
    public void tick(boolean doesTimeFlow) {
        super.tick(doesTimeFlow);
        if (textureRotation != direction.getValue()) {
            textureRotation = (textureRotation + TEXTURE_ROTATION_SPEED) % 360;
        }
    }

    @Override
    public void resetLevel() {
        super.resetLevel();
        arrowCount = 0;
    }

    // get the current texture based on the rotation and direction
    @Override
    public Image getTexture() {
        this.textureIdNow = ((this.textureRotation)/ TEXTURE_ROTATION_SPEED + NUMBER_OF_TEXTURE_ROTATION) % this.textures.size();
        return this.textures.get(this.textureIdNow);
    }
}
