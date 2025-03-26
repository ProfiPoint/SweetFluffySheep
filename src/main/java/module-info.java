module cz.cvut.copakond.pfu.pinkfluffyunicorn {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;


    opens cz.cvut.copakond.pinkfluffyunicorn to javafx.fxml;
    exports cz.cvut.copakond.pinkfluffyunicorn;
    //opens cz.cvut.copakond.pinkfluffyunicorn to javafx.fxml;
}