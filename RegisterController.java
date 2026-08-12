
//------LUJAIN HELMI-------

package projecttry1;

import java.io.IOException;
import java.time.LocalDate;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import projecttry1.Customer;
import projecttry1.Database;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private DatePicker dobPicker;
    @FXML private TextField phoneField;
    @FXML private TextField dietaryField;
    @FXML private Label messageLabel;

    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        LocalDate dob = dobPicker.getValue();
        String phone = phoneField.getText().trim();
        String dietary = dietaryField.getText().trim();

        if (username.isEmpty() || dob == null) {
            showError("Please fill in at least your username and date of birth.");
            return;
        }

        for (Customer c : Database.customers) {
            if (c.getUsername().equals(username)) {
                showError("That username is already taken.");
                return;
            }
        }

        // register() already validates the password (6+ chars) and phone
        // number (11 digits) internally — see Customer.java from Milestone 1.
        Customer newCustomer = new Customer();
        boolean success = newCustomer.register(username, password, dob, phone, dietary);

        if (success) {
            Database.customers.add(newCustomer);
            showSuccess("Account created! You can log in now.");
        } else {
            showError("Registration failed: password needs 6+ characters and phone number needs 11 digits.");
        }
    }

    @FXML
    private void goToLogin() {
        try {
            App.switchScene("Login");
        } catch (IOException e) {
            showError("Could not open the login screen.");
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
