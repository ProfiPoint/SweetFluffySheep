package cz.cvut.copakond.sweetfluffysheep.model.utils.json;

import cz.cvut.copakond.sweetfluffysheep.model.entities.Wolf;
import cz.cvut.copakond.sweetfluffysheep.model.utils.game.GameObject;
import cz.cvut.copakond.sweetfluffysheep.model.world.Goal;
import cz.cvut.copakond.sweetfluffysheep.model.world.Start;
import cz.cvut.copakond.sweetfluffysheep.model.world.Tile;
import org.json.JSONArray;
import org.json.JSONObject;
import cz.cvut.copakond.sweetfluffysheep.model.items.*;

import java.util.List;
import java.util.Map;

/**
 * SaveManager is responsible for managing the JSON data structure used for saving game levels.
 * It provides methods to add various types of data to the JSON object, including level information,
 * player information, tiles, enemies, and items.
 */
public class SaveManager {
    private final JSONObject data;

    /**
     * Constructor for SaveManager.
     * Initializes the JSON object to store level data.
     */
    public SaveManager(JSONObject data) {
        this.data = data;
    }

    /**
     * Returns the JSON object containing the level data.
     *
     * @param isDefaultLevel Indicates whether the level is the default level or not.
     */
    public void addDefaultLevelData(boolean isDefaultLevel) {
        data.put("defaultLevel", isDefaultLevel);
    }

    /**
     * Adds start and goal data to the JSON object.
     * The start and goal positions are rounded to the nearest integer.
     * @param start The starting position of the player.
     * @param goal The destination point for the player.
     */
    public void addStartGoalData(Start start, Goal goal) {
        data.put("start", new JSONArray(new int[]{(int)Math.round(start.getPosition()[0]),
                (int)Math.round(start.getPosition()[1]),
                start.getDirection().getValue()}));
        data.put("goal", new JSONArray(new int[]{(int)Math.round(goal.getPosition()[0]),
                (int)Math.round(goal.getPosition()[1]), goal.getDirection().getValue()}));
    }

    /**
     * Adds map size data to the JSON object.
     * The map size is represented as an array of integers.
     *
     * @param mapSize An array of integers representing the map size.
     */
    public void addMapSizeData(int[] mapSize) {
        data.put("mapSize", new JSONArray(mapSize));
    }

    /**
     * Adds level information to the JSON object.
     * The level information includes time limit, number of sheep, goal sheep, max arrows,
     * creation time, and updated time.
     *
     * @param levelInfo A map containing level information.
     */
    public void addLevelInfo(Map<String, Integer> levelInfo) {
        int currentTimeInMinutes = (int) (System.currentTimeMillis() / (1000 * 60));
        data.put("timeLimit", levelInfo.get("timeLimit"));
        data.put("sheep", levelInfo.get("sheep"));
        data.put("goalSheep", levelInfo.get("goalSheep"));
        data.put("maxArrows", levelInfo.get("maxArrows"));
        data.put("creationTime", levelInfo.get("creationTime"));
        data.put("updatedTime", currentTimeInMinutes); // in minutes
    }

    /**
     * Adds player information to the JSON object.
     * The player information includes the creator name and updated time.
     *
     * @param playerInfo A map containing player information.
     */
    public void addPlayerInfo(Map<String, String> playerInfo) {
        data.put("creator", playerInfo.get("creator"));
        data.put("creatorUpdated", playerInfo.get("creatorUpdated"));
    }

    /**
     * Adds tiles data to the JSON object.
     * The tiles data includes the position and texture type of each tile.
     *
     * @param tiles A list of Tile objects representing the tiles in the level.
     */
    public void addTilesData(List<Tile> tiles) {
        JSONObject tilesObj = new JSONObject();
        for (Tile tile : tiles) {
            String key = (int)Math.round(tile.getPosition()[0]) + "-" + (int)Math.round(tile.getPosition()[1]);
            tilesObj.put(key, tile.getTextureType());
        }
        data.put("tiles", tilesObj);
    }

    /**
     * Adds enemies data to the JSON object.
     * The enemies data includes the position and direction of each enemy.
     *
     * @param enemies A list of Wolf objects representing the enemies in the level.
     */
    public void addEnemiesData(List<Wolf> enemies) {
        JSONObject enemiesObj = new JSONObject();
        for (Wolf enemy : enemies) {
            String key = (int)Math.round(enemy.getPosition()[0]) + "-" + (int)Math.round(enemy.getPosition()[1]);
            enemiesObj.put(key, enemy.getDirection().getValue());
        }
        data.put("enemies", enemiesObj);
    }

    /**
     * Adds items data to the JSON object.
     * The items data includes the position, effect type, and duration of each item.
     *
     * @param items A list of Item objects representing the items in the level.
     */
    public void addItemsData(List<Item> items) {
        JSONArray itemsArray = new JSONArray();
        for (Item item : items) {
            double[] itemPos = item.getPosition();
            itemsArray.put(new JSONArray(new int[]{(int)Math.round(itemPos[0]), (int)Math.round(itemPos[1]),
                    item.getItemEffect().ordinal(), item.getDurationTicks() / GameObject.getFPS()})); // duration in seconds
        }
        data.put("items", itemsArray);
    }
}
