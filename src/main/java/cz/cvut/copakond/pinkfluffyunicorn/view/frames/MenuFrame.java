package cz.cvut.copakond.pinkfluffyunicorn.view.frames;

import cz.cvut.copakond.pinkfluffyunicorn.Launcher;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.ErrorMsgsEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.SoundListEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.files.SoundManager;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.game.GameObject;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.game.ProfileManager;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.json.JsonFileManager;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.levels.LevelStatusUtils;
import cz.cvut.copakond.pinkfluffyunicorn.model.world.Level;
import cz.cvut.copakond.pinkfluffyunicorn.view.utils.AppViewManager;
import cz.cvut.copakond.pinkfluffyunicorn.view.interfaces.IDrawableFrame;
import cz.cvut.copakond.pinkfluffyunicorn.view.interfaces.IResizableFrame;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class MenuFrame extends VBox implements IResizableFrame, IDrawableFrame {
    private static final Logger logger = Logger.getLogger(MenuFrame.class.getName());

    private final Label logo = new Label("PINK FLUFFY UNICORN");
    private final Label creator = new Label("Created by: Ondřej Čopák, ProfiPoint 2025");
    private final Button playButton = new Button("PLAY");
    private final Button continueButton = new Button("CONTINUE");
    private final Button editorButton = new Button("LEVEL EDITOR");
    private final Button profileButton = new Button("SWITCH PROFILE");
    private final Button settingsButton = new Button("SETTINGS");
    private final Button exitButton = new Button("EXIT");

    public MenuFrame() {
        setAlignment(Pos.CENTER);
        setSpacing(20);

        int[] continueLevel = LevelStatusUtils.getNextUncompletedLevel();
        logger.info("Continue level: " + continueLevel[0] + " " + continueLevel[1]);
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
            logger.info(" Level continued" + continueLevel[0] + " clicked");
            Integer levelNum = continueLevel[0];
            Level level = new Level(Integer.toString(levelNum), false, continueLevel[1] == 0);
            if (!level.loadLevel()) {
                ErrorMsgsEnum.LOAD_ERROR.getValue();
                return;
            }
            AppViewManager.get().switchTo(new LevelFrame(level, false));
        });

        editorButton.setOnAction(e -> AppViewManager.get().switchTo(new LevelSelectionFrame(true)));
        profileButton.setOnAction(e -> AppViewManager.get().switchTo(new ProfileFrame()));
        exitButton.setOnAction(e -> System.exit(0));

        settingsButton.setOnAction(event -> {
            AppViewManager.get().openSettings();
        });

        logo.setTextFill(Color.HOTPINK);
        creator.setTextFill(Color.DARKORANGE);
        getChildren().addAll(logo, playButton, continueButton, editorButton, profileButton, settingsButton,
                exitButton, creator);

        SoundManager.playSound(SoundListEnum.MENU_THEME);
    }

    @Override
    public void onResizeCanvas(double width, double height) {
        double fontSize = height / 25;
        double spacing = height / 30;

        logo.setFont(Font.font("Arial", fontSize * 1.5));
        creator.setFont(Font.font("Arial", fontSize * .5));

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
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
    }
}
