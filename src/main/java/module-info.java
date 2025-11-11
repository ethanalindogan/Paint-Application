module ca.utoronto.utm.paint {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;


    opens ca.utoronto.utm.paint to javafx.fxml;
    exports ca.utoronto.utm.paint;
}