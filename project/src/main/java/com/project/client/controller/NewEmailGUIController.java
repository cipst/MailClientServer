package com.project.client.controller;

import com.project.models.EmailSerializable;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

public class NewEmailGUIController {

    @FXML
    public Label lblFrom;
    @FXML
    public TextField toField;
    @FXML
    public TextField subjectField;
    @FXML
    public HTMLEditor msgHtml;
    @FXML
    public Button btnSend;
    @FXML
    private ClientGUIController clientGUIController;

    public NewEmailGUIController() {
        this.lblFrom = new Label();
        this.toField = new TextField();
        this.subjectField = new TextField();
        this.msgHtml = new HTMLEditor();
    }

    @FXML
    public void initialize() {
        System.out.println("NewEmailGUIController initialized");
        lblFrom.setText(UserController.getUser().getAddress());
//                toField.setText(ClientGUIController.getTo());
//                subjectField.setText(ClientGUIController.getSubject());
//                txtEmail.setHtmlText(ClientGUIController.getTxtEmail());
        System.out.println("Action: " + ClientGUIController.getAction());
        switch (ClientGUIController.getAction()) {
            case REPLY:
            case REPLY_ALL:
                toField.setEditable(false);
                subjectField.setEditable(true);
                msgHtml.setDisable(false);
                break;

            case FORWARD:
                toField.setEditable(true);
                subjectField.setEditable(false);
                msgHtml.setDisable(true);
                break;

            case NEW_EMAIL:
                toField.setEditable(true);
                subjectField.setEditable(true);
                msgHtml.setDisable(false);
                break;

            default:
                break;
        }
    }

    private ArrayList<String> checkEmail(String[] addresses) {
        // Regex to check email address
        Pattern pattern = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~\\-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~\\-]+)*|\\\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\\\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
        ArrayList<String> invalidAddresses = new ArrayList<>();

        for (String address : addresses) {
            if (!pattern.matcher(address.trim()).matches()) {
                invalidAddresses.add(address.trim());
            }
        }

        return invalidAddresses;
    }

    private String checkEmail(String address) {
        // Regex to check email address
        Pattern pattern = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~\\-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~\\-]+)*|\\\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\\\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
        String invalidAddresses = "";

        if (!pattern.matcher(address.trim()).matches()) {
            invalidAddresses = address.trim();
        }

        return invalidAddresses;
    }

    private boolean isValidToField(String to) {
        if (to.equals("")) {
            new Alert(Alert.AlertType.ERROR, "Missing 'TO' address").showAndWait();
            System.out.println("Missing 'TO' address: " + to);
            return false;
        }

        if (to.contains(",")) {
            String[] addresses = to.split(",");
            ArrayList<String> invalidAddresses = checkEmail(addresses);
            if (invalidAddresses.size() > 0) {
                new Alert(Alert.AlertType.ERROR, "Invalid 'TO' addresses: " + invalidAddresses).showAndWait();
                System.out.println("Invalid addresses: " + invalidAddresses);
                return false;
            }
        } else {
            String invalidAddress = checkEmail(to);
            if (!invalidAddress.equals("")) {
                new Alert(Alert.AlertType.ERROR, "Invalid 'TO' address: " + invalidAddress).showAndWait();
                System.out.println("Invalid address: " + invalidAddress);
                return false;
            }
        }
        return true;
    }

    @FXML
    public void sendEmail() {
        ArrayList<String> addresses = new ArrayList<>();

        if (!isValidToField(toField.getText())) return;

        if (subjectField.getText().equals("")) {
            new Alert(Alert.AlertType.ERROR, "Missing 'SUBJECT'").showAndWait();
            System.out.println("Missing 'SUBJECT'");
            return;
        }

        if (msgHtml.getHtmlText().equals("<html dir=\"ltr\"><head></head><body contenteditable=\"true\"></body></html>")) {
            new Alert(Alert.AlertType.ERROR, "Missing 'MESSAGE'").showAndWait();
            System.out.println("Missing 'MESSAGE'");
            return;
        }

        if (toField.getText().contains(",")) {
            String[] ads = toField.getText().split(",");
            for (String address : ads) {
                addresses.add(address.trim());
            }
        } else {
            addresses.add(toField.getText());
        }

        System.out.println("Send email");

        boolean success = ConnectionController.sendEmail(new EmailSerializable(lblFrom.getText(), addresses, subjectField.getText(), msgHtml.getHtmlText().replace("contenteditable=\"true\"", "contenteditable=\"false\""),
                DateFormat.getDateTimeInstance().format(new Date())));

        if (success) {
            // Close window
            Stage stage = (Stage) btnSend.getScene().getWindow();
            stage.close();
        }
    }
}
