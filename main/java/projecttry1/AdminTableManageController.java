package projecttry1;

import java.io.IOException;
import enums.TableLocation;
import enums.TableStatus;
import enums.TableType;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class AdminTableManageController {

    @FXML private TableView<Table> tableView;
    @FXML private TableColumn<Table, Integer> colNum;
    @FXML private TableColumn<Table, Integer> colCap;
    @FXML private TableColumn<Table, String>  colLoc;
    @FXML private TableColumn<Table, String>  colStatus;
    @FXML private TableColumn<Table, String>  colTime;

    @FXML private TextField tableNumField;
    @FXML private TextField capacityField;
    @FXML private ComboBox<String> locationCombo;
    @FXML private ComboBox<String> statusCombo;
    @FXML private ComboBox<String> timeCombo;
    @FXML private Label feedbackLabel;

    @FXML
    public void initialize() {
        colNum.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getTableNumber()).asObject());
        colCap.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getCapacity()).asObject());
        colLoc.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTableType() != null ? data.getValue().getTableType().name() : "INDOOR"));
        colStatus.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStatus() != null ? data.getValue().getStatus().name() : "AVAILABLE"));
        colTime.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTimeSlot() != null ? data.getValue().getTimeSlot() : "12:00 PM"));

        for (TableType t : TableType.values()) {
            locationCombo.getItems().add(t.name());
        }
        locationCombo.setValue(TableType.INDOOR.name());

        for (TableStatus s : TableStatus.values()) {
            statusCombo.getItems().add(s.name());
        }
        statusCombo.setValue(TableStatus.AVAILABLE.name());

        timeCombo.getItems().addAll("12:00 PM", "2:00 PM", "4:00 PM", "6:00 PM", "8:00 PM");
        timeCombo.setValue("12:00 PM");

        refreshTable();
    }

    private void refreshTable() {
        tableView.setItems(FXCollections.observableArrayList(Database.getTables()));
        tableView.refresh();
    }

    @FXML
    private void handleSelectTable() {
        Table selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            tableNumField.setText(String.valueOf(selected.getTableNumber()));
            tableNumField.setDisable(true); // Table number is primary identifier
            capacityField.setText(String.valueOf(selected.getCapacity()));
            if (selected.getTableType() != null) {
                locationCombo.setValue(selected.getTableType().name());
            }
            if (selected.getStatus() != null) {
                statusCombo.setValue(selected.getStatus().name());
            }
            if (selected.getTimeSlot() != null) {
                timeCombo.setValue(selected.getTimeSlot());
            }
        }
    }

    @FXML
    private void handleAdd() {
        String numText = tableNumField.getText().trim();
        String capText = capacityField.getText().trim();
        String locStr = locationCombo.getValue();
        String statusStr = statusCombo.getValue();
        String timeStr = timeCombo.getValue();

        if (numText.isEmpty() || capText.isEmpty()) {
            showFeedback("Please fill in table number and capacity.", false);
            return;
        }

        int tableNum;
        int capacity;
        try {
            tableNum = Integer.parseInt(numText);
            capacity = Integer.parseInt(capText);
            if (tableNum <= 0 || capacity <= 0) {
                showFeedback("Table number and capacity must be positive integers.", false);
                return;
            }
        } catch (NumberFormatException e) {
            showFeedback("Invalid number format for table number or capacity.", false);
            return;
        }

        // Check for duplicate table number
        for (Table t : Database.getTables()) {
            if (t.getTableNumber() == tableNum) {
                showFeedback("Table #" + tableNum + " already exists. Use Update to modify it.", false);
                return;
            }
        }

        TableType tableType = TableType.valueOf(locStr != null ? locStr : TableType.INDOOR.name());
        TableStatus status = TableStatus.valueOf(statusStr != null ? statusStr : TableStatus.AVAILABLE.name());
        String timeSlot = (timeStr != null && !timeStr.isEmpty()) ? timeStr : "12:00 PM";

        Table newTable = new Table(tableNum, capacity, tableType, status, timeSlot);

        Staff staff = Session.getCurrentStaff();
        if (staff instanceof Admin) {
            ((Admin) staff).createTable(newTable);
        } else {
            Database.tables.add(newTable);
        }

        refreshTable();
        handleClearForm();
        showFeedback("Table #" + tableNum + " added successfully.", true);
    }

    @FXML
    private void handleUpdate() {
        Table selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showFeedback("Please select a table from the list to update.", false);
            return;
        }

        String capText = capacityField.getText().trim();
        String locStr = locationCombo.getValue();
        String statusStr = statusCombo.getValue();
        String timeStr = timeCombo.getValue();

        int capacity;
        try {
            capacity = Integer.parseInt(capText);
            if (capacity <= 0) {
                showFeedback("Capacity must be a positive integer.", false);
                return;
            }
        } catch (NumberFormatException e) {
            showFeedback("Invalid capacity number.", false);
            return;
        }

        TableType tableType = TableType.valueOf(locStr != null ? locStr : TableType.INDOOR.name());
        TableStatus status = TableStatus.valueOf(statusStr != null ? statusStr : TableStatus.AVAILABLE.name());
        String timeSlot = (timeStr != null && !timeStr.isEmpty()) ? timeStr : "12:00 PM";

        selected.setCapacity(capacity);
        selected.setTableType(tableType);
        selected.setTimeSlot(timeSlot);

        Staff staff = Session.getCurrentStaff();
        if (staff instanceof Admin) {
            ((Admin) staff).updateTableStatus(selected.getTableNumber(), status);
        } else {
            selected.setStatus(status);
        }

        refreshTable();
        showFeedback("Table #" + selected.getTableNumber() + " updated successfully.", true);
    }

    @FXML
    private void handleDelete() {
        Table selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showFeedback("Please select a table from the list to delete.", false);
            return;
        }

        int num = selected.getTableNumber();
        Staff staff = Session.getCurrentStaff();
        if (staff instanceof Admin) {
            ((Admin) staff).deleteTable(num);
        } else {
            Database.tables.remove(selected);
        }

        refreshTable();
        handleClearForm();
        showFeedback("Table #" + num + " deleted successfully.", true);
    }

    @FXML
    private void handleClearForm() {
        tableNumField.clear();
        tableNumField.setDisable(false);
        capacityField.clear();
        locationCombo.setValue(TableType.INDOOR.name());
        statusCombo.setValue(TableStatus.AVAILABLE.name());
        timeCombo.setValue("12:00 PM");
        tableView.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleBack() {
        try {
            App.switchScene("AdminDashboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showFeedback(String text, boolean success) {
        feedbackLabel.setStyle(success
                ? "-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-font-size: 12px;"
                : "-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 12px;");
        feedbackLabel.setText(text);
    }
}
