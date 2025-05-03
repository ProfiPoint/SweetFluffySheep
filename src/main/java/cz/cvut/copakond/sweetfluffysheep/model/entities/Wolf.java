package cz.cvut.copakond.sweetfluffysheep.model.entities;

import cz.cvut.copakond.sweetfluffysheep.model.items.FreezeItem;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.DirectionEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.SoundListEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.files.SoundManager;
import javafx.scene.image.Image;

import java.util.logging.Logger;

/**
 * Represents a Wolf enemy in the game.
 * The Wolf can move and attack the player.
 * It has a special behavior when frozen by a FreezeItem.
 */
public class Wolf extends Character {
    private static final Logger logger = Logger.getLogger(Wolf.class.getName());
    private boolean canMove = true;

    /**
     * Constructor for the Wolf class.
     *
     * @param position  The initial position of the Wolf.
     * @param direction The initial direction of the Wolf.
     */
    public Wolf(double[] position, DirectionEnum direction) {
        super("wolf", position, direction);
        this.setEnemy(true);
    }

    @Override
    public void tick(boolean doesTimeFlow) {
        if (FreezeItem.isActive()) {
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
    public void kill() {
        super.kill();
        SoundManager.playSound(SoundListEnum.HERO_ENEMY_COLLISION);
        SoundManager.playSound(SoundListEnum.ENEMY_DOWN);
        logger.info("Wolf killed");
    }
}