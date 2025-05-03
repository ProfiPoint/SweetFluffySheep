package cz.cvut.copakond.sweetfluffysheep.view.frames;

import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.SoundListEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.files.FileUtils;
import cz.cvut.copakond.sweetfluffysheep.model.utils.files.FolderUtils;
import cz.cvut.copakond.sweetfluffysheep.model.utils.files.SoundManager;
import cz.cvut.copakond.sweetfluffysheep.model.utils.json.JsonFileManager;
import cz.cvut.copakond.sweetfluffysheep.model.utils.game.ProfileManager;
import cz.cvut.copakond.sweetfluffysheep.view.utils.AppViewManager;
import cz.cvut.copakond.sweetfluffysheep.view.interfaces.IInteractableFrame;
import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.List;
import java.util.logging.Logger;

/**
 * ProfileFrame is a JavaFX component that allows users to select and manage profiles.
 * It provides a user interface for creating new profiles, selecting existing ones,
 * and displaying profile information.
 */
public class ProfileFrame extends VBox implements IInteractableFrame {
    private static final Logger logger = Logger.getLogger(ProfileFrame.class.getName());

    private final Label titleLabel = new Label("Select Profile");
    private final ScrollPane scrollPane = new ScrollPane();
    private final VBox profileListBox = new VBox();
    private final TextField nameField = new TextField();
    private final Button confirmButton = new Button("Add New Profile");
    private final Button backButton = new Button("Back to Menu");

    private List<String> profiles;

    /**
     * Constructor for ProfileFrame.
     * Initializes the UI components and sets up event handlers.
     */
    public ProfileFrame() {
        setAlignment(Pos.TOP_CENTER);
        setSpacing(20);
        setFillWidth(true);

        titleLabel.setTextFill(Color.WHITE);
        getChildren().add(titleLabel);

        scrollPane.setContent(profileListBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        getChildren().add(scrollPane);

        nameField.setPromptText("Enter new profile name...");
        nameField.setAlignment(Pos.CENTER);
        getChildren().add(nameField);

        confirmButton.setOnAction(e -> {
            String newName = nameField.getText();
            // Check if the name is not empty, if so, do not add
            if (!newName.isBlank()) {
                logger.info("Confirmed new profile: " + newName);
                if (!ProfileManager.addNewProfile(newName)) {
                    return;
                }
                addNewProfile(newName);
                drawProfileButtons();
                AppViewManager.get().update();
            }
        });
        getChildren().add(confirmButton);

        backButton.setOnAction(e -> {
            // a profile must be selected to go back to a menu
            if (!ProfileManager.getCurrentProfile().isBlank()) {
                logger.info("Back to Menu");
                AppViewManager.get().switchTo(new MenuFrame());
            } else {
                logger.info("No profile selected, no back to menu");
            }
        });
        getChildren().add(backButton);

        profiles = FolderUtils.getAllFolders(ProfileManager.getProfileFolderPath());
        SoundManager.playSound(SoundListEnum.MENU_THEME);
        drawProfileButtons();
        show();
    }

    /**
     * Adds a new profile to the list of profiles.
     *
     * @param name The name of the new profile.
     */
    private void addNewProfile(String name) {
        if (!profiles.contains(name)) {
            profiles.add(name);
            Button profileBtn = new Button(name);
            profileBtn.setMaxWidth(Double.MAX_VALUE);
            profileListBox.getChildren().add(profileBtn);
            SoundManager.playSound(SoundListEnum.PROFILE_CREATED);
            backButton.setText("Back to Menu");
        }
    }

    /**
     * Draws the profile buttons based on the available profiles.
     */
    private void drawProfileButtons() {
        profileListBox.getChildren().clear();
        profileListBox.setSpacing(10);
        profileListBox.setAlignment(Pos.TOP_CENTER);


        if (ProfileManager.getCurrentProfile().isBlank()) {
            backButton.setText("Select Profile First");
        }

        // Add a button to create a new profile
        for (String profile : profiles) {
            String text = "[" + profile + "]";
            List<List<Integer>> completedLevels = JsonFileManager.getProfileLFromJsonFile(FolderUtils.getProfilesPath() + "/" + profile + "/_DATA.json");
            int totalStoryLevels = FileUtils.getNumberOfFilesInDirectory(FolderUtils.getLevelsPath());
            int totalCustomLevels = FileUtils.getNumberOfFilesInDirectory(FolderUtils.getProfilesPath() + "/" + profile);

            // Check if the profile has completed levels
            if (completedLevels != null && !completedLevels.isEmpty() && totalCustomLevels >= 0 && totalStoryLevels >= 0) {
                if (totalCustomLevels > 0) {
                    text += " | (Story: " + completedLevels.getFirst().size() + "/" + totalStoryLevels + " Custom: " + totalCustomLevels + ")";
                } else {
                    text += " | (Story: " + completedLevels.getFirst().size() + "/" + totalStoryLevels + ")";
                }
            }

            Button profileBtn = getButton(profile, text);
            profileListBox.getChildren().add(profileBtn);
        }
    }

    /**
     * Creates a button for the specified profile.
     *
     * @param profile The name of the profile.
     * @param text    The text to display on the button.
     * @return A Button object for the specified profile.
     */
    private Button getButton(String profile, String text) {
        Button profileBtn = new Button(text);
        if (ProfileManager.getCurrentProfile().equals(profile)) {
            profileBtn.setStyle("-fx-background-color: green; -fx-text-fill: white;");
        }

        profileBtn.setOnAction(e -> {
            logger.info("Selected profile: " + profile);
            ProfileManager.switchProfile(profile);
            drawProfileButtons();
            backButton.setText("Back to Menu");
            AppViewManager.get().update();
        });

        profileBtn.setMaxWidth(Double.MAX_VALUE);
        return profileBtn;
    }

    /**
     * Show the initial state of the ProfileFrame.
     */
    public void show() {
        profiles = FolderUtils.getAllFolders(ProfileManager.getProfileFolderPath());
        drawProfileButtons();
        AppViewManager.get().update();
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
        backButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-size: " + (fontSize * 0.8) + "px;");
        backButton.setAlignment(Pos.CENTER);

        setSpacing(height / 30);

        double buttonWidth = fieldWidth * 0.95;
        double buttonHeight = fieldHeight * 0.9;

        // Set the button size and font size for each profile button
        for (javafx.scene.Node node : profileListBox.getChildren()) {
            if (node instanceof Button btn) {
                btn.setPrefWidth(buttonWidth);
                btn.setPrefHeight(buttonHeight);
                String currentStyle = btn.getStyle();
                String fontSizeStyle = "-fx-font-size: " + (fontSize * 0.8) + "px;";
                btn.setStyle(currentStyle + fontSizeStyle);
            }
        }
    }

    @Override
    public void draw(GraphicsContext gc) {
        AppViewManager.playBackgroundVideo();
        AppViewManager.get().resizeBackgroundVideo();
    }
}