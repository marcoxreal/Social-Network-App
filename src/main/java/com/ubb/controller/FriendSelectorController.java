package com.ubb.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import com.ubb.domain.User;
import com.ubb.service.MessageService;
import com.ubb.service.UserService;

public class FriendSelectorController {
    @FXML private ListView<User> friendsList;

    private User loggedUser;
    private UserService userService;
    private MessageService messageService;
    private Stage stage;

    public void init(User loggedUser, UserService userService, MessageService messageService, Stage stage) {
        this.loggedUser = loggedUser;
        this.userService = userService;
        this.messageService = messageService;
        this.stage = stage;

        friendsList.getItems().setAll(
                userService.getFriendsOfUser(loggedUser.getId())
        );
    }

    @FXML
    private void handleOpenChat() {
        User selectedFriend = friendsList.getSelectionModel().getSelectedItem();
        if (selectedFriend == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ubb/views/chat-view.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/com/ubb/views/chat-style.css").toExternalForm());
            Stage chatStage = new Stage();
            chatStage.setTitle("Chat with " + selectedFriend.getUsername());
            chatStage.setScene(scene);

            ChatController chatController = loader.getController();
            chatController.setServices(messageService, userService);
            chatController.setUsers(loggedUser, selectedFriend);

            chatStage.show();
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
