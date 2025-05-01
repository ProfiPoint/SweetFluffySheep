package cz.cvut.copakond.sweetfluffysheep.model.utils.enums;

public enum LevelEditorObjectsEnum {
    TILE("tile"),
    REMOVETILE("removeTile"),
    WOLF("wolf"),
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

}

