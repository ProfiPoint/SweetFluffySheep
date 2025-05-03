package cz.cvut.copakond.sweetfluffysheep.view.frames;

import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.ErrorMsgsEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.SoundListEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.files.SoundManager;
import cz.cvut.copakond.sweetfluffysheep.model.utils.game.ProfileManager;
import cz.cvut.copakond.sweetfluffysheep.model.utils.levels.LevelStatusUtils;
import cz.cvut.copakond.sweetfluffysheep.model.world.Level;
import cz.cvut.copakond.sweetfluffysheep.view.utils.AppViewManager;
import cz.cvut.copakond.sweetfluffysheep.view.interfaces.IInteractableFrame;
import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.layout.VBox;

import java.util.logging.Logger;

/**
 * MenuFrame class represents the main menu of the game.
 * It provides options to play, continue, edit levels, switch profiles, access settings, and exit the game.
 */
public class MenuFrame extends VBox implements IInteractableFrame {
    private static final Logger logger = Logger.getLogger(MenuFrame.class.getName());

    private final Label logo = new Label("SWEET FLUFFY SHEEP");
    private final Label profileName = new Label("Welcome back, " + ProfileManager.getCurrentProfile());
    private final Label creator = new Label("Created by: Ondřej Čopák, ProfiPoint 2025, Based of 'Ovečky' Špidla Data Processing 2009");

    private final Button playButton = new Button("PLAY");
    private final Button continueButton = new Button("CONTINUE");
    private final Button editorButton = new Button("LEVEL EDITOR");
    private final Button profileButton = new Button("SWITCH PROFILE");
    private final Button settingsButton = new Button("SETTINGS");
    private final Button exitButton = new Button("EXIT");

    /**
     * Constructor for MenuFrame.
     * Initializes the menu layout, buttons, and their actions.
     */
    public MenuFrame() {
        setAlignment(Pos.CENTER);
        setSpacing(20);

        int[] continueLevel = LevelStatusUtils.getNextUncompletedLevel();
        logger.info("Continue level: " + continueLevel[0] + " " + continueLevel[1]);

        // Set up the titles and profile name
        if (continueLevel[0] == 1 && continueLevel[1] == 0) {
            continueButton.setDisable(true);
        } else {
            if (continueLevel[1] == 0) {
                continueButton.setText("CONTINUE (Level " + continueLevel[0] + ")");
            } else {
                continueButton.setText("CONTINUE (Custom Level " + continueLevel[0] + ")");
            }
        }

        playButton.setOnAction(e -> AppViewManager.get().switchTo(new LevelSelectionFrame(false)));

        continueButton.setOnAction(e -> {
            logger.info("Level continued " + continueLevel[0]);
            Level level = new Level(Integer.toString(continueLevel[0]), false, continueLevel[1] == 0);
            if (!level.loadLevel()) {
                logger.warning(ErrorMsgsEnum.LOAD_ERROR.getValue());
                return;
            }
            AppViewManager.get().switchTo(new LevelFrame(level, false));
        });

        editorButton.setOnAction(e -> AppViewManager.get().switchTo(new LevelSelectionFrame(true)));
        profileButton.setOnAction(e -> AppViewManager.get().switchTo(new ProfileFrame()));
        settingsButton.setOnAction(e -> AppViewManager.get().openSettings());
        exitButton.setOnAction(e -> {
            logger.info("Exited game");
            System.exit(0);
        });

        logo.setTextFill(Color.WHITE);
        profileName.setTextFill(Color.BLACK);
        creator.setTextFill(Color.DARKORANGE);

        getChildren().addAll(
                logo, profileName, playButton, continueButton,
                editorButton, profileButton, settingsButton,
                exitButton, creator
        );

        SoundManager.playSound(SoundListEnum.MENU_THEME);
    }

    @Override
    public void onResizeCanvas(double width, double height) {
        double fontSize = height / 25;
        double spacing = height / 30;

        logo.setStyle("-fx-font-size: " + (fontSize * 1.7) + "px; -fx-font-weight: bold;");
        profileName.setStyle("-fx-font-size: " + (fontSize * 0.8) + "px; -fx-font-weight: bold;");
        creator.setStyle("-fx-font-size: " + (fontSize * 0.5) + "px; -fx-font-weight: bold;");

        playButton.setStyle("-fx-font-size: " + fontSize + "px;");
        continueButton.setStyle("-fx-font-size: " + fontSize + "px;");
        editorButton.setStyle("-fx-font-size: " + fontSize + "px;");
        profileButton.setStyle("-fx-font-size: " + fontSize + "px;");
        settingsButton.setStyle("-fx-font-size: " + fontSize + "px;");
        exitButton.setStyle("-fx-font-size: " + fontSize + "px;");

        setSpacing(spacing);
    }

    @Override
    public void draw(GraphicsContext gc) {
        AppViewManager.playBackgroundVideo();
        AppViewManager.get().resizeBackgroundVideo();
    }
}
