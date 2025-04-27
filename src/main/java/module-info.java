module cz.cvut.copakond.pfu.pinkfluffyunicorn {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;
    requires jdk.compiler;
    requires java.desktop;
    requires javafx.media;
    requires java.logging;

    opens cz.cvut.copakond.pinkfluffyunicorn to javafx.fxml;
    exports cz.cvut.copakond.pinkfluffyunicorn;
    exports cz.cvut.copakond.pinkfluffyunicorn.model.utils.files;
}