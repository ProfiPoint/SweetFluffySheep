package cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum TextureListEnum {
    CLOUD("cloud", "characters/clouds/cloud_{i}.png",100),
    UNICORN("unicorn", "characters/unicorns/unicorn_{i}.png", 100),
    TILE("tile", "tiles/tile_{i}.png",32), // tile_001, tile_002
    START("start", "objects/start/start_{b}.png",2), // start_true - unlocked, start_false - locked
    GOAL("goal", "objects/goal/goal_{i}.png",256), // 1,128 - unlocked, 129,256 - locked
    EMPTY("empty", "missing_texture.png",1, false),
    COIN("coin", "items/coin/coin_{i}.png", 32),
    FIRE("fire", "items/fire/fire_{i}.png", 32),
    RAINBOW("rainbow", "items/rainbow/rainbow_{i}.png", 32),
    ARROW("arrow", "arrows/arrow_{i}.png", 36);

    private final String name;
    private String fileName;
    private int count;
    private boolean returnList;
    private boolean returnAuto; // automatically return

    private static String levelsPath;

    public static void setLevelsPath(String path) {
        levelsPath = path;
    }

    TextureListEnum(String name, String fileName) {
        this.name = name;
        this.fileName = fileName;
        this.count = 1;
        this.returnAuto = true;
        this.returnList = true;
    }

    TextureListEnum(String name, String fileName, int count) {
        this.name = name;
        this.fileName = fileName;
        this.count = count;
        this.returnAuto = true;
        this.returnList = true;
    }

    TextureListEnum(String name, String fileName, int count, boolean returnList) {
        this.name = name;
        this.fileName = fileName;
        this.count = count;
        this.returnAuto = false;
        this.returnList = returnList;
    }

    public String[] getTextures() {
        if (!returnAuto && !returnList) {
            return new String[]{getName()[0]};
        }
        return getNames();
    }

    private String[] getName() {
        String[] names = new String[count];
        names[0] = levelsPath + "/level/" + fileName.replace("{i}", String.format("%03d",
                1));
        names[0] = names[0].replace("{b}", "false");
        return names;
    }

    private String[] getNames() {
        String[] names = new String[count];
        for (int i = 0; i < count; i++) {
            names[i] = levelsPath + "/level/" + fileName.replace("{i}", String.format("%03d"
                , i + 1));
            names[i] = names[i].replace("{b}", i == 0 ? "false" : "true");
        }
        return names;
    }

    public String getValue() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public List<String> getTextureByOrder(int order) {
        if (order < 0 || order >= count) {
            return new ArrayList<>();
        }
        return Arrays.asList(getNames()[order]);
    }

    public static TextureListEnum fromValue(String value) {
        for (TextureListEnum texture : TextureListEnum.values()) {
            if (texture.getValue().equals(value)) {
                return texture;
            }
        }
        String allPaths = "";
        for (TextureListEnum texture : TextureListEnum.values()) {
            for (String path : texture.getTextures()) {
                allPaths += " - " + path + "; CODE NAME: [" + texture.getValue() + "]\n ";
            }
        }

        ErrorMsgsEnum.TEXTURE_UNKNOWN_NAME.getValue("Texture name: " + value + ")\n [List of all defined textures] " +
                "\n " + allPaths);
        return TextureListEnum.EMPTY;
    }
}
