package cz.cvut.copakond.sweetfluffysheep.model.world;

import cz.cvut.copakond.sweetfluffysheep.model.utils.game.GameObject;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.DirectionEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.RenderPriorityEnums;
import javafx.scene.image.Image;

/**
 * Represents the starting point of the game.
 * The starting point is represented by an arrow that indicates the direction of the character.
 * The arrow can be rotated to change the direction of the character.
 */
public class Start extends GameObject {
    private DirectionEnum direction;
    private boolean visible = true;

    /**
     * Constructor for the Start class.
     *
     * @param position   The position of the starting point in the game world.
     * @param orientation The initial direction of the arrow.
     */
    public Start(double[] position, DirectionEnum orientation) {
        super("start", position, RenderPriorityEnums.ARROW.getValue());
        this.direction = orientation;
    }

    public DirectionEnum getDirection() {
        return this.direction;
    }

    public void rotateCharacterLE(){
        this.direction = this.direction.next();
    }

    public void setVisibility(boolean visibility) {
        this.visible = visibility;
    }

    @Override
    public Image getTexture() {
        if (visible) {
            this.textureIdNow = (direction.getValue() / 90 + 1) % 4 + 1;
        } else {
            this.textureIdNow = 0;
        }
        return this.textures.get(this.textureIdNow);
    }

}