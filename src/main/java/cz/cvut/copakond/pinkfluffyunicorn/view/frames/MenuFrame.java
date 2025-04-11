package cz.cvut.copakond.pinkfluffyunicorn.view.frames;

import cz.cvut.copakond.pinkfluffyunicorn.view.scenebuilder.AppViewManager;
import cz.cvut.copakond.pinkfluffyunicorn.view.scenebuilder.DrawableFrame;
import cz.cvut.copakond.pinkfluffyunicorn.view.scenebuilder.ResizableFrame;
import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.layout.VBox;

public class MenuFrame extends VBox implements ResizableFrame, DrawableFrame {
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

        playButton.setOnAction(e -> AppViewManager.get().switchTo(new LevelSelectionFrame(false)));
        continueButton.setOnAction(e -> System.out.println("Continue clicked"));
        editorButton.setOnAction(e -> AppViewManager.get().switchTo(new LevelSelectionFrame(true)));
        profileButton.setOnAction(e -> AppViewManager.get().switchTo(new ProfileFrame()));
        exitButton.setOnAction(e -> System.exit(0));

        logo.setTextFill(Color.HOTPINK);
        creator.setTextFill(Color.DARKORANGE);
        getChildren().addAll(logo, playButton, continueButton, editorButton, profileButton, exitButton, creator);

        //show(); // Initial draw
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
