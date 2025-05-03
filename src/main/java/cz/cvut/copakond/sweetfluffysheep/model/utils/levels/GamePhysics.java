package cz.cvut.copakond.sweetfluffysheep.model.utils.levels;

import cz.cvut.copakond.sweetfluffysheep.model.entities.Character;
import cz.cvut.copakond.sweetfluffysheep.model.entities.Wolf;
import cz.cvut.copakond.sweetfluffysheep.model.entities.Sheep;
import cz.cvut.copakond.sweetfluffysheep.model.items.FreezeItem;
import cz.cvut.copakond.sweetfluffysheep.model.items.Item;
import cz.cvut.copakond.sweetfluffysheep.model.items.RageItem;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.*;
import cz.cvut.copakond.sweetfluffysheep.model.utils.game.GameObject;
import cz.cvut.copakond.sweetfluffysheep.model.world.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * GamePhysics class is responsible for handling the physics of the game.
 * It checks for collisions between game objects, handles character movement,
 * and manages the interactions between characters and items.
 */
public class GamePhysics {

    private static final Logger logger = Logger.getLogger(GamePhysics.class.getName());

    /* dynamically calculated limit for collision detection, so even in max speed the collision will be detected
     * for normal settings it is 0.0367, and the max speed per tick is 0.0667, so because the collision is checked
     * around, it is 0.0367 * 2 = 0.0734, so the collision will be detected even in max speed
     * 1.1 is for to make sure there are no floating point errors
     */
    private static final double collisionLimit = (1 / (double) GameObject.getFPS()) * GameObject.getCollisionLimit() * 1 / 2 * 1.1;

    private static Map<int[], Tile> tileMap;
    private static List<Wolf> enemies;
    private static List<Character> allCharacters;
    private static List<Item> items;
    private static Start start;
    private static Goal goal;
    private static List<Arrow> arrows;

    private static boolean initialized = false;

    /**
     * Initializes the GamePhysics class with the given parameters.
     * @param start The starting point of the game.
     * @param goal The goal point of the game.
     * @param tiles The list of tiles in the game.
     * @param enemies The list of enemies in the game.
     * @param sheep The list of sheep in the game.
     * @param items The list of items in the game.
     * @param arrows The list of arrows in the game.
     */
    public static void loadMapObjects(Start start, Goal goal, List<Tile> tiles, List<Wolf> enemies,
                                      List<Sheep> sheep, List<Item> items, List<Arrow> arrows) {
        if (initialized) {
            logger.severe(ErrorMsgsEnum.PHYSICS_ALREADY_INIT.getValue());
        }

        GamePhysics.initialized = true;
        GamePhysics.enemies = enemies;
        GamePhysics.items = items;
        GamePhysics.start = start;
        GamePhysics.goal = goal;
        GamePhysics.arrows = arrows;

        GamePhysics.allCharacters = new ArrayList<>();
        GamePhysics.allCharacters.addAll(enemies);
        GamePhysics.allCharacters.addAll(sheep);

        GamePhysics.tileMap = new HashMap<>();
        for (Tile tile : tiles) {
            if (tile.isWalkable()){
                GamePhysics.tileMap.put(new int[]{(int) tile.getX(), (int) tile.getY()}, tile);
            }
        }
    }

    /**
     * Unloads the map objects and resets the GamePhysics class.
     */
    public static void unloadMapObjects() {
        GamePhysics.initialized = false;
        GamePhysics.tileMap = null;
        GamePhysics.enemies = null;
        GamePhysics.allCharacters = null;
        GamePhysics.items = null;
        GamePhysics.start = null;
        GamePhysics.goal = null;
        GamePhysics.arrows = null;
    }

    /**
     * Checks if the GamePhysics class is initialized.
     * @param position1 The first position to check.
     * @param position2 The second position to check.
     * @return the distance
     */
    private static double getDistanceOnePoint(double position1, double position2) {
        return Math.abs(position2 - position1);
    }

    /**
     * Checks the distance between two positions.
     * @param position1 The first position.
     * @param position2 The second position.
     * @return the distance
     */
    private static double getDistance(double[] position1, double[] position2) {
        return Math.sqrt(Math.pow(position2[0] - position1[0], 2) + Math.pow(position2[1] - position1[1], 2));
    }

    /**
     * Checks the distance between two objects.
     * @param obj1 The first object.
     * @param obj2 The second object.
     * @return the distance
     */
    private static double distanceBetweenObjects(GameObject obj1, GameObject obj2) {
        if (obj1 == null || obj2 == null) return Double.MAX_VALUE;
        return getDistance(new double[]{obj1.getX(), obj1.getY()}, new double[]{obj2.getX(), obj2.getY()});
    }

