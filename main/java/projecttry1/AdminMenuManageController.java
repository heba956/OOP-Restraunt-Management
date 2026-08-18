package projecttry1;

import java.io.IOException;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class AdminMenuManageController {

    @FXML private TableView<MenuItem> menuTable;
    @FXML private TableColumn<MenuItem, String> colName;
    @FXML private TableColumn<MenuItem, Double> colPrice;
    @FXML private TableColumn<MenuItem, String> colCat;
    @FXML private TableColumn<MenuItem, Boolean> colAvail;

    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private TextArea descField;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private CheckBox availableCheck;
    @FXML private Label feedbackLabel;

    @FXML
    public void initialize() {
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        colPrice.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPrice()).asObject());
        colCat.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getCategory() != null ? data.getValue().getCategory().getCategoryName() : "General"));
        colAvail.setCellValueFactory(data -> new SimpleBooleanProperty(data.getValue().isAvailable()));

        refreshCategoryCombo();
        refreshTable();
    }

    private void refreshCategoryCombo() {
        categoryCombo.getItems().clear();
        for (MenuCategory cat : Database.getMenuCategories()) {
            categoryCombo.getItems().add(cat.getCategoryName());
        }
        if (!categoryCombo.getItems().isEmpty()) {
            categoryCombo.setValue(categoryCombo.getItems().get(0));
        }
    }

    private void refreshTable() {
        menuTable.setItems(FXCollections.observableArrayList(Database.getMenuItems()));
        menuTable.refresh();
    }

    @FXML
    private void handleSelectItem() {
        MenuItem selected = menuTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            nameField.setText(selected.getName());
            priceField.setText(String.valueOf(selected.getPrice()));
            descField.setText(selected.getDescription() != null ? selected.getDescription() : "");
            if (selected.getCategory() != null) {
                categoryCombo.setValue(selected.getCategory().getCategoryName());
            }
            availableCheck.setSelected(selected.isAvailable());
        }
    }

    @FXML
    private void handleAdd() {
        String name = nameField.getText().trim();
        String priceText = priceField.getText().trim();
        String desc = descField.getText().trim();
        String catName = categoryCombo.getValue();
        boolean available = availableCheck.isSelected();

        if (name.isEmpty()) {
            showFeedback("Please enter an item name.", false);
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
            if (price < 0) {
                showFeedback("Price must be a positive number.", false);
                return;
            }
        } catch (NumberFormatException e) {
            showFeedback("Invalid price format. Example: 85.0", false);
            return;
        }

        MenuCategory matchedCategory = findCategoryByName(catName);
        if (matchedCategory == null) {
            matchedCategory = new MenuCategory(catName != null ? catName : "General", "General Category");
            Database.menuCategories.add(matchedCategory);
        }

        MenuItem newItem = new MenuItem(name, price, desc, matchedCategory, available);

        Staff staff = Session.getCurrentStaff();
        if (staff instanceof Admin) {
            ((Admin) staff).createMenuItem(newItem);
        } else {
            Database.menuItems.add(newItem);
        }

        refreshTable();
        handleClearForm();
        showFeedback("Menu item '" + name + "' added successfully.", true);
    }

    @FXML
    private void handleUpdate() {
        MenuItem selected = menuTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showFeedback("Please select an item from the table to update.", false);
            return;
        }

        String name = nameField.getText().trim();
        String priceText = priceField.getText().trim();
        String desc = descField.getText().trim();
        String catName = categoryCombo.getValue();
        boolean available = availableCheck.isSelected();

        if (name.isEmpty()) {
            showFeedback("Please enter an item name.", false);
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
            if (price < 0) {
                showFeedback("Price must be positive.", false);
                return;
            }
        } catch (NumberFormatException e) {
            showFeedback("Invalid price format.", false);
            return;
        }

        MenuCategory matchedCategory = findCategoryByName(catName);

        selected.setName(name);
        selected.setDescription(desc);
        if (matchedCategory != null) {
            selected.setCategory(matchedCategory);
        }

        Staff staff = Session.getCurrentStaff();
        if (staff instanceof Admin) {
            Admin admin = (Admin) staff;
            admin.updateMenuItemPrice(selected, price);
            admin.updateMenuItemAvailability(selected, available);
        } else {
            selected.setPrice(price);
            selected.setAvailable(available);
        }

        refreshTable();
        showFeedback("Menu item '" + name + "' updated successfully.", true);
    }

    @FXML
    private void handleDelete() {
        MenuItem selected = menuTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showFeedback("Please select an item from the table to delete.", false);
            return;
        }

        Staff staff = Session.getCurrentStaff();
        if (staff instanceof Admin) {
            ((Admin) staff).deleteMenuItem(selected);
        } else {
            Database.menuItems.remove(selected);
        }

        refreshTable();
        handleClearForm();
        showFeedback("Menu item deleted successfully.", true);
    }

    @FXML
    private void handleClearForm() {
        nameField.clear();
        priceField.clear();
        descField.clear();
        availableCheck.setSelected(true);
        if (!categoryCombo.getItems().isEmpty()) {
            categoryCombo.setValue(categoryCombo.getItems().get(0));
        }
        menuTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleBack() {
        try {
            App.switchScene("AdminDashboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MenuCategory findCategoryByName(String name) {
        if (name == null) return null;
        for (MenuCategory cat : Database.getMenuCategories()) {
            if (cat.getCategoryName().equalsIgnoreCase(name)) {
                return cat;
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
