package cz.cvut.copakond.pinkfluffyunicorn.model.utils.files;

import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.SoundListEnum;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static void setMusicVolume(int newMusicVolume) {
        musicVolume = newMusicVolume;
        updateMusicVolume();
    }

    public static void setSfxVolume(int newSfxVolume) {
        sfxVolume = newSfxVolume;
        updateAllSfxVolumes();
    }

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

    public static void stopMusic() {
        if (musicPlayer != null) {
            musicPlayer.stop();
            musicPlayer.dispose();
            musicPlayer = null;
            currentMusicUri = null;
            currentMusicSound = null;
        }
    }

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

    private static void updateMusicVolume() {
        if (musicPlayer != null && currentMusicSound != null) {
            setEffectiveVolume(musicPlayer, currentMusicSound.getVolume(), musicVolume);
        }
    }

    private static void updateAllSfxVolumes() {
        for (Map.Entry<MediaPlayer, SoundListEnum> entry : sfxToSoundMap.entrySet()) {
            MediaPlayer player = entry.getKey();
            SoundListEnum sound = entry.getValue();
            setEffectiveVolume(player, sound.getVolume(), sfxVolume);
        }
    }

    private static void setEffectiveVolume(MediaPlayer player, int soundVolume, int generalVolume) {
        double effectiveVolume = (soundVolume * generalVolume) / 10000.0;
        player.setVolume(effectiveVolume);
    }

    private static Media getCachedMedia(String uri) {
        return mediaCache.computeIfAbsent(uri, key -> new Media(key));
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