    /**
     * Checks if two objects are colliding.
     * @param obj1 The first object.
     * @param obj2 The second object.
     * @param hitboxMultiplier The hitbox multiplier.
     * @return true if colliding, false otherwise.
     */
    private static boolean isColliding(GameObject obj1, GameObject obj2, int hitboxMultiplier) {
        return distanceBetweenObjects(obj1, obj2) <= collisionLimit * hitboxMultiplier;
    }

    /**
     * Returns the tile position based on the given coordinates.
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @return the tile position as an array of integers.
     */
    private static int[] getTilePosition(double x, double y) {
        return new int[]{(int) Math.round(x), (int) Math.round(y)};
    }

    /**
     * Checks if the character is colliding with the given tile.
     * @param currentTilePos The current tile position.
     * @param characterPos The character position.
     * @param targetTilePos The target tile position.
     * @return true if colliding, false otherwise.
     */
    private static boolean isCollidingWithWall(int currentTilePos, double characterPos, int[] targetTilePos) {
        return getDistanceOnePoint(currentTilePos, characterPos) <= collisionLimit * 4 && tileNotExists(targetTilePos);
    }

    /**
     * Gets the tile that is half a tile before the character.
     * @param direction The direction of the character.
     * @param character The character.
     * @return the target tile position indexes
     */
    private static int[] getTargetTilePosition(DirectionEnum direction, Character character) {
        return switch (direction) {
            case RIGHT -> getTilePosition(character.getX() + 1, character.getY());
            case LEFT -> getTilePosition(character.getX() - 1, character.getY());
            case UP -> getTilePosition(character.getX(), character.getY() - 1);
            case DOWN -> getTilePosition(character.getX(), character.getY() + 1);
        };
    }

    /**
     * Checks if the character is not colliding with a wall.
     * @param playerDirection The direction of the player.
     * @param currentTilePos The current tile position.
     * @param character The character.
     * @param targetTilePos The target tile position.
     * @return true if not colliding with a wall, false otherwise.
     */
    private static boolean isNotWall(DirectionEnum playerDirection, int[] currentTilePos, Character character,
                              int[] targetTilePos) {
        return !switch (playerDirection) {
            case UP -> isCollidingWithWall(currentTilePos[1] - 1, character.getY() - 1, targetTilePos);
            case DOWN -> isCollidingWithWall(currentTilePos[1] + 1, character.getY() + 1, targetTilePos);
            case LEFT -> isCollidingWithWall(currentTilePos[0] - 1, character.getX() - 1, targetTilePos);
            case RIGHT -> isCollidingWithWall(currentTilePos[0] + 1, character.getX() + 1, targetTilePos);
        };
    }

