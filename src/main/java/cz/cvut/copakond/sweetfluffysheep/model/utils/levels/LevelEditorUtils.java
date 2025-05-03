package cz.cvut.copakond.sweetfluffysheep.model.utils.levels;

import cz.cvut.copakond.sweetfluffysheep.model.entities.Wolf;
import cz.cvut.copakond.sweetfluffysheep.model.items.Item;
import cz.cvut.copakond.sweetfluffysheep.model.items.ItemFactory;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.DirectionEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.ItemEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.LevelEditorObjectsEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.SoundListEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.files.SoundManager;
import cz.cvut.copakond.sweetfluffysheep.model.utils.game.GameObject;
import cz.cvut.copakond.sweetfluffysheep.model.world.Goal;
import cz.cvut.copakond.sweetfluffysheep.model.world.Level;
import cz.cvut.copakond.sweetfluffysheep.model.world.Start;
import cz.cvut.copakond.sweetfluffysheep.model.world.Tile;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Utility class for managing level objects in the level editor.
 * This class provides methods to add, remove, and manipulate game objects in the level.
 */
public class LevelEditorUtils {
    private static final Logger logger = Logger.getLogger(LevelEditorUtils.class.getName());
    
    private static Level level;

    public static void setLevel(Level level) {
        LevelEditorUtils.level = level;
    }

    /**
     * Adds an object to the level based on the specified position and object type.
     *
     * @param position   The position where the object should be added.
     * @param objectType The type of object to add.
     */
    public static void addObjectToLevel(double[] position, LevelEditorObjectsEnum objectType) {
        logger.info("Adding object: " + objectType);
        switch (objectType) {
            case TILE -> addTile(position);
            case REMOVETILE -> removeTile(position);
            case WOLF -> addWolf(position);
            case COIN -> addCoin(position);
            case FREEZE -> addFreeze(position);
            case RAGE -> addRage(position);
            case START -> addStart(position);
            case GOAL -> addGoal(position);
            case ROTATE -> rotateObject(position);
            case DESTROY -> destroyObject(position);
        }
    }

    /**
     * Adds a tile to the level at the specified position.
     *
     * @param position The position where the tile should be added.
     */
    private static void addTile(double[] position) {
        if (isNotTileAtPosition(position)) {
            Tile tile = new Tile(position, (int)((position[0] + position[1]) % 2 + 1) * 16, true);
            level.getTiles().add(tile);
            SoundManager.playSound(SoundListEnum.ARROW);
        }
    }

    /**
     * Removes a tile from the level at the specified position.
     *
     * @param position The position of the tile to remove.
     */
    public static void removeTile(double[] position) {
        List<Tile> tiles = level.getTiles();
        List<Tile> objectsInPosition = checkPosition(position, tiles);
        for (Tile object : objectsInPosition) {
            if (object.isWalkable()) {
                logger.info("Removing tile at position: " + position[0] + ", " + position[1]);
                tiles.remove(object);
                destroyObject(position);
                SoundManager.playSound(SoundListEnum.ARROW_DEL);
            }
        }
    }

    /**
     * Adds a wolf to the level at the specified position.
     *
     * @param position The position where the wolf should be added.
     */
    private static void addWolf(double[] position) {
        if (isNotTileAtPosition(position)) {
            logger.info("Position is not a tile: " + position[0] + ", " + position[1]);
            return;
        }
        List<Wolf> wolves = level.getEnemies();
        List<Wolf> objectsInPosition = checkPosition(position, wolves);
        if (objectsInPosition.isEmpty()) {
            Wolf wolf = new Wolf(position, DirectionEnum.LEFT);
            wolves.add(wolf);
            SoundManager.playSound(SoundListEnum.ENEMY_DOWN);
            logger.info("Adding wolf at position: " + position[0] + ", " + position[1]);
        } else {
            logger.info("Wolf already exists at position: " + position[0] + ", " + position[1]);
            rotateObject(position);
        }
    }

    /**
     * Adds a coin to the level at the specified position.
     *
     * @param position The position where the coin should be added.
     */
    private static void addCoin(double[] position) {
        addItem(position, ItemEnum.COIN);
        SoundManager.playSound(SoundListEnum.MONEY);
    }

    /**
     * Adds a freeze item to the level at the specified position.
     *
     * @param position The position where the freeze item should be added.
     */
    private static void addFreeze(double[] position) {
        addItem(position, ItemEnum.FREEZE);
        SoundManager.playSound(SoundListEnum.PRIZE);
    }

    /**
     * Adds a rage item to the level at the specified position.
     *
     * @param position The position where the rage item should be added.
     */
    private static void addRage(double[] position) {
        addItem(position, ItemEnum.RAGE);
        SoundManager.playSound(SoundListEnum.PRIZE);
    }

    /**
     * Adds a start point to the level at the specified position.
     *
     * @param position The position where the start point should be added.
     */
    private static void addStart(double[] position) {
        if (isNotTileAtPosition(position)) {
            return;
        }

        Start start = level.getStart();
        if (start == null) {
            level.setStart(new Start(position, DirectionEnum.RIGHT));
            SoundManager.playSound(SoundListEnum.ARROW);
        } else if (samePosition(start.getPosition(), position)) {
            rotateObject(position);
        } else {
            start.setPosition(position);
            SoundManager.playSound(SoundListEnum.ARROW);
        }
    }

