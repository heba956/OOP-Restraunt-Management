package gui.controllers;

import enums.TableLocation;
import enums.TableStatus;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.RestaurantTable;

import java.util.ArrayList;
import java.util.List;

public class TableController {

    @FXML
    private ComboBox<String> locationFilter;

    @FXML
    private ComboBox<Integer> capacityFilter;

    @FXML
    private ComboBox<String> timeFilter;

    @FXML
    private TableView<RestaurantTable> tableView;

    @FXML
    private TableColumn<RestaurantTable, Number> numberColumn;

    @FXML
    private TableColumn<RestaurantTable, Number> capacityColumn;

    @FXML
    private TableColumn<RestaurantTable, String> locationColumn;
    
    @FXML
private TableColumn<RestaurantTable, String> timeColumn;
    
    @FXML
    private TableColumn<RestaurantTable, String> statusColumn;

    @FXML
    private Label feedbackLabel;

    private List<RestaurantTable> allTables;

    @FXML
    public void initialize() {

        locationFilter.getItems().addAll(
                "All", "INDOOR", "OUTDOOR", "VIP"
        );

        capacityFilter.getItems().addAll(
                2, 4, 6, 8
        );

        timeFilter.getItems().addAll(
                "All",
                "12:00 PM",
                "2:00 PM",
                "4:00 PM",
                "6:00 PM",
                "8:00 PM"
        );

        locationFilter.setValue("All");
        timeFilter.setValue("All");

        numberColumn.setCellValueFactory(
                data -> new javafx.beans.property.SimpleIntegerProperty(
                        data.getValue().getTableNumber()
                )
        );

        capacityColumn.setCellValueFactory(
                data -> new javafx.beans.property.SimpleIntegerProperty(
                        data.getValue().getCapacity()
                )
        );

        locationColumn.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getLocation().toString()
                )
        );

        statusColumn.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getStatus().toString()
                )
        );
        
        timeColumn.setCellValueFactory(
        data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getTimeSlot()
        )
        );

        loadTables();
    }

    private void loadTables() {

    allTables = new ArrayList<>();

    allTables.add(new RestaurantTable(
            1, 2, TableLocation.INDOOR,
            TableStatus.AVAILABLE, "12:00 PM"));

    allTables.add(new RestaurantTable(
            2, 4, TableLocation.INDOOR,
            TableStatus.RESERVED, "2:00 PM"));

    allTables.add(new RestaurantTable(
            3, 6, TableLocation.OUTDOOR,
            TableStatus.AVAILABLE, "4:00 PM"));

    allTables.add(new RestaurantTable(
            4, 4, TableLocation.VIP,
            TableStatus.AVAILABLE, "6:00 PM"));

    allTables.add(new RestaurantTable(
            5, 8, TableLocation.VIP,
            TableStatus.OCCUPIED, "8:00 PM"));

    showTables(allTables);
} 

    private void showTables(List<RestaurantTable> tables) {

        tableView.setItems(
                FXCollections.observableArrayList(tables)
        );

        feedbackLabel.setText(
                tables.size() + " table(s) found."
        );
    }

    @FXML
    private void filterTables() {

        String selectedLocation = locationFilter.getValue();
        Integer selectedCapacity = capacityFilter.getValue();
        String selectedTime = timeFilter.getValue();

        List<RestaurantTable> filteredTables = new ArrayList<>();

        for (RestaurantTable table : allTables) {

            boolean locationMatch =
                    selectedLocation.equals("All") ||
                    table.getLocation().toString().equals(selectedLocation);

            boolean capacityMatch =
                    selectedCapacity == null ||
                    table.getCapacity() == selectedCapacity;
            
            boolean timeMatch =
        selectedTime.equals("All") ||
        table.getTimeSlot().equals(selectedTime);

            if (locationMatch && capacityMatch && timeMatch) {
                filteredTables.add(table);
            }
        }

        showTables(filteredTables);
    }
}