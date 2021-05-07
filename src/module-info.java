module PAPart3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    opens sample.Controller to javafx.fxml;
    opens sample.View;
    opens sample;
}