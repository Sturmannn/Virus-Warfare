module com.virus_warfare {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires com.google.gson;
    requires java.jws;
    requires java.xml.ws;
    requires jdk.httpserver;

    requires java.sql;
    requires com.sun.xml.ws;
    requires org.eclipse.persistence.core;
    requires org.eclipse.persistence.moxy;

    opens com.virus_warfare.sockets.client.models to com.google.gson;
    opens com.virus_warfare.sockets.server.models to com.google.gson;

    opens com.virus_warfare.sockets.client.controllers to javafx.fxml;
    opens com.virus_warfare.soap.client.controllers to javafx.fxml;
    opens com.virus_warfare.soap.server to org.eclipse.persistence.moxy, org.eclipse.persistence.core;
    opens com.virus_warfare.soap.shared to org.eclipse.persistence.moxy, org.eclipse.persistence.core;

    exports com.virus_warfare.soap;
    exports com.virus_warfare.soap.server;
    exports com.virus_warfare.soap.shared;
    exports com.virus_warfare.sockets;
    exports com.virus_warfare.sockets.shared;
    exports com.virus_warfare.sockets.server.models;
    exports com.virus_warfare.sockets.client.models;

    exports com.virus_warfare.soap.client.controllers to javafx.fxml;
    exports com.virus_warfare.sockets.client.controllers to javafx.fxml;
}