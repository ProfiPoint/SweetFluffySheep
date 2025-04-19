package cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums;

public enum LevelEditorObjectsEnum {
    TILE("tile"),
    REMOVETILE("removeTile"),
    CLOUD("cloud"),
    COIN("coin"),
    FIRE("fire"),
    RAINBOW("rainbow"),
    START("start"),
    GOAL("goal"),
    ROTATE("rotate"),
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

