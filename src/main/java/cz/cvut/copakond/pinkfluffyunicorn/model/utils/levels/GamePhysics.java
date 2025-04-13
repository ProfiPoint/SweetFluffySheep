package cz.cvut.copakond.pinkfluffyunicorn.model.utils.levels;

import cz.cvut.copakond.pinkfluffyunicorn.model.entities.Character;
import cz.cvut.copakond.pinkfluffyunicorn.model.entities.Cloud;
import cz.cvut.copakond.pinkfluffyunicorn.model.items.FireItem;
import cz.cvut.copakond.pinkfluffyunicorn.model.items.IItem;
import cz.cvut.copakond.pinkfluffyunicorn.model.items.Item;
import cz.cvut.copakond.pinkfluffyunicorn.model.items.RainbowItem;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.GameObject;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.DirectionEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.ErrorMsgsEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.PhisicsEventsEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.world.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// "public static class GamePhysics"
public class GamePhysics {
    private static final double collisionLimit = (1 / (double) GameObject.getFPS()) * GameObject.getCollisionLimit() * 1 / 2 * 1.1;
    // dynamically calculated limit for collision detection, so even in max speed the collision will be detected
    // for normal settings it is 0.0367, and the max speed per tick is 0.0667, so because the collision is checked
    // around, it is 0.0367 * 2 = 0.0734, so the collision will be detected even in max speed

    private static int[] mapSize;
    private static Map<int[], Tile> tileMap;
    private static List<Cloud> enemies;
    private static List<IItem> items;
    private static Start start;
    private static Goal goal;
    private static List<Arrow> arrows;

    private static boolean initialized = false;

    public static void loadMapObjects(int[] mapSize, Start start, Goal goal, List<Tile> tiles, List<Cloud> enemies,
                                      List<IItem> items, List<Arrow> arrows) {
        if (initialized) {
            ErrorMsgsEnum.PHISICS_ALREADY_INIT.getValue();
        }

        GamePhysics.initialized = true;
        GamePhysics.mapSize = mapSize;
        GamePhysics.enemies = enemies;
        GamePhysics.items = items;
        GamePhysics.start = start;
        GamePhysics.goal = goal;
        GamePhysics.arrows = arrows;

        GamePhysics.tileMap = new HashMap<int[], Tile>();
        for (Tile tile : tiles) {
            GamePhysics.tileMap.put(new int[]{(int) tile.getX(), (int) tile.getY()}, tile);
        }
    }

    public static void unloadMapObjects() {
        GamePhysics.initialized = false;
        GamePhysics.mapSize = null;
        GamePhysics.tileMap = null;
        GamePhysics.enemies = null;
        GamePhysics.items = null;
        GamePhysics.start = null;
        GamePhysics.goal = null;
        GamePhysics.arrows = null;
    }

    static double getDistanceOnePoint(double position1, double position2) {
        return Math.abs(position2 - position1);
    }

    static int[] getTilePosition(double x, double y) {
        return new int[]{(int) Math.round(x), (int) Math.round(y)};
    }

    // custom method to check if the tile is in the tileMap, because tileMap.containsKey() does not work for arrays key
    public static boolean tileExists(int[] tilePos) {
        for (Map.Entry<int[], Tile> entry : tileMap.entrySet()) {
            int[] key = entry.getKey();
            if (key[0] == tilePos[0] && key[1] == tilePos[1]) {
                return true;
            }
        }
        return false;
    }

    static boolean isCollidingWithWall(int currentTilePos, double characterPos, int[] targetTilePos) {
        return getDistanceOnePoint(currentTilePos, characterPos) <= collisionLimit && !tileExists(targetTilePos);
    }

    static double getDistance(double[] position1, double[] position2) {
        return Math.sqrt(Math.pow(position2[0] - position1[0], 2) + Math.pow(position2[1] - position1[1], 2));
    }

    static double distanceBetweenObjects(GameObject gameObject1, GameObject gameObject2) {
        double x1 = gameObject1.getX();
        double y1 = gameObject1.getY();
        double x2 = gameObject2.getX();
        double y2 = gameObject2.getY();

        return getDistance(new double[]{x1, y1}, new double[]{x2, y2});
    }

    static boolean isColliding(GameObject gameObject1, GameObject gameObject2) {
        double distance = distanceBetweenObjects(gameObject1, gameObject2);
        if (distance <= collisionLimit) {
        }
        return distance <= collisionLimit;
    }

    static int[] getTargetTilePosition(DirectionEnum direction, Character character) {
        return switch (direction) {
            case RIGHT -> getTilePosition(character.getX() + 1, character.getY());
            case LEFT -> getTilePosition(character.getX() - 1, character.getY());
            case UP -> getTilePosition(character.getX(), character.getY() - 1);
            case DOWN -> getTilePosition(character.getX(), character.getY() + 1);
            default -> null;
        };
    }

    static boolean isItWall(DirectionEnum playerDirection, int[] currentTilePos, Character character, int[] targetTilePos) {
        if (playerDirection == DirectionEnum.UP) {
            return (isCollidingWithWall(currentTilePos[1]-1, character.getY() - 1, targetTilePos));
        } else if (playerDirection == DirectionEnum.DOWN) {
            return (isCollidingWithWall(currentTilePos[1]+1, character.getY() + 1, targetTilePos));
        } else if (playerDirection == DirectionEnum.LEFT) {
            return (isCollidingWithWall(currentTilePos[0]-1, character.getX() - 1, targetTilePos));
        } else if (playerDirection == DirectionEnum.RIGHT) {
            return (isCollidingWithWall(currentTilePos[0]+1, character.getX() + 1, targetTilePos));
        } else {
            return false;
        }
    }

