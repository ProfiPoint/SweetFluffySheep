package cz.cvut.copakond.sweetfluffysheep.view.utils;

import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.SoundListEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.files.SoundManager;
import cz.cvut.copakond.sweetfluffysheep.model.utils.game.GameObject;
import cz.cvut.copakond.sweetfluffysheep.model.utils.game.ProfileManager;
import cz.cvut.copakond.sweetfluffysheep.model.utils.json.JsonFileManager;
import cz.cvut.copakond.sweetfluffysheep.view.interfaces.IClickListener;
import cz.cvut.copakond.sweetfluffysheep.view.interfaces.IInteractableFrame;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import java.io.File;
import java.util.Random;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * AppViewManager is a singleton class that manages the main application view,
 * including the canvas, overlay, and background video.
 * It handles user interactions, settings, and switching between different frames.
 */
public class AppViewManager {
    private static final Logger logger = Logger.getLogger(AppViewManager.class.getName());

    private static AppViewManager instance;

    private IClickListener clickListener;
    private final Canvas canvas = new Canvas();
    private final StackPane overlay = new StackPane(); // for frames/views
    private final Media backgroundMedia;
    private final MediaPlayer preloadedMediaPlayer;
    private MediaPlayer backgroundMediaPlayer;
    private MediaView backgroundVideoView;
    private static boolean isBackgroundVideoPlaying;
    private final Scene scene;
    private final Stage stage;
    private Pane currentFrame; // currently visible frame

    private static String profilesPath;
    private static String texturesPath;

    /**
     * Initializes the AppViewManager singleton instance with the given stage.
     * This method should be called only once at the start of the application.
     *
     * @param stage The primary stage of the application.
     */
    public static void init(Stage stage) {
        if (instance == null) {
            instance = new AppViewManager(stage);
        }
    }

    /**
     * Returns the singleton instance of AppViewManager.
     *
     * @return The AppViewManager instance.
     */
    public static AppViewManager get() {
        return instance;
    }

    /**
     * Returns the canvas used for drawing.
     *
     * @return The canvas.
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * Returns the overlay pane used for displaying frames.
     *
     * @return The overlay pane.
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Sets the full-screen mode for the application.
     *
     * @param fullScreen true to enable full-screen mode, false to disable it.
     */
    public void setFullScreen(boolean fullScreen) {
        stage.setFullScreen(fullScreen);
    }

    /**
     * Sets the click listener for handling mouse clicks.
     *
     * @param listener The click listener to set.
     */
    public void setClickListener(IClickListener listener) {
        this.clickListener = listener;
    }

    public static void setProfilesPath(String path) {
        profilesPath = path;
    }

    public static void setTexturesPath(String path) {
        texturesPath = path;
    }

    /**
     * Creates a new AppViewManager instance with the given stage.
     * This constructor is private to enforce the singleton pattern.
     *
     * @param stage The primary stage of the application.
     */
    private AppViewManager(Stage stage) {
        this.stage = stage;

        // Load the background video, this is not a part of TextureEnum, to avoid supporting video files
        File videoFile = new File(texturesPath+"/level/backgrounds/background.mp4");
        if (!videoFile.exists()) {
            throw new RuntimeException("Background video file not found at " + videoFile.getAbsolutePath());
        }

        // Set the media player to loop indefinitely
        logger.info("Background video loaded from " + videoFile.getAbsolutePath());
        this.backgroundMedia = new Media(videoFile.toURI().toString());
        this.preloadedMediaPlayer = new MediaPlayer(backgroundMedia);
        this.preloadedMediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);

        // Set the black strips and background color
        StackPane canvasHolder = new StackPane(canvas);
        canvasHolder.setAlignment(Pos.CENTER);
        canvasHolder.setStyle("-fx-background-color: black;");

        overlay.setPickOnBounds(false);
        overlay.setAlignment(Pos.CENTER);

        StackPane root = new StackPane(canvasHolder, overlay);
        root.setStyle("-fx-background-color: black;");

        scene = new Scene(root, stage.getWidth(), stage.getHeight(), Color.BLACK);

