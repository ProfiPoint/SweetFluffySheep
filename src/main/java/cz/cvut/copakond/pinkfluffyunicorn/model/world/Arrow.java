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

public class Arrow extends GameObject {
    private final int maxArrows = 10;
    private static final int textureRotationSpeed = 10;

    private static int arrowCount = 0;
    private int textureRotation = 0;
    private DirectionEnum direction;

    public Arrow(double[] position) {
        super("arrow", position, RenderPriorityEnums.ARROW.getValue());
        arrowCount++;
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
        if (textureRotation % 90 != 0) {
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
        Image img = this.textures.get(this.textureIdNow);

        if (this.textureRotation != 0) {
            double width = img.getWidth();
            double height = img.getHeight();

            Canvas canvas = new Canvas(width, height);
            GraphicsContext gc = canvas.getGraphicsContext2D();

            // Draw rotated image onto transparent canvas
            gc.save();
            gc.translate(width / 2, height / 2);
            gc.rotate(this.textureRotation);
            gc.translate(-width / 2, -height / 2);
            gc.drawImage(img, 0, 0);
            gc.restore();

            // Set up snapshot parameters with transparency
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT); // this is the key to keeping transparency

            WritableImage rotatedImg = new WritableImage((int) width, (int) height);
            canvas.snapshot(params, rotatedImg);

            return rotatedImg;
        }

        return img;
    }
}
