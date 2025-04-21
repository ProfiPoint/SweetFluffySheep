package cz.cvut.copakond.pinkfluffyunicorn.model.entities;

import cz.cvut.copakond.pinkfluffyunicorn.model.items.RainbowItem;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.DirectionEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.GameStatusEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.PhisicsEventsEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.SoundListEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.files.SoundManager;
import javafx.scene.image.Image;

public class Unicorn extends Character {
    private static int counter = 0;
    private static int goalUnicorns = 1;
    private static int unicornsInGoal = 0;

    public Unicorn(double[] position, DirectionEnum direction) {
        super("unicorn", position, direction, PhisicsEventsEnum.BEFORE_START);
        this.setEnemy(false);
        counter++;
    }

    public static void setGoalUnicorns(int goal) {goalUnicorns = goal;}

    public void kill() {
        super.kill();
        counter--;
        SoundManager.playSound(SoundListEnum.HERO_DOWN);
        unicornEnteredGoal(false);
        if (counter < goalUnicorns && gameStatus != GameStatusEnum.WIN) {
            System.out.println("Game Over");
            gameStatus = GameStatusEnum.LOSE;
        }
    }

    public static void unicornEnteredGoal(boolean entered) {
        if (entered) {
            unicornsInGoal++;
            SoundManager.playSound(SoundListEnum.HERO_FINISH);
        }

        if (counter - unicornsInGoal == 0 && unicornsInGoal >= goalUnicorns) {
            System.out.println("You win!");
            gameStatus = GameStatusEnum.WIN;
        }
    }

    @Override
    public void resetLevel() {
        super.resetLevel();
        counter = 0;
        unicornsInGoal = 0;
        gameStatus = GameStatusEnum.RUNNING;
    }

    @Override
    public Image getTexture() {
        if (RainbowItem.isActive()) {
            this.textureIdNow = getTextureNumber() + 100;
            return this.textures.get(this.textureIdNow);
        }
        return super.getTexture();
    }

    public static int getGoalUnicorns() {
        return goalUnicorns;
    }

    public static int getUnicornsAlive() {
        return counter;
    }
}