    /**
     * Checks if the tile does not exist in the tile map.
     * @param tilePos The tile position.
     * @return true if the tile does not exist, false otherwise.
     */
    public static boolean tileNotExists(int[] tilePos) {
        for (int[] key : tileMap.keySet()) {
            if (key[0] == tilePos[0] && key[1] == tilePos[1]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if there are any characters before the given character nearby.
     * Prevents characters from getting stuck in each other,
     * there is any character in front of the character, slow down
     * @param character The character to check.
     * @return SLOWDOWN if characters are close, NO_COLLISION otherwise.
     */
    public static PhysicsEventsEnum checkCharactersStruggle(Character character) {
        DirectionEnum direction = character.getDirection();
        boolean isEnemy = character.isEnemy();
        double[] position = character.getPosition();
        Character firstCharacter = character;

        for (Character other : allCharacters) {
            if (other == character || !other.isVisible() || !other.isAlive()) continue;
            if (isEnemy != other.isEnemy() || direction != other.getDirection()) continue;

            double[] otherPosition = other.getPosition();
            double distance = distanceBetweenObjects(character, other);
            if (distance > 1) continue;

            boolean aligned = switch (direction) {
                case UP, DOWN -> getDistanceOnePoint(position[0], otherPosition[0]) < 0.5;
                case LEFT, RIGHT -> getDistanceOnePoint(position[1], otherPosition[1]) < 0.5;
            };
            if (!aligned) continue;

            boolean isCloser = switch (direction) {
                case UP -> otherPosition[1] < firstCharacter.getPosition()[1];
                case DOWN -> otherPosition[1] > firstCharacter.getPosition()[1];
                case LEFT -> otherPosition[0] < firstCharacter.getPosition()[0];
                case RIGHT -> otherPosition[0] > firstCharacter.getPosition()[0];
            };
            if (isCloser) {
                firstCharacter = other;
            }
        }

        return firstCharacter == character ? PhysicsEventsEnum.NO_COLLISION : PhysicsEventsEnum.SLOWDOWN;
    }

    /**
     * Checks for collisions between the character and other game objects.
     * @param character The character to check for collisions.
     * @return the type of collision event, more at PhysicsEventsEnum class.
     */
    public static PhysicsEventsEnum checkCollision(Character character) {
        // only apply this part for the sheep
        if (!character.isEnemy()){
            // check if the character has entered the start
            if (character.getPreviousEvent() == PhysicsEventsEnum.BEFORE_START && !isColliding(character, start, 2)) {
                return PhysicsEventsEnum.BEFORE_START;
            }

            // check if the character has entered the goal
            if (character.getPreviousEvent() == PhysicsEventsEnum.IN_GOAL || (isColliding(character, goal, 2) && !goal.isLocked())) {
                return PhysicsEventsEnum.IN_GOAL;
            }

            // solve collisions with enemies
            for (Wolf enemy : enemies) {
                if (enemy.isVisible() && isColliding(character, enemy, GameObject.getFPS() * 2)) {
                    if (RageItem.isActive()) {
                        enemy.kill(); // kill the enemy if rage is active
                    } else if (!FreezeItem.isActive()) {
                        return PhysicsEventsEnum.SHEEP_KILLED; // sheep is killed if not
                    }
                }
            }

            // activate unactivated items upon collision
            for (Item item : items) {
                if (item.isVisible() && isColliding(character, item, 2)) {
                    boolean used = item.use();
                    if (used) {
                        logger.info("Item used: " + item.getItemEffect());
                    }
                }
            }
        }

        // check if the character is colliding with an arrow
        DirectionEnum arrowDirectionChange = null;
        for (Arrow arrow : arrows) {
            if (isColliding(character, arrow, 2)) {
                arrowDirectionChange = arrow.getDirection();
                break;
            }
        }

        // if the character position is either around X.5 or Y.5, check if there is a wall
        int[] currentTilePos = getTilePosition(character.getX(), character.getY());
        int[] targetTilePos;

        // check if the character detected to be colliding with an arrow
        if (arrowDirectionChange != null) {
            targetTilePos = getTargetTilePosition(arrowDirectionChange, character);

            if (arrowDirectionChange != character.getDirection() && isNotWall(arrowDirectionChange, currentTilePos, character, targetTilePos)) {
                // ARROW POINTING TO EMPTY SPACE, ALLOW ROTATION
                return switch (arrowDirectionChange) {
                    case LEFT -> PhysicsEventsEnum.ROTATION_LEFT;
                    case RIGHT -> PhysicsEventsEnum.ROTATION_RIGHT;
                    case UP -> PhysicsEventsEnum.ROTATION_UP;
                    case DOWN -> PhysicsEventsEnum.ROTATION_DOWN;
                };
            }
        }

        /* no arrow / useless arrow -> check if there is a wall
         * tile straight in front of the character
         */
        targetTilePos = getTargetTilePosition(character.getDirection(), character);
        if (isNotWall(character.getDirection(), currentTilePos, character, targetTilePos)) {
            // in front of the character is a tile, not a wall
            return PhysicsEventsEnum.NO_COLLISION;
        }

        /* now there is a wall in front of the character
         * check if there is a wall on the right side first
         */
        targetTilePos = getTargetTilePosition(character.getDirection().next(), character);
        if (isNotWall(character.getDirection(), currentTilePos, character, targetTilePos)) {
            // on the right side from the character perspective is a tile, not a wall -> rotate right
            return PhysicsEventsEnum.convertDirectionToPhysicsEvent(character.getDirection().next());
        }

        // check if there is a wall on the left side
        targetTilePos = getTargetTilePosition(character.getDirection().next().getOppositeDirection(), character);
        if (isNotWall(character.getDirection(), currentTilePos, character, targetTilePos)) {
            // on the left side from the character perspective is a tile, not a wall -> rotate left
            return PhysicsEventsEnum.convertDirectionToPhysicsEvent(character.getDirection().next().getOppositeDirection());
        }

        /* now, this is getting tricky
         * to check if there is a wall behind the character, if the character can even move
         */
        targetTilePos = getTargetTilePosition(character.getDirection().getOppositeDirection(), character);
        if (isNotWall(character.getDirection(), currentTilePos, character, targetTilePos)) {
            // behind the character is a tile, not a wall -> rotate opposite
            return PhysicsEventsEnum.convertDirectionToPhysicsEvent(character.getDirection().getOppositeDirection());
        }

        /* ok, well... now it is certain that the character is stuck in a wall...
         * that is not nice of you...
         * please consider making the given character more space to move
         * as our characters have claustrophobia, and they are not able to move in such a small space
         * "thank you for your understanding" - the creator of the game
         * just rotate to the right...
         */
        return PhysicsEventsEnum.ROTATION_STUCK_4WALLS;
    }

    /**
     * Decide which direction to rotate
     * @param textureRotation the rotation of the texture
     * @param direction the direction to rotate
     * @return true if the rotation is clockwise, false otherwise
     */
    public static boolean decideClockwiseRotation(int textureRotation, DirectionEnum direction) {
        return (textureRotation - direction.getValue() + 360) % 360 < 180;
    }
}