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

public class LevelEditorUtils {
    private static final Logger logger = Logger.getLogger(LevelEditorUtils.class.getName());
    
    private static Level level;

    public static void setLevel(Level level) {
        LevelEditorUtils.level = level;
    }

    public static void addObjectToLevel(double[] position, LevelEditorObjectsEnum objectType) {
        logger.info("Adding object: " + objectType);
        switch (objectType) {
            case TILE -> addTile(position);
            case REMOVETILE -> removeTile(position);
            case WOLF -> addWolf(position);
            case COIN -> addCoin(position);
            case FIRE -> addFire(position);
            case RAINBOW -> addRainbow(position);
            case START -> addStart(position);
            case GOAL -> addGoal(position);
            case ROTATE -> rotateObject(position);
            case DESTROY -> destroyObject(position);
        }
    }

    private static void addTile(double[] position) {
        if (isNotTileAtPosition(position)) {
            Tile tile = new Tile(position, (int)((position[0] + position[1]) % 2 + 1) * 16, true);
            level.getTiles().add(tile);
            SoundManager.playSound(SoundListEnum.ARROW);
        }
    }

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
        }
    }

    private static void addCoin(double[] position) {
        addItem(position, ItemEnum.COIN);
        SoundManager.playSound(SoundListEnum.MONEY);
    }

    private static void addFire(double[] position) {
        addItem(position, ItemEnum.FIRE);
        SoundManager.playSound(SoundListEnum.PRIZE);
    }

    private static void addRainbow(double[] position) {
        addItem(position, ItemEnum.RAINBOW);
        SoundManager.playSound(SoundListEnum.PRIZE);
    }

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

    public static void rotateObject(double[] position) {
        for (Wolf wolf : checkPosition(position, level.getEnemies())) {
            wolf.rotateCharacterLE();
            logger.info("Rotating wolf to " + wolf.getDirection());
            SoundManager.playSound(SoundListEnum.ROTATE);
        }

        Start start = level.getStart();
        if (start != null && samePosition(start.getPosition(), position)) {
            start.rotateCharacterLE();
            logger.info("Rotating start to " + start.getDirection());
            SoundManager.playSound(SoundListEnum.ROTATE);
        }

        Goal goal = level.getGoal();
        if (goal != null && samePosition(goal.getPosition(), position)) {
            goal.rotateCharacterLE();
            logger.info("Rotating goal to " + goal.getDirection());
            SoundManager.playSound(SoundListEnum.ROTATE);
        }
    }

    public static void destroyObject(double[] position) {
        for (Wolf wolf : checkPosition(position, level.getEnemies())) {
            level.getEnemies().remove(wolf);
        }

        for (Item item : checkPosition(position, level.getItems())) {
            level.getItems().remove(item);
            logger.info("Removing item: " + item.getItemEffect());
            SoundManager.playSound(SoundListEnum.DESTROY);
        }

        if (level.getStart() != null && samePosition(level.getStart().getPosition(), position)) {
            level.setStart(null);
            logger.info("Removing start");
            SoundManager.playSound(SoundListEnum.DESTROY);
        }

        if (level.getGoal() != null && samePosition(level.getGoal().getPosition(), position)) {
            level.setGoal(null);
            logger.info("Removing goal");
            SoundManager.playSound(SoundListEnum.DESTROY);
        }
    }

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

    private static boolean isNotTileAtPosition(double[] position) {
        for (Tile tile : level.getTiles()) {
            if (tile.isWalkable() && tile.getPosition()[0] == position[0] && tile.getPosition()[1] == position[1]) {
                return false;
            }
        }
        return true;
    }

    // returns a list of given class objects that are at the given position
    private static <T extends GameObject> List<T> checkPosition(double[] position, List<T> toCheck) {
        List<T> objects = new ArrayList<>();
        for (T object : toCheck) {
            if (samePosition(object.getPosition(), position)) {
                objects.add(object);
            }
        }
        return objects;
    }

    private static boolean samePosition(double[] pos1, double[] pos2) {
        return pos1[0] == pos2[0] && pos1[1] == pos2[1];
    }
}
