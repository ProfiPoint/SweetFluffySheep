package cz.cvut.copakond.pinkfluffyunicorn.model.entities;

import cz.cvut.copakond.pinkfluffyunicorn.model.enums.DirectionEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.world.GameObject;
import javafx.scene.image.Image;

import java.util.List;

public class Character extends GameObject implements ICharacter {
    DirectionEnum direction;
    boolean alive = true;
    boolean isEnemy;

    public Character(int[] position, int renderPriority, List<Image> textures, List<int[]> textureSizes, DirectionEnum direction) {
        super(position, renderPriority, textures, textureSizes);
        this.direction = direction;
    }

    public void move(int pixels) {
        switch (direction) {
            case UP:
                position[1] -= pixels;
                break;
            case DOWN:
                position[1] += pixels;
                break;
            case LEFT:
                position[0] -= pixels;
                break;
            case RIGHT:
                position[0] += pixels;
                break;
        }
    }

    public void changeDirection(DirectionEnum direction) {
        this.direction = direction;
    }

    public boolean isAlive() {
        return this.alive;
    }

    public boolean isEnemy() {
        return this.isEnemy;
    }

    public void kill() {
        this.alive = false;
    }

    protected void setEnemy(boolean isEnemy) {
        this.isEnemy = isEnemy;
    }
}
