package com.project.server.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ServerModel {
    private ObservableList<String> logList;

    public ServerModel() {
        logList = FXCollections.observableArrayList();
    }

    public ObservableList<String> getLogList() {
        return logList;
    }

    public void addLog(String log) {
        logList.add(0, log);
    }
}

