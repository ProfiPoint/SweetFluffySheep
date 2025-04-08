package cz.cvut.copakond.pinkfluffyunicorn.view.scenes;

import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MenuScene extends ResponsiveScene {

    private final Label logo = new Label("PINK FLUFFY UNICORN");
    private final Label creator = new Label("Created by: Ondřej Čopák, ProfiPoint 2025");
    private final Button playButton = new Button("PLAY");
    private final Button continueButton = new Button("CONTINUE");
    private final Button editorButton = new Button("LEVEL EDITOR");
    private final Button profileButton = new Button("SWITCH PROFILE");
    private final Button exitButton = new Button("EXIT");

    private final VBox menuLayout = new VBox(20); // spacing will be resized

    public MenuScene(Stage stage) {
        super(stage);

        playButton.setOnAction(e -> new LevelSelectionScene(stage).show());
        continueButton.setOnAction(e -> System.out.println("Continue clicked"));
        editorButton.setOnAction(e -> System.out.println("Editor clicked"));
        profileButton.setOnAction(e -> new ProfileScene(stage).show());
        exitButton.setOnAction(e -> stage.close());

        logo.setTextFill(Color.HOTPINK);
        creator.setTextFill(Color.DARKORANGE);
        menuLayout.getChildren().addAll(logo, playButton, continueButton, editorButton, profileButton, exitButton, creator);
        menuLayout.setAlignment(Pos.CENTER);

        overlay.getChildren().add(menuLayout);
    }

    @Override
    protected void onResizeCanvas(double width, double height) {
        double fontSize = height / 25;
        double spacing = height / 30;

        logo.setFont(Font.font("Arial", fontSize * 1.5));
        creator.setFont(Font.font("Arial", fontSize * .5));
        playButton.setStyle("-fx-font-size: " + fontSize + "px;");
        continueButton.setStyle("-fx-font-size: " + fontSize + "px;");
        editorButton.setStyle("-fx-font-size: " + fontSize + "px;");
        profileButton.setStyle("-fx-font-size: " + fontSize + "px;");
        exitButton.setStyle("-fx-font-size: " + fontSize + "px;");

        menuLayout.setSpacing(spacing);
    }

    @Override
    protected void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Optional: draw a faded background logo or design
    }
}
