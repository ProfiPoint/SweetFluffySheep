package cz.cvut.copakond.pinkfluffyunicorn.model.utils.levels;

import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.LevelEditorObjectsEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.game.GameObject;
import cz.cvut.copakond.pinkfluffyunicorn.model.world.Tile;

import java.util.ArrayList;
import java.util.List;

public class LevelEditorUtils {
    private static Level level;

    public static void setLevel(Level level) {
        LevelEditorUtils.level = level;
    }

    /*PATH("path"),
    REMOVE_PATH("removePath"),
    CLOUD("cloud"),
    COIN("coin"),
    FIRE("fire"),
    RAINBOW("rainbow"),
    START("start"),
    GOAL("goal"),
    DESTROY("destroy"),
    EMPTY("empty");*/

    // returns a list of objects that are at the given position
    private static List<GameObject> checkPosition(double[] position, List<GameObject> toCheck) {
        List<GameObject> objects = new ArrayList<>();
        for (GameObject object : toCheck) {
            if (object.getPosition()[0] == position[0] && object.getPosition()[1] == position[1]) {
                objects.add(object);
            }
        }
        return objects;
    }

    public static void add_path(double[] position) {
        List<Tile> tiles = level.getTiles();
        //List<Tile> objectsInPosition = checkPosition(position, tiles);
    }

    public static void remove_path(double[] position) {

    }

    public static void add_cloud(double[] position) {

    }

    public static void add_coin(double[] position) {

    }

    public static void add_fire(double[] position) {

    }

    public static void add_rainbow(double[] position) {

    }

    public static void add_start(double[] position) {

    }

    public static void add_goal(double[] position) {

    }

    public static void destroy_object(double[] position) {

    }

    public static void addObjectToLevel( double[] position, LevelEditorObjectsEnum objectType) {
        switch (objectType) {
            case PATH -> add_path(position);
            case REMOVEPATH -> remove_path(position);
            case CLOUD -> add_cloud(position);
            case COIN -> add_coin(position);
            case FIRE -> add_fire(position);
            case RAINBOW -> add_rainbow(position);
            case START -> add_start(position);
            case GOAL -> add_goal(position);
            case DESTROY -> destroy_object(position);
        }
    }
}
