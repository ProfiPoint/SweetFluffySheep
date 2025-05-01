package cz.cvut.copakond.pinkfluffyunicorn.model.world;

import cz.cvut.copakond.pinkfluffyunicorn.model.utils.game.GameObject;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.DirectionEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.RenderPriorityEnums;
import javafx.scene.image.Image;

import java.util.logging.Logger;

public class Arrow extends GameObject {
    private static final Logger logger = Logger.getLogger(Arrow.class.getName());

    private static final int NUMBER_OF_TEXTURE_ROTATION = 9;
    private static final int textureRotationSpeed = 10;
    private static int arrowCount = 0;
    private int textureRotation = 0;
    private DirectionEnum direction;

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

    // on click rotate arrow
    public void rotate() {
        this.textureRotation = (textureRotation + textureRotationSpeed) % 360;
        direction = direction.next();
    }

    public void destroy() {
        arrowCount--;
        super.visible = false;
    }

    @Override
    public void tick(boolean doesTimeFlow) {
        super.tick(doesTimeFlow);
        if (textureRotation != direction.getValue()) {
            textureRotation = (textureRotation + textureRotationSpeed) % 360;
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
        this.textureIdNow = ((this.textureRotation)/textureRotationSpeed + NUMBER_OF_TEXTURE_ROTATION) % this.textures.size();
        return this.textures.get(this.textureIdNow);
    }
}
