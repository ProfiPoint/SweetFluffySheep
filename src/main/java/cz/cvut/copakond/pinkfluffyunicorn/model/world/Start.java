package cz.cvut.copakond.pinkfluffyunicorn.model.world;

import cz.cvut.copakond.pinkfluffyunicorn.model.utils.game.GameObject;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.DirectionEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.RenderPriorityEnums;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class Start extends GameObject {
    private DirectionEnum direction;
    private boolean visible = true;

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