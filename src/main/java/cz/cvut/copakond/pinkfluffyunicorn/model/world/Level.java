package cz.cvut.copakond.pinkfluffyunicorn.model.world;
import cz.cvut.copakond.pinkfluffyunicorn.model.entities.Cloud;
import cz.cvut.copakond.pinkfluffyunicorn.model.entities.Unicorn;
import cz.cvut.copakond.pinkfluffyunicorn.model.items.Coin;
import cz.cvut.copakond.pinkfluffyunicorn.model.items.IItem;
import cz.cvut.copakond.pinkfluffyunicorn.model.items.ItemFactory;
import cz.cvut.copakond.pinkfluffyunicorn.model.profile.ProfileManager;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.GameObject;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.levels.GamePhysics;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.DirectionEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.ErrorMsgsEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.ItemEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.TextureListEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.json.JsonFileManager;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.json.LoadManager;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.json.SaveManager;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.levels.LevelStatusUtils;
import org.json.JSONObject;

import java.util.*;

public class Level {
    // for render purposes
    private static JSONObject levelData;
    private static List<GameObject> objects;
    private static String levelPath;
    private static String profilesPath;
    private static String path;
    private String levelName;
    private boolean isLevelEditor = false;
    private boolean isStoryLevel = false; // false = custom level, true = non deleteable default level

    // game objects itselfs
    private int[] mapSize;

    private Start start;
    private Goal goal;
    private boolean defaultLevel = false;
    private List<Tile> tiles = new ArrayList<Tile>();
    private List<Cloud> enemies = new ArrayList<Cloud>();
    private List<Unicorn> unicorns = new ArrayList<Unicorn>();
    private List<IItem> items = new ArrayList<IItem>();
    private List<Arrow> arrows = new ArrayList<Arrow>();
    // creator, creatorUpdated
    private Map<String, String> playerInfo = new HashMap<String, String>();
    // timeLimit, unicorns, goalUnicorns, maxArrows, creationTime, updatedTime
    private Map<String, Integer> levelInfo = new HashMap<String, Integer>();

    private Map<int[], Integer> tileMap = new HashMap<int[], Integer>();

    private double timeLeft;

