package com.project.client.controller;

import com.project.client.ClientGUI;
import com.project.models.Email;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    public WebView webViewEmail;

    private static Actions action;
    private static Email selectedEmail;

    public void initialize() {
        System.out.println("ClientGUIController initialized");
        btnReply.setDisable(true);
        btnReplyAll.setDisable(true);
        btnForward.setDisable(true);
        btnDelete.setDisable(true);


        listViewEmails.itemsProperty().bind(ConnectionController.emailsInboxProperty());

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
                System.out.println("Selected item: " + newValue);
                btnReply.setDisable(false);
                btnReplyAll.setDisable(false);
                btnForward.setDisable(false);
                btnDelete.setDisable(false);
                selectedEmail = newValue;
                sender.setText(newValue.getSender());

                recipients.setText(newValue.getRecipients().get(0));
                for (int i = 1; i < newValue.getRecipients().size(); i++) {
                    recipients.getItems().add(new MenuItem(newValue.getRecipients().get(i)));
                }

                subject.setText(newValue.getSubject());
                webViewEmail.getEngine().loadContent(newValue.getMessage());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
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
                webViewEmail.getEngine().loadContent("");
                btnReply.setDisable(true);
                btnReplyAll.setDisable(true);
                btnForward.setDisable(true);
                btnDelete.setDisable(true);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static Actions getAction() {
        return action;
    }

    public static Email getSelectedEmail() {
        return selectedEmail;
    }

    public static void setSelectedEmail(Email selectedEmail) {
        ClientGUIController.selectedEmail = selectedEmail;
    }

    public void showSelectedEmail() {
//        treeViewEmailsInbox.getSelectionModel().getSelectedItem().getValue();
    }
}
