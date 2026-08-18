package projecttry1;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import gui.tasks.MenuLoadTask;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MenuController {

    @FXML private ComboBox<String> categoryFilter;
    @FXML private TableView<MenuItem> menuTable;
    @FXML private TableColumn<MenuItem, MenuItem> imageColumn;
    @FXML private TableColumn<MenuItem, String> nameColumn;
    @FXML private TableColumn<MenuItem, String> descriptionColumn;
    @FXML private TableColumn<MenuItem, Number> priceColumn;
    @FXML private TableColumn<MenuItem, String> categoryColumn;
    @FXML private TableColumn<MenuItem, Boolean> availableColumn;
    @FXML private Label feedbackLabel;
    @FXML private Label totalLabel;

    private static final Map<String, Image> CATEGORY_IMAGES = new HashMap<>();
    static {
        String[] categories = {"appetizers", "salads", "soups", "main", "desserts", "beverages"};
        for (String cat : categories) {
            try {
                InputStream is = MenuController.class.getResourceAsStream("/projecttry1/img_" + cat + ".png");
                if (is != null) CATEGORY_IMAGES.put(cat, new Image(is, 80, 70, true, true));
            } catch (Exception ignored) {}
        }
    }

    private List<MenuItem> currentOrderItems = new ArrayList<>();
    private double orderTotal = 0.0;
    private List<MenuItem> allItems;

    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "MenuLoadThread");
        t.setDaemon(true);
        return t;
    });

    @FXML
    public void initialize() {
        categoryFilter.getItems().add("All");
        for (MenuCategory cat : Database.getMenuCategories()) {
            categoryFilter.getItems().add(cat.getCategoryName());
        }
        categoryFilter.setValue("All");

        // Image column
        imageColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue()));
        imageColumn.setCellFactory(col -> new TableCell<MenuItem, MenuItem>() {
            private final ImageView iv = new ImageView();
            { iv.setFitWidth(80); iv.setFitHeight(68); iv.setPreserveRatio(true); setGraphic(iv); }
            @Override
            protected void updateItem(MenuItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { iv.setImage(null); return; }
                String name = item.getName() != null ? item.getName().toLowerCase() : "";
                String cat = item.getCategory() != null ? item.getCategory().getCategoryName().toLowerCase() : "";
                String key = name.contains("salad") ? "salads"
                        : cat.contains("appetizer") ? "appetizers"
                        : cat.contains("salad") ? "salads"
                        : cat.contains("soup") ? "soups"
                        : cat.contains("main") ? "main"
                        : cat.contains("dessert") ? "desserts"
                        : cat.contains("bever") ? "beverages" : "main";
                iv.setImage(CATEGORY_IMAGES.getOrDefault(key, CATEGORY_IMAGES.get("main")));
            }
        });

        nameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        descriptionColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDescription()));
        priceColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getPrice()));
        categoryColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getCategory() != null ? data.getValue().getCategory().getCategoryName() : "General"));
        availableColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleBooleanProperty(data.getValue().isAvailable()));

        // Taller rows for the images
        menuTable.setRowFactory(tv -> { TableRow<MenuItem> row = new TableRow<>(); row.setPrefHeight(75); return row; });

        totalLabel.setText("$0.00");
        Customer customer = Session.getCurrentCustomer();
        if (customer != null) {
            for (int i = Database.orders.size() - 1; i >= 0; i--) {
                Order o = Database.orders.get(i);
                if (o.getCustomer() != null && o.getCustomer().getUsername().equals(customer.getUsername()) && o.getStatus() != enums.OrderStatus.PAID) {
                    orderTotal = o.calculateTotalCost();
                    totalLabel.setText(String.format("$%.2f", orderTotal));
                    break;
                }
            }
        }
        loadMenuAsync();
    }


    private void loadMenuAsync() {
        if (feedbackLabel != null) feedbackLabel.setText("Loading menu on background thread...");

        MenuLoadTask task = new MenuLoadTask();
        task.setOnSucceeded(e -> {
            allItems = task.getValue();
            showMenu(allItems);
        });
        task.setOnFailed(e -> {
            if (feedbackLabel != null) feedbackLabel.setText("Failed to load menu asynchronously.");
        });

        executor.submit(task);
    }

    private void showMenu(List<MenuItem> items) {
        if (items != null) {
            menuTable.setItems(FXCollections.observableArrayList(items));
            if (feedbackLabel != null) {
                feedbackLabel.setText(items.size() + " item(s) found (loaded asynchronously).");
            }
        }
    }

    @FXML
    private void filterMenu() {
        if (allItems == null) return;
        String selectedCategory = categoryFilter.getValue();
        if (selectedCategory == null || selectedCategory.equals("All")) {
            showMenu(allItems);
            return;
        }

        List<MenuItem> filteredItems = new ArrayList<>();
        for (MenuItem item : allItems) {
            if (item.getCategory() != null && item.getCategory().getCategoryName().equalsIgnoreCase(selectedCategory)) {
                filteredItems.add(item);
            }
        }
        showMenu(filteredItems);
    }

    @FXML
    private void addToOrder() {
        MenuItem selectedItem = menuTable.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            if (feedbackLabel != null) feedbackLabel.setText("Please select an item first.");
            return;
        }

        if (!selectedItem.isAvailable()) {
            if (feedbackLabel != null) feedbackLabel.setText("This item is currently unavailable.");
            return;
        }

        Customer customer = Session.getCurrentCustomer();
        if (customer != null && !Database.tables.isEmpty()) {
            Table defaultTable = Database.tables.get(0);
            Order activeOrder = null;

            // Look for an existing UNPAID order for this customer (newest first)
            for (int i = Database.orders.size() - 1; i >= 0; i--) {
                Order o = Database.orders.get(i);
                if (o.getCustomer() != null && o.getCustomer().getUsername().equals(customer.getUsername()) && o.getStatus() != enums.OrderStatus.PAID) {
                    activeOrder = o;
                    break;
                }
            }

            // If no active unpaid order exists, create a brand new order
            if (activeOrder == null) {
                activeOrder = new Order(defaultTable, customer);
                Database.orders.add(activeOrder);
            }

            activeOrder.addItem(selectedItem, 1, "");
            currentOrderItems.add(selectedItem);
            Database.orderItems.add(new OrderItem(selectedItem, 1, ""));

            orderTotal = activeOrder.calculateTotalCost();
            totalLabel.setText(String.format("$%.2f", orderTotal));

            final int id = activeOrder.getOrderId();
            final String username = customer.getUsername();
            activeOrder.setStatus(enums.OrderStatus.PLACED);
            OrderStatusSocketPublisher.sendStatusUpdate("localhost", 8080, username, id, "PLACED");
        }

        if (feedbackLabel != null) {
            feedbackLabel.setText("Added '" + selectedItem.getName() + "' to order.");
        }
    }
    @FXML
    private void handleBack() {
        if (executor != null) {
            executor.shutdownNow();
        }
        try {
            if (Session.getCurrentStaff() != null) {
                App.switchScene("AdminDashboard");
            } else {
                App.switchScene("CustDashboard");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
