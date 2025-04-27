package cz.cvut.copakond.pinkfluffyunicorn.model.utils.levels;

import cz.cvut.copakond.pinkfluffyunicorn.Launcher;
import cz.cvut.copakond.pinkfluffyunicorn.model.entities.Cloud;
import cz.cvut.copakond.pinkfluffyunicorn.model.items.IItem;
import cz.cvut.copakond.pinkfluffyunicorn.model.items.Item;
import cz.cvut.copakond.pinkfluffyunicorn.model.items.ItemFactory;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.DirectionEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.ItemEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.LevelEditorObjectsEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.game.GameObject;
import cz.cvut.copakond.pinkfluffyunicorn.model.world.Goal;
import cz.cvut.copakond.pinkfluffyunicorn.model.world.Level;
import cz.cvut.copakond.pinkfluffyunicorn.model.world.Start;
import cz.cvut.copakond.pinkfluffyunicorn.model.world.Tile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class LevelEditorUtils {
    private static final Logger logger = Logger.getLogger(LevelEditorUtils.class.getName());
    
    private static Level level;

    public static void setLevel(Level level) {
        LevelEditorUtils.level = level;
    }

    // returns a list of given class objects that are at the given position
    private static <T extends GameObject> List<T> checkPosition(double[] position, List<T> toCheck) {
        List<T> objects = new ArrayList<>();
        for (T object : toCheck) {
            if (object.getPosition()[0] == position[0] && object.getPosition()[1] == position[1]) {
                objects.add(object);
            }
        }
        return objects;
    }

    public static void addTile(double[] position) {
        List<Tile> tiles = level.getTiles();
        List<Tile> objectsInPosition = checkPosition(position, tiles);
        if (!isTileAtPosition(position)) {
            Tile tile = new Tile(position, (int)((position[0] + position[1]) % 2 + 1) * 16, true);
            tiles.add(tile);
        }
    }

    private static boolean isTileAtPosition(double[] position) {
        List<Tile> tiles = level.getTiles();
        for (Tile tile : tiles) {
            if (tile.isWalkable() && tile.getPosition()[0] == position[0] && tile.getPosition()[1] == position[1]) {
                return true;
            }
        }
        return false;
    }

    public static void removeTile(double[] position) {
        List<Tile> tiles = level.getTiles();
        List<Tile> objectsInPosition = checkPosition(position, tiles);
        for (Tile object : objectsInPosition) {
            if (object.isWalkable()) {
                tiles.remove(object);
                destroyObject(position);
            }
        }
    }

    public static void addCloud(double[] position) {
        if (!isTileAtPosition(position)) {
            logger.info("Position is not a tile: " + position[0] + ", " + position[1]);
            return;
        }
        List<Cloud> clouds = level.getEnemies();
        List<Cloud> objectsInPosition = checkPosition(position, clouds);
        if (objectsInPosition.isEmpty()) {
            Cloud cloud = new Cloud(position, DirectionEnum.LEFT);
            clouds.add(cloud);
            logger.info("Adding cloud at position: " + position[0] + ", " + position[1]);
        } else {
            logger.info("Cloud already exists at position: " + position[0] + ", " + position[1]);
        }
    }

    private static void addItem(double[] position, ItemEnum itemEffect) {
        if (!isTileAtPosition(position)) {
            logger.info("Position is not a tile: " + position[0] + ", " + position[1]);
            return;
        }
        List<Item> items = level.getItems();
        List<Item> objectsInPosition = checkPosition(position, items);
        for (Item object : objectsInPosition) {
            if (object.getItemEffect() == itemEffect && itemEffect != ItemEnum.COIN) {
                logger.info("Item " + itemEffect + " already exists at position: " + position[0] + ", " + position[1]);
                return; // item already exists
            }
        }
        Map<String, Integer> levelInfo = level.getLevelInfo();
        IItem iitem = ItemFactory.createItem(itemEffect, position, levelInfo.get("deafultItemDuration"));
        items.add((Item) iitem);
    }

    public static void addCoin(double[] position) {
        addItem(position, ItemEnum.COIN);
    }

    public static void addFire(double[] position) {
        addItem(position, ItemEnum.FIRE);
    }

    public static void addRainbow(double[] position) {
        addItem(position, ItemEnum.RAINBOW);
    }

    public static void addStart(double[] position) {
        if (!isTileAtPosition(position)) {
            return;
        }
        Start start = level.getStart();
        if (start == null) {
            start = new Start(position, DirectionEnum.RIGHT);
            level.setStart(start);
        } else {
            start.setPosition(position);
        }
    }

    public static void addGoal(double[] position) {
        if (!isTileAtPosition(position)) {
            return;
        }
        Goal goal = level.getGoal();
        if (goal == null) {
            goal = new Goal(position, DirectionEnum.RIGHT);
            level.setGoal(goal);
        } else {
            goal.setPosition(position);
        }
    }

    public static void rotateObject(double[] position) {
        List<Cloud> clouds = level.getEnemies();
        Start start = level.getStart();
        Goal goal = level.getGoal();

        List<Cloud> objectsInPosition = checkPosition(position, clouds);
        for (Cloud object : objectsInPosition) {
            object.rotateCharacterLE();
            logger.info("Rotating cloud to " + object.getDirection());
        }

        if (start != null && start.getPosition()[0] == position[0] && start.getPosition()[1] == position[1]) {
            start.rotateCharacterLE();
            logger.info("Rotating start to " + start.getDirection());
        }

        if (goal != null && goal.getPosition()[0] == position[0] && goal.getPosition()[1] == position[1]) {
            goal.rotateCharacterLE();
            logger.info("Rotating goal to " + goal.getDirection());
        }
    }

    public static void destroyObject(double[] position) {
        List<Cloud> clouds = level.getEnemies();
        List<Item> items = level.getItems();
        List<Cloud> objectsInPosition = checkPosition(position, clouds);
        for (Cloud object : objectsInPosition) {
            clouds.remove(object);
        }
        List<Item> objectsInPosition2 = checkPosition(position, items);
        for (Item object : objectsInPosition2) {
            items.remove(object);
            logger.info("Removing item: " + object.getItemEffect());
        }
        Start start = level.getStart();
        if (start != null && start.getPosition()[0] == position[0] && start.getPosition()[1] == position[1]) {
            level.setStart(null);
            logger.info("Removing start");
        }
        Goal goal = level.getGoal();
        if (goal != null && goal.getPosition()[0] == position[0] && goal.getPosition()[1] == position[1]) {
            level.setGoal(null);
            logger.info("Removing goal");
        }
    }

    public static void addObjectToLevel(double[] position, LevelEditorObjectsEnum objectType) {
        logger.info("Adding object: " + objectType);
        switch (objectType) {
            case TILE -> addTile(position);
            case REMOVETILE -> removeTile(position);
            case CLOUD -> addCloud(position);
            case COIN -> addCoin(position);
            case FIRE -> addFire(position);
            case RAINBOW -> addRainbow(position);
            case START -> addStart(position);
            case GOAL -> addGoal(position);
            case ROTATE -> rotateObject(position);
            case DESTROY -> destroyObject(position);
        }
    }
}
