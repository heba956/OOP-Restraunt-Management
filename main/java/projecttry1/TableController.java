package projecttry1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import gui.tasks.TableAvailabilityService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Duration;

public class TableController {

    @FXML private ComboBox<String> locationFilter;
    @FXML private ComboBox<Integer> capacityFilter;
    @FXML private ComboBox<String> timeFilter;
    @FXML private TableView<Table> tableView;
    @FXML private TableColumn<Table, Number> numberColumn;
    @FXML private TableColumn<Table, Number> capacityColumn;
    @FXML private TableColumn<Table, String> locationColumn;
    @FXML private TableColumn<Table, String> timeColumn;
    @FXML private TableColumn<Table, String> statusColumn;
    @FXML private Label feedbackLabel;

    private List<Table> allTables;
    private final TableAvailabilityService tableService = new TableAvailabilityService();

    @FXML
    public void initialize() {
        locationFilter.getItems().addAll("All", "INDOOR", "OUTDOOR", "VIP");
        capacityFilter.getItems().addAll(2, 4, 6, 8);
        timeFilter.getItems().addAll("All", "12:00 PM", "2:00 PM", "4:00 PM", "6:00 PM", "8:00 PM");

        locationFilter.setValue("All");
        timeFilter.setValue("All");

        numberColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getTableNumber()));
        capacityColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getCapacity()));
        locationColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTableType().toString()));
        statusColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus().toString()));
        timeColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getTimeSlot() != null ? data.getValue().getTimeSlot() : "12:00 PM"));

        startAvailabilityPolling();
    }

    private void startAvailabilityPolling() {
        tableService.setPeriod(Duration.seconds(3));
        tableService.setOnSucceeded(event -> {
            allTables = tableService.getValue();
            filterTables();
        });
        tableService.setOnFailed(event -> {
            if (feedbackLabel != null) feedbackLabel.setText("Couldn't refresh table availability.");
        });
        tableService.start();
    }

    private void showTables(List<Table> tables) {
        tableView.setItems(FXCollections.observableArrayList(tables));
        if (feedbackLabel != null) {
            feedbackLabel.setText(tables.size() + " table(s) found (live background polling active).");
        }
    }

    @FXML
    private void filterTables() {
        if (allTables == null) return;

        String selectedLocation = locationFilter.getValue();
        Integer selectedCapacity = capacityFilter.getValue();
        String selectedTime = timeFilter.getValue();

        List<Table> filteredTables = new ArrayList<>();

        for (Table table : allTables) {
            boolean locationMatch = selectedLocation == null || selectedLocation.equals("All") ||
                    table.getTableType().toString().equalsIgnoreCase(selectedLocation);

            boolean capacityMatch = selectedCapacity == null || table.getCapacity() == selectedCapacity;

            boolean timeMatch = selectedTime == null || selectedTime.equals("All") ||
                    (table.getTimeSlot() != null && table.getTimeSlot().equalsIgnoreCase(selectedTime));

            if (locationMatch && capacityMatch && timeMatch) {
                filteredTables.add(table);
            }
        }

        showTables(filteredTables);
    }

    @FXML
    private void handleBack() {
        if (tableService != null) {
            tableService.cancel();
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
