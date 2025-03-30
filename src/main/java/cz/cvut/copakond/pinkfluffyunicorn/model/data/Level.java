package cz.cvut.copakond.pinkfluffyunicorn.model.data;
import cz.cvut.copakond.pinkfluffyunicorn.model.entities.Cloud;
import cz.cvut.copakond.pinkfluffyunicorn.model.entities.Unicorn;
import cz.cvut.copakond.pinkfluffyunicorn.model.items.IItem;
import cz.cvut.copakond.pinkfluffyunicorn.model.items.ItemFactory;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.GameObject;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.GamePhysics;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.DirectionEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.ErrorMsgsEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.ItemEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.TextureListEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.items.Coin;
import cz.cvut.copakond.pinkfluffyunicorn.model.world.Arrow;
import cz.cvut.copakond.pinkfluffyunicorn.model.world.Goal;
import cz.cvut.copakond.pinkfluffyunicorn.model.world.Start;
import cz.cvut.copakond.pinkfluffyunicorn.model.world.Tile;
import org.json.JSONObject;

import java.util.*;

public class Level {
    // for render purposes
    JSONObject levelData;
    List<GameObject> objects;
    String path = "src/main/resources/datasaves/levels/";


    // game objects itselfs
    int[] mapSize;
    boolean defaultLevel = false; // false = custom level, true = non deleteable default level
    Start start;
    Goal goal;
    List<Tile> tiles = new ArrayList<Tile>();
    List<Cloud> enemies = new ArrayList<Cloud>();
    List<Unicorn> unicorns = new ArrayList<Unicorn>();
    List<Coin> coins = new ArrayList<Coin>();
    List<IItem> items = new ArrayList<IItem>();
    List<Arrow> arrows = new ArrayList<Arrow>();
    // creator, creatorUpdated
    Map<String, String> playerInfo = new HashMap<String, String>();
    // timeLimit, unicorns, goalUnicorns, maxArrows, creationTime, updatedTime
    Map<String, Integer> levelInfo = new HashMap<String, Integer>();

    Map<int[], Integer> tileMap = new HashMap<int[], Integer>();


    int score;
    double timeLeft;

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
        defaultLevel = lm.getBoolean("defaultLevel");
        if (mapSize == null) {return false;}

        int[] startCoords = lm.getList3("start", mapSize);
        int[] endCoords = lm.getList3("goal", mapSize);
        if (startCoords == null || endCoords == null) {return false;}
        start = new Start(new double[]{startCoords[0], startCoords[1]}, DirectionEnum.fromValue(startCoords[2]));
        goal = new Goal(new double[]{endCoords[0], endCoords[1]}, DirectionEnum.fromValue(endCoords[2]));

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
            tiles.add(new Tile(new double[]{tileCoords[0], tileCoords[1]}, tileCoords[2]));
        }

        List<int[]> enemiesCoords = lm.getListOfListsWithDirFromDict("enemies", mapSize);
        if (enemiesCoords == null) {return false;}
        for (int[] enemiesCoord : enemiesCoords) {
            enemies.add(new Cloud(new double[]{enemiesCoord[0], enemiesCoord[1]},
                    DirectionEnum.fromValue(enemiesCoord[2]), tileMap));
        }

        List<int[]> coinsCoords = lm.getListOfLists("coins", mapSize);
        if (coinsCoords == null) {return false;}
        for (int[] coinCoord : coinsCoords) {
            coins.add(new Coin(new double[]{coinCoord[0], coinCoord[1]}, 0));
        }

        List<int[]> itemsCoords = lm.getListOfListsWithLimit("items", mapSize, ItemEnum.getNumberOfItems());
        if (itemsCoords == null) {return false;}
        for (int[] itemCoord : itemsCoords) {
            ItemEnum itemEnum = ItemEnum.values()[itemCoord[2]]; // Get the ItemEnum from the coordinate
            IItem item = ItemFactory.createItem(itemEnum, new double[]{itemCoord[0], itemCoord[1]}, 5);
            items.add(item);
        }

        return true; // level is loaded successfully, without any errors :D
    }

    public boolean saveLevel(String levelName) {
        JSONObject levelData = new JSONObject();
        SaveManager sm = new SaveManager(levelData);

        sm.addDefaultLevelData(defaultLevel);
        sm.addStartGoalData(start, goal);
        sm.addMapSizeData(mapSize);
        sm.addLevelInfo(levelInfo);
        sm.addPlayerInfo(playerInfo);
        sm.addTilesData(tiles);
        sm.addEnemiesData(enemies);
        sm.addCoinsData(coins);
        sm.addItemsData(items);

        return JsonFileManager.writeJsonToFile(path + levelName + ".json", levelData);
    }

    public void Play() {
        timeLeft = (double) levelInfo.get("timeLimit");
        // init unicorns
        DirectionEnum direction = goal.getDirection();
        double[] coords;
        DirectionEnum unicornDirection = direction.getOppositeDirection();
        double[] unitDirection = new double[]{0, 0};
        if (start.getDirection() == DirectionEnum.LEFT) {
            coords = new double[]{-1, start.getPosition()[1]};
            unitDirection[0] = -1;
        } else if (start.getDirection() == DirectionEnum.RIGHT) {
            coords = new double[]{mapSize[0], start.getPosition()[1]};
            unitDirection[0] = 1;
        } else if (start.getDirection() == DirectionEnum.UP) {
            coords = new double[]{start.getPosition()[0], -1};
            unitDirection[1] = -1;
        } else {
            coords = new double[]{start.getPosition()[0], mapSize[1]};
            unitDirection[1] = 1;
        }

        for (int i = 0; i < levelInfo.get("unicorns"); i++) {
            unicorns.add(new Unicorn(coords, unicornDirection));
            coords[0] += unitDirection[0];
            coords[1] += unitDirection[1];
        }


        GamePhysics.loadMapObjects(mapSize, start, goal, tiles, enemies, items, coins, arrows);
    }

    public void Unload() {
        objects = new ArrayList<>();
        GamePhysics.unloadMapObjects();
    }

    void buildObjectsList() {
        objects = new ArrayList<GameObject>();
        objects.add(start);
        objects.add(goal);
        for (Tile tile : tiles) {
            objects.add(tile);
        }
        for (Cloud enemy : enemies) {
            objects.add(enemy);
        }
        for (Coin coin : coins) {
            objects.add(coin);
        }
        for (IItem item : items) {
            objects.add((GameObject)item);
        }
        for (Unicorn unicorn : unicorns) {
            objects.add(unicorn);
        }
    }
}

