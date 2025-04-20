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
        if (visibility) {
            this.textureIdNow = 1;
        } else {
            this.textureIdNow = 0;
        }
    }

    @Override
    public Image getTexture() {
        Image img = this.textures.get(this.textureIdNow);

        if (this.direction.getValue() != 0) {
            double width = img.getWidth();
            double height = img.getHeight();

            Canvas canvas = new Canvas(width, height);
            GraphicsContext gc = canvas.getGraphicsContext2D();

            // Draw rotated image onto transparent canvas
            gc.save();
            gc.translate(width / 2, height / 2);
            gc.rotate(this.direction.getOppositeDirection().getValue());
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