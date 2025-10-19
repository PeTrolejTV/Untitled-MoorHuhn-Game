module com.example.untitledmoorhuhngame {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires javafx.media;
    requires transitive javafx.graphics;

    opens com.example.untitledmoorhuhngame to javafx.fxml;
    exports com.example.untitledmoorhuhngame;
}
