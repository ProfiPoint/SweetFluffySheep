package cz.cvut.copakond.pinkfluffyunicorn.model.entities;

import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.DirectionEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.TextureManager;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.PhisicsEventsEnum;
import javafx.scene.image.Image;
import java.util.List;
import java.util.Map;

public class Cloud extends Character {
    static int counter = 0;

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
        counter--;
    }
}
