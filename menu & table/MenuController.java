

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gui.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.MenuCategory;
import model.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MenuController {

    @FXML
    private ComboBox<String> categoryFilter;

    @FXML
    private TableView<MenuItem> menuTable;

    @FXML
    private TableColumn<MenuItem, String> nameColumn;

    @FXML
    private TableColumn<MenuItem, String> descriptionColumn;

    @FXML
    private TableColumn<MenuItem, Number> priceColumn;

    @FXML
    private TableColumn<MenuItem, String> categoryColumn;

    @FXML
    private TableColumn<MenuItem, Boolean> availableColumn;

    @FXML
    private Label feedbackLabel;
    
    @FXML
    private Label totalLabel;
    
    private List<MenuItem> orderItems;
    
    private double orderTotal = 0.0;

    private List<MenuItem> allItems;

    @FXML
    public void initialize() {

        categoryFilter.getItems().addAll(
                "All",
                "Appetizers",
                "Main Course",
                "Desserts",
                "Drinks"
        );

        categoryFilter.setValue("All");

        nameColumn.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getName()
                )
        );

        descriptionColumn.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getDescription()
                )
        );

        priceColumn.setCellValueFactory(
                data -> new javafx.beans.property.SimpleDoubleProperty(
                        data.getValue().getPrice()
                )
        );

        categoryColumn.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getCategory().getCategoryName()
                )
        );

        availableColumn.setCellValueFactory(
                data -> new javafx.beans.property.SimpleBooleanProperty(
                        data.getValue().isAvailable()
                )
        );
        
        orderItems = new ArrayList<>();
        totalLabel.setText("0.0");

        loadMenu();
    }

    private void loadMenu() {

        allItems = new ArrayList<>();

        MenuCategory appetizers =
                new MenuCategory("Appetizers", "Starter dishes");

        MenuCategory mainCourse =
                new MenuCategory("Main Course", "Main dishes");

        MenuCategory desserts =
                new MenuCategory("Desserts", "Sweet dishes");

        MenuCategory drinks =
                new MenuCategory("Drinks", "Cold and hot drinks");

        allItems.add(new MenuItem(
                "Caesar Salad",
                120.0,
                "Fresh Caesar salad",
                appetizers,
                true
        ));

        allItems.add(new MenuItem(
                "Grilled Chicken",
                250.0,
                "Grilled chicken with vegetables",
                mainCourse,
                true
        ));

        allItems.add(new MenuItem(
                "Chocolate Cake",
                100.0,
                "Chocolate cake",
                desserts,
                true
        ));

        allItems.add(new MenuItem(
                "Fresh Juice",
                70.0,
                "Fresh orange juice",
                drinks,
                true
        ));

        showMenu(allItems);
    }

    private void showMenu(List<MenuItem> items) {

        menuTable.setItems(
                FXCollections.observableArrayList(items)
        );

        feedbackLabel.setText(
                items.size() + " item(s) found."
        );
    }

    @FXML
    private void filterMenu() {

        String selectedCategory =
                categoryFilter.getValue();

        List<MenuItem> filteredItems =
                new ArrayList<>();

        for (MenuItem item : allItems) {

            boolean categoryMatch =
                    selectedCategory.equals("All") ||
                    item.getCategory()
                        .getCategoryName()
                        .equals(selectedCategory);

            if (categoryMatch) {
                filteredItems.add(item);
            }
        }

        showMenu(filteredItems);
    }
    
    @FXML
private void addToOrder() {

    MenuItem selectedItem =
            menuTable.getSelectionModel().getSelectedItem();

    if (selectedItem == null) {

        feedbackLabel.setText(
                "Please select an item first."
        );

        return;
    }

    if (!selectedItem.isAvailable()) {

        feedbackLabel.setText(
                "This item is not available."
        );

        return;
    }

    orderItems.add(selectedItem);

    orderTotal += selectedItem.getPrice();

    totalLabel.setText(
            String.valueOf(orderTotal)
    );

    feedbackLabel.setText(
            selectedItem.getName() + " added to order."
    );
}
}