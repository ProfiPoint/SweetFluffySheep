package cz.cvut.copakond.pinkfluffyunicorn.model.entities;

import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.DirectionEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.TextureManager;
import javafx.scene.image.Image;
import java.util.List;

public class Unicorn extends Character {
    static int counter = 0;

    public Unicorn(int[] position, DirectionEnum direction) {
        super("unicorn", position, direction);
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
}
