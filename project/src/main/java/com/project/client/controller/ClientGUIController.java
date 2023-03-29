package com.project.client.controller;

import com.project.client.ClientGUI;
import com.project.models.Email;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

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
    public TreeView<String> treeViewEmailsInbox;
    @FXML
    public TreeView<String> treeViewEmailsOutbox;
    @FXML
    public WebView webViewEmail;

    private static Actions action;
    private static Email selectedEmail;
//    private static User user;

    public void initialize() {
        System.out.println("ClientGUIController initialized");

        //Select all SplitPane dividers and set their position to 0.5

        webViewEmail.getEngine().loadContent("<h1>Test</h1><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><h1>CIAO</h1>");

        treeViewEmailsInbox.setRoot(new TreeItem<>("Inbox"));
        treeViewEmailsOutbox.setRoot(new TreeItem<>("Outbox"));

//        treeViewEmailsInbox.getRoot().getChildren().add(new TreeItem<>("Email 1"));

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
    public void newEmail(){
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
            var response = new Alert(Alert.AlertType.CONFIRMATION, "Sei sicuro di voler eliminare la mail?", ButtonType.YES, ButtonType.NO).showAndWait();
            if (response.isPresent() && response.get() == ButtonType.YES) {
                ClientController.deleteEmail(selectedEmail);
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


    public void showSelectedEmail(){

        treeViewEmailsInbox.getSelectionModel().getSelectedItem().getValue();
    }
}
