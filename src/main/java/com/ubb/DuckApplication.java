package com.ubb;

import com.ubb.repository.*;
import com.ubb.service.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.ubb.controller.UserController;
import com.ubb.validator.ValidationContext;
import com.ubb.domain.User;

import java.io.IOException;

public class DuckApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        String url = "jdbc:postgresql://localhost:5432/social_network";
        String username = "postgres";
        String password = "123skem2";

        PagingRepository<User> userRepo = new UserRepositoryDB(url, username, password);
        UserService userService = new UserService(userRepo, new ValidationContext<>(null));
        MessageRepositoryDB messageRepo = new MessageRepositoryDB(url, username, password);
        messageRepo.setUserRepo(userRepo);
        MessageService messageService = new MessageService(messageRepo);
        AuthRepositoryDB authRepo = new AuthRepositoryDB(url, username, password);
        AuthService authService = new AuthService(authRepo);

        FriendshipService friendshipService = new FriendshipService(userRepo);
        FriendRequestRepositoryDB friendRequestRepo = new FriendRequestRepositoryDB(url, username, password);
        friendRequestRepo.setUserRepo(userRepo);
        FriendRequestService friendRequestService = new FriendRequestService(friendRequestRepo, friendshipService);

        FXMLLoader loader =
                new FXMLLoader(DuckApplication.class.getResource("views/duck-view.fxml"));
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(
                DuckApplication.class.getResource("views/main-style.css").toExternalForm()
        );


        stage.setTitle("Duck Manager");
        stage.setScene(scene);

        UserController controller = loader.getController();
        controller.setService(userService, authService, messageService);
        controller.setFriendshipService(friendshipService);
        controller.setFriendRequestService(friendRequestService);

        stage.setOnCloseRequest(event -> controller.cleanup());
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