    public static PhisicsEventsEnum checkCollision(Character character) {
        if (!character.isEnemy()){
            if (character.getPreviousEvent() == PhisicsEventsEnum.BEFORE_START && !isColliding(character, start)) {
                return PhisicsEventsEnum.BEFORE_START;
            }
            if (character.getPreviousEvent() == PhisicsEventsEnum.IN_GOAL || (isColliding(character, goal) && !goal.isLocked())) {
                return PhisicsEventsEnum.IN_GOAL;
            }
            if (!FireItem.isActive() && !RainbowItem.isActive()) {
                for (Cloud enemy : enemies) {
                    if (enemy.isVisible() && isColliding(character, enemy)) {
                        return PhisicsEventsEnum.SHEEP_KILLED;
                    }
                }
            }

            if (RainbowItem.isActive()){
                for (Cloud enemy : enemies) {
                    if (character.isVisible() && isColliding(character, enemy)) {
                        enemy.kill();
                    }
                }
            }

            for (IItem item : items) {
                if (item.isVisible() && isColliding(character, (Item) item)) {
                    boolean used = item.use();
                    if (used) {
                        System.out.println("Item used: " + item.getItemEffect());
                    }
                }
            }
        }



        DirectionEnum arrowDirectionChange = null;
        for (Arrow arrow : arrows) {
            if (isColliding(character, arrow)) {
                DirectionEnum arrowDirection = arrow.getDirection();
                arrowDirectionChange = arrowDirection;
                break;
            }
        }

        // if characters position is either around X.5 or Y.5, check if there is a wall
        int[] currentTilePos = getTilePosition(character.getX(), character.getY());
        int[] targetTilePos = null;

        if (arrowDirectionChange != null) {
            targetTilePos = getTargetTilePosition(arrowDirectionChange, character);


            if (arrowDirectionChange == character.getDirection()) {
                 // CHECK IF THE ARROW DIRECTION IS THE SAME AS THE CHARACTER DIRECTION -> IGNORE ARROW
            } else if (isItWall(arrowDirectionChange, currentTilePos, character, targetTilePos)) {
                 // ARROW POINTING TO WALL -> IGNORE ARROW
            } else {
                // ARROW POINTING TO EMPTY SPACE, ALLOW ROTATION
                switch (arrowDirectionChange) {
                    case LEFT:
                        return PhisicsEventsEnum.ROTATION_LEFT;
                    case RIGHT:
                        return PhisicsEventsEnum.ROTATION_RIGHT;
                    case UP:
                        return PhisicsEventsEnum.ROTATION_UP;
                    case DOWN:
                        return PhisicsEventsEnum.ROTATION_DOWN;
                }
            }
        }

        // no arrow / useless arrow -> check if there is a wall

        // tile straight in front of the character
        targetTilePos = getTargetTilePosition(character.getDirection(), character);
        if (!isItWall(character.getDirection(), currentTilePos, character, targetTilePos)) {
            // in front of the character is a tile, not a wall
            return PhisicsEventsEnum.NO_COLLISION;
        }

        // now there is a wall in front of the character
        // check if there is a wall on the right side first
        targetTilePos = getTargetTilePosition(character.getDirection().next(), character);
        if (!isItWall(character.getDirection(), currentTilePos, character, targetTilePos)) {
            // on right side from characters perspective is a tile, not a wall -> rotate right
            System.out.println("EVENT 1");
            return PhisicsEventsEnum.convertDirectionToPhisicsEvent(character.getDirection().next());
        }

        // check if there is a wall on the left side
        targetTilePos = getTargetTilePosition(character.getDirection().next().getOppositeDirection(), character);
        if (!isItWall(character.getDirection(), currentTilePos, character, targetTilePos)) {
            // on left side from characters perspective is a tile, not a wall -> rotate left
            System.out.println("EVENT 2");
            return PhisicsEventsEnum.convertDirectionToPhisicsEvent(character.getDirection().next().getOppositeDirection());
        }

        // now, this is getting tricky
        // check if there is a wall behind the character, if the character can even move
        targetTilePos = getTargetTilePosition(character.getDirection().getOppositeDirection(), character);
        if (!isItWall(character.getDirection(), currentTilePos, character, targetTilePos)) {
            // behind the character is a tile, not a wall -> rotate opposite
            System.out.println("EVENT 3");
            System.out.println("current direction: " + character.getDirection());
            System.out.println("opposite direction: " + character.getDirection().getOppositeDirection());
            return PhisicsEventsEnum.convertDirectionToPhisicsEvent(character.getDirection().getOppositeDirection());
        }

        // ok, well... now it is certain that the character is stuck in a wall...
        // that is not nice of you...
        // please consider making the given character more space to move
        // as our characters has claustrophobia, and they are not able to move in such a small space
        // "thank you for your understanding" - the creator of the game

        // just rotate to the right,
        System.out.println("EVENT 4");
        return PhisicsEventsEnum.ROTATION_STUCK_4WALLS;
    }

    // decide what direction to rotate
    public static boolean decideClockwiseRotation(int textureRotation, DirectionEnum direction) {
        return (textureRotation - direction.getValue() + 360) % 360 < 180;
    }
}
