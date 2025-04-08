package cz.cvut.copakond.pinkfluffyunicorn.view.scenes;

import cz.cvut.copakond.pinkfluffyunicorn.model.data.FileUtils;
import cz.cvut.copakond.pinkfluffyunicorn.model.data.FolderUtils;
import cz.cvut.copakond.pinkfluffyunicorn.model.data.JsonFileManager;
import cz.cvut.copakond.pinkfluffyunicorn.model.profile.ProfileManager;
import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.List;

public class ProfileScene extends ResponsiveScene {

    private final VBox layout = new VBox();
    private final Label titleLabel = new Label("Select Profile");
    private final ScrollPane scrollPane = new ScrollPane();
    private final VBox profileListBox = new VBox();
    private final TextField nameField = new TextField();
    private final Button confirmButton = new Button("Confirm");
    private final Button backButton = new Button("Back");

    private List<String> profiles = List.of();

    public ProfileScene(Stage stage) {
        super(stage);

        layout.setAlignment(Pos.TOP_CENTER);
        layout.setSpacing(20);
        layout.setFillWidth(true);

        // Title
        titleLabel.setTextFill(Color.WHITE);
        nameField.setAlignment(Pos.CENTER);
        layout.getChildren().add(titleLabel);

        // Scroll area
        scrollPane.setContent(profileListBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: black; -fx-background-color: transparent;");
        layout.getChildren().add(scrollPane);

        // Text input for new profile
        nameField.setPromptText("Enter new profile name...");
        layout.getChildren().add(nameField);


        // Confirm button
        confirmButton.setOnAction(e -> {
            String newName = nameField.getText();
            if (!newName.isBlank()) {
                System.out.println("Confirmed new profile: " + newName);
                if (!ProfileManager.addNewProfile(newName)){return;};
                addNewProfile(newName);
                drawProfileButtons();
                super.updateCanvasSize();
            }
        });
        layout.getChildren().add(confirmButton);
        layout.getChildren().add(backButton);

        backButton.setOnAction(e -> {
            System.out.println("Back to menu");
            new MenuScene(stage).show();
        });



        overlay.getChildren().add(layout);

        drawProfileButtons();
    }

    private void addNewProfile(String name) {
        if (!profiles.contains(name)) {
            profiles.add(name);
            Button profileBtn = new Button(name);
            profileBtn.setMaxWidth(Double.MAX_VALUE);
            profileListBox.getChildren().add(profileBtn);
        }
    }

    private void drawProfileButtons() {
        profileListBox.getChildren().clear();
        profileListBox.setSpacing(10);
        profileListBox.setAlignment(Pos.TOP_CENTER);

        for (String profile : profiles) {
            String text = "[" + profile + "]";
            List<List<Integer>> completedLevels =
                    JsonFileManager.getProfileLFromJsonFile(FolderUtils.getProfilesPath() + "/" + profile + "/_DATA" +
                            ".json");
            int totalStoryLevels = FileUtils.getNumberOfFilesInDirectory(FolderUtils.getLevelsPath());
            int totalCustomLevels = FileUtils.getNumberOfFilesInDirectory(FolderUtils.getProfilesPath() + "/" + profile);
            if (completedLevels != null && !completedLevels.isEmpty() && totalCustomLevels >= 0 && totalStoryLevels >= 0) {
                if (totalCustomLevels > 0){
                    text += " | (Story: " + completedLevels.get(0).size() + "/" + totalStoryLevels + " Custom: " + totalCustomLevels + ")";
                } else {
                    text += " | (Story: " + completedLevels.get(0).size() + "/" + totalStoryLevels + ")";
                }
            }

            Button profileBtn = new Button(text);
            if (ProfileManager.getCurrentProfile().equals(profile)) {
                profileBtn.setStyle("-fx-background-color: green; -fx-text-fill: white;");
            }

            profileBtn.setOnAction(e -> {
                System.out.println("Selected profile: " + profile);
                ProfileManager.switchProfile(profile);
                drawProfileButtons();
                super.updateCanvasSize();
            });

            profileBtn.setMaxWidth(Double.MAX_VALUE);
            profileListBox.getChildren().add(profileBtn);
        }
    }

    @Override
    protected void onResizeCanvas(double width, double height) {
        double fontSize = height / 25;
        double fieldWidth = width * 0.4;
        double fieldHeight = height / 20;

        titleLabel.setFont(Font.font(fontSize * 1.2));
        scrollPane.setPrefViewportHeight(height * 0.4);
        scrollPane.setMaxWidth(fieldWidth);

        nameField.setStyle("-fx-alignment: center; -fx-font-size: " + (fontSize * 0.8) + "px;");
        nameField.setAlignment(Pos.CENTER);
        nameField.setPrefWidth(fieldWidth);
        nameField.setMaxWidth(fieldWidth);
        nameField.setPrefHeight(fieldHeight);

        confirmButton.setPrefWidth(fieldWidth);
        confirmButton.setPrefHeight(fieldHeight);
        confirmButton.setStyle("-fx-font-size: " + (fontSize * 0.8) + "px;");

        backButton.setPrefWidth(fieldWidth/2);
        backButton.setPrefHeight(fieldHeight/2);
        backButton.setStyle("-fx-font-size: " + (fontSize * 0.8) + "px;");
        backButton.setAlignment(Pos.CENTER);
        backButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-size: " + (fontSize * 0.8) + "px;");

        layout.setSpacing(height / 30);

        // 🔥 Resize profile buttons based on scroll pane width
        double buttonWidth = fieldWidth * 0.95;
        double buttonHeight = fieldHeight * 0.9;

        for (javafx.scene.Node node : profileListBox.getChildren()) {
            if (node instanceof Button btn) {
                btn.setPrefWidth(buttonWidth);
                btn.setPrefHeight(buttonHeight);
                String currentStyle = btn.getStyle(); // keep existing styles
                String fontSizeStyle = "-fx-font-size: " + (fontSize * 0.8) + "px;";
                btn.setStyle(currentStyle + fontSizeStyle);
            }
        }
    }

    @Override
    protected void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    @Override
    public void show() {
        profiles = FolderUtils.getAllFolders("src/main/resources/datasaves/profiles");
        drawProfileButtons();
        super.show();
    }

}
