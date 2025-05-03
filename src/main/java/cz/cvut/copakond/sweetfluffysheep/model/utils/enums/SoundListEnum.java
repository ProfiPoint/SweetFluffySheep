package cz.cvut.copakond.sweetfluffysheep.model.utils.enums;

/**
 * Enum representing a list of sound effects and music tracks used in the game.
 * <p>
 * Each enum constant contains information about the sound file name, whether it is a sound effect (SFX),
 * whether it should play on repeat, the number of variants available, and the volume level.
 */
public enum SoundListEnum {
    MENU_THEME("music/menu.wav", false,true, 1, 75),
    GAME_THEME("music/game.wav", false,true, 1, 75),
    EDITOR_THEME("music/editor.wav", false,true, 1, 75),
    NONE("", false,true, 1, 75),

    ARROW("sfx/arrow.wav", true,false, 1, 80),
    ARROW_DEL("sfx/arrow-del.wav", true,false, 1, 75),
    DESTROY("sfx/destroy.wav", true,false, 1, 75),
    ENEMY_DOWN("sfx/enemy-down.wav", true,false, 1, 75),
    FINISH("sfx/finish.wav", true,false, 1, 60),
    GAME_OVER("sfx/game-over.wav", true,false, 1, 80),
    GOAL_UNLOCKED("sfx/goal-unlocked.wav", true,false, 1, 70),
    HERO_DOWN("sfx/hero-down_{i}.wav", true,false, 5, 70),
    HERO_ENEMY_COLLISION("sfx/hero-enemy-collision.wav", true,false, 1, 75),
    HERO_FINISH("sfx/hero-finish.wav", true,false, 1, 65),
    HOLD("sfx/hold.wav", true,true, 1, 70),
    IMMORTAL("sfx/immortal.wav", true,true, 1, 70),
    MONEY("sfx/money.wav", true,false, 1, 75),
    MOUSE_CLICK("sfx/mouse-click.wav", true,false, 1, 75),
    PRIZE("sfx/prize.wav", true,false, 1, 75),
    PROFILE_CREATED("sfx/profile-created.wav", true,false, 1, 65),
    ROTATE("sfx/rotate.wav", true,false, 1, 75),
    TIME_OUT("sfx/time-out.wav", true,false, 1, 60);

    private final String fileName;
    private final boolean isSFX;
    private final boolean playOnRepeat;
    private final int count;
    private final int volume;

    private static String soundPath;

    /**
     * Constructor for SoundListEnum.
     *
     * @param fileName      The file name of the sound effect or music track.
     * @param isSFX         Indicates if the sound is a sound effect (true) or music (false).
     * @param playOnRepeat   Indicates if the sound should play on repeat (true) or not (false).
     * @param variants       The number of variants available for the sound.
     * @param volume         The volume level of the sound.
     */
    SoundListEnum(String fileName, boolean isSFX, boolean playOnRepeat, int variants, int volume) {
        this.fileName = fileName;
        this.isSFX = isSFX;
        this.playOnRepeat = playOnRepeat;
        this.count = variants;
        this.volume = volume;
    }

    /**
     * Returns the file name of the sound effect or music track.
     */
    public static void setSoundPath(String path) {
        soundPath = path == null ? "" : path.endsWith("/") ? path : path + "/";
    }

    /**
     * Returns the file name of the sound effect or music track.
     *
     * @return The file name of the sound.
     */
    public String getRandomSound() {
        String[] names = getSounds();
        int randomIndex = (int) (Math.random() * names.length);
        return names[randomIndex];
    }

    /**
     * Returns the file names of the sound effect or music track.
     *
     * @return The file names of the sound.
     */
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