        // add Fullscreen toggle F11
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.F11) {
                stage.setFullScreen(!stage.isFullScreen());
            }
        });

        // add resize listener
        scene.widthProperty().addListener(onResize());
        scene.heightProperty().addListener(onResize());

        // add mouse click listener
        scene.setOnMouseClicked(event -> {
            if (clickListener != null) {
                clickListener.handleClick(event);
            }
        });

        stage.setScene(scene);
        stage.show();

        updateCanvasSize();
    }

    /**
     * Opens the settings dialog for adjusting application settings.
     * This includes music volume, SFX volume, FPS, and fullscreen mode.
     */
    public void openSettings() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Settings");
        dialog.initOwner(stage); // set the owner of the dialog to the main stage
        dialog.initModality(Modality.APPLICATION_MODAL); // block input to other windows

        ButtonType confirmButtonType = new ButtonType("Confirm");
        dialog.getDialogPane().getButtonTypes().add(confirmButtonType);

        // get current settings values
        int musicVolume = SoundManager.getMusicVolume();
        int sfxVolume = SoundManager.getSfxVolume();
        int[] fps = { GameObject.getFPS() }; // use an array to allow modification in the lambda

        Label musicVolumeLabel = new Label();
        Label sfxVolumeLabel = new Label();
        Label fpsLabel = new Label();
        Label restartWarning = new Label("Game NEEDS to RESTART to apply FPS changes.");
        restartWarning.setTextFill(Color.WHITE);

        Slider musicVolumeSlider = createSlider(musicVolume, 0, 100, 25, musicVolumeLabel, "Music Volume", SoundManager::setMusicVolume);

        // SFX volume slider
        long[] lastTimeSFXPlayed = { 0 };
        Slider sfxVolumeSlider = createSlider(sfxVolume, 0, 100, 25, sfxVolumeLabel, "SFX Volume", newVal -> {
            SoundManager.setSfxVolume(newVal);
            if (System.currentTimeMillis() - lastTimeSFXPlayed[0] > 200) {
                lastTimeSFXPlayed[0] = System.currentTimeMillis();
                SoundManager.playSound(SoundListEnum.ENEMY_DOWN);
            }
        });

        // FPS slider
        Slider fpsSlider = createSlider(fps[0], 0, 240, 60, fpsLabel, "FPS", newVal -> {
            if (newVal != fps[0]) {
                restartWarning.setTextFill(Color.RED);
            } else {
                restartWarning.setTextFill(Color.WHITE);
            }
        });

        // Fullscreen checkbox
        CheckBox fullscreenCheckBox = new CheckBox("Enable Fullscreen");
        fullscreenCheckBox.setSelected(stage.isFullScreen());
        fullscreenCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            logger.info("Fullscreen: " + newVal);
            stage.setFullScreen(newVal);
        });

        // Create a grid layout for the dialog
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(30, 50, 30, 50));
        grid.setAlignment(Pos.CENTER);

        grid.add(musicVolumeLabel, 0, 0);
        grid.add(musicVolumeSlider, 1, 0);
        grid.add(sfxVolumeLabel, 0, 1);
        grid.add(sfxVolumeSlider, 1, 1);
        grid.add(fpsLabel, 0, 2);
        grid.add(fpsSlider, 1, 2);
        grid.add(restartWarning, 0, 3);
        grid.add(fullscreenCheckBox, 1, 4);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setMinWidth(700);

        // Set the button types for the dialog
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == confirmButtonType) {
            musicVolume = (int) musicVolumeSlider.getValue();
            sfxVolume = (int) sfxVolumeSlider.getValue();
            boolean hasFpsChanged = fps[0] != (int) fpsSlider.getValue();
            fps[0] = Math.max(1,(int) fpsSlider.getValue());
            boolean isFullscreen = fullscreenCheckBox.isSelected();
            String path = profilesPath + "/" + ProfileManager.getCurrentProfile() + "/_SETTINGS.json";
            // write settings to JSON
            if (!JsonFileManager.writeSettingsToJson(path, musicVolume, sfxVolume, fps[0], isFullscreen)) {
                logger.severe("Failed to write settings to file " + path);
            }
            // closes the game if FPS changed
            if (hasFpsChanged) {
                GameObject.setFPS(fps[0]);
                logger.info("Game is going to close, because FPS changed.");
                System.exit(0);
            }
        }
    }

    /**
     * Initializes the application settings from a JSON file.
     * This method reads the settings from the specified path and applies them to the application.
     *
     * @param profilesPath The path to the profiles' directory.
     */
    public static void initSettings(String profilesPath) {
        String profile = ProfileManager.getCurrentProfile();
        // check if the selected profile exists
        if (profile == null || profile.isBlank()) {
            logger.info("No profile selected, using default settings.");
            return;
        }
        // apply settings from JSON
        String path = profilesPath + "/" + profile + "/_SETTINGS.json";
        List<Integer> settings = JsonFileManager.readSettingsFromJson(path);
        if (settings != null) {
            int musicVolume = settings.get(0);
            int sfxVolume = settings.get(1);
            int fps = settings.get(2);
            boolean isFullscreen = settings.get(3) == 1;
            SoundManager.setMusicVolume(musicVolume);
            SoundManager.setSfxVolume(sfxVolume);
            AppViewManager.get().setFullScreen(isFullscreen);
            GameObject.setFPS(fps);
        } else {
            logger.info("Settings not found, using default values.");
        }
    }

    /**
     * Creates a slider with the specified parameters.
     *
     * @param initialValue The initial value of the slider.
     * @param min          The minimum value of the slider.
     * @param max          The maximum value of the slider.
     * @param majorTick    The major tick unit of the slider.
     * @param label        The label to display the current value.
     * @param labelPrefix  The prefix for the label text.
     * @param valueHandler The handler to call when the slider value changes.
     * @return The created slider.
     */
    private Slider createSlider(int initialValue, int min, int max, int majorTick,
                                Label label, String labelPrefix, java.util.function.IntConsumer valueHandler) {
        label.setText(labelPrefix + " (" + initialValue + "): ");
        Slider slider = new Slider(min, max, initialValue);
        slider.setPrefWidth(300);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(majorTick);
        slider.setBlockIncrement(1);
        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int val = newVal.intValue();
            label.setText(labelPrefix + " (" + val + "): ");
            valueHandler.accept(val);
        });
        return slider;
    }

    /**
     * Returns the current frame displayed in the overlay.
     *
     * @return The current frame.
     */
    private ChangeListener<Number> onResize() {
        return (obs, oldVal, newVal) -> updateCanvasSize();
    }

    /**
     * Updates the size of the canvas and overlay based on the current scene dimensions.
     * This method ensures that the canvas maintains a 16:9 aspect ratio.
     */
    private void updateCanvasSize() {
        double maxW = scene.getWidth();
        double maxH = scene.getHeight();
        double ratio = 16.0 / 9.0;

        double w = maxW;
        double h = maxW / ratio;

        // ensure the canvas fits within the scene dimensions
        if (h > maxH) {
            h = maxH;
            w = h * ratio;
        }

        canvas.setWidth(w);
        canvas.setHeight(h);

        // set the black strips size
        overlay.setMinSize(w, h);
        overlay.setPrefSize(w, h);
        overlay.setMaxSize(w, h);

        if (currentFrame instanceof IInteractableFrame frame) {
            frame.onResizeCanvas(w, h);
        }

        draw();
    }

    /**
     * Update the canvas size and redraw the current frame.
     * Used to refresh the canvas when the scene is resized.
     */
    public void update() {
        this.updateCanvasSize();
    }

    /**
     * Draws the current frame on the canvas.
     * This method is called to render the current frame.
     */
    private void draw() {
        var gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        if (currentFrame instanceof IInteractableFrame frame) {
            frame.draw(gc);
        }
    }

    /**
     * Preloads the background video for smoother playback.
     * This method should be called before playing the video.
     */
    public static void playBackgroundVideo() {
        if (isBackgroundVideoPlaying) {
            return;
        }
        stopBackgroundVideo();
        isBackgroundVideoPlaying = true;

        instance.backgroundMediaPlayer = instance.preloadedMediaPlayer;
        double duration = instance.backgroundMedia.getDuration().toSeconds();
        double randomStart = new Random().nextDouble() * duration;
        instance.backgroundMediaPlayer.seek(javafx.util.Duration.seconds(randomStart));

        instance.backgroundVideoView = new MediaView(instance.backgroundMediaPlayer);
        instance.backgroundVideoView.setPreserveRatio(true);
        instance.overlay.getChildren().addFirst(instance.backgroundVideoView);

        instance.backgroundMediaPlayer.play();
    }

    /**
     * Stops the background video playback.
     * This method should be called when the video is no longer needed.
     */
    public static void stopBackgroundVideo() {
        if (instance.backgroundMediaPlayer != null) {
            instance.backgroundMediaPlayer.pause();
            instance.overlay.getChildren().remove(instance.backgroundVideoView);
            instance.backgroundVideoView = null;
            isBackgroundVideoPlaying = false;
        }
    }

    /**
     * Resizes the background video to fit the canvas dimensions.
     * This method is called to ensure the video covers the entire canvas area.
     */
    public void resizeBackgroundVideo() {
        if (backgroundVideoView != null) {
            backgroundVideoView.setFitWidth(canvas.getWidth());
            backgroundVideoView.setFitHeight(canvas.getHeight());
        }
    }

    /**
     * Switches to a new frame and updates the canvas size.
     * This method is used to change the current view displayed in the overlay.
     *
     * @param newFrame The new frame to switch to.
     */
    public void switchTo(Pane newFrame) {
        overlay.getChildren().removeIf(node -> node != backgroundVideoView); // keep the video playing
        currentFrame = newFrame;
        overlay.getChildren().add(newFrame);
        updateCanvasSize();
        ButtonSoundInjector.injectToAllButtons(newFrame);
        logger.info("Switched to " + newFrame.getClass().getSimpleName());
    }
}