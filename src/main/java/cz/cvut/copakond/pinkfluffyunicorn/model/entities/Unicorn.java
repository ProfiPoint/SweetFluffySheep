package cz.cvut.copakond.pinkfluffyunicorn.model.entities;

import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.DirectionEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.TextureManager;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.PhisicsEventsEnum;
import javafx.scene.image.Image;
import java.util.List;
import java.util.Map;

public class Unicorn extends Character {
    static int counter = 0;
    boolean inBound = true;
    int ticksToGetInBound = 0;

    public Unicorn(double[] position, DirectionEnum direction) {
        super("unicorn", position, direction, PhisicsEventsEnum.BEFORE_START);
        this.setEnemy(false);
        counter++;
    }

    public void kill() {
        super.kill();
        counter--;
        if (counter <= 0) {
            throw new IllegalStateException("All unicorns are dead :(");
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (inBound == false) {
            // unaffected by arrows and borders
            ticksToGetInBound++;
            if (ticksToGetInBound >= 0) {
                inBound = true;
            }
        } else {
            // affected by arrows and borders
        }
    }
}
