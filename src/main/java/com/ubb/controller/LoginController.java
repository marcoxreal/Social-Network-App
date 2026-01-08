package com.ubb.controller;

import com.ubb.DuckApplication;
import com.ubb.domain.User;
import com.ubb.service.AppContext;
import com.ubb.service.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.Optional;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button btnLogin;

    @FXML
    private Label errorLabel;

    private AppContext context;

    public void setContext(AppContext context) {
        this.context = context;
    }

    @FXML
    private void onLogin() {
        Optional<User> result =
                context.authService.login(
                        usernameField.getText().trim(),
                        passwordField.getText()
                );

        if (result.isEmpty()) {
            showError("Login failed", "Invalid credentials");
            return;
        }

        User loggedUser = result.get();

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/ubb/views/duck-view.fxml")
            );
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(
                    DuckApplication.class.getResource("views/main-style.css").toExternalForm()
            );
            Stage stage = new Stage();

            UserController ctrl = loader.getController();
            ctrl.setService(
                    context.userService,
                    context.authService,
                    context.messageService
            );
            ctrl.setFriendshipService(context.friendshipService);
            ctrl.setFriendRequestService(context.friendRequestService);
            ctrl.setLoggedUser(loggedUser);

            stage.setTitle("Session: " + loggedUser.getUsername());
            stage.setScene(scene);
            stage.show();

            ((Stage) btnLogin.getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
