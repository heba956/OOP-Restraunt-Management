package projecttry1;

import java.io.IOException;
import enums.Role;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class StaffLoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;
    @FXML private Label adminBadge;
    @FXML private Label waiterBadge;

    @FXML
    public void initialize() {
        // Make the role badges clickable to fill in demo credentials
        if (adminBadge != null) {
            adminBadge.setOnMouseClicked(e -> {
                highlightBadge(true);
            });
        }
        if (waiterBadge != null) {
            waiterBadge.setOnMouseClicked(e -> {
                highlightBadge(false);
            });
        }
    }

    private void highlightBadge(boolean isAdmin) {
        String activeStyle   = "-fx-background-color: #2c3e50; -fx-text-fill: white; "
                             + "-fx-padding: 5 16; -fx-background-radius: 20; "
                             + "-fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand;";
        String inactiveStyle = "-fx-background-color: #ecf0f1; -fx-text-fill: #7f8c8d; "
                             + "-fx-padding: 5 16; -fx-background-radius: 20; "
                             + "-fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand;";
        adminBadge.setStyle(isAdmin ? activeStyle : inactiveStyle);
        waiterBadge.setStyle(isAdmin ? inactiveStyle : activeStyle);
    }

    @FXML
    private void handleStaffLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password.");
            return;
        }

        Staff matchedStaff = null;
        for (Staff s : Database.staffMembers) {
            if (s.getUsername().equalsIgnoreCase(username)) {
                matchedStaff = s;
                break;
            }
        }

        if (matchedStaff == null) {
            showError("No staff account found with that username.");
            return;
        }

        if (!matchedStaff.getPassword().equals(password)) {
            showError("Incorrect password.");
            return;
        }

        // Login successful
        Session.setCurrentStaff(matchedStaff);
        showSuccess("Welcome, " + matchedStaff.getUsername() + "!");

        try {
            if (matchedStaff.getRole() == Role.ADMIN) {
                App.switchScene("AdminDashboard");
            } else if (matchedStaff.getRole() == Role.WAITER) {
                App.switchScene("WaiterDashboard");
            } else {
                showError("Unknown role. Contact system administrator.");
            }
        } catch (IOException e) {
            showError("Error navigating to dashboard.");
            e.printStackTrace();
        }
    }

    @FXML
    private void goToCustomerLogin() {
        try {
            App.switchScene("Login");
        } catch (IOException e) {
            showError("Could not return to login screen.");
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
