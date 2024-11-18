module com.virus_warfare {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires com.google.gson;

    opens com.virus_warfare.client.models to com.google.gson;
    opens com.virus_warfare.server.models to com.google.gson;
    exports com.virus_warfare.shared;

    opens com.virus_warfare.client.controllers to javafx.fxml;
    exports com.virus_warfare;
}