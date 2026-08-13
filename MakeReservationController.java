/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
//farha code -- might need fix 
package projecttry1;

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

/**
 * Controller for MakeReservation.fxml.
 * Reuses the existing Reservation, Customer and Table model classes.
 *
 * NOTE: this assumes Table has getTableNumber(), getCapacity() and
 * getStatus()/setStatus(), and that Database exposes some way to list
 * tables (e.g. Database.getTables()). Adjust the two TODO spots below
 * to match your actual Database/Table API if the method names differ.
 *
 * @author malak
 */
public class MakeReservationController {

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<String> timeComboBox;

    @FXML
    private Spinner<Integer> partySizeSpinner;

    @FXML
    private ComboBox<Table> tableComboBox;

    @FXML
    private Label statusLabel;

    @FXML
    private Button confirmButton;

    @FXML
    private Button cancelButton;

    // The customer making the reservation (set via setCustomer() when this
    // screen is opened from the dashboard).
    private Customer currentCustomer;

    @FXML
    public void initialize() {
        // party size: 1 - 20, default 2
        partySizeSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 2));

        // time slots every 30 minutes between 10:00 and 23:00
        for (LocalTime t = LocalTime.of(10, 0); !t.isAfter(LocalTime.of(23, 0)); t = t.plusMinutes(30)) {
            timeComboBox.getItems().add(t.toString());
        }

        // only allow picking today or a future date
        datePicker.setValue(LocalDate.now());

        loadAvailableTables();
        statusLabel.setText("");
    }

    public void setCustomer(Customer customer) {
        this.currentCustomer = customer;
    }

    /**
     * TODO: replace with your real Database call, e.g.
     * List<Table> tables = Database.getInstance().getAllTables();
     */
    private void loadAvailableTables() {
        tableComboBox.getItems().clear();

        List<Table> tables = Database.getTables(); // <-- adjust to your Database API

        for (Table table : tables) {
            if (table.getStatus() == TableStatus.AVAILABLE) {
                tableComboBox.getItems().add(table);
            }
        }
    }

    @FXML
    private void handleConfirm() {
        statusLabel.setText("");

        if (currentCustomer == null) {
            showError("No logged-in customer found.");
            return;
        }
        if (datePicker.getValue() == null) {
            showError("Please pick a date.");
            return;
        }
        if (timeComboBox.getValue() == null) {
            showError("Please pick a time.");
            return;
        }
        if (tableComboBox.getValue() == null) {
            showError("Please pick a table.");
            return;
        }

        LocalTime time = LocalTime.parse(timeComboBox.getValue());
        LocalDateTime dateTime = LocalDateTime.of(datePicker.getValue(), time);
        int partySize = partySizeSpinner.getValue();
        Table selectedTable = tableComboBox.getValue();

        try {
            Reservation reservation = new Reservation(currentCustomer, selectedTable, dateTime, partySize);

            if (!reservation.isValidReservation()) {
                showError("This reservation is not valid (check date/time/party size).");
                return;
            }

            selectedTable.setStatus(TableStatus.RESERVED);

            // TODO: persist the reservation, e.g.
            // Database.saveReservation(reservation);

            showConfirmation(reservation);

        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        Button current = confirmButton != null ? confirmButton : cancelButton;
        current.getScene().getWindow().hide();
    }

    private void showError(String message) {
        statusLabel.setText(message);
    }

    private void showConfirmation(Reservation reservation) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Reservation Confirmed");
        alert.setHeaderText(null);
        alert.setContentText(reservation.toString());
        alert.showAndWait();

        cancelButton.getScene().getWindow().hide();
    }
}
