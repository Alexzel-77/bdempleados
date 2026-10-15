module ni.edu.ni.bdempleados {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens ni.edu.ni.bdempleados to javafx.fxml;


    opens ni.edu.ni.bdempleados.controller to javafx.fxml;


    opens ni.edu.ni.bdempleados.model to javafx.base;

    exports ni.edu.ni.bdempleados;
}