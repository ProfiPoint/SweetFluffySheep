package cz.cvut.copakond.pinkfluffyunicorn.model.world;

import cz.cvut.copakond.pinkfluffyunicorn.model.items.Coin;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.game.GameObject;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.DirectionEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.RenderPriorityEnums;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class Goal extends GameObject {
    private boolean locked = true;
    private DirectionEnum direction;

    public Goal(double[] position, DirectionEnum orientation) {
        super("goal", position, RenderPriorityEnums.ARROW.getValue());
        this.direction = orientation;
    }

    public DirectionEnum getDirection() {
        return this.direction;
    }

    public void rotateCharacterLE(){
        this.direction = this.direction.next();
    }

    @Override
    public void tick(boolean doesTimeFlow) {
        super.tick(doesTimeFlow);
        if (this.locked && Coin.getCoinsLeft() <= 0) {
            this.locked = false;
        }
    }

    public boolean isLocked() {
        return locked;
    }

    @Override
    public Image getTexture() {
        int orientation = (this.direction.getValue() / 90);
        this.textureIdNow = (32 * ((orientation+2) % 4)) + (int)((Level.getCurrentCalculatedFrame()/6) % 32);
        if (this.locked) {
            this.textureIdNow += 32*4;
        }
        return this.textures.get(this.textureIdNow);
    }
}
