package cz.cvut.copakond.pinkfluffyunicorn.model.world;
import cz.cvut.copakond.pinkfluffyunicorn.model.data.SaveManager;
import cz.cvut.copakond.pinkfluffyunicorn.model.entities.Cloud;
import cz.cvut.copakond.pinkfluffyunicorn.model.items.Item;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.GameObject;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.DirectionEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.ErrorMsgsEnum;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

public class Level {
    // for render purposes
    List<GameObject> objects;

    // game objects itselfs
    Goal goal;
    Start start;
    List<Tile> tiles;
    List<Cloud> enemies;
    List<Coin> coins;
    List<Item> items;
    // creator, creatorUpdated
    Dictionary<String, String> playerInfo;
    // timeLimit, unicorns, goalUnicorns, maxArrows, creationTime, updatedTime
    Dictionary<String, Integer> levelInfo;

    int score;
    int timeLeft;

    public Level(String level, boolean levelEditor) {
        if (levelEditor) {
            JSONObject levelData = SaveManager.readJsonFromFile("/datasaves/levels/_TEMPLATE.json");
            return;
        }
        JSONObject levelData = SaveManager.readJsonFromFile(level + ".json");
        if (levelData == null) {
            System.err.println("Error loading level data");
            return;
        } else {
            System.out.println("Level loaded");
        }
    }

    // used to render the level
    public List<GameObject> getListOfObjects() {
        return objects;
    }

    // used to update the level by the game loop
    public void tick(){
        for (GameObject object : objects) {
            object.tick();
        }
    }

    // main function to load the level
    boolean loadLevel(JSONObject levelData) {
        /* JSON STRUCTURE:

          "start": [10,8,0],
          "goal": [11,12,0],
          "timeLimit":180,
          "unicorns": 10,
          "goalUnicorns": 1,
          "maxArrows": 20,
          "creator": "ProfiPoint",
          "creatorUpdated": "ProfiPoint",
          "creationTime": 0,
          "updatedTime": 0,
          "tiles": {
            "10-9": 2,
            "11-9": 1,
            "10-10": 1,
            "10-11": 2,
            "11-10": 2,
            "11-11": 1
          },
          "enemies": {
            "10-10": 0
          },
          "coins":[
            [10,11]
          ],
          "items":[
            [11,11,0]
          ],
          "decorations":[]
        } */

        JSONArray tempData = getJSONArray("start", levelData, 3);
        if (tempData == null) {return false;}
        int[] startData = new int[3];
        for (int i = 0; i < 3; i++) {
            try {
                startData[i] = tempData.getInt(i);
            } catch (JSONException e) {
                System.err.println(ErrorMsgsEnum.LOAD_LIST_IntIntOrientation.getValue("Key: start"));
                return false;
            }
        }
        int[] position = {startData[0], startData[1]};
        DirectionEnum direction = DirectionEnum.fromValue(startData[2]);
        if (direction == null) {
            System.err.println(ErrorMsgsEnum.LOAD_ORIENTATION.getValue("Key: start[3]"));
            return false;
        }
        start = new Start(position, direction);

        return true;
    }



    JSONArray getJSONArray(String key, JSONObject levelData, int expectedLength) {
        JSONArray tempData;
        try {
            tempData = levelData.getJSONArray(key);
        } catch (JSONException e) {
            System.err.println(ErrorMsgsEnum.LOAD_JSON_KEY_NOT_FOUND.getValue("Key: " + key));
            return null;
        }
        if (tempData.length() != expectedLength || expectedLength != -1) {
            System.err.println(ErrorMsgsEnum.LOAD_JSON_KEY_NOT_FOUND.getValue("Key: " + key + " Expected length: " + expectedLength));
            return null;
        }
        return tempData;
    }

    int[] getIntArray(JSONArray tempData, int expectedLength) {
        int[] data = new int[expectedLength];
        for (int i = 0; i < expectedLength; i++) {
            try {
                data[i] = tempData.getInt(i);
            } catch (JSONException e) {
                System.err.println(ErrorMsgsEnum.LOAD_EXPECTED_INT.getValue("Expected integer " + i));
                return null;
            }
        }
        return data;
    }

}

