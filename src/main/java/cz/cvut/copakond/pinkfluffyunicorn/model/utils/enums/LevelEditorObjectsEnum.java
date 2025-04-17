package cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum LevelEditorObjectsEnum {
    //"path", "removePath", "cloud", "coin", "fire", "rainbow", "start", "goal", "destroy"
    PATH("path"),
    REMOVEPATH("removePath"),
    CLOUD("cloud"),
    COIN("coin"),
    FIRE("fire"),
    RAINBOW("rainbow"),
    START("start"),
    GOAL("goal"),
    DESTROY("destroy"),
    EMPTY("empty");

    private final String name;

    LevelEditorObjectsEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public LevelEditorObjectsEnum getLevelEditorObjectsEnum(String name) {
        for (LevelEditorObjectsEnum object : LevelEditorObjectsEnum.values()) {
            if (object.getName().equals(name)) {
                return object;
            }
        }
        return EMPTY;
    }
}

