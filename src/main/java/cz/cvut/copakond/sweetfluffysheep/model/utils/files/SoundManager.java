package cz.cvut.copakond.sweetfluffysheep.model.utils.files;

import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.SoundListEnum;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SoundManager is responsible for managing sound effects and music in the application.
 * It handles playing, stopping, and adjusting the volume of sounds.
 */
public class SoundManager {
    private static final Map<String, Media> mediaCache = new HashMap<>();
    private static final Map<String, List<MediaPlayer>> loopedSfxPlayers = new HashMap<>();
    private static final Map<MediaPlayer, SoundListEnum> sfxToSoundMap = new HashMap<>();

    private static int musicVolume = 50;
    private static int sfxVolume = 50;

    private static MediaPlayer musicPlayer;
    private static SoundListEnum currentMusicSound;
    private static String currentMusicUri;

    public static int getMusicVolume() {
        return musicVolume;
    }

    public static int getSfxVolume() {
        return sfxVolume;
    }

    /**
     * Sets the volume for music and sound effects.
     * And updates the volume of currently playing music.
     * @param newMusicVolume The new volume for music (0-100).
     */
    public static void setMusicVolume(int newMusicVolume) {
        musicVolume = newMusicVolume;
        updateMusicVolume();
    }

    /**
     * Sets the volume for sound effects.
     * And updates the volume of all currently playing sound effects.
     * @param newSfxVolume The new volume for sound effects (0-100).
     */
    public static void setSfxVolume(int newSfxVolume) {
        sfxVolume = newSfxVolume;
        updateAllSfxVolumes();
    }

    /**
     * Plays a sound effect or music track.
     * If the sound is a music track, it stops any currently playing music.
     * @param sound The sound to play.
     */
    public static void playSound(SoundListEnum sound) {
        if (sound == SoundListEnum.NONE) return;

        String filePath = sound.getRandomSound();
        String uri = new File(filePath).toURI().toString();

        if (sound.isSFX()) {
            playSfx(uri, sound);
        } else {
            stopAllSfx();
            playMusic(uri, sound, sound.shouldPlayOnRepeat());
        }
    }

    /**
     * Stops all sound effects and music.
     */
    public static void stopSfx(SoundListEnum sound) {
        List<MediaPlayer> players = loopedSfxPlayers.remove(sound.name());
        if (players != null) {
            for (MediaPlayer player : players) {
                sfxToSoundMap.remove(player);
                player.stop();
                player.dispose();
            }
        }

        if (loopedSfxPlayers.isEmpty() && musicPlayer != null && musicPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
            musicPlayer.play();
        }
    }

    /**
     * Stops all sound effects and music.
     */
    public static void stopMusic() {
        if (musicPlayer != null) {
            musicPlayer.stop();
            musicPlayer.dispose();
            musicPlayer = null;
            currentMusicUri = null;
            currentMusicSound = null;
        }
    }

    /**
     * Plays a sound effect.
     * @param uri The URI of the sound file.
     * @param sound The sound enum representing the sound to play.
     */
    private static void playSfx(String uri, SoundListEnum sound) {
        Media media = getCachedMedia(uri);
        MediaPlayer player = new MediaPlayer(media);
        setEffectiveVolume(player, sound.getVolume(), sfxVolume);

        if (sound.shouldPlayOnRepeat()) {
            if (musicPlayer != null && musicPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                musicPlayer.pause();
            }

            player.setCycleCount(MediaPlayer.INDEFINITE);
            loopedSfxPlayers.computeIfAbsent(sound.name(), k -> new ArrayList<>()).add(player);
            sfxToSoundMap.put(player, sound);
        } else {
            player.setCycleCount(1);
            player.setOnEndOfMedia(() -> {
                player.stop();
                player.dispose();
            });
        }

        player.play();
    }

    /**
     * Plays a music track.
     * @param uri The URI of the music file.
     * @param sound The sound enum representing the music to play.
     * @param loop Whether to loop the music track.
     */
    private static void playMusic(String uri, SoundListEnum sound, boolean loop) {
        if (uri.equals(currentMusicUri)) {
            if (musicPlayer != null &&
                    (musicPlayer.getStatus() == MediaPlayer.Status.PLAYING ||
                            musicPlayer.getStatus() == MediaPlayer.Status.PAUSED)) {
                return;
            }
        }

        stopMusic();

        Media media = getCachedMedia(uri);
        MediaPlayer player = new MediaPlayer(media);
        setEffectiveVolume(player, sound.getVolume(), musicVolume);

        player.setCycleCount(loop ? MediaPlayer.INDEFINITE : 1);
        player.play();

        musicPlayer = player;
        currentMusicUri = uri;
        currentMusicSound = sound;
    }

    /**
     * Updates the volume of the currently playing music.
     */
    private static void updateMusicVolume() {
        if (musicPlayer != null && currentMusicSound != null) {
            setEffectiveVolume(musicPlayer, currentMusicSound.getVolume(), musicVolume);
        }
    }

    /**
     * Updates the volume of all currently playing sound effects.
     */
    private static void updateAllSfxVolumes() {
        for (Map.Entry<MediaPlayer, SoundListEnum> entry : sfxToSoundMap.entrySet()) {
            MediaPlayer player = entry.getKey();
            SoundListEnum sound = entry.getValue();
            setEffectiveVolume(player, sound.getVolume(), sfxVolume);
        }
    }

    /**
     * Sets the effective volume of a MediaPlayer.
     * @param player The MediaPlayer to set the volume for.
     * @param soundVolume The sound volume (0-100).
     * @param generalVolume The general volume (0-100).
     */
    private static void setEffectiveVolume(MediaPlayer player, int soundVolume, int generalVolume) {
        double effectiveVolume = (soundVolume * generalVolume) / 10000.0;
        player.setVolume(effectiveVolume);
    }

    private static Media getCachedMedia(String uri) {
        return mediaCache.computeIfAbsent(uri, Media::new);
    }

    private static void stopAllSfx() {
        for (List<MediaPlayer> players : loopedSfxPlayers.values()) {
            for (MediaPlayer player : players) {
                sfxToSoundMap.remove(player);
                player.stop();
                player.dispose();
            }
        }
        loopedSfxPlayers.clear();
    }
}