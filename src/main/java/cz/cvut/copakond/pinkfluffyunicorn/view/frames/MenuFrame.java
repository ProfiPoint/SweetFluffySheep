package cz.cvut.copakond.pinkfluffyunicorn.view.frames;

import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.SoundListEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.files.SoundManager;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.levels.LevelStatusUtils;
import cz.cvut.copakond.pinkfluffyunicorn.model.world.Level;
import cz.cvut.copakond.pinkfluffyunicorn.view.utils.AppViewManager;
import cz.cvut.copakond.pinkfluffyunicorn.view.interfaces.IDrawableFrame;
import cz.cvut.copakond.pinkfluffyunicorn.view.interfaces.IResizableFrame;
import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.layout.VBox;

public class MenuFrame extends VBox implements IResizableFrame, IDrawableFrame {
    private final Label logo = new Label("PINK FLUFFY UNICORN");
    private final Label creator = new Label("Created by: Ondřej Čopák, ProfiPoint 2025");
    private final Button playButton = new Button("PLAY");
    private final Button continueButton = new Button("CONTINUE");
    private final Button editorButton = new Button("LEVEL EDITOR");
    private final Button profileButton = new Button("SWITCH PROFILE");
    private final Button exitButton = new Button("EXIT");

    public MenuFrame() {
        setAlignment(Pos.CENTER);
        setSpacing(20);

        int[] continueLevel = LevelStatusUtils.getNextUncompletedLevel();
        System.out.println("Continue level: " + continueLevel[0] + " " + continueLevel[1]);
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
            System.out.println(" Level continued" + continueLevel[0] + " clicked");
            Integer levelNum = continueLevel[0];
            Level level = new Level(Integer.toString(levelNum), false, continueLevel[1] == 0);
            if (!level.loadLevel()) {
                System.out.println("Level not loaded successfully");
                return;
            }
            AppViewManager.get().switchTo(new LevelFrame(level, false));
        });



        editorButton.setOnAction(e -> AppViewManager.get().switchTo(new LevelSelectionFrame(true)));
        profileButton.setOnAction(e -> AppViewManager.get().switchTo(new ProfileFrame()));
        exitButton.setOnAction(e -> System.exit(0));

        logo.setTextFill(Color.HOTPINK);
        creator.setTextFill(Color.DARKORANGE);
        getChildren().addAll(logo, playButton, continueButton, editorButton, profileButton, exitButton, creator);

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
        exitButton.setStyle("-fx-font-size: " + fontSize + "px;");

        setSpacing(spacing);
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
    }
}
