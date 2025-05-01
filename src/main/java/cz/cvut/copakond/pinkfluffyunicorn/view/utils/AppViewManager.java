package cz.cvut.copakond.pinkfluffyunicorn.view.utils;

import cz.cvut.copakond.pinkfluffyunicorn.Launcher;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.SoundListEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.files.SoundManager;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.game.GameObject;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.game.ProfileManager;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.json.JsonFileManager;
import cz.cvut.copakond.pinkfluffyunicorn.view.interfaces.IClickListener;
import cz.cvut.copakond.pinkfluffyunicorn.view.interfaces.IDrawableFrame;
import cz.cvut.copakond.pinkfluffyunicorn.view.interfaces.IResizableFrame;
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
    private final StackPane root = new StackPane();
    private final Scene scene;
    private final Stage stage;

    private Pane currentFrame; // currently visible frame
    private static String profilesPath;

    public static void setProfilesPath(String path) {
        profilesPath = path;
    }

    public static void init(Stage stage) {
        if (instance == null) {
            instance = new AppViewManager(stage);
        }
    }

    public static AppViewManager get() {
        return instance;
    }

    private AppViewManager(Stage stage) {
        this.stage = stage;

        StackPane canvasHolder = new StackPane(canvas);
        canvasHolder.setAlignment(Pos.CENTER);
        canvasHolder.setStyle("-fx-background-color: black;");

        overlay.setPickOnBounds(false);
        overlay.setAlignment(Pos.CENTER);

        root.getChildren().addAll(canvasHolder, overlay);
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
        dialog.initOwner(AppViewManager.get().getStage());
        dialog.initModality(Modality.APPLICATION_MODAL);

        ButtonType confirmButtonType = new ButtonType("Confirm");
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType);

        int musicVolume = SoundManager.getMusicVolume();
        int sfxVolume = SoundManager.getSfxVolume();
        int[] fps = {GameObject.getFPS()}; // used array to allow modification in lambda, because reference as pointer.

        Label musicVolumeLabel = new Label("Music Volume (" + musicVolume + "): ");
        Label sfxVolumeLabel = new Label("SFX Volume (" + sfxVolume + "): ");
        Label fpsLabel = new Label("FPS (" + fps[0] + "): ");

        Slider musicVolumeSlider = new Slider(0, 100, musicVolume);
        musicVolumeSlider.setPrefWidth(300);
        musicVolumeSlider.setShowTickLabels(true);
        musicVolumeSlider.setShowTickMarks(true);
        musicVolumeSlider.setMajorTickUnit(25);
        musicVolumeSlider.setBlockIncrement(1);
        musicVolumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            logger.info("Music Volume: " + newVal.intValue());
            musicVolumeLabel.setText("Music Volume (" + newVal.intValue() + "): ");
            SoundManager.setMusicVolume(newVal.intValue());
        });

        long[] lastTimeSFXPlayed = {0}; // used array to allow modification in lambda, because reference as pointer.

        Slider sfxVolumeSlider = new Slider(0, 100, sfxVolume);
        sfxVolumeSlider.setPrefWidth(300);
        sfxVolumeSlider.setShowTickLabels(true);
        sfxVolumeSlider.setShowTickMarks(true);
        sfxVolumeSlider.setMajorTickUnit(25);
        sfxVolumeSlider.setBlockIncrement(1);
        sfxVolumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            logger.info("SFX Volume: " + newVal.intValue());
            sfxVolumeLabel.setText("SFX Volume (" + newVal.intValue() + "): ");
            SoundManager.setSfxVolume(newVal.intValue());
            // avoid playing sample sfx too fast.
            if (System.currentTimeMillis() - lastTimeSFXPlayed[0] > 200) {
                lastTimeSFXPlayed[0] = System.currentTimeMillis();
                SoundManager.playSound(SoundListEnum.ENEMY_DOWN);
            }
        });

        Slider fpsSlider = new Slider(0, 240, GameObject.getFPS());
        fpsSlider.setPrefWidth(300);
        fpsSlider.setShowTickLabels(true);
        fpsSlider.setShowTickMarks(true);
        fpsSlider.setMajorTickUnit(60);
        fpsSlider.setBlockIncrement(5);

        CheckBox fullscreenCheckBox = new CheckBox("Enable Fullscreen");
        fullscreenCheckBox.setSelected(AppViewManager.get().getStage().isFullScreen());
        fullscreenCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            logger.info("Fullscreen: " + newVal);
            AppViewManager.get().getStage().setFullScreen(newVal);
        });

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(30, 50, 30, 50));
        grid.setAlignment(Pos.CENTER); // center the grid

        grid.add(musicVolumeLabel, 0, 0);
        grid.add(musicVolumeSlider, 1, 0);

        grid.add(sfxVolumeLabel, 0, 1);
        grid.add(sfxVolumeSlider, 1, 1);

        grid.add(fpsLabel, 0, 2);
        grid.add(fpsSlider, 1, 2);

        Label restartWarning = new Label("Game NEEDS to RESTART to apply FPS changes.");
        restartWarning.setTextFill(Color.WHITE);

        grid.add(restartWarning, 0, 3);
        grid.add(fullscreenCheckBox, 1, 4);

        fpsSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            logger.info("FPS: " + Math.max(newVal.intValue(), 1));
            fpsLabel.setText("FPS (" + Math.max(newVal.intValue(), 1) + "): ");
            if (newVal.intValue() != fps[0]) {
                restartWarning.setTextFill(Color.RED);
            } else {
                restartWarning.setTextFill(Color.WHITE);
            }
        });

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setMinWidth(700);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == confirmButtonType) {
            musicVolume = (int) musicVolumeSlider.getValue();
            sfxVolume = (int) sfxVolumeSlider.getValue();
            boolean hasFpsChanged = fps[0] != (int) fpsSlider.getValue();
            fps[0] = (int) fpsSlider.getValue();
            boolean isFullscreen = fullscreenCheckBox.isSelected();
            String path = profilesPath + "/" + ProfileManager.getCurrentProfile() + "/_SETTINGS.json";
            if (!JsonFileManager.writeSettingsToJson(path, musicVolume, sfxVolume, Math.max(fps[0], 1),
                    isFullscreen)){
                logger.severe("Failed to write settings to file " + path);
            }
            if (hasFpsChanged) {
                GameObject.setFPS(Math.max(fps[0], 1));
                logger.info("Game is going to close, because FPS changed.");
                System.exit(0);
            }
        }
    }

    public static boolean initSettings(String profilesPath) {
        String profile = ProfileManager.getCurrentProfile();
        if (profile == null || profile.isBlank()) {
            logger.info("No profile selected, using default settings.");
            return true;
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
            return true;
        } else {
            logger.info("Settings not found, using default values.");
            return false;
        }
    }

    protected int[] getOverlaySize() {
        return new int[]{(int) overlay.getWidth(), (int) overlay.getHeight()};
    }

    private ChangeListener<Number> onResize() {
        return (obs, oldVal, newVal) -> updateCanvasSize();
    }

    protected int[] getSceneSize() {
        return new int[]{(int) scene.getWidth(), (int) scene.getHeight()};
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

        if (currentFrame instanceof IResizableFrame frame) {
            frame.onResizeCanvas(w, h);
        }

        draw();
    }

    public void update() {
        this.updateCanvasSize();
    }

    public void setFullScreen(boolean fullScreen) {
        stage.setFullScreen(fullScreen);
    }

    public boolean isFullScreen() {
        return stage.isFullScreen();
    }

    private void draw() {
        var gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        if (currentFrame instanceof IDrawableFrame frame) {
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

    public Scene getScene() {
        return scene;
    }

    public Stage getStage() {
        return stage;
    }

    public void setClickListener(IClickListener listener) {
        this.clickListener = listener;
    }

}
