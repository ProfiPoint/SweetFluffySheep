package cz.cvut.copakond.sweetfluffysheep.model.utils.levels;

import cz.cvut.copakond.sweetfluffysheep.model.world.Level;

/**
 * Utility class for handling level frame interactions.
 * This class provides methods to calculate the tile clicked based on the mouse coordinates.
 */
public class LevelFrameUtils {
    private static final double LEVEL_BOX_HEIGHT_COEFF = 1 - 11.111 / 100;

    /**
     * Calculates the tile clicked based on the mouse coordinates and the level size.
     *
     * @param x             The x-coordinate of the mouse click.
     * @param y             The y-coordinate of the mouse click.
     * @param appCanvasSize The size of the application canvas.
     * @param sceneSize     The size of the scene.
     * @param level         The level object.
     * @return An array containing the x and y indices of the clicked tile, or {-1, -1} if outside bounds.
     */
    public static int[] getTileClicked(int x, int y, int[] appCanvasSize, int[] sceneSize, Level level) {
        // the scene size is without the UI box, so we need to adjust its height
        sceneSize[1] = (int) (sceneSize[1] / LEVEL_BOX_HEIGHT_COEFF);

        int[] offset = {
                (appCanvasSize[0] - sceneSize[0]) / 2,
                (appCanvasSize[1] - sceneSize[1]) / 2
        };

        int[] relativeClick = {
                x - offset[0],
                y - offset[1]
        };

        // check if the click is inside the canvas
        if (relativeClick[0] < 0 || relativeClick[1] < 0 ||
                relativeClick[0] >= sceneSize[0] || relativeClick[1] >= sceneSize[1]) {
            return new int[] { -1, -1 };
        }

        // get the size of the map
        int[] mapSize = level.getMapSize();
        double[] tileSize = {
                (double) sceneSize[0] / mapSize[0],
                (sceneSize[1] * LEVEL_BOX_HEIGHT_COEFF) / mapSize[1]
        };

        // calculate the position of the tile clicked
        int[] position = new int[] {
                (int) Math.floor(relativeClick[0] / tileSize[0]),
                (int) Math.floor(relativeClick[1] / tileSize[1])
        };

        // check if the position is inside the map
        if (position[0] < 0 || position[1] < 0 ||
                position[0] >= mapSize[0] || position[1] >= mapSize[1]) {
            return new int[] { -1, -1 };
        }
        return position;
    }
}