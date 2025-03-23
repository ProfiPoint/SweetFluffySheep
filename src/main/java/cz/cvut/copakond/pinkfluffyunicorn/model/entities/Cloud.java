package cz.cvut.copakond.pinkfluffyunicorn.model.entities;

import cz.cvut.copakond.pinkfluffyunicorn.model.enums.DirectionEnum;

public class Cloud {
    private int x;
    private int y;
    private int speed;

    public Cloud(int x, int y, int speed) {
        this.x = x;
        this.y = y;
        this.speed = speed;
    }

    public void move() {
        // move the cloud
    }

    // changes the direction of the character
    public void changeDirection(DirectionEnum direction) {
        // change the direction of the cloud
    }

    // manages the character's state
    public boolean isAlive() {
        // check if the cloud is alive
        return true;
    }
    public boolean isEnemy() {
        // check if the cloud is an enemy
        return false;
    }
    public void kill() {
        // kill the cloud
    }

    
}
