package cz.cvut.copakond.pinkfluffyunicorn.model.entities;

import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.DirectionEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.TextureManager;
import javafx.scene.image.Image;
import java.util.List;

public class Cloud extends Character {
    static int counter = 0;

    public Cloud(double[] position, DirectionEnum direction) {
        super("cloud", position, direction);
        this.setEnemy(true);
        counter++;
    }

    public void kill() {
        super.kill();
        counter--;
    }
}
