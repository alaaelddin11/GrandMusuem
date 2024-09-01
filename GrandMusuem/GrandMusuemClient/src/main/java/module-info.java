module com.example.grandmusuemclient {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires javafx.media;
    requires java.sql;
    requires jBCrypt;

    opens com.example.grandmusuemclient to javafx.fxml;
    exports com.example.grandmusuemclient;
}