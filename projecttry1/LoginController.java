package projecttry1;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both a username and password.");
            return;
        }

        Customer matchedCustomer = null;
        for (Customer c : Database.customers) {
            if (c.getUsername().equalsIgnoreCase(username)) {
                matchedCustomer = c;
                break;
            }
        }

        if (matchedCustomer == null) {
            showError("No account found with that username.");
            return;
        }

        boolean loginSuccessful = matchedCustomer.login(matchedCustomer.getUsername(), password);

        if (loginSuccessful) {
            showSuccess("Login successful! Welcome, " + matchedCustomer.getUsername() + ".");
            Session.setCurrentCustomer(matchedCustomer);
            try {
                App.switchScene("CustDashboard");
            } catch (IOException e) {
                showError("Error navigating to dashboard.");
                e.printStackTrace();
            }
        } else {
            showError("Incorrect username or password.");
        }
    }

    @FXML
    private void goToRegister() {
        try {
            App.switchScene("Register");
        } catch (IOException e) {
            showError("Could not open registration screen.");
            e.printStackTrace();
        }
    }

    private void showError(String text) {
        messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        messageLabel.setText(text);
    }

    private void showSuccess(String text) {
        messageLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
        messageLabel.setText(text);
    }
}
