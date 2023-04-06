package com.project.client.controller;

import com.project.models.EmailSerializable;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

        System.out.println("Action: " + ClientGUIController.getAction());
        switch (ClientGUIController.getAction()) {
            case REPLY:
                toField.setEditable(false);
                subjectField.setEditable(true);
                msgHtml.setDisable(false);
                toField.setText(ClientGUIController.getSelectedEmail().getSender());
                break;

            case REPLY_ALL:
                toField.setEditable(false);
                subjectField.setEditable(true);
                msgHtml.setDisable(false);

                ArrayList<String> recipients = new ArrayList<>(ClientGUIController.getSelectedEmail().getRecipients());
                recipients.removeIf(s -> s.equals(UserController.getUser().getAddress()));
                recipients.add(ClientGUIController.getSelectedEmail().getSender());

                toField.setText(recipients.toString().replace("[", "").replace("]", ""));
                break;

            case FORWARD:
                toField.setEditable(true);
                subjectField.setEditable(false);
                msgHtml.setDisable(true);

                subjectField.setText("Fwd: " + ClientGUIController.getSelectedEmail().getSubject());
                msgHtml.setHtmlText(ClientGUIController.getSelectedEmail().getMessage());
                break;

            case NEW_EMAIL:
                toField.setEditable(true);
                subjectField.setEditable(true);
                msgHtml.setDisable(false);
                break;

            default:
                new Alert(Alert.AlertType.ERROR, "Action not found").showAndWait();
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
        try {
            ConnectionController.sendEmail(new EmailSerializable(lblFrom.getText(), addresses, subjectField.getText(), msgHtml.getHtmlText().replace("contenteditable=\"true\"", "contenteditable=\"false\""),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))));

            // Close window
            Stage stage = (Stage) btnSend.getScene().getWindow();
            stage.close();

            new Alert(Alert.AlertType.INFORMATION, "Email sent successfully").showAndWait();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }
}
