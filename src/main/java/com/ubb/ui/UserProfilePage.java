package com.ubb.ui;

import com.ubb.controller.UserProfileController;
import com.ubb.domain.User;
import com.ubb.service.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class UserProfilePage implements UiPage {

    private final User viewer;     // loggedUser (poate fi null)
    private final User profileUser;

    private final UserService userService;
    private final FriendshipService friendshipService;
    private final FriendRequestService friendRequestService;
    private final MessageService messageService;

    public UserProfilePage(
            User viewer,
            User profileUser,
            UserService userService,
            FriendshipService friendshipService,
            FriendRequestService friendRequestService,
            MessageService messageService
    ) {
        this.viewer = viewer;
        this.profileUser = profileUser;
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.friendRequestService = friendRequestService;
        this.messageService = messageService;
    }

    @Override
    public void show() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/ubb/views/user-profile-view.fxml")
            );

            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(
                    getClass().getResource("/com/ubb/views/profile-style.css").toExternalForm()
            );

            Stage stage = new Stage();
            stage.setTitle("Profile: " + profileUser.getUsername());
            stage.setScene(scene);

            UserProfileController ctrl = loader.getController();
            ctrl.init(viewer, profileUser, userService, friendshipService, friendRequestService, messageService, stage);

            stage.show();
        } catch (Exception e) {
            throw new RuntimeException("Could not open profile page: " + e.getMessage(), e);
        }
    }
}