    /**
     * Adds a goal to the level at the specified position.
     *
     * @param position The position where the goal should be added.
     */
    private static void addGoal(double[] position) {
        if (isNotTileAtPosition(position)) {
            return;
        }

        Goal goal = level.getGoal();
        if (goal == null) {
            level.setGoal(new Goal(position, DirectionEnum.RIGHT));
            SoundManager.playSound(SoundListEnum.ARROW);
        } else if (samePosition(goal.getPosition(), position)) {
            rotateObject(position);
        } else {
            goal.setPosition(position);
            SoundManager.playSound(SoundListEnum.ARROW);
        }
    }

    /**
     * Rotates the object at the specified position.
     *
     * @param position The position of the object to rotate.
     */
    public static void rotateObject(double[] position) {
        // rotate the wolf if it is at the given position
        for (Wolf wolf : checkPosition(position, level.getEnemies())) {
            wolf.rotateCharacterLE();
            logger.info("Rotating wolf to " + wolf.getDirection());
            SoundManager.playSound(SoundListEnum.ROTATE);
        }

        // rotate the item if it is at the given position
        Start start = level.getStart();
        if (start != null && samePosition(start.getPosition(), position)) {
            start.rotateCharacterLE();
            logger.info("Rotating start to " + start.getDirection());
            SoundManager.playSound(SoundListEnum.ROTATE);
        }

        // rotate the goal if it is at the given position
        Goal goal = level.getGoal();
        if (goal != null && samePosition(goal.getPosition(), position)) {
            goal.rotateCharacterLE();
            logger.info("Rotating goal to " + goal.getDirection());
            SoundManager.playSound(SoundListEnum.ROTATE);
        }
    }

    /**
     * Destroys the object at the specified position, except for the tile.
     *
     * @param position The position of the object to destroy.
     */
    public static void destroyObject(double[] position) {
        // destroy the wolf if it is at the given position
        for (Wolf wolf : checkPosition(position, level.getEnemies())) {
            level.getEnemies().remove(wolf);
        }

        // destroy the item if it is at the given position
        for (Item item : checkPosition(position, level.getItems())) {
            level.getItems().remove(item);
            logger.info("Removing item: " + item.getItemEffect());
            SoundManager.playSound(SoundListEnum.DESTROY);
        }

        // destroy the start if it is at the given position
        if (level.getStart() != null && samePosition(level.getStart().getPosition(), position)) {
            level.setStart(null);
            logger.info("Removing start");
            SoundManager.playSound(SoundListEnum.DESTROY);
        }

        // destroy the goal if it is at the given position
        if (level.getGoal() != null && samePosition(level.getGoal().getPosition(), position)) {
            level.setGoal(null);
            logger.info("Removing goal");
            SoundManager.playSound(SoundListEnum.DESTROY);
        }
    }

    /**
     * Adds an item to the level at the specified position.
     *
     * @param position   The position where the item should be added.
     * @param itemEffect The type of item to add.
     */
    private static void addItem(double[] position, ItemEnum itemEffect) {
        if (isNotTileAtPosition(position)) {
            logger.info("Position is not a tile: " + position[0] + ", " + position[1]);
            return;
        }

        for (Item item : checkPosition(position, level.getItems())) {
            if (item.getItemEffect() == itemEffect && itemEffect != ItemEnum.COIN) {
                logger.info("Item " + itemEffect + " already exists at position: " + position[0] + ", " + position[1]);
                return;
            }
        }

        int duration = level.getLevelInfo().get("defaultItemDuration");
        level.getItems().add((Item) ItemFactory.createItem(itemEffect, position, duration));
    }

    /**
     * Checks if the position is not a tile.
     *
     * @param position The position to check.
     * @return True if the position is not a tile, false otherwise.
     */
    private static boolean isNotTileAtPosition(double[] position) {
        for (Tile tile : level.getTiles()) {
            if (tile.isWalkable() && tile.getPosition()[0] == position[0] && tile.getPosition()[1] == position[1]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a list of given class objects that are at the given position
     *
     * @param position The position to check.
     * @param toCheck  The list of objects to check against.
     * @param <T>      The type of objects in the list.
     * @return A list of objects at the specified position.
     */
    private static <T extends GameObject> List<T> checkPosition(double[] position, List<T> toCheck) {
        List<T> objects = new ArrayList<>();
        for (T object : toCheck) {
            if (samePosition(object.getPosition(), position)) {
                objects.add(object);
            }
        }
        return objects;
    }

    /**
     * Checks if two positions are the same.
     *
     * @param pos1 The first position.
     * @param pos2 The second position.
     * @return True if the positions are the same, false otherwise.
     */
    private static boolean samePosition(double[] pos1, double[] pos2) {
        return pos1[0] == pos2[0] && pos1[1] == pos2[1];
    }
}
