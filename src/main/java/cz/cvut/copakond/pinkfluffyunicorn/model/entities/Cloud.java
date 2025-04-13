package cz.cvut.copakond.pinkfluffyunicorn.model.entities;

import cz.cvut.copakond.pinkfluffyunicorn.model.items.FireItem;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.DirectionEnum;

import java.util.Map;

public class Cloud extends Character {
    private static int counter = 0;

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

    @Override
    public void tick(boolean doesTimeFlow) {
        if (FireItem.isActive()){
            move(0);
        } else {
            super.tick(doesTimeFlow);
        }
    }

    @Override
    public void resetLevel() {
        super.resetLevel();
        counter = 0;
    }
}
