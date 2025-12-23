package com.greengrocer.controller;

import com.greengrocer.dao.UserDAO;
import com.greengrocer.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField phoneField;

    private UserDAO userDAO;

    public RegisterController() {
        userDAO = new UserDAO();
    }

    @FXML
    public void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String address = addressField.getText();
        String phone = phoneField.getText();

        if (username.isEmpty() || password.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            showAlert("Error", "All fields are required.");
            return;
        }

        if (password.length() < 4) {
            showAlert("Weak Password", "Password must be at least 4 characters.");
            return;
        }

        if (userDAO.usernameExists(username)) {
            showAlert("Error", "Username already taken.");
            return;
        }

        // Create user object (ID is auto-inc, Role is customer default)
        User newUser = new User(0, username, null, "customer", address, phone, 0);

        if (userDAO.register(newUser, password)) {
            showAlert("Success", "Registration successful! Please login.");
            goBack();
        } else {
            showAlert("Error", "Registration failed.");
        }
    }

    @FXML
    public void goBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setTitle("Local Greengrocer Login");
            stage.setScene(new Scene(root, 960, 540));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
