package com.project.client.controller;

import com.project.client.ClientGUI;
import com.project.client.model.UserModel;
import com.project.models.Email;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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
    public ListView<Email> listViewEmails;
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
    private static Email selectedEmail;
    private static ObjectProperty<Email> selectedEmailProperty = new SimpleObjectProperty<>();


    public void initialize() {
        System.out.println("ClientGUIController initialized");

        selectedEmailProperty.set(selectedEmail);

        /**
         * PROPERTY BINDING
         */
        listViewEmails.setItems(ConnectionController.emailsInboxProperty());
        statusServer.fillProperty().bind(ConnectionController.serverStatusProperty());

        /**
         * Disable buttons until an email is selected
         */
        btnReply.disableProperty().bind(ConnectionController.actionsDisabledProperty());
        btnReplyAll.disableProperty().bind(ConnectionController.actionsDisabledProperty());
        btnForward.disableProperty().bind(ConnectionController.actionsDisabledProperty());
        btnDelete.disableProperty().bind(ConnectionController.actionsDisabledProperty());


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
            public ListCell<Email> call(ListView<Email> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Email item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            Platform.runLater(() -> setText(item.getSubject()));
                        } else {
                            Platform.runLater(() -> setText(""));
                        }
                    }
                };
            }
        });

        listViewEmails.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            try {
                ConnectionController.setActionsDisabled(false);

                if(newValue == null) {
                    Platform.runLater(() -> {
                        sender.setText("");
                        recipients.setText("");
                        recipients.getItems().clear();
                        subject.setText("");
                        date.setText("");
                        webViewEmail.getEngine().loadContent("");
                    });
                    return;
                }

                selectedEmail = newValue;
                sender.setText(selectedEmail.getSender());

                recipients.getItems().clear();

                recipients.setText(selectedEmail.getRecipients().get(0));
                for (int i = 1; i < selectedEmail.getRecipients().size(); i++) {
                    recipients.getItems().add(new MenuItem(selectedEmail.getRecipients().get(i)));
                }

                subject.setText(selectedEmail.getSubject());
                date.setText(selectedEmail.getDate());
                webViewEmail.getEngine().loadContent(selectedEmail.getMessage());
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

                ConnectionController.setActionsDisabled(true);

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

    public static Email getSelectedEmail() {
        return selectedEmail;
    }

    public static void setSelectedEmail(Email email) {
        selectedEmail = email;
    }
}
