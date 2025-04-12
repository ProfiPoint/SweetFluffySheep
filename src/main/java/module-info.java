module cz.cvut.copakond.pfu.pinkfluffyunicorn {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;
    requires org.reflections;
    requires jdk.compiler;


    opens cz.cvut.copakond.pinkfluffyunicorn to javafx.fxml;
    exports cz.cvut.copakond.pinkfluffyunicorn;
    //opens cz.cvut.copakond.pinkfluffyunicorn to javafx.fxml;
}