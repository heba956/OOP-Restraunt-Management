package projecttry1;

import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class AdminDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label statCustomers;
    @FXML private Label statOrders;
    @FXML private Label statMenuItems;
    @FXML private Label statReservations;

    @FXML private TableView<Order> ordersTable;
    @FXML private TableColumn<Order, Integer> colOrderId;
    @FXML private TableColumn<Order, String>  colOrderCustomer;
    @FXML private TableColumn<Order, String>  colOrderTable;
    @FXML private TableColumn<Order, String>  colOrderStatus;
    @FXML private TableColumn<Order, Double>  colOrderTotal;

    @FXML
    public void initialize() {
        Staff admin = Session.getCurrentStaff();
        if (admin != null) {
            welcomeLabel.setText("Welcome, " + admin.getUsername() + "  ·  Role: " + admin.getRole());
        }

        statCustomers.setText(String.valueOf(Database.customers.size()));
        statOrders.setText(String.valueOf(Database.orders.size()));
        statMenuItems.setText(String.valueOf(Database.menuItems.size()));
        statReservations.setText(String.valueOf(Database.reservations.size()));

        colOrderId.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getOrderId()).asObject());
        colOrderCustomer.setCellValueFactory(data -> {
            Customer c = data.getValue().getCustomer();
            return new SimpleStringProperty(c != null ? c.getUsername() : "Walk-in");
        });
        colOrderTable.setCellValueFactory(data -> {
            Table t = data.getValue().getTable();
            return new SimpleStringProperty(t != null ? "Table " + t.getTableNumber() : "—");
        });
        colOrderStatus.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStatus().name()));
        colOrderTotal.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().calculateTotalCost()).asObject());

        ordersTable.setItems(FXCollections.observableArrayList(Database.orders));
    }

    @FXML
    private void handleViewCustomers() {
        try {
            App.switchScene("AdminViewCustomers");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewTables() {
        try {
            App.switchScene("TableView");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewReservations() {
        try {
            App.switchScene("AdminViewReservations");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleManageMenu() {
        try {
            App.switchScene("AdminMenuManage");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleManageCategories() {
        try {
            App.switchScene("AdminCategoryManage");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleManageTables() {
        try {
            App.switchScene("AdminTableManage");
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
}
