package projecttry1;

import java.io.IOException;
import java.time.LocalDate;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

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

        if (username.isEmpty() || password.isEmpty() || phone.isEmpty()) {
            showError("Please fill in all required fields (username, password, phone).");
            return;
        }

        if (password.length() < 6) {
            showError("Password must be at least 6 characters long.");
            return;
        }

        if (phone.length() != 11) {
            showError("Phone number must be exactly 11 digits.");
            return;
        }

        for (Customer c : Database.customers) {
            if (c.getUsername().equalsIgnoreCase(username)) {
                showError("Username already exists. Please choose another.");
                return;
            }
        }

        if (dob == null) {
            dob = LocalDate.now().minusYears(20);
        }

        Customer newCustomer = new Customer(username, password, dob, 500.0, phone, dietary.isEmpty() ? "None" : dietary);
        Database.customers.add(newCustomer);
        Session.setCurrentCustomer(newCustomer);

        showSuccess("Registration successful! Redirecting to dashboard...");

        try {
            App.switchScene("CustDashboard");
        } catch (IOException e) {
            showError("Error navigating to dashboard.");
            e.printStackTrace();
        }
    }

    @FXML
    private void goToLogin() {
        try {
            App.switchScene("Login");
        } catch (IOException e) {
            showError("Could not open login screen.");
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
