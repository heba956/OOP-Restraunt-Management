package projecttry1;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import enums.TableStatus;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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

    @FXML private Button updateButton;
    @FXML private Button cancelReservationButton;
    @FXML private Button backButton;

    private Customer currentCustomer;
    private Reservation currentReservation;

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

    public void setReservation(Reservation reservation) {
        this.currentReservation = reservation;
        if (reservation != null) {
            if (reservation.getDate() != null) {
                datePicker.setValue(reservation.getDate().toLocalDate());
                timeComboBox.setValue(reservation.getDate().toLocalTime().toString());
            }
            partySizeSpinner.getValueFactory().setValue(reservation.getPartySize());
            if (reservation.getTable() != null) {
                if (!tableComboBox.getItems().contains(reservation.getTable())) {
                    tableComboBox.getItems().add(reservation.getTable());
                }
                tableComboBox.setValue(reservation.getTable());
            }
        }
    }

    private void loadAvailableTables() {
        tableComboBox.getItems().clear();
        List<Table> tables = Database.getTables();

        for (Table table : tables) {
            if (table.getStatus() == TableStatus.AVAILABLE ||
                    (currentReservation != null && currentReservation.getTable() != null && currentReservation.getTable().equals(table))) {
                tableComboBox.getItems().add(table);
            }
        }

        if (!tableComboBox.getItems().isEmpty()) {
            tableComboBox.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void handleMakeNewReservation() {
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
            Reservation newReservation = currentCustomer.makeReservation(selectedTable, dateTime, partySize);
            selectedTable.setStatus(enums.TableStatus.RESERVED);
            showSuccessAlert("Reservation Created", "Reservation Successfully Created!", newReservation.toString());
            handleBack();
        } catch (IllegalArgumentException e) {
            showError("Could not create reservation: " + e.getMessage());
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        if (statusLabel != null) statusLabel.setText("");

        if (currentCustomer == null) {
            showError("No logged-in customer found.");
            return;
        }
        if (currentReservation == null) {
            showError("No active reservation selected for update.");
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
            Table oldTable = currentReservation.getTable();
            if (oldTable != null && !oldTable.equals(selectedTable)) {
                oldTable.setStatus(TableStatus.AVAILABLE);
            }

            currentReservation.setDate(dateTime);
            currentReservation.setPartySize(partySize);
            currentReservation.setTable(selectedTable);
            selectedTable.setStatus(TableStatus.RESERVED);

            showSuccessAlert("Reservation Updated", "Reservation Successfully Updated!", currentReservation.toString());
            handleBack();

        } catch (Exception e) {
            showError("Update Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelReservation() {
        if (currentCustomer == null) {
            showError("No logged-in customer found.");
            return;
        }
        if (currentReservation == null) {
            showError("No reservation selected to cancel.");
            return;
        }

        Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
        confirmAlert.setTitle("Cancel Reservation");
        confirmAlert.setHeaderText("Are you sure you want to cancel this reservation?");
        confirmAlert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Uses the Customer class's cancelReservation method safely
                currentCustomer.cancelReservation(currentReservation);

                showSuccessAlert("Reservation Cancelled", "Reservation Successfully Cancelled.", "");
                handleBack();

            } catch (Exception e) {
                showError("Cancellation Error: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleBack() {
        try {
            App.switchScene("CustDashboard");
        } catch (IOException e) {
            if (backButton != null && backButton.getScene() != null) {
                backButton.getScene().getWindow().hide();
            }
        }
    }

    private void showError(String message) {
        if (statusLabel != null) {
            statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            statusLabel.setText(message);
        }
    }

    private void showSuccessAlert(String title, String header, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        if (!content.isEmpty()) {
            alert.setContentText(content);
        }
        alert.showAndWait();
    }
}