package cz.cvut.copakond.pinkfluffyunicorn;

import cz.cvut.copakond.pinkfluffyunicorn.model.data.FileUtils;
import cz.cvut.copakond.pinkfluffyunicorn.model.data.InitClasses;
import cz.cvut.copakond.pinkfluffyunicorn.model.profile.ProfileManager;
import cz.cvut.copakond.pinkfluffyunicorn.view.scenes.MenuScene;
import cz.cvut.copakond.pinkfluffyunicorn.view.scenes.ProfileScene;
import javafx.application.Application;
import javafx.stage.Stage;

/*
public class Launcher  {
    public static void main(String[] args) {
        //launch();
        Level level = new Level("test_level", false);
        if (!level.loadLevel()) {
            System.err.println("Error loading level data - main launcher");
            return;
        }
        System.out.println("Level loaded successfully from MAIN LAUNCHER:D");
        if (!level.saveLevel("test_level2")) {
            System.out.println("Error saving level data - main launcher");
        }
        System.out.println("Level saved successfully from MAIN LAUNCHER:D");
    }
}*/

public class Launcher extends Application {

    @Override
    public void start(Stage primaryStage) {
        InitClasses initClasses = new InitClasses(
            "src/main/resources/datasaves/levels",
            "src/main/resources/datasaves/profiles"
        );

        String currentProfile = FileUtils.readFile("src/main/resources/datasaves/profiles/_CURRENT.txt");
        MenuScene menu = new MenuScene(primaryStage);
        ProfileScene profileScene = new ProfileScene(primaryStage);
        if (currentProfile.length() == 0) {
            profileScene.show();
        } else {
            ProfileManager.switchProfile(currentProfile);
            menu.show();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

