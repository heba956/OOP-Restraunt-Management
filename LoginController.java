
//-----------LUJAIN HELMI----------
package projecttry1;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import projecttry1.App;
import projecttry1.Customer;
import projecttry1.Database;

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

        // Customer.login() checks credentials against ONE existing Customer
        // object, so first we have to find that customer in the Database.
        Customer matchedCustomer = null;
        for (Customer c : Database.customers) {
            if (c.getUsername().equals(username)) {
                matchedCustomer = c;
                break;
            }
        }

        if (matchedCustomer == null) {
            showError("No account found with that username.");
            return;
        }

        boolean loginSuccessful = matchedCustomer.login(username, password);

        if (loginSuccessful) {
            showSuccess("Login successful! Welcome, " + username + ".");
            // Once a teammate's Customer Dashboard screen exists, replace this
            // comment with:  App.switchScene("CustomerDashboard");
        } else {
            showError("Incorrect username or password.");
        }
    }

    @FXML
    private void goToRegister() {
        try {
            App.switchScene("Register");
        } catch (IOException e) {
            showError("Could not open the registration screen.");
            e.printStackTrace();
        }
    }

    private void showError(String text) {
        messageLabel.setStyle("-fx-text-fill: crimson;");
        messageLabel.setText(text);
    }

    private void showSuccess(String text) {
        messageLabel.setStyle("-fx-text-fill: green;");
        messageLabel.setText(text);
    }
}
