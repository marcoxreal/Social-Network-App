package com.ubb.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import org.mindrot.jbcrypt.BCrypt;

import com.ubb.domain.TipRata;
import com.ubb.service.UserService;

public class AddUserController {

    @FXML private TextField fieldId;
    @FXML private TextField fieldUsername;
    @FXML private TextField fieldEmail;
    @FXML private PasswordField fieldPassword;

    @FXML private ComboBox<String> comboUserType;
    @FXML private ComboBox<TipRata> comboTipRata;

    @FXML private TextField fieldViteza;
    @FXML private TextField fieldRezistenta;

    private UserService userService;
    private Stage stage;

    public void init(UserService userService, Stage stage) {
        this.userService = userService;
        this.stage = stage;

        comboUserType.getItems().setAll("PERSON", "DUCK");
        comboTipRata.getItems().setAll(TipRata.values());

        setDuckFieldsVisible(false);

        comboUserType.setOnAction(e ->
                setDuckFieldsVisible("DUCK".equals(comboUserType.getValue()))
        );
    }

    private void setDuckFieldsVisible(boolean visible) {
        comboTipRata.setVisible(visible);
        fieldViteza.setVisible(visible);
        fieldRezistenta.setVisible(visible);
    }

    @FXML
    public void onSave(ActionEvent e) {
        try {
            Long id = Long.parseLong(fieldId.getText());
            String username = fieldUsername.getText();
            String email = fieldEmail.getText();
            String password = fieldPassword.getText();

            if (email.isEmpty() || password.isEmpty()) {
                throw new IllegalArgumentException("Email și parola sunt obligatorii!");
            }

            String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());

            String type = comboUserType.getValue();

            if ("PERSON".equals(type)) {

                userService.addPerson(id, username, email, passwordHash);

            } else {

                TipRata tip = comboTipRata.getValue();
                double viteza = Double.parseDouble(fieldViteza.getText());
                double rezistenta = Double.parseDouble(fieldRezistenta.getText());

                userService.addDuck(id, username, email, passwordHash, tip, viteza, rezistenta);
            }

            stage.close();

        } catch (Exception ex) {
            MessageAlert.showErrorMessage(null, ex.getMessage());
        }
    }

    @FXML
    public void onCancel(ActionEvent e) {
        stage.close();
    }
}
