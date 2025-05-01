package cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums;

public enum SoundListEnum {
    MENU_THEME("music/menu.wav", false,true, 1, 100),
    GAME_THEME("music/game.wav", false,true, 1, 100),
    EDITOR_THEME("music/editor.wav", false,true, 1, 100),
    NONE("", false,true, 1, 100),

    ARROW("sfx/arrow.wav", true,false, 1, 100),
    ARROW_DEL("sfx/arrow-del.wav", true,false, 1, 100),
    ENEMY_DOWN("sfx/enemy-down.wav", true,false, 1, 100),
    FINISH("sfx/finish.wav", true,false, 1, 100),
    GAME_OVER("sfx/game-over.wav", true,false, 1, 100),
    GOAL_UNLOCKED("sfx/goal-unlocked.wav", true,false, 1, 100),
    HERO_DOWN("sfx/hero-down_{i}.wav", true,false, 5, 100),
    HERO_ENEMY_COLLISION("sfx/hero-enemy-collision.wav", true,false, 1, 100),
    HERO_FINISH("sfx/hero-finish.wav", true,false, 1, 100),
    HOLD("sfx/hold.wav", true,true, 1, 100),
    IMMORTAL("sfx/immortal.wav", true,true, 1, 100),
    MONEY("sfx/money.wav", true,false, 1, 100),
    MOUSE_CLICK("sfx/mouse-click.wav", true,false, 1, 100),
    PRIZE("sfx/prize.wav", true,false, 1, 100),
    PROFILE_CREATED("sfx/profile-created.wav", true,false, 1, 100),
    TIME_OUT("sfx/time-out.wav", true,false, 1, 100);

    private final String fileName;
    private final boolean isSFX;
    private final boolean playOnRepeat;
    private final int count;
    private final int volume;

    private static String soundPath;

    public static void setSoundPath(String path) {
        soundPath = path == null ? "" : path.endsWith("/") ? path : path + "/";
    }


    SoundListEnum(String fileName, boolean isSFX, boolean playOnRepeat, int variants, int volume) {
        this.fileName = fileName;
        this.isSFX = isSFX;
        this.playOnRepeat = playOnRepeat;
        this.count = variants;
        this.volume = volume;
    }

    public String getRandomSound() {
        String[] names = getSounds();
        int randomIndex = (int) (Math.random() * names.length);
        return names[randomIndex];
    }

    public String[] getSounds() {
        String[] names = new String[count];
        for (int i = 0; i < count; i++) {
            names[i] = soundPath + fileName.replace("{i}", String.format("%03d", i + 1));
        }
        return names;
    }

    public int getVolume() {
        return volume;
    }

    public boolean isSFX() {
        return isSFX;
    }

    public boolean shouldPlayOnRepeat() {
        return playOnRepeat;
    }
}