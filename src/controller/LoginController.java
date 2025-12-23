package controller;

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

    @FXML
    public void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setStyle("-fx-background-color: rgba(231, 76, 60, 0.85);");
            errorLabel.setText("Username and password required!");
            return;
        }

        if (username.equals("cust") && password.equals("cust")) {
            errorLabel.setStyle("-fx-background-color: rgba(46, 204, 113, 0.85);");
            errorLabel.setText("Customer login successful");
        } else if (username.equals("carr") && password.equals("carr")) {
            errorLabel.setStyle("-fx-background-color: rgba(52, 152, 219, 0.85);");
            errorLabel.setText("Carrier login successful");
        } else if (username.equals("own") && password.equals("own")) {
            errorLabel.setStyle("-fx-background-color: rgba(155, 89, 182, 0.85);");
            errorLabel.setText("Owner login successful");
        } else {
            errorLabel.setStyle("-fx-background-color: rgba(231, 76, 60, 0.85);");
            errorLabel.setText("Invalid username or password");
        }
    }

    @FXML
    public void handleRegister() {
        errorLabel.setStyle("-fx-background-color: rgba(241, 196, 15, 0.85);");
        errorLabel.setText("Register not implemented yet");
    }
}
