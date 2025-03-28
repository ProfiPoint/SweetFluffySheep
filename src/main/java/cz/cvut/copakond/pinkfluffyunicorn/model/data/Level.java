package cz.cvut.copakond.pinkfluffyunicorn.model.data;
import cz.cvut.copakond.pinkfluffyunicorn.model.entities.Cloud;
import cz.cvut.copakond.pinkfluffyunicorn.model.items.IItem;
import cz.cvut.copakond.pinkfluffyunicorn.model.items.Item;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.GameObject;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.ItemFactory;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.DirectionEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.ErrorMsgsEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.ItemEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.TextureListEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.world.Coin;
import cz.cvut.copakond.pinkfluffyunicorn.model.world.Goal;
import cz.cvut.copakond.pinkfluffyunicorn.model.world.Start;
import cz.cvut.copakond.pinkfluffyunicorn.model.world.Tile;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

public class Level {
    // for render purposes
    JSONObject levelData;
    List<GameObject> objects;
    String path = "src/main/resources/datasaves/levels/";

    // game objects itselfs
    int[] mapSize;
    Start start;
    Goal goal;
    List<Tile> tiles = new ArrayList<Tile>();
    List<Cloud> enemies = new ArrayList<Cloud>();
    List<Coin> coins = new ArrayList<Coin>();
    List<IItem> items = new ArrayList<IItem>();
    // creator, creatorUpdated
    Map<String, String> playerInfo = new HashMap<String, String>();
    // timeLimit, unicorns, goalUnicorns, maxArrows, creationTime, updatedTime
    Map<String, Integer> levelInfo = new HashMap<String, Integer>();

    int score;
    int timeLeft;

    public Level(String level, boolean levelEditor) {
        if (levelEditor) {
            levelData = JsonFileManager.readJsonFromFile(path + "_TEMPLATE.json");
            if (levelData == null) {
                ErrorMsgsEnum.LOAD_DEFAULT.getValue();
            } else {
                System.out.println("Default Level loaded");
            }
        }
        levelData = JsonFileManager.readJsonFromFile(path + level + ".json");
        if (levelData == null) {
            ErrorMsgsEnum.CUSTOM_ERROR.getValue("Error loading level data");
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
    public boolean loadLevel() {
        if (levelData == null) {return false;}
        LoadManager lm = new LoadManager(levelData);
        mapSize = lm.getList2NoLimit("mapSize");
        if (mapSize == null) {return false;}

        int[] startCoords = lm.getList3("start", mapSize);
        int[] endCoords = lm.getList3("goal", mapSize);
        if (startCoords == null || endCoords == null) {return false;}
        start = new Start(new int[]{startCoords[0], startCoords[1]}, DirectionEnum.fromValue(startCoords[2]));
        goal = new Goal(new int[]{endCoords[0], endCoords[1]}, DirectionEnum.fromValue(endCoords[2]));

        playerInfo.put("creator", lm.getString("creator"));
        playerInfo.put("creatorUpdated", lm.getString("creatorUpdated"));
        if (playerInfo.get("creator") == null || playerInfo.get("creatorUpdated") == null) {return false;}

        levelInfo.put("timeLimit", lm.getIntLimit("timeLimit", 60*60*24));
        levelInfo.put("unicorns", lm.getIntLimit("unicorns", 1000));
        levelInfo.put("maxArrows", lm.getIntLimit("maxArrows", mapSize[0]*mapSize[1]));
        levelInfo.put("creationTime", lm.getInt("creationTime"));
        levelInfo.put("updatedTime", lm.getInt("updatedTime"));
        if (levelInfo.get("updatedTime") == 0) {levelInfo.put("updatedTime",(int) (System.currentTimeMillis() / (1000 * 60)));}
        if (levelInfo.get("timeLimit") == null || levelInfo.get("unicorns") == null || levelInfo.get("maxArrows") == null ||
                levelInfo.get("creationTime") == null || levelInfo.get("updatedTime") == null) {return false;}
        levelInfo.put("goalUnicorns", lm.getIntLimit("goalUnicorns", levelData.getInt("unicorns")));
        if (levelInfo.get("goalUnicorns") == null) {return false;}

        List<int[]> tilesCoords = lm.getListOfListsWithLimitFromDict("tiles", mapSize, TextureListEnum.TILE.getCount());
        if (tilesCoords == null) {return false;}
        for (int[] tileCoords : tilesCoords) {
            tiles.add(new Tile(new int[]{tileCoords[0], tileCoords[1]}, tileCoords[2]));
        }

        List<int[]> enemiesCoords = lm.getListOfListsWithDirFromDict("enemies", mapSize);
        if (enemiesCoords == null) {return false;}
        for (int[] enemiesCoord : enemiesCoords) {
            enemies.add(new Cloud(new int[]{enemiesCoord[0], enemiesCoord[1]}, DirectionEnum.fromValue(enemiesCoord[2])));
        }

        List<int[]> coinsCoords = lm.getListOfLists("coins", mapSize);
        if (coinsCoords == null) {return false;}
        for (int[] coinCoord : coinsCoords) {
            coins.add(new Coin(new int[]{coinCoord[0], coinCoord[1]}));
        }

        List<int[]> itemsCoords = lm.getListOfListsWithLimit("items", mapSize, ItemEnum.getNumberOfItems());
        if (itemsCoords == null) {return false;}
        for (int[] itemCoord : itemsCoords) {
            ItemEnum itemEnum = ItemEnum.values()[itemCoord[2]]; // Get the ItemEnum from the coordinate
            IItem item = ItemFactory.createItem(itemEnum, new int[]{itemCoord[0], itemCoord[1]}, 5);
            items.add(item);
        }

        return true; // level is loaded successfully, without any errors :D
    }

    public boolean saveLevel(String levelName) {
        // Create a JSONObject to store the level data
        JSONObject levelData = new JSONObject();

        // Instantiate SaveManager with the JSONObject
        SaveManager sm = new SaveManager(levelData);

        // Use SaveManager to populate the JSONObject with the level data
        sm.addStartGoalData(start, goal);
        sm.addMapSizeData(mapSize);
        sm.addLevelInfo(levelInfo);
        sm.addPlayerInfo(playerInfo);
        sm.addTilesData(tiles);
        sm.addEnemiesData(enemies);
        sm.addCoinsData(coins);
        sm.addItemsData(items);

        //print all keys of the levelData
        for (String key : levelData.keySet()) {
            System.out.println(key + ": " + levelData.get(key));
        }

        // Save the level to a file using JsonFileManager
        return JsonFileManager.writeJsonToFile(path + levelName + ".json", levelData);
    }




}

