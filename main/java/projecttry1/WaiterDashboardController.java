package projecttry1;

import java.io.IOException;
import enums.OrderStatus;
import enums.TableType;
import enums.TableStatus;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class WaiterDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label feedbackLabel;

    @FXML private TableView<Order> ordersTable;
    @FXML private TableColumn<Order, Integer> colOrderId;
    @FXML private TableColumn<Order, String>  colCustomer;
    @FXML private TableColumn<Order, String>  colTable;
    @FXML private TableColumn<Order, String>  colStatus;
    @FXML private TableColumn<Order, Double>  colTotal;

    @FXML private TableView<Table> tablesTable;
    @FXML private TableColumn<Table, Integer> colTableNum;
    @FXML private TableColumn<Table, String>  colTableType;
    @FXML private TableColumn<Table, Integer> colTableCap;
    @FXML private TableColumn<Table, String>  colTableStatus;

    @FXML private ComboBox<String> statusComboBox;
    @FXML private Label tableFeedbackLabel;

    @FXML
    public void initialize() {
        Staff waiter = Session.getCurrentStaff();
        if (waiter != null) {
            welcomeLabel.setText("Welcome, " + waiter.getUsername() + "  ·  Role: " + waiter.getRole());
        }

        // Order status choices
        statusComboBox.setItems(FXCollections.observableArrayList(
                "PLACED", "PREPARING", "SERVED", "PAID"
        ));

        // Orders table
        colOrderId.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getOrderId()).asObject());
        colCustomer.setCellValueFactory(data -> {
            Customer c = data.getValue().getCustomer();
            return new SimpleStringProperty(c != null ? c.getUsername() : "—");
        });
        colTable.setCellValueFactory(data -> {
            Table t = data.getValue().getTable();
            return new SimpleStringProperty(t != null ? "Table " + t.getTableNumber() : "—");
        });
        colStatus.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStatus().name()));
        colTotal.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().calculateTotalCost()).asObject());

        ordersTable.setItems(FXCollections.observableArrayList(Database.orders));

        // Tables table
        colTableNum.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getTableNumber()).asObject());
        colTableType.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTableType() != null
                        ? data.getValue().getTableType().name() : "—"));
        colTableCap.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getCapacity()).asObject());
        colTableStatus.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStatus() != null
                        ? data.getValue().getStatus().name() : "—"));

        tablesTable.setItems(FXCollections.observableArrayList(Database.tables));
    }

    @FXML
    private void handleUpdateStatus() {
        Order selectedOrder = ordersTable.getSelectionModel().getSelectedItem();
        String selectedStatus = statusComboBox.getValue();

        if (selectedOrder == null) {
            showFeedback("Please select an order first.", false);
            return;
        }
        if (selectedStatus == null) {
            showFeedback("Please choose a new status.", false);
            return;
        }

        try {
            OrderStatus newStatus = OrderStatus.valueOf(selectedStatus);
            Staff staff = Session.getCurrentStaff();
            if (staff instanceof Waiter) {
                Waiter waiter = (Waiter) staff;
                waiter.updateOrderStatus(selectedOrder, newStatus);
            } else {
                // fallback for other staff
                selectedOrder.updateStatus(newStatus);
            }
            // Refresh the orders table to show the new status
            ordersTable.refresh();
            showFeedback("Order #" + selectedOrder.getOrderId() + " → " + selectedStatus, true);
        } catch (IllegalArgumentException e) {
            showFeedback("Invalid status selected.", false);
        }
    }

    @FXML
    private void handleAssignTable() {
        Table selectedTable = tablesTable.getSelectionModel().getSelectedItem();
        if (selectedTable == null) {
            showTableFeedback("Please select a table first.", false);
            return;
        }
        Staff staff = Session.getCurrentStaff();
        if (staff instanceof Waiter) {
            Waiter waiter = (Waiter) staff;
            waiter.assignTable(selectedTable);
            waiter.manageSeating(selectedTable);
        } else {
            selectedTable.setStatus(TableStatus.OCCUPIED);
        }
        tablesTable.refresh();
        showTableFeedback("Table #" + selectedTable.getTableNumber() + " → OCCUPIED", true);
    }

    @FXML
    private void handleUnassignTable() {
        Table selectedTable = tablesTable.getSelectionModel().getSelectedItem();
        if (selectedTable == null) {
            showTableFeedback("Please select a table first.", false);
            return;
        }
        Staff staff = Session.getCurrentStaff();
        if (staff instanceof Waiter) {
            Waiter waiter = (Waiter) staff;
            waiter.assignTable(selectedTable);   // ensure it's in assigned list
            waiter.checkOutTable(selectedTable); // sets AVAILABLE + removes from list
        } else {
            selectedTable.setStatus(TableStatus.AVAILABLE);
        }
        tablesTable.refresh();
        showTableFeedback("Table #" + selectedTable.getTableNumber() + " → AVAILABLE", true);
    }

    @FXML
    private void handleTakeOrder() {
        try {
            App.switchScene("WaiterTakeOrder");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        Session.setCurrentStaff(null);
        try {
            App.switchScene("StaffLogin");
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

    private void showTableFeedback(String text, boolean success) {
        tableFeedbackLabel.setStyle(success
                ? "-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-font-size: 12px;"
                : "-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 12px;");
        tableFeedbackLabel.setText(text);
    }
}
