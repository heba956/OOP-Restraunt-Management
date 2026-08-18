package projecttry1;

import java.io.IOException;
import java.time.LocalDate;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class AdminViewCustomersController {

    @FXML private Label totalCustomersLabel;
    @FXML private Label totalBalanceLabel;
    @FXML private Label totalLoyaltyLabel;

    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, String> colUsername;
    @FXML private TableColumn<Customer, String> colPhone;
    @FXML private TableColumn<Customer, Double> colBalance;
    @FXML private TableColumn<Customer, Integer> colLoyalty;
    @FXML private TableColumn<Customer, String> colDietary;
    @FXML private TableColumn<Customer, LocalDate> colDOB;

    @FXML
    public void initialize() {
        colUsername.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getUsername()));
        colPhone.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getPhoneNumber() != null ? data.getValue().getPhoneNumber() : "—"));
        colBalance.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getBalance()).asObject());
        colLoyalty.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getLoyaltyPoints()).asObject());
        colDietary.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDietaryPreferences() != null ? data.getValue().getDietaryPreferences() : "None"));
        colDOB.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getDateOfBirth()));

        var customers = Database.getCustomers();
        customerTable.setItems(FXCollections.observableArrayList(customers));

        totalCustomersLabel.setText(String.valueOf(customers.size()));

        double totalBal = 0;
        int totalPts = 0;
        for (Customer c : customers) {
            totalBal += c.getBalance();
            totalPts += c.getLoyaltyPoints();
        }
        totalBalanceLabel.setText(String.format("$%.2f", totalBal));
        totalLoyaltyLabel.setText(totalPts + " pts");
    }

    @FXML
    private void handleBack() {
        try {
            App.switchScene("AdminDashboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
