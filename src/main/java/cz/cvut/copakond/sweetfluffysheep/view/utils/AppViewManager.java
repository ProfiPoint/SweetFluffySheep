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

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class AppViewManager {
    private static final Logger logger = Logger.getLogger(AppViewManager.class.getName());

    private static AppViewManager instance;

    private IClickListener clickListener;
    private final Canvas canvas = new Canvas();
    private final StackPane overlay = new StackPane(); // for frames/views
    private final Scene scene;
    private final Stage stage;
    private Pane currentFrame; // currently visible frame

    private static String profilesPath;

    public static void init(Stage stage) {
        if (instance == null) {
            instance = new AppViewManager(stage);
        }
    }

    public static AppViewManager get() {
        return instance;
    }

    public Scene getScene() {
        return scene;
    }

    public Stage getStage() {
        return stage;
    }

    public void setFullScreen(boolean fullScreen) {
        stage.setFullScreen(fullScreen);
    }

    public void setClickListener(IClickListener listener) {
        this.clickListener = listener;
    }

    public static void setProfilesPath(String path) {
        profilesPath = path;
    }

    private AppViewManager(Stage stage) {
        this.stage = stage;

        StackPane canvasHolder = new StackPane(canvas);
        canvasHolder.setAlignment(Pos.CENTER);
        canvasHolder.setStyle("-fx-background-color: black;");

        overlay.setPickOnBounds(false);
        overlay.setAlignment(Pos.CENTER);

        StackPane root = new StackPane(canvasHolder, overlay);
        root.setStyle("-fx-background-color: black;");

        scene = new Scene(root, stage.getWidth(), stage.getHeight(), Color.BLACK);

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.F11) {
                stage.setFullScreen(!stage.isFullScreen());
            }
        });

        scene.widthProperty().addListener(onResize());
        scene.heightProperty().addListener(onResize());

        scene.setOnMouseClicked(event -> {
            if (clickListener != null) {
                clickListener.handleClick(event);
            }
        });

        stage.setScene(scene);
        stage.show();

        updateCanvasSize();
    }

    public void openSettings() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Settings");
        dialog.initOwner(stage);
        dialog.initModality(Modality.APPLICATION_MODAL);

        ButtonType confirmButtonType = new ButtonType("Confirm");
        dialog.getDialogPane().getButtonTypes().add(confirmButtonType);

        int musicVolume = SoundManager.getMusicVolume();
        int sfxVolume = SoundManager.getSfxVolume();
        int[] fps = { GameObject.getFPS() }; // use an array to allow modification in the lambda

        Label musicVolumeLabel = new Label();
        Label sfxVolumeLabel = new Label();
        Label fpsLabel = new Label();
        Label restartWarning = new Label("Game NEEDS to RESTART to apply FPS changes.");
        restartWarning.setTextFill(Color.WHITE);

        Slider musicVolumeSlider = createSlider(musicVolume, 0, 100, 25, musicVolumeLabel, "Music Volume", SoundManager::setMusicVolume);

        long[] lastTimeSFXPlayed = { 0 };
        Slider sfxVolumeSlider = createSlider(sfxVolume, 0, 100, 25, sfxVolumeLabel, "SFX Volume", newVal -> {
            SoundManager.setSfxVolume(newVal);
            if (System.currentTimeMillis() - lastTimeSFXPlayed[0] > 200) {
                lastTimeSFXPlayed[0] = System.currentTimeMillis();
                SoundManager.playSound(SoundListEnum.ENEMY_DOWN);
            }
        });

        Slider fpsSlider = createSlider(fps[0], 0, 240, 60, fpsLabel, "FPS", newVal -> {
            if (newVal != fps[0]) {
                restartWarning.setTextFill(Color.RED);
            } else {
                restartWarning.setTextFill(Color.WHITE);
            }
        });

        CheckBox fullscreenCheckBox = new CheckBox("Enable Fullscreen");
        fullscreenCheckBox.setSelected(stage.isFullScreen());
        fullscreenCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            logger.info("Fullscreen: " + newVal);
            stage.setFullScreen(newVal);
        });

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

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == confirmButtonType) {
            musicVolume = (int) musicVolumeSlider.getValue();
            sfxVolume = (int) sfxVolumeSlider.getValue();
            boolean hasFpsChanged = fps[0] != (int) fpsSlider.getValue();
            fps[0] = Math.max(1,(int) fpsSlider.getValue());
            boolean isFullscreen = fullscreenCheckBox.isSelected();
            String path = profilesPath + "/" + ProfileManager.getCurrentProfile() + "/_SETTINGS.json";
            if (!JsonFileManager.writeSettingsToJson(path, musicVolume, sfxVolume, fps[0], isFullscreen)) {
                logger.severe("Failed to write settings to file " + path);
            }
            if (hasFpsChanged) {
                GameObject.setFPS(fps[0]);
                logger.info("Game is going to close, because FPS changed.");
                System.exit(0);
            }
        }
    }

    public static void initSettings(String profilesPath) {
        String profile = ProfileManager.getCurrentProfile();
        if (profile == null || profile.isBlank()) {
            logger.info("No profile selected, using default settings.");
            return;
        }
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


    private ChangeListener<Number> onResize() {
        return (obs, oldVal, newVal) -> updateCanvasSize();
    }

    private void updateCanvasSize() {
        double maxW = scene.getWidth();
        double maxH = scene.getHeight();
        double ratio = 16.0 / 9.0;

        double w = maxW;
        double h = maxW / ratio;

        if (h > maxH) {
            h = maxH;
            w = h * ratio;
        }

        canvas.setWidth(w);
        canvas.setHeight(h);

        overlay.setMinSize(w, h);
        overlay.setPrefSize(w, h);
        overlay.setMaxSize(w, h);

        if (currentFrame instanceof IInteractableFrame frame) {
            frame.onResizeCanvas(w, h);
        }

        draw();
    }

    public void update() {
        this.updateCanvasSize();
    }

    private void draw() {
        var gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        if (currentFrame instanceof IInteractableFrame frame) {
            frame.draw(gc);
        }
    }

    public void switchTo(Pane newFrame) {
        overlay.getChildren().clear();
        currentFrame = newFrame;
        overlay.getChildren().add(newFrame);
        updateCanvasSize();
        ButtonSoundInjector.injectToAllButtons(newFrame);
        logger.info("Switched to " + newFrame.getClass().getSimpleName());
    }
}