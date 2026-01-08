package com.ubb.controller;

import com.ubb.domain.User;
import com.ubb.exceptions.RepositoryException;
import com.ubb.service.FriendshipService;
import com.ubb.service.UserService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

public class RemoveFriendsController {

    @FXML
    private ComboBox<User> comboUser1;

    @FXML
    private ComboBox<User> comboUser2;

    private UserService userService;
    private FriendshipService friendshipService;

    public void setUserService(UserService userService) {
        this.userService = userService;
        loadUsers();
    }

    public void setFriendshipService(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    private void loadUsers() {
        comboUser1.setItems(
                FXCollections.observableArrayList(userService.getAllUsers())
        );
        comboUser2.setItems(
                FXCollections.observableArrayList(userService.getAllUsers())
        );
    }

    @FXML
    public void onRemove() {
        User u1 = comboUser1.getValue();
        User u2 = comboUser2.getValue();

        if (u1 == null || u2 == null) return;

        try {
            friendshipService.removeFriendship(u1.getId(), u2.getId());
        } catch (RepositoryException e) {
            showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }


        ((Stage) comboUser1.getScene().getWindow()).close();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
