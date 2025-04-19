package cz.cvut.copakond.pinkfluffyunicorn.model.entities;

import cz.cvut.copakond.pinkfluffyunicorn.model.items.FireItem;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.DirectionEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.SoundListEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.files.SoundManager;
import cz.cvut.copakond.pinkfluffyunicorn.model.world.Level;
import javafx.scene.image.Image;

import java.util.Map;

public class Cloud extends Character {
    private static int counter = 0;
    private boolean canMove = true;

    public Cloud(double[] position, DirectionEnum direction) {
        super("cloud", position, direction);
        this.setEnemy(true);
        counter++;
    }

    public Cloud(double[] position, DirectionEnum direction, Map<int[], Integer> tileMap) {
        super("cloud", position, direction);
        this.setEnemy(true);
        counter++;
    }

    public void kill() {
        super.kill();
        SoundManager.playSound(SoundListEnum.HERO_ENEMY_COLLISION);
        SoundManager.playSound(SoundListEnum.ENEMY_DOWN);
        counter--;
    }

    @Override
    public void tick(boolean doesTimeFlow) {
        if (FireItem.isActive()){
            move(0, doesTimeFlow);
            canMove = false;
        } else {
            super.tick(doesTimeFlow);
            canMove = true;
        }
    }

    @Override
    public Image getTexture() {
        if (canMove) {
            return super.getTexture();
        }
        return this.textures.get(this.textureIdNow);
    }

    @Override
    public void resetLevel() {
        super.resetLevel();
        counter = 0;
    }
}