    public Level(String level, boolean isLevelEditor, boolean storyLevel) {
        if (storyLevel) {
            path = levelPath + "/";
        } else {
            path = profilesPath + "/" + ProfileManager.getCurrentProfile() + "/";
        }
        this.levelName = level;
        this.isLevelEditor = isLevelEditor;
        this.isStoryLevel = storyLevel;

        if (isLevelEditor) {
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
        buildObjectsList();
        return objects;
    }

    public int[] getMapSize() {
        return mapSize;
    }

    public boolean isLevelEditor() {
        return isLevelEditor;
    }

    public boolean isStoryLevel() {
        return isStoryLevel;
    }

    public static void setLevelPath(String p) {
        levelPath = p;
    }

    public static void setProfilesPath(String p) {
       profilesPath = p;
    }

    // used to update the level by the game loop
    public void tick(boolean doesTimeFlow) {
        if (doesTimeFlow) {
            timeLeft--;
            if (timeLeft <= 0) {
                timeLeft = 0;
            }
        }

        // GameObjects are updated in another thread, so we need to catch the exception
        try {
            for (GameObject object : objects) {
                object.tick(doesTimeFlow);
            }
        } catch (ConcurrentModificationException e) {
            System.out.println("Frame skipped due to concurrent modification, if this happens often, consider lowering the FPS");
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
        Unicorn.setGoalUnicorns(levelData.getInt("goalUnicorns"));

        List<int[]> tilesCoords = lm.getListOfListsWithLimitFromDict("tiles", mapSize, TextureListEnum.TILE.getCount());
        if (tilesCoords == null) {return false;}
        for (int[] tileCoords : tilesCoords) {
            tiles.add(new Tile(new double[]{tileCoords[0], tileCoords[1]}, tileCoords[2]));
        }

        List<int[]> enemiesCoords = lm.getListOfListsWithDirFromDict("enemies", mapSize, true);
        if (enemiesCoords == null) {return false;}
        for (int[] enemiesCoord : enemiesCoords) {
            enemies.add(new Cloud(new double[]{enemiesCoord[0], enemiesCoord[1]},
                    DirectionEnum.fromValue(enemiesCoord[2]), tileMap));
        }

        List<int[]> itemsCoords = lm.getListOfListsWithLimit("items", mapSize, ItemEnum.getNumberOfItems());
        if (itemsCoords == null) {return false;}
        for (int[] itemCoord : itemsCoords) {
            ItemEnum itemEnum = ItemEnum.values()[itemCoord[2]]; // Get the ItemEnum from the coordinate
            IItem item = ItemFactory.createItem(itemEnum, new double[]{itemCoord[0], itemCoord[1]}, itemCoord[3]);
            items.add(item);
        }

        buildObjectsList();
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
        sm.addItemsData(items);

        return JsonFileManager.writeJsonToFile(path + levelName + ".json", levelData);
    }

    public void PlaceRotateRemoveArrow(int[] tileClick, int button) {
        if (button == 1) {
            Arrow arrow;
            for (int i = 0; i < arrows.size(); i++) {
                arrow = arrows.get(i);
                if (arrow.getPosition()[0] == tileClick[0] && arrow.getPosition()[1] == tileClick[1]) {
                    arrow.rotate();
                    return;
                }
            }
            // no arrow found, create new one
            arrow = new Arrow(new double[]{tileClick[0], tileClick[1]});
            if (arrow.isVisible()) {
                arrows.add(arrow);
            } else {
                System.out.println("Arrow not created");
            }

        } else if (button == 2) {
            // remove arrow
            for (int i = 0; i < arrows.size(); i++) {
                Arrow arrow = arrows.get(i);
                if (arrow.getPosition()[0] == tileClick[0] && arrow.getPosition()[1] == tileClick[1]) {
                    arrow.destroy();
                    arrows.remove(i);
                    return;
                }
            }
        }
    }

    public void Play() {
        timeLeft = (double) levelInfo.get("timeLimit") * GameObject.getFPS();
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

        System.out.println("number of unicorns: " + levelInfo.get("unicorns"));
        for (int i = 0; i < levelInfo.get("unicorns"); i++) {
            unicorns.add(new Unicorn(new double[]{coords[0], coords[1]}, unicornDirection));
            coords[0] += unitDirection[0];
            coords[1] += unitDirection[1];
        }

        GamePhysics.loadMapObjects(mapSize, start, goal, tiles, enemies, items, arrows);
        buildObjectsList();
    }

    public void Unload() {
        for (GameObject object : objects) {
            object.resetLevel();
        }
        objects = new ArrayList<>();
        GamePhysics.unloadMapObjects();
    }

    public void Completed() {
        LevelStatusUtils.markLevelAsCompleted(this);
    }

    public String[] getLevelData() {
        String isLevelEditor = this.isLevelEditor ? "true" : "false";
        String isStoryLevel = this.isStoryLevel ? "true" : "false";
        return new String[]{levelName, isLevelEditor, isStoryLevel};
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
        for (IItem item : items) {
            objects.add((GameObject)item);
        }
        for (Unicorn unicorn : unicorns) {
            objects.add(unicorn);
        }
        for (Arrow arrow : arrows) {
            objects.add(arrow);
        }
    }

    public int getLifes() {
        return Unicorn.getUnicornsAlive() - levelInfo.get("goalUnicorns") + 1;
    }

    public int getTimeLeft() {
        return (int) timeLeft / GameObject.getFPS();
    }

    public int[] getCoinsLeftAndCoins() {
        return new int[]{Coin.getCoinsLeft(), Coin.getTotalCoins()};
    }
}

