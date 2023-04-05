package com.project.client.controller;

import com.project.client.ClientGUI;
import com.project.client.model.UserModel;
import com.project.models.EmailSerializable;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.shape.Circle;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class ClientGUIController {

    //TODO: delete can be removed
    public enum Actions {
        NEW_EMAIL, REPLY, REPLY_ALL, FORWARD, DELETE
    }

    @FXML
    public Button btnReply;
    @FXML
    public Button btnReplyAll;
    @FXML
    public Button btnForward;
    @FXML
    public Button btnDelete;
    @FXML
    public Button btnNewEmail;
    @FXML
    public ListView<EmailSerializable> listViewEmails;
    @FXML
    public Label sender;
    @FXML
    public MenuButton recipients;
    @FXML
    public Label subject;
    @FXML
    public Label date;
    @FXML
    public WebView webViewEmail;
    @FXML
    public Label userData;
    @FXML
    public Circle statusServer;

    private static Actions action;
    private static EmailSerializable selectedEmail;


    public void initialize() {
        System.out.println("ClientGUIController initialized");

        /**
         * PROPERTY BINDING
         */
        listViewEmails.itemsProperty().bind(ConnectionController.emailsInboxProperty());
        statusServer.fillProperty().bind(ConnectionController.serverStatusProperty());

        /**
         * Disable buttons until an email is selected
         */
        btnReply.setDisable(true);
        btnReplyAll.setDisable(true);
        btnForward.setDisable(true);
        btnDelete.setDisable(true);

        /**
         * Set the user data label to the user's first and last name
         */
        UserModel user = UserController.getUser();
        String firstName = user.getAddress().split("\\.")[0];
        String lastName = user.getAddress().split("\\.")[1];
        firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1);
        lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1).split("@")[0];
        userData.setText(firstName + " " + lastName);

        /**
         * Set the cell factory for the list view
         * This will allow us to set the text of the list view to the subject of the email
         * instead of the default toString() method instead of the address
         */
        listViewEmails.setCellFactory(new Callback<>() {
            @Override
            public ListCell<EmailSerializable> call(ListView<EmailSerializable> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(EmailSerializable item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item.getSubject());
                        } else {
                            setText("");
                        }
                    }
                };
            }
        });

        listViewEmails.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            try {
                btnReply.setDisable(false);
                btnReplyAll.setDisable(false);
                btnForward.setDisable(false);
                btnDelete.setDisable(false);
                selectedEmail = newValue;
                sender.setText(newValue.getSender());

                recipients.getItems().clear();

                recipients.setText(newValue.getRecipients().get(0));
                for (int i = 1; i < newValue.getRecipients().size(); i++) {
                    recipients.getItems().add(new MenuItem(newValue.getRecipients().get(i)));
                }

                subject.setText(newValue.getSubject());
                date.setText(newValue.getDate());
                webViewEmail.getEngine().loadContent(newValue.getMessage());
            } catch (Exception e) {
                System.out.println("[selectedItem] Error: " + e.getMessage());
            }
        });

        ConnectionController.startExecutorService();
    }

    private static void launchEmailWindow() throws IOException {
        Parent newEmailParent = FXMLLoader.load(Objects.requireNonNull(ClientGUI.class.getResource("NewEmailGUI.fxml")));
        Stage newEmailStage = new Stage();
        newEmailStage.setScene(new Scene(newEmailParent));
        newEmailStage.show();

        // when the window is closed, the application is closed
        newEmailStage.setOnCloseRequest(event -> {
            System.out.println("New Email closed");
        });
    }

    @FXML
    public void newEmail() {
        System.out.println("btnNewEmail clicked");
        try {
            action = Actions.NEW_EMAIL;
            launchEmailWindow();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void reply() {
        System.out.println("btnReply clicked");
        try {
            action = Actions.REPLY;
            launchEmailWindow();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void replyAll() {
        System.out.println("btnReplyAll clicked");
        try {
            action = Actions.REPLY_ALL;
            launchEmailWindow();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void forward() {
        System.out.println("btnForward clicked");
        try {
            action = Actions.FORWARD;
            launchEmailWindow();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void delete() {
        System.out.println("btnDelete clicked");
        try {
            Optional<ButtonType> response = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this email?", ButtonType.YES, ButtonType.NO).showAndWait();
            if (response.isPresent() && response.get() == ButtonType.YES) {
                ConnectionController.deleteEmail(selectedEmail);

                selectedEmail = null;
                sender.setText("");
                recipients.setText("");
                recipients.getItems().clear();
                subject.setText("");
                date.setText("");
                webViewEmail.getEngine().loadContent("");
                btnReply.setDisable(true);
                btnReplyAll.setDisable(true);
                btnForward.setDisable(true);
                btnDelete.setDisable(true);
                new Alert(Alert.AlertType.INFORMATION, "Email deleted").showAndWait();
            }
        } catch (Exception e) {
            System.out.println("[FXML delete] Error: " + e.getMessage());
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    public static Actions getAction() {
        return action;
    }

    public static EmailSerializable getSelectedEmail() {
        return selectedEmail;
    }

}
