package cz.cvut.copakond.pinkfluffyunicorn.view.frames;

import cz.cvut.copakond.pinkfluffyunicorn.model.utils.files.FileUtils;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.files.FolderUtils;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.json.JsonFileManager;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.game.ProfileManager;
import cz.cvut.copakond.pinkfluffyunicorn.view.utils.AppViewManager;
import cz.cvut.copakond.pinkfluffyunicorn.view.interfaces.IDrawableFrame;
import cz.cvut.copakond.pinkfluffyunicorn.view.interfaces.IResizableFrame;
import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.List;

public class ProfileFrame extends VBox implements IResizableFrame, IDrawableFrame {
    private final Label titleLabel = new Label("Select Profile");
    private final ScrollPane scrollPane = new ScrollPane();
    private final VBox profileListBox = new VBox();
    private final TextField nameField = new TextField();
    private final Button confirmButton = new Button("Add New Profile");
    private final Button backButton = new Button("Back");

    private List<String> profiles = List.of();

    public ProfileFrame() {
        setAlignment(Pos.TOP_CENTER);
        setSpacing(20);
        setFillWidth(true);

        titleLabel.setTextFill(Color.WHITE);
        nameField.setAlignment(Pos.CENTER);
        getChildren().add(titleLabel);

        scrollPane.setContent(profileListBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: black; -fx-background-color: transparent;");
        getChildren().add(scrollPane);

        nameField.setPromptText("Enter new profile name...");
        getChildren().add(nameField);

        confirmButton.setOnAction(e -> {
            String newName = nameField.getText();
            if (!newName.isBlank()) {
                System.out.println("Confirmed new profile: " + newName);
                if (!ProfileManager.addNewProfile(newName)) {
                    return;
                }
                addNewProfile(newName);
                drawProfileButtons();
                AppViewManager.get().update();
            }
        });
        getChildren().add(confirmButton);
        getChildren().add(backButton);

        backButton.setOnAction(e -> {
            System.out.println("Back to menu");
            AppViewManager.get().switchTo(new MenuFrame()); // Switch back to menu
        });

        drawProfileButtons();
        show(); // Initial draw
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
                    JsonFileManager.getProfileLFromJsonFile(FolderUtils.getProfilesPath() + "/" + profile + "/_DATA.json");
            int totalStoryLevels = FileUtils.getNumberOfFilesInDirectory(FolderUtils.getLevelsPath());
            int totalCustomLevels = FileUtils.getNumberOfFilesInDirectory(FolderUtils.getProfilesPath() + "/" + profile);

            if (completedLevels != null && !completedLevels.isEmpty() && totalCustomLevels >= 0 && totalStoryLevels >= 0) {
                if (totalCustomLevels > 0) {
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
                AppViewManager.get().update();
            });

            profileBtn.setMaxWidth(Double.MAX_VALUE);
            profileListBox.getChildren().add(profileBtn);
        }
    }

    @Override
    public void onResizeCanvas(double width, double height) {
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

        backButton.setPrefWidth(fieldWidth / 2);
        backButton.setPrefHeight(fieldHeight / 2);
        backButton.setStyle("-fx-font-size: " + (fontSize * 0.8) + "px;");
        backButton.setAlignment(Pos.CENTER);
        backButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-size: " + (fontSize * 0.8) + "px;");

        setSpacing(height / 30);

        double buttonWidth = fieldWidth * 0.95;
        double buttonHeight = fieldHeight * 0.9;

        for (javafx.scene.Node node : profileListBox.getChildren()) {
            if (node instanceof Button btn) {
                btn.setPrefWidth(buttonWidth);
                btn.setPrefHeight(buttonHeight);
                String currentStyle = btn.getStyle(); // Keep existing styles
                String fontSizeStyle = "-fx-font-size: " + (fontSize * 0.8) + "px;";
                btn.setStyle(currentStyle + fontSizeStyle);
            }
        }
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
    }

    public void show() {
        profiles = FolderUtils.getAllFolders(ProfileManager.getProfileFolderPath());
        drawProfileButtons();
        AppViewManager.get().update();
    }
}
