package cz.cvut.copakond.sweetfluffysheep.model.entities;

import cz.cvut.copakond.sweetfluffysheep.model.items.RageItem;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.DirectionEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.GameStatusEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.PhisicsEventsEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.SoundListEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.files.SoundManager;
import javafx.scene.image.Image;

import java.util.logging.Logger;

public class Sheep extends Character {
    private static final Logger logger = Logger.getLogger(Sheep.class.getName());

    private static int counter = 0;
    private static int goalSheep = 1;
    private static int sheepInGoal = 0;

    public Sheep(double[] position, DirectionEnum direction) {
        super("sheep", position, direction, PhisicsEventsEnum.BEFORE_START);
        this.setEnemy(false);
        counter++;
    }

    public static void setGoalSheep(int goal) {goalSheep = goal;}

    public static int getSheepAlive() {
        return counter;
    }

    public static int getSheepInGoal() {
        return sheepInGoal;
    }

    public static void sheepEnteredGoal(boolean entered) {
        if (entered) {
            sheepInGoal++;
            SoundManager.playSound(SoundListEnum.HERO_FINISH);
            logger.info("Sheep entered goal");
        }

        if (counter - sheepInGoal == 0 && sheepInGoal >= goalSheep) {
            logger.info("You win!");
            gameStatus = GameStatusEnum.WIN;
        }
    }

    public void kill() {
        super.kill();
        counter--;
        SoundManager.playSound(SoundListEnum.HERO_DOWN);
        sheepEnteredGoal(false);
        logger.info("Sheep killed");
        if (counter < goalSheep && gameStatus != GameStatusEnum.WIN) {
            logger.info("Game Over");
            gameStatus = GameStatusEnum.LOSE;
        }
    }

    @Override
    public void resetLevel() {
        super.resetLevel();
        counter = 0;
        sheepInGoal = 0;
        gameStatus = GameStatusEnum.RUNNING;
    }

    @Override
    public Image getTexture() {
        if (RageItem.isActive()) {
            this.textureIdNow = getTextureNumber() + 100;
            return this.textures.get(this.textureIdNow);
        }
        return super.getTexture();
    }
}