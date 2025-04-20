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

    private static MediaPlayer musicPlayer;
    private static String currentMusicUri;

    public static void playSound(SoundListEnum sound) {
        if (sound == SoundListEnum.NONE) return;

        String filePath = sound.getRandomSound();
        String uri = new File(filePath).toURI().toString();

        if (sound.isSFX()) {
            playSfx(uri, sound);
        } else {
            stopAllSfx();
            playMusic(uri, sound.shouldPlayOnRepeat());
        }
    }

    public static void stopSfx(SoundListEnum sound) {
        List<MediaPlayer> players = loopedSfxPlayers.remove(sound.name());
        if (players != null) {
            for (MediaPlayer player : players) {
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
        }
    }

    private static void playSfx(String uri, SoundListEnum sound) {
        Media media = getCachedMedia(uri);
        MediaPlayer player = new MediaPlayer(media);

        if (sound.shouldPlayOnRepeat()) {
            if (musicPlayer != null && musicPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                musicPlayer.pause();
            }

            player.setCycleCount(MediaPlayer.INDEFINITE);
            loopedSfxPlayers.computeIfAbsent(sound.name(), k -> new ArrayList<>()).add(player);
        } else {
            player.setCycleCount(1);
            player.setOnEndOfMedia(() -> {
                player.stop();
                player.dispose();
            });
        }

        player.play();
    }

    private static void playMusic(String uri, boolean loop) {
        if (uri.equals(currentMusicUri)) {
            if (musicPlayer != null && (musicPlayer.getStatus() == MediaPlayer.Status.PLAYING || musicPlayer.getStatus() == MediaPlayer.Status.PAUSED)) {
                return;
            }
        }

        stopMusic();

        Media media = getCachedMedia(uri);
        MediaPlayer player = new MediaPlayer(media);
        player.setCycleCount(loop ? MediaPlayer.INDEFINITE : 1);
        player.play();

        musicPlayer = player;
        currentMusicUri = uri;
    }

    private static Media getCachedMedia(String uri) {
        return mediaCache.computeIfAbsent(uri, key -> {
            return new Media(key);
        });
    }

    private static void stopAllSfx() {
        for (List<MediaPlayer> players : loopedSfxPlayers.values()) {
            for (MediaPlayer player : players) {
                player.stop();
                player.dispose();
            }
        }
        loopedSfxPlayers.clear();
    }
}
