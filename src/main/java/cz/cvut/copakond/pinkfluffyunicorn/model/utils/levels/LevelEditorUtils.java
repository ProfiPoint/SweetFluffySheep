package cz.cvut.copakond.pinkfluffyunicorn.model.utils.levels;

import cz.cvut.copakond.pinkfluffyunicorn.model.entities.Cloud;
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

    private static void addTile(double[] position) {
        if (isNotTileAtPosition(position)) {
            Tile tile = new Tile(position, (int)((position[0] + position[1]) % 2 + 1) * 16, true);
            level.getTiles().add(tile);
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
            }
        }
    }

    private static void addCloud(double[] position) {
        if (isNotTileAtPosition(position)) {
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

    private static void addCoin(double[] position) {
        addItem(position, ItemEnum.COIN);
    }

    private static void addFire(double[] position) {
        addItem(position, ItemEnum.FIRE);
    }

    private static void addRainbow(double[] position) {
        addItem(position, ItemEnum.RAINBOW);
    }

    private static void addStart(double[] position) {
        if (isNotTileAtPosition(position)) {
            return;
        }

        Start start = level.getStart();
        if (start == null) {
            level.setStart(new Start(position, DirectionEnum.RIGHT));
        } else if (samePosition(start.getPosition(), position)) {
            rotateObject(position);
        } else {
            start.setPosition(position);
        }
    }

    private static void addGoal(double[] position) {
        if (isNotTileAtPosition(position)) {
            return;
        }

        Goal goal = level.getGoal();
        if (goal == null) {
            level.setGoal(new Goal(position, DirectionEnum.RIGHT));
        } else if (samePosition(goal.getPosition(), position)) {
            rotateObject(position);
        } else {
            goal.setPosition(position);
        }
    }

    public static void rotateObject(double[] position) {
        for (Cloud cloud : checkPosition(position, level.getEnemies())) {
            cloud.rotateCharacterLE();
            logger.info("Rotating cloud to " + cloud.getDirection());
        }

        Start start = level.getStart();
        if (start != null && samePosition(start.getPosition(), position)) {
            start.rotateCharacterLE();
            logger.info("Rotating start to " + start.getDirection());
        }

        Goal goal = level.getGoal();
        if (goal != null && samePosition(goal.getPosition(), position)) {
            goal.rotateCharacterLE();
            logger.info("Rotating goal to " + goal.getDirection());
        }
    }

    public static void destroyObject(double[] position) {
        for (Cloud cloud : checkPosition(position, level.getEnemies())) {
            level.getEnemies().remove(cloud);
        }

        for (Item item : checkPosition(position, level.getItems())) {
            level.getItems().remove(item);
            logger.info("Removing item: " + item.getItemEffect());
        }

        if (level.getStart() != null && samePosition(level.getStart().getPosition(), position)) {
            level.setStart(null);
            logger.info("Removing start");
        }

        if (level.getGoal() != null && samePosition(level.getGoal().getPosition(), position)) {
            level.setGoal(null);
            logger.info("Removing goal");
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
