package cz.cvut.copakond.sweetfluffysheep.model.entities;

import cz.cvut.copakond.sweetfluffysheep.model.items.FireItem;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.DirectionEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.SoundListEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.files.SoundManager;
import javafx.scene.image.Image;

import java.util.logging.Logger;

public class Wolf extends Character {
    private static final Logger logger = Logger.getLogger(Wolf.class.getName());

    private boolean canMove = true;

    public Wolf(double[] position, DirectionEnum direction) {
        super("wolf", position, direction);
        this.setEnemy(true);
    }

    @Override
    public void tick(boolean doesTimeFlow) {
        if (FireItem.isActive()) {
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
        return this.textures.get(this.textureIdNow + 100);
    }

    @Override
    public void resetLevel() {
        super.resetLevel();
    }

    public void kill() {
        super.kill();
        SoundManager.playSound(SoundListEnum.HERO_ENEMY_COLLISION);
        SoundManager.playSound(SoundListEnum.ENEMY_DOWN);
        logger.info("Wolf killed");
    }
}