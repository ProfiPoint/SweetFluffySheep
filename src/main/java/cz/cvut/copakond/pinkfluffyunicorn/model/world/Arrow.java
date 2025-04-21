package cz.cvut.copakond.pinkfluffyunicorn.model.world;

import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.TextureListEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.game.GameObject;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.DirectionEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.RenderPriorityEnums;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class Arrow extends GameObject {
    private int maxArrows = 10;
    private static final int textureRotationSpeed = 10;
    private static int arrowCount = 0;
    private int textureRotation = 0;
    private DirectionEnum direction;

    public Arrow(double[] position, int maxArrows) {
        super("arrow", position, RenderPriorityEnums.ARROW.getValue());
        arrowCount++;
        this.maxArrows = maxArrows;
        if (arrowCount > maxArrows) {
            destroy();
            System.out.println("Too many arrows! Limit is " + maxArrows);
        }
        this.direction = DirectionEnum.UP;
        this.textureRotation = this.direction.getValue();
    }

    // on click rotate arrow
    public void rotate() {
        this.textureRotation = (textureRotation + textureRotationSpeed) % 360;
        direction = direction.next();
    }

    public DirectionEnum getDirection() {
        return direction;
    }

    public void destroy() {
        arrowCount--;
        super.visible = false;
    }

    @Override // tick
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

    @Override
    public Image getTexture() {
        this.textureIdNow = ((this.textureRotation)/textureRotationSpeed + 9) % this.textures.size();
        return this.textures.get(this.textureIdNow);
    }
}
