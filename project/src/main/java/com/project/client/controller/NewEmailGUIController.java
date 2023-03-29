package com.project.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.web.HTMLEditor;

public class NewEmailGUIController {

    @FXML
    public Label lblFrom;
    @FXML
    public TextField toField;
    @FXML
    public TextField subjectField;
    @FXML
    public HTMLEditor txtEmail;

    public NewEmailGUIController() {
        this.lblFrom = new Label();
        this.toField = new TextField();
        this.subjectField = new TextField();
        this.txtEmail = new HTMLEditor();
    }

    @FXML
    public void initialize() {
        System.out.println("NewEmailGUIController initialized");
        lblFrom.setText("stefano@gmail.com");
//                toField.setText(ClientGUIController.getTo());
//                subjectField.setText(ClientGUIController.getSubject());
//                txtEmail.setHtmlText(ClientGUIController.getTxtEmail());
        System.out.println("Action: " + ClientGUIController.getAction());
        switch(ClientGUIController.getAction()){
            case REPLY:
            case REPLY_ALL :
                toField.setEditable(false);
                subjectField.setEditable(true);
                txtEmail.setDisable(false);
                break;

            case FORWARD:
                toField.setEditable(true);
                subjectField.setEditable(false);
                txtEmail.setDisable(true);
                break;

            case NEW_EMAIL:
                toField.setEditable(true);
                subjectField.setEditable(true);
                txtEmail.setDisable(false);
                break;

            default:
                break;
        }
    }
}
