package com.ubb.controller;

import com.ubb.domain.FriendRequest;
import com.ubb.domain.User;
import com.ubb.service.FriendRequestService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class FriendRequestsController {

    @FXML
    private ListView<FriendRequest> requestsList;

    private FriendRequestService friendRequestService;
    private User loggedUser;

    public void init(FriendRequestService service, User user) {
        this.friendRequestService = service;
        this.loggedUser = user;

        requestsList.getItems().setAll(
                service.getPendingForUser(user)
        );
    }

    @FXML
    public void initialize() {
        requestsList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(FriendRequest fr, boolean empty) {
                super.updateItem(fr, empty);

                if (empty || fr == null) {
                    setText(null);
                } else {
                    setText(
                            "From: " + fr.getFrom().getUsername() +
                                    " | " + fr.getStatus() +
                                    " | " + fr.getDate().toLocalDate()
                    );

                }
            }
        });
    }


    @FXML
    private void handleAccept() {
        FriendRequest fr = requestsList.getSelectionModel().getSelectedItem();
        if (fr == null) return;

        friendRequestService.acceptRequest(fr);
        requestsList.getItems().remove(fr);
    }

    @FXML
    private void handleReject() {
        FriendRequest fr = requestsList.getSelectionModel().getSelectedItem();
        if (fr == null) return;

        friendRequestService.rejectRequest(fr);
        requestsList.getItems().remove(fr);
    }
}
