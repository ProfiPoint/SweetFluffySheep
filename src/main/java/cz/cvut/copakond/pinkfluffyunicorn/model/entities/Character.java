package cz.cvut.copakond.pinkfluffyunicorn.model.entities;

import cz.cvut.copakond.pinkfluffyunicorn.model.enums.DirectionEnum;

public class Character {
    int[] position;
    DirectionEnum direction;
    boolean alive;
    boolean enemy;

    public Character(int x, int y, DirectionEnum direction, boolean enemy) {
        this.position = new int[]{x, y};
        this.direction = direction;
        this.alive = true;
        this.enemy = enemy;
    }



    
}
