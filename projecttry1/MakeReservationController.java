package projecttry1;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import enums.TableStatus;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

public class MakeReservationController {

    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> timeComboBox;
    @FXML private Spinner<Integer> partySizeSpinner;
    @FXML private ComboBox<Table> tableComboBox;
    @FXML private Label statusLabel;
    @FXML private Button confirmButton;
    @FXML private Button cancelButton;

    private Customer currentCustomer;

    @FXML
    public void initialize() {
        currentCustomer = Session.getCurrentCustomer();
        if (currentCustomer == null && !Database.customers.isEmpty()) {
            currentCustomer = Database.customers.get(0);
            Session.setCurrentCustomer(currentCustomer);
        }

        partySizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 2));

        for (LocalTime t = LocalTime.of(10, 0); !t.isAfter(LocalTime.of(23, 0)); t = t.plusMinutes(30)) {
            timeComboBox.getItems().add(t.toString());
        }

        datePicker.setValue(LocalDate.now().plusDays(1));
        loadAvailableTables();
        if (statusLabel != null) statusLabel.setText("");
    }

    public void setCustomer(Customer customer) {
        this.currentCustomer = customer;
    }

    private void loadAvailableTables() {
        tableComboBox.getItems().clear();
        List<Table> tables = Database.getTables();

        for (Table table : tables) {
            if (table.getStatus() == TableStatus.AVAILABLE) {
                tableComboBox.getItems().add(table);
            }
        }

        if (!tableComboBox.getItems().isEmpty()) {
            tableComboBox.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void handleConfirm() {
        if (statusLabel != null) statusLabel.setText("");

        if (currentCustomer == null) {
            showError("No logged-in customer found.");
            return;
        }
        if (datePicker.getValue() == null) {
            showError("Please select a date.");
            return;
        }
        if (timeComboBox.getValue() == null) {
            showError("Please select a time.");
            return;
        }
        if (tableComboBox.getValue() == null) {
            showError("Please select a table.");
            return;
        }

        LocalTime time = LocalTime.parse(timeComboBox.getValue());
        LocalDateTime dateTime = LocalDateTime.of(datePicker.getValue(), time);
        int partySize = partySizeSpinner.getValue();
        Table selectedTable = tableComboBox.getValue();

        try {
            Reservation reservation = currentCustomer.makeReservation(selectedTable, dateTime, partySize);
            selectedTable.setStatus(TableStatus.RESERVED);

            showConfirmation(reservation);

        } catch (Exception e) {
            showError("Reservation Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        try {
            App.switchScene("CustDashboard");
        } catch (IOException e) {
            if (cancelButton != null && cancelButton.getScene() != null) {
                cancelButton.getScene().getWindow().hide();
            }
        }
    }

    private void showError(String message) {
        if (statusLabel != null) {
            statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            statusLabel.setText(message);
        }
    }

    private void showConfirmation(Reservation reservation) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Reservation Confirmed");
        alert.setHeaderText("Reservation Successfully Placed!");
        alert.setContentText(reservation.toString());
        alert.showAndWait();

        handleCancel();
    }
}
