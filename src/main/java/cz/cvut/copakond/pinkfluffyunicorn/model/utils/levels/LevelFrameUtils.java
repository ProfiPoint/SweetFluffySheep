package cz.cvut.copakond.pinkfluffyunicorn.model.utils.levels;

public class LevelFrameUtils {
    private final static double levelBoxHeightCoeff = (1 - 11.111 / 100);
    
    public static int[] getTileClicked(int x, int y, int[] appCanvasSize, int[] sceneSize, Level level) {
        // scene size is without the hui box, so we need to adjust it
        sceneSize[1] = (int) (sceneSize[1] * (1/levelBoxHeightCoeff));

        int[] offset = new int[] {
                (appCanvasSize[0] - sceneSize[0]) / 2,
                (int)(appCanvasSize[1] - sceneSize[1]) / 2
        };
        int[] relativeClick = new int[] {
                x - offset[0],
                y - offset[1]
        };

        // check if the click is inside the canvas
        if (relativeClick[0] < 0 || relativeClick[1] < 0 || relativeClick[0] >= sceneSize[0] || relativeClick[1] >= sceneSize[1]) {
            return new int[] {-1,-1};
        }

        // get the size of the map
        int[] mapSize = level.getMapSize();
        double[] oneTileSize = new double[] {
                (double) sceneSize[0] / mapSize[0],
                (double) (sceneSize[1] * levelBoxHeightCoeff) / mapSize[1]
        };
        int[] tileClick = new int[] {
                (int) Math.floor(relativeClick[0] / oneTileSize[0]),
                (int) Math.floor(relativeClick[1] / oneTileSize[1])
        };


        return tileClick;
    }
}
