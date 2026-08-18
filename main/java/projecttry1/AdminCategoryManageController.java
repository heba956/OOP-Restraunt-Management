package projecttry1;

import java.io.IOException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class AdminCategoryManageController {

    @FXML private TableView<MenuCategory> catTable;
    @FXML private TableColumn<MenuCategory, String> colCatName;
    @FXML private TableColumn<MenuCategory, String> colCatDesc;

    @FXML private TextField catNameField;
    @FXML private TextField catDescField;
    @FXML private Label feedbackLabel;

    @FXML
    public void initialize() {
        colCatName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCategoryName()));
        colCatDesc.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescription()));

        refreshTable();
    }

    private void refreshTable() {
        catTable.setItems(FXCollections.observableArrayList(Database.getMenuCategories()));
        catTable.refresh();
    }

    @FXML
    private void handleSelectCategory() {
        MenuCategory selected = catTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            catNameField.setText(selected.getCategoryName());
            catDescField.setText(selected.getDescription() != null ? selected.getDescription() : "");
        }
    }

    @FXML
    private void handleAdd() {
        String name = catNameField.getText().trim();
        String desc = catDescField.getText().trim();

        if (name.isEmpty()) {
            showFeedback("Please enter a category name.", false);
            return;
        }

        for (MenuCategory c : Database.getMenuCategories()) {
            if (c.getCategoryName().equalsIgnoreCase(name)) {
                showFeedback("Category '" + name + "' already exists.", false);
                return;
            }
        }

        MenuCategory newCat = new MenuCategory(name, desc);

        Staff staff = Session.getCurrentStaff();
        if (staff instanceof Admin) {
            ((Admin) staff).createMenuCategory(newCat);
        } else {
            Database.menuCategories.add(newCat);
        }

        refreshTable();
        handleClearForm();
        showFeedback("Category '" + name + "' added successfully.", true);
    }

    @FXML
    private void handleUpdate() {
        MenuCategory selected = catTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showFeedback("Please select a category from the table to update.", false);
            return;
        }

        String name = catNameField.getText().trim();
        String desc = catDescField.getText().trim();

        if (name.isEmpty()) {
            showFeedback("Please enter a category name.", false);
            return;
        }

        selected.setCategoryName(name);
        selected.setDescription(desc);

        refreshTable();
        showFeedback("Category '" + name + "' updated successfully.", true);
    }

    @FXML
    private void handleDelete() {
        MenuCategory selected = catTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showFeedback("Please select a category from the table to delete.", false);
            return;
        }

        Staff staff = Session.getCurrentStaff();
        if (staff instanceof Admin) {
            ((Admin) staff).deleteMenuCategory(selected);
        } else {
            Database.menuCategories.remove(selected);
        }

        refreshTable();
        handleClearForm();
        showFeedback("Category deleted successfully.", true);
    }

    @FXML
    private void handleClearForm() {
        catNameField.clear();
        catDescField.clear();
        catTable.getSelectionModel().clearSelection();
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
