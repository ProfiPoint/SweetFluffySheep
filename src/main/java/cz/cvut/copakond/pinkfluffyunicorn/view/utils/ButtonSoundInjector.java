package cz.cvut.copakond.pinkfluffyunicorn.view.utils;

import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.SoundListEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.files.SoundManager;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;

public class ButtonSoundInjector {
    public static void injectToAllButtons(Parent root) {
        for (Node node : root.lookupAll(".button")) {
            if (node instanceof Button button) {
                attachSound(button);
            }
        }
    }

    private static void attachSound(Button button) {
        // avoid multiple handlers if already injected
        button.addEventHandler(javafx.event.ActionEvent.ACTION, e -> {
            SoundManager.playSound(SoundListEnum.MOUSE_CLICK);
        });
    }
}