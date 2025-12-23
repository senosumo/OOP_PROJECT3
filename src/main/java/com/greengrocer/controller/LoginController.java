package com.greengrocer.controller;

import com.greengrocer.dao.UserDAO;
import com.greengrocer.model.User;
import com.greengrocer.util.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;

    private UserDAO userDAO;

    public LoginController() {
        userDAO = new UserDAO();
    }

    @FXML
    public void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please enter both username and password.");
            return;
        }

        User user = userDAO.login(username, password);

        if (user != null) {
            Session.setCurrentUser(user);
            routeToDashboard(user.getRole());
        } else {
            showAlert("Login Failed", "Invalid username or password.");
        }
    }

    @FXML
    public void goToRegister() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/register.fxml"));
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setTitle("Register Customer");
            stage.setScene(new Scene(root, 960, 540));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void routeToDashboard(String role) {
        try {
            String fxmlPath = "";
            String title = "";

            switch (role.toLowerCase()) {
                case "customer":
                    fxmlPath = "/fxml/customer_main.fxml";
                    title = "GroupXX GreenGrocer - Customer";
                    break;
                case "owner":
                    fxmlPath = "/fxml/owner_main.fxml";
                    title = "GroupXX GreenGrocer - Owner";
                    break;
                case "carrier":
                    fxmlPath = "/fxml/carrier_main.fxml";
                    title = "GroupXX GreenGrocer - Carrier";
                    break;
                default:
                    showAlert("Error", "Unknown role: " + role);
                    return;
            }

            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(new Scene(root, 960, 540));
            // Ensure full screen behavior if needed or center
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load dashboard for role: " + role);
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
