package com.ubb.controller;

import com.ubb.domain.User;
import com.ubb.service.FriendRequestService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.stage.Stage;

import java.util.List;

public class AddFriendsController {

    @FXML
    private ComboBox<User> comboUser;

    private User loggedUser;
    private FriendRequestService friendRequestService;

    @FXML
    private void initialize() {
        comboUser.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(User u, boolean empty) {
                super.updateItem(u, empty);
                setText(empty || u == null ? null : u.getUsername());
            }
        });
        comboUser.setButtonCell(comboUser.getCellFactory().call(null));
    }

    public void init(User loggedUser,
                     FriendRequestService frService,
                     List<User> users) {

        this.loggedUser = loggedUser;
        this.friendRequestService = frService;

        ObservableList<User> data = FXCollections.observableArrayList(users);
        data.removeIf(u -> u.getId().equals(loggedUser.getId()));

        comboUser.setItems(data);
    }

    @FXML
    private void onSendRequest() {
        User to = comboUser.getValue();

        if (to == null) {
            showAlert("Error", "Selectează un user", Alert.AlertType.ERROR);
            return;
        }

        try {
            friendRequestService.sendRequest(loggedUser, to);
            showAlert("Succes", "Friend request trimis", Alert.AlertType.INFORMATION);
            close();
        } catch (RuntimeException e) {
            showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    @FXML
    private void onCancel() {
        close();
    }

    private void close() {
        Stage stage = (Stage) comboUser.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
