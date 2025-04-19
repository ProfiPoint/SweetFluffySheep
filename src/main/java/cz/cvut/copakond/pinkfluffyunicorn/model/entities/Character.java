package cz.cvut.copakond.pinkfluffyunicorn.model.entities;

import cz.cvut.copakond.pinkfluffyunicorn.model.utils.levels.GamePhysics;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.DirectionEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.game.GameObject;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.PhisicsEventsEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.RenderPriorityEnums;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class Character extends GameObject implements ICharacter {
    private static final int textureRotationSpeed = 10;

    private DirectionEnum direction;
    private PhisicsEventsEnum previousEvent = PhisicsEventsEnum.NO_COLLISION;
    private boolean alive = true;
    private boolean isEnemy;
    private String name;
    private int textureRotation = 0;

    public Character(String textureName, double[] position, DirectionEnum direction) {
        super(textureName, position, RenderPriorityEnums.CHARACTER.getValue());
        this.direction = direction;
        this.textureRotation = direction.getValue();
        this.name = textureName;
    }

    public Character(String textureName, double[] position, DirectionEnum direction, PhisicsEventsEnum previousEvent) {
        super(textureName, position, RenderPriorityEnums.CHARACTER.getValue());
        this.direction = direction;
        this.textureRotation = direction.getValue();
        this.name = textureName;
        this.previousEvent = previousEvent;
    }

    public void move(double tiles) {
        PhisicsEventsEnum event = GamePhysics.checkCollision(this);

        // continue rotating if it started rotating, thus is not in stable rotation rn.
        if (textureRotation != direction.getValue()) {
            boolean clockwise = GamePhysics.decideClockwiseRotation(textureRotation, direction);
            if (!clockwise) {
                textureRotation = (int)(textureRotation + textureRotationSpeed) % 360;
            } else {
                textureRotation = (int)(textureRotation - textureRotationSpeed + 360) % 360;
            }
        }

        //System.out.println("Moving character " + this.name + " in direction " + this.direction + " with event " +
        // event + " and position " + this.position[0] + ", " + this.position[1]);

        switch (event) {
            case NO_COLLISION:
                break;
            case SHEEP_KILLED:
                this.kill();
                previousEvent = PhisicsEventsEnum.SHEEP_KILLED;
                return;
            case IN_GOAL:
                // CAN NOT DIE + CAN NOT ROTATE, AND IT WILL

                if (previousEvent != PhisicsEventsEnum.IN_GOAL) {
                    Unicorn.unicornEnteredGoal(true);
                }
                previousEvent = PhisicsEventsEnum.IN_GOAL;
                break;
            case BEFORE_START:

                break;
            case ROTATION_OPPOSITE:
                this.direction = this.direction.getOppositeDirection();
                break;
            case ROTATION_STUCK_4WALLS:
                break;
            default:
                this.direction = PhisicsEventsEnum.convertPhisicsEvent(event);
                break;
        }

        if (!event.isRotation()) {
            switch (direction) {
                case UP: position[1] -= tiles; break;
                case DOWN: position[1] += tiles; break;
                case LEFT: position[0] -= tiles; break;
                case RIGHT: position[0] += tiles; break;
            }
        }

        previousEvent = event;
    }

    public DirectionEnum getDirection() {
        return this.direction;
    }

    public PhisicsEventsEnum getPreviousEvent() {return this.previousEvent;}

    public void changeDirection(DirectionEnum direction) {
        this.direction = direction;
    }

    public boolean isAlive() {
        return this.alive;
    }

    public boolean isEnemy() {
        return this.isEnemy;
    }

    void kill() {
        this.visible = false;
        this.alive = false;
    }

    // for level editor visualization purposes
    public void rotateCharacterLE(){
        DirectionEnum direction = this.direction.next();
        this.direction = direction;
        this.textureRotation = direction.getValue();
    }

    protected void setEnemy(boolean isEnemy) {
        this.isEnemy = isEnemy;
    }

    @Override
    public void tick(boolean doesTimeFlow) {
        super.tick(doesTimeFlow);
        move((double)1/GameObject.getFPS());
    }

    @Override
    public Image getTexture() {
        Image img = this.textures.get(this.textureIdNow);

        if (this.textureRotation != 0) {
            double width = img.getWidth();
            double height = img.getHeight();

            Canvas canvas = new Canvas(width, height);
            GraphicsContext gc = canvas.getGraphicsContext2D();

            // Draw rotated image onto transparent canvas
            gc.save();
            gc.translate(width / 2, height / 2);
            gc.rotate(this.textureRotation);
            gc.translate(-width / 2, -height / 2);
            gc.drawImage(img, 0, 0);
            gc.restore();

            // Set up snapshot parameters with transparency
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT); // this is the key to keeping transparency

            WritableImage rotatedImg = new WritableImage((int) width, (int) height);
            canvas.snapshot(params, rotatedImg);

            return rotatedImg;
        }

        return img;
    }
}
