module cz.cvut.copakond.pfu.pinkfluffyunicorn {
    requires javafx.controls;
    requires javafx.fxml;


    opens cz.cvut.copakond.pinkfluffyunicorn to javafx.fxml;
    exports cz.cvut.copakond.pinkfluffyunicorn;
}