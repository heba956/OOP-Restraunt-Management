package projecttry1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import enums.OrderStatus;
import enums.TableStatus;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class WaiterTakeOrderController {

    @FXML private ComboBox<String> tableCombo;
    @FXML private ComboBox<String> customerCombo;

    @FXML private TableView<MenuItem> menuTable;
    @FXML private TableColumn<MenuItem, String> colItemName;
    @FXML private TableColumn<MenuItem, String> colItemCat;
    @FXML private TableColumn<MenuItem, Double> colItemPrice;

    @FXML private Spinner<Integer> qtySpinner;
    @FXML private TextField notesField;

    @FXML private TableView<OrderItem> draftTable;
    @FXML private TableColumn<OrderItem, String> colDraftItem;
    @FXML private TableColumn<OrderItem, Integer> colDraftQty;
    @FXML private TableColumn<OrderItem, String> colDraftNotes;
    @FXML private TableColumn<OrderItem, Double> colDraftSubtotal;

    @FXML private Label totalCostLabel;
    @FXML private Label feedbackLabel;

    private final ObservableList<OrderItem> draftItems = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Table choices
        tableCombo.getItems().clear();
        for (Table t : Database.getTables()) {
            tableCombo.getItems().add("Table #" + t.getTableNumber() + " (" + t.getCapacity() + " seats - " + t.getStatus() + ")");
        }
        if (!tableCombo.getItems().isEmpty()) {
            tableCombo.setValue(tableCombo.getItems().get(0));
        }

        // Customer choices
        customerCombo.getItems().clear();
        customerCombo.getItems().add("Walk-in Customer");
        for (Customer c : Database.getCustomers()) {
            customerCombo.getItems().add(c.getUsername() + " (" + c.getPhoneNumber() + ")");
        }
        customerCombo.setValue("Walk-in Customer");

        // Spinner setup
        qtySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 1));

        // Menu table setup
        colItemName.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getName()));
        colItemCat.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCategory() != null ? data.getValue().getCategory().getCategoryName() : "General"));
        colItemPrice.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getPrice()).asObject());

        List<MenuItem> availableItems = new ArrayList<>();
        for (MenuItem item : Database.getMenuItems()) {
            if (item.isAvailable()) {
                availableItems.add(item);
            }
        }
        menuTable.setItems(FXCollections.observableArrayList(availableItems));

        // Draft table setup
        colDraftItem.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getMenuItem().getName()));
        colDraftQty.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getQuantity()).asObject());
        colDraftNotes.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNotes() != null ? data.getValue().getNotes() : ""));
        colDraftSubtotal.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getSubtotal()).asObject());

        draftTable.setItems(draftItems);
        updateTotalCost();
    }

    @FXML
    private void handleAddToOrder() {
        MenuItem selected = menuTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showFeedback("Please select a menu item first.", false);
            return;
        }

        int qty = qtySpinner.getValue() != null ? qtySpinner.getValue() : 1;
        String notes = notesField.getText().trim();

        // Check if item already exists in draft list with same notes, increment quantity if so
        boolean merged = false;
        for (OrderItem item : draftItems) {
            if (item.getMenuItem().getName().equals(selected.getName()) &&
                ((item.getNotes() == null && notes.isEmpty()) || (item.getNotes() != null && item.getNotes().equals(notes)))) {
                item.setQuantity(item.getQuantity() + qty);
                merged = true;
                break;
            }
        }

        if (!merged) {
            draftItems.add(new OrderItem(selected, qty, notes));
        }

        draftTable.refresh();
        updateTotalCost();
        notesField.clear();
        qtySpinner.getValueFactory().setValue(1);
        showFeedback("Added " + qty + "x '" + selected.getName() + "' to draft order.", true);
    }

    @FXML
    private void handleRemoveDraftItem() {
        OrderItem selected = draftTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showFeedback("Please select an item from the draft order to remove.", false);
            return;
        }

        draftItems.remove(selected);
        draftTable.refresh();
        updateTotalCost();
        showFeedback("Item removed from order.", true);
    }

    @FXML
    private void handleClearDraft() {
        draftItems.clear();
        draftTable.refresh();
        updateTotalCost();
        showFeedback("Draft order cleared.", true);
    }

    @FXML
    private void handlePlaceOrder() {
        if (draftItems.isEmpty()) {
            showFeedback("Cannot place an empty order. Please add items first.", false);
            return;
        }

        String tableSelection = tableCombo.getValue();
        if (tableSelection == null || tableSelection.isEmpty()) {
            showFeedback("Please select a table for this order.", false);
            return;
        }

        Table targetTable = findSelectedTable(tableSelection);
        if (targetTable == null) {
            targetTable = !Database.getTables().isEmpty() ? Database.getTables().get(0) : null;
        }

        Customer targetCustomer = findSelectedCustomer(customerCombo.getValue());

        Order newOrder = new Order(targetTable, targetCustomer);
        for (OrderItem draftItem : draftItems) {
            newOrder.addItem(draftItem.getMenuItem(), draftItem.getQuantity(), draftItem.getNotes());
            Database.orderItems.add(draftItem);
        }

        // Add to orders using Waiter model if staff is waiter
        Staff staff = Session.getCurrentStaff();
        if (staff instanceof Waiter) {
            ((Waiter) staff).takeOrder(newOrder);
        } else {
            Database.orders.add(newOrder);
        }

        // Update table status to OCCUPIED
        if (targetTable != null) {
            targetTable.setStatus(TableStatus.OCCUPIED);
        }

        // Broadcast status over socket if customer is registered
        if (targetCustomer != null) {
            OrderStatusSocketPublisher.sendStatusUpdate(
                    "localhost", 8080, targetCustomer.getUsername(), newOrder.getOrderId(), "PLACED");
        }

        showFeedback("✅ Order #" + newOrder.getOrderId() + " placed successfully! (Total: $" + String.format("%.2f", newOrder.calculateTotalCost()) + ")", true);
        handleClearDraft();
    }

    @FXML
    private void handleBack() {
        try {
            App.switchScene("WaiterDashboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateTotalCost() {
        double total = 0.0;
        for (OrderItem item : draftItems) {
            total += item.getSubtotal();
        }
        totalCostLabel.setText(String.format("$%.2f", total));
    }

    private Table findSelectedTable(String selection) {
        if (selection == null) return null;
        for (Table t : Database.getTables()) {
            if (selection.startsWith("Table #" + t.getTableNumber() + " ")) {
                return t;
            }
        }
        return null;
    }

    private Customer findSelectedCustomer(String selection) {
        if (selection == null || selection.equals("Walk-in Customer")) {
            return null;
        }
        for (Customer c : Database.getCustomers()) {
            if (selection.startsWith(c.getUsername() + " ")) {
                return c;
            }
        }
        return null;
    }

    private void showFeedback(String text, boolean success) {
        feedbackLabel.setStyle(success
                ? "-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-font-size: 12px;"
                : "-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 12px;");
        feedbackLabel.setText(text);
    }
}
