package com.ubb.controller;

import com.ubb.domain.Duck;
import com.ubb.domain.User;
import com.ubb.service.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;

public class UserProfileController {

    @FXML private Label lblUsername;
    @FXML private Label lblType;
    @FXML private Label lblStats;
    @FXML private Label lblRelationship;

    @FXML private Button btnAddFriend;
    @FXML private Button btnRemoveFriend;
    @FXML private Button btnMessage;

    @FXML private ListView<User> listFriends;

    private Stage stage;

    private User viewer;      // logged user (poate fi null)
    private User profileUser; // userul vizualizat

    private UserService userService;
    private FriendshipService friendshipService;
    private FriendRequestService friendRequestService;
    private MessageService messageService;

    public void init(
            User viewer,
            User profileUser,
            UserService userService,
            FriendshipService friendshipService,
            FriendRequestService friendRequestService,
            MessageService messageService,
            Stage stage
    ) {
        this.viewer = viewer;
        this.profileUser = profileUser;
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.friendRequestService = friendRequestService;
        this.messageService = messageService;
        this.stage = stage;

        listFriends.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getUsername());
            }
        });

        loadProfile();
        refreshButtons();
    }

    private void loadProfile() {
        // re-fetch ca să fim siguri că prietenii sunt up-to-date
        this.profileUser = userService.findById(profileUser.getId())
                .orElse(profileUser);

        lblUsername.setText(profileUser.getUsername());

        if (profileUser instanceof Duck d) {
            lblType.setText("DUCK: " + d.getTip());
            lblStats.setText("Viteza: " + d.getViteza() + " | Rezistenta: " + d.getRezistenta());
        } else {
            lblType.setText("PERSOANA");
            lblStats.setText("");
        }

        List<User> friends = userService.getFriendsOfUser(profileUser.getId());
        listFriends.setItems(FXCollections.observableArrayList(friends));
    }

    private boolean isSelf() {
        return viewer != null && viewer.getId().equals(profileUser.getId());
    }

    private boolean areFriends() {
        if (viewer == null) return false;
        // profileUser e încărcat din DB, deci friends ar trebui să fie populated
        return profileUser.getFriends().contains(viewer.getId());
    }

    private void refreshButtons() {
        boolean loggedIn = viewer != null;
        boolean self = isSelf();
        boolean friends = areFriends();

        btnAddFriend.setDisable(!loggedIn || self || friends);
        btnRemoveFriend.setDisable(!loggedIn || self || !friends);
        btnMessage.setDisable(!loggedIn || self || !friends);

        if (!loggedIn) {
            lblRelationship.setText("Not logged in.");
        } else if (self) {
            lblRelationship.setText("This is you.");
        } else if (friends) {
            lblRelationship.setText("You are friends.");
        } else {
            lblRelationship.setText("Not friends.");
        }
    }

    @FXML
    private void onAddFriend() {
        try {
            if (viewer == null) throw new RuntimeException("You must be logged in.");

            // trimite friend request
            friendRequestService.sendRequest(viewer, profileUser);

            showInfo("Friend request sent to " + profileUser.getUsername());
            refreshButtons();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void onRemoveFriend() {
        try {
            if (viewer == null) throw new RuntimeException("You must be logged in.");

            friendshipService.removeFriendship(viewer.getId(), profileUser.getId());

            // refresh viewer + profile (ca să se vadă imediat)
            viewer = userService.findById(viewer.getId()).orElse(viewer);
            loadProfile();
            refreshButtons();

            showInfo("Removed friend: " + profileUser.getUsername());
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void onMessage() {
        try {
            if (viewer == null) throw new RuntimeException("You must be logged in.");

            // exemplu: trimite un mesaj simplu prin dialog
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Send message");
            dialog.setHeaderText("To: " + profileUser.getUsername());
            dialog.setContentText("Message:");

            dialog.showAndWait().ifPresent(text -> {
                String msg = text == null ? "" : text.trim();
                if (!msg.isEmpty()) {
                    messageService.sendMessage(viewer, profileUser, msg);
                    showInfo("Message sent.");
                }
            });

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void onClose() {
        stage.close();
    }

    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Info");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
