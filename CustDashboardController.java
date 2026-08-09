package projecttry1;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class CustDashboardController {

    // ---- fx:id fields, must match your FXML exactly ----
    @FXML private Label avatarLabel;
    @FXML private Label usernameLabel;
    @FXML private Label loyaltyLabel;
    @FXML private Label balanceLabel;

    private Customer customer;
    @FXML
    public void initialize() {
        customer = Session.getCurrentCustomer();

        if (customer != null) {
            String username = customer.getUsername();
            avatarLabel.setText(username.substring(0, 1).toUpperCase());
            usernameLabel.setText(username);
            loyaltyLabel.setText(customer.getLoyaltyPoints() + " pts");
            balanceLabel.setText(String.format("$%.2f", customer.getBalance()));
        }
    }


    @FXML
    private void handleViewMenu(ActionEvent event) {
        switchScene(event, "menu.fxml");
    }

    @FXML
    private void handleViewTables(ActionEvent event) {
        switchScene(event, "tables.fxml");
    }

    @FXML
    private void handleMakeReservation(ActionEvent event) {
        switchScene(event, "reservation.fxml");
    }

    @FXML
    private void handleOrder(ActionEvent event) {
        switchScene(event, "order.fxml");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        Session.setCurrentCustomer(null);
        switchScene(event, "login.fxml");
    }

    // ---- Helper: swaps the current scene's root ----
    private void switchScene(ActionEvent event, String fxmlFile) {
        try {
            Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource(fxmlFile));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("theme.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: show an alert to the user instead of just printing the stack trace
        }
    }
}
