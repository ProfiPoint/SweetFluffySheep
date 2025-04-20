package cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum SoundListEnum {
    MENU_THEME("menu",  "music/menu.wav", false,true, 1, 100),
    GAME_THEME("game",  "music/game.wav", false,true, 1, 100),
    EDITOR_THEME("editor",  "music/editor.wav", false,true, 1, 100),
    NONE("none",  "", false,true, 1, 100),

    ARROW("arrow",  "sfx/arrow.wav", true,false, 1, 100),
    ARROW_DEL("arrow-del",  "sfx/arrow-del.wav", true,false, 1, 100),
    ENEMY_DOWN("enemy-down",  "sfx/enemy-down.wav", true,false, 1, 100),
    FINISH("finish",  "sfx/finish.wav", true,false, 1, 100),
    GAME_OVER("game-over",  "sfx/game-over.wav", true,false, 1, 100),
    GOAL_UNLOCKED("goal-unlocked",  "sfx/goal-unlocked.wav", true,false, 1, 100),
    HERO_DOWN("hero-down",  "sfx/hero-down_{i}.wav", true,false, 5, 100),
    HERO_ENEMY_COLLISION("hero-enemy-collision",  "sfx/hero-enemy-collision.wav", true,false, 1, 100),
    HERO_FINISH("hero-finish",  "sfx/hero-finish.wav", true,false, 1, 100),
    HOLD("hold",  "sfx/hold.wav", true,true, 1, 100),
    IMMORTAL("immortal",  "sfx/immortal.wav", true,true, 1, 100),
    MONEY("money",  "sfx/money.wav", true,false, 1, 100),
    MOUSE_CLICK("mouse-click",  "sfx/mouse-click.wav", true,false, 1, 100),
    PRIZE("prize",  "sfx/prize.wav", true,false, 1, 100),
    PROFILE_CREATED("profile-created",  "sfx/profile-created.wav", true,false, 1, 100),
    TIME_OUT("time_out",  "sfx/time-out.wav", true,false, 1, 100);

    private final String name;
    private String fileName;
    private boolean isSFX;
    private boolean playOnRepeat;
    private int count;
    private int volume;


    private static String soundPath;

    public static void setSoundPath(String path) {
        soundPath = path + "/";
    }

    SoundListEnum(String name, String fileName, boolean isSFX, boolean playOnRepeat, int variants, int volume) {
        this.name = name;
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
            names[i] = soundPath + fileName.replace("{i}", String.format("%03d"
                , i + 1));
            names[i] = names[i].replace("{b}", i == 0 ? "false" : "true");
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
