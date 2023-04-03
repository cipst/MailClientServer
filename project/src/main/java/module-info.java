module com.project {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.google.gson;

    opens com.project.client.controller to javafx.fxml;
    opens com.project.models to com.google.gson;

    exports com.project.server;
    exports com.project.server.controller;
//    exports com.project.server.model;
    exports com.project.client;
    exports com.project.client.controller;
    exports com.project.models;
}