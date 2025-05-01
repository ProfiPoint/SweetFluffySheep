module cz.cvut.copakond.sweetfluffysheep {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;
    requires jdk.compiler;
    requires java.desktop;
    requires javafx.media;
    requires java.logging;

    opens cz.cvut.copakond.sweetfluffysheep to javafx.fxml;
    exports cz.cvut.copakond.sweetfluffysheep;
    exports cz.cvut.copakond.sweetfluffysheep.model.utils.files;
    exports cz.cvut.copakond.sweetfluffysheep.model.utils.logging;
    exports cz.cvut.copakond.sweetfluffysheep.model.utils.enums;
}