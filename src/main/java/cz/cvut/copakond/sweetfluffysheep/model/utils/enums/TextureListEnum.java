package cz.cvut.copakond.sweetfluffysheep.model.utils.enums;

import java.util.logging.Logger;

/**
 * Enum representing different texture lists used in the game.
 * Each enum constant contains information about the texture name, file name pattern, count of textures,
 * and whether it should return a list of textures or a single texture.
 */
public enum TextureListEnum {
    WOLF("wolf", "characters/wolves/wolf_{i}.png",(16+9)*4*2),
    SHEEP("sheep", "characters/sheep/sheep_{i}.png", (16+9)*4*2),
    TILE("tile", "tiles/tile_{i}.png",32), // tile_001, tile_002
    START("start", "objects/start/start_{i}.png",5), // 1,2,3,4 rotations, 5 - invisible
    GOAL("goal", "objects/goal/goal_{i}.png",32*4*2), // 1,128 - unlocked, 129,256 - locked
    EMPTY("empty", "missing_texture.png",1, false),
    COIN("coin", "items/coin/coin_{i}.png", 32),
    FREEZE("freeze", "items/freeze/freeze_{i}.png", 32),
    RAGE("rage", "items/rage/rage_{i}.png", 32),
    ARROW("arrow", "arrows/arrow_{i}.png", 9*4),
    BACKGROUND("background", "backgrounds/background_{i}.png", 1),
    ICON("icon", "backgrounds/sfs_icon.png", 1);

    private static final Logger logger = Logger.getLogger(TextureListEnum.class.getName());

    private final String name;
    private final String fileName;
    private final int count;
    private final boolean return_list; // return a list of textures?
    private final boolean return_auto; // automatically return?

    private static String levelsPath;

    /**
     * Constructor for TextureListEnum.
     *
     * @param name      the name of the texture list
     * @param fileName  the file name pattern for the textures
     * @param count     the number of textures in the list
     */
    TextureListEnum(String name, String fileName, int count) {
        this.name = name;
        this.fileName = fileName;
        this.count = count;
        this.return_auto = true;
        this.return_list = true;
    }

    /**
     * Constructor for TextureListEnum with a flag to return a list of textures.
     *
     * @param name      the name of the texture list
     * @param fileName  the file name pattern for the textures
     * @param count     the number of textures in the list
     * @param returnList whether to return a list of textures or a single texture
     */
    TextureListEnum(String name, String fileName, int count, boolean returnList) {
        this.name = name;
        this.fileName = fileName;
        this.count = count;
        this.return_auto = false;
        this.return_list = returnList;
    }


    public static void setLevelsPath(String path) {
        levelsPath = path;
    }

    public String getValue() {
        return name;
    }

    public int getCount() {
        return count;
    }

    /**
     * Returns the list of textures for the given enum constant.
     *
     * @return an array of texture paths
     */
    public String[] getTextures() {
        if (!return_auto && !return_list) {
            return getSingleTexture();
        }
        return getTexturesList();
    }

    /**
     * Returns the list of a single texture for the given enum constant.
     *
     * @return an array of texture paths
     */
    private String[] getSingleTexture() {
        String[] names = new String[1];
        names[0] = generateTexturePath(1);
        return names;
    }

    /**
     * Returns the list of textures for the given enum constant.
     *
     * @return an array of texture paths
     */
    private String[] getTexturesList() {
        String[] names = new String[count];
        for (int i = 0; i < count; i++) {
            names[i] = generateTexturePath(i + 1);
        }
        return names;
    }

    /**
     * Generates the texture path for the given index based on the level path and file name.
     *
     * @param index the index of the texture
     * @return the generated texture path
     */
    private String generateTexturePath(int index) {
        return levelsPath + "/level/" + fileName.replace("{i}", String.format("%03d", index));
    }

    /**
     * Returns the texture path for the given enum constant.
     *
     * @return the texture path
     */
    public static TextureListEnum fromValue(String value) {
        for (TextureListEnum texture : TextureListEnum.values()) {
            if (texture.getValue().equals(value)) {
                return texture;
            }
        }

        // for debugging purposes
        StringBuilder allPaths = new StringBuilder();
        for (TextureListEnum texture : TextureListEnum.values()) {
            for (String path : texture.getTextures()) {
                allPaths.append(" - ").append(path).append("; CODE NAME: [").append(texture.getValue()).append("]\n ");
            }
        }

        logger.severe(ErrorMsgsEnum.TEXTURE_UNKNOWN_NAME.getValue("Texture name: " + value + ")\n [List of all " +
                "defined textures] " + "\n " + allPaths));
        return TextureListEnum.EMPTY;
    }
}