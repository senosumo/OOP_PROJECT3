package controller;

import db.DatabaseAdapter;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private DatabaseAdapter db;

    @FXML
    public void initialize() {
        db = new DatabaseAdapter(); // Initialize DB connection
    }

    @FXML
    public void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setStyle("-fx-background-color: rgba(231, 76, 60, 0.85);");
            errorLabel.setText("Username and password required!");
            return;
        }

        String role = db.validateLogin(username, password);

        if (role == null) {
            errorLabel.setStyle("-fx-background-color: rgba(231, 76, 60, 0.85);");
            errorLabel.setText("Invalid username or password");
        } else if (role.equalsIgnoreCase("customer")) {
            errorLabel.setStyle("-fx-background-color: rgba(46, 204, 113, 0.85);");
            errorLabel.setText("Customer login successful");
        } else if (role.equalsIgnoreCase("carrier")) {
            errorLabel.setStyle("-fx-background-color: rgba(52, 152, 219, 0.85);");
            errorLabel.setText("Carrier login successful");
        } else if (role.equalsIgnoreCase("owner")) {
            errorLabel.setStyle("-fx-background-color: rgba(155, 89, 182, 0.85);");
            errorLabel.setText("Owner login successful");
        }
    }

    @FXML
    public void handleRegister() {
        errorLabel.setStyle("-fx-background-color: rgba(241, 196, 15, 0.85);");
        errorLabel.setText("Register not implemented yet");
    }
}
