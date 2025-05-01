package cz.cvut.copakond.pinkfluffyunicorn.model.utils.levels;

import cz.cvut.copakond.pinkfluffyunicorn.model.items.Item;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.ItemEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.PathFinderEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.world.Goal;
import cz.cvut.copakond.pinkfluffyunicorn.model.world.Level;
import cz.cvut.copakond.pinkfluffyunicorn.model.world.Start;
import cz.cvut.copakond.pinkfluffyunicorn.model.world.Tile;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Logger;

public class PathFinder {
    private static final Logger logger = Logger.getLogger(PathFinder.class.getName());

    private final Level level;
    private PathFinderEnum[][] tileMap;
    private int[] mapSize;
    private int coinCount = 0;

    private int startX;
    private int startY;
    private int goalX;
    private int goalY;
    private String reasonForFailure = "Level can be completed";

    public PathFinder(Level level) {
        this.level = level;
    }

    public String reasonForFailure() {
        return reasonForFailure;
    }

    private boolean initTileMap() {
        Start start = level.getStart();
        if (start == null || start.getPosition() == null) {
            reasonForFailure = "No start point found";
            return false;
        }

        Goal goal = level.getGoal();
        if (goal == null || goal.getPosition() == null) {
            reasonForFailure = "No goal point found";
            return false;
        }

        // level must contain at least 1 coin
        boolean containsCoin = false;
        List<Item> items = level.getItems();
        for (Item item : items) {
            if (item.getItemEffect() == ItemEnum.COIN) {
                containsCoin = true;
                break;
            }
        }

        if (!containsCoin) {
            reasonForFailure = "No coins found";
            return false;
        }

        mapSize = level.getMapSize();
        tileMap = new PathFinderEnum[mapSize[0]][mapSize[1]];

        for (int i = 0; i < mapSize[0]; i++) {
            Arrays.fill(tileMap[i], PathFinderEnum.VOID);
        }

        List<Tile> tiles = level.getTiles();
        for (Tile tile : tiles) {
            int x = (int) Math.round(tile.getPosition()[0]);
            int y = (int) Math.round(tile.getPosition()[1]);

            if (x < 0 || x >= mapSize[0] || y < 0 || y >= mapSize[1]) {
                reasonForFailure = "Tile out of bounds";
                return false;
            }

            tileMap[x][y] = PathFinderEnum.TILE;
        }

        // check if start and goal are on tiles
        startX = (int) Math.round(start.getPosition()[0]);
        startY = (int) Math.round(start.getPosition()[1]);

        if (startX < 0 || startX >= mapSize[0] || startY < 0 || startY >= mapSize[1]) {
            reasonForFailure = "Start out of bounds";
            return false;
        }

        if (tileMap[startX][startY] != PathFinderEnum.TILE) {
            reasonForFailure = "Start is not on a tile";
            return false;
        }

        goalX = (int) Math.round(goal.getPosition()[0]);
        goalY = (int) Math.round(goal.getPosition()[1]);

        if (goalX < 0 || goalX >= mapSize[0] || goalY < 0 || goalY >= mapSize[1]) {
            reasonForFailure = "Goal out of bounds";
            return false;
        }

        if (tileMap[goalX][goalY] != PathFinderEnum.TILE) {
            reasonForFailure = "Goal is not on a tile";
            return false;
        }

        for (Item coin : level.getItems()) {
            if (coin.getItemEffect() != ItemEnum.COIN) continue;

            int x = (int) Math.round(coin.getPosition()[0]);
            int y = (int) Math.round(coin.getPosition()[1]);

            if (x < 0 || x >= mapSize[0] || y < 0 || y >= mapSize[1]) {
                reasonForFailure = "Coin out of bounds";
                return false;
            }

            if (tileMap[x][y] == PathFinderEnum.VOID) {
                reasonForFailure = "Coin is not on a tile";
                return false;
            }

            // do not count coins on start or goal
            if (tileMap[x][y] == PathFinderEnum.TILE) {
                coinCount++;
                tileMap[x][y] = PathFinderEnum.COIN;
            }
        }

        return true;
    }

    private boolean check(){
        /* example map [
        * [0,0,1,2,1]
        * [4,1,2,0,1]
        * [0,0,0,1,1]
        * [0,3,1,1,0]
        * ]
        * 0 - empty
        * 1 - tile
        * 2 - tile with coin
        * 3 - start
        * 4 - goal
        * enums are represented by numbers
        * */

        int rows = mapSize[0];
        int cols = mapSize[1];
        boolean[][] visited = new boolean[rows][cols];
        Queue<int[]> queue = new LinkedList<>();

        queue.add(new int[]{startX, startY});
        visited[startX][startY] = true;

        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        boolean goalFound = false;

        while (!queue.isEmpty()) {
            int[] pos = queue.poll();
            int x = pos[0];
            int y = pos[1];

            tileMap[x][y] = PathFinderEnum.VISITED;

            if (x == goalX && y == goalY) {
                goalFound = true;
                if (coinCount == 0) {
                    return true;
                }
            }

            for (int[] dir : directions) {
                int newX = x + dir[0];
                int newY = y + dir[1];

                if (newX < 0 || newX >= rows || newY < 0 || newY >= cols)
                    continue;

                if (visited[newX][newY])
                    continue;

                if (tileMap[newX][newY] == PathFinderEnum.TILE || tileMap[newX][newY] == PathFinderEnum.COIN) {
                    visited[newX][newY] = true;
                    queue.add(new int[]{newX, newY});

                    if (tileMap[newX][newY] == PathFinderEnum.COIN) {
                        coinCount--;
                        if (coinCount == 0 && goalFound) {
                            return true;
                        }
                    }
                }
            }
        }

        if (!goalFound) {
            reasonForFailure = "Goal is unreachable";
        } else if (coinCount > 0) {
            reasonForFailure = "Not all coins can be collected (" + coinCount + " unreachable)";
        } else {
            reasonForFailure = "Unknown error";
        }
        logger.warning(reasonForFailure);
        return false;
    }

    public boolean canLevelBeCompleted() {
        if (!initTileMap()) {
            logger.warning(reasonForFailure);
            return false;
        }

        return check();
    }
}