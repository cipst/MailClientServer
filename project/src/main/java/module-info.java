module com.project {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires javafx.graphics;
    requires com.google.gson;

    opens com.project to javafx.fxml;
    opens com.project.models to com.google.gson;

    exports com.project;
    exports com.project.server;
    exports com.project.server.controller;
    exports com.project.server.model;
    exports com.project.client;
}