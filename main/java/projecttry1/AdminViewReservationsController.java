package projecttry1;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import enums.ReservationStatus;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class AdminViewReservationsController {

    @FXML private Label totalResLabel;
    @FXML private Label activeResLabel;
    @FXML private Label cancelledResLabel;

    @FXML private TableView<Reservation> reservationTable;
    @FXML private TableColumn<Reservation, String> colCustomer;
    @FXML private TableColumn<Reservation, Integer> colTableNum;
    @FXML private TableColumn<Reservation, String> colDateTime;
    @FXML private TableColumn<Reservation, Integer> colPartySize;
    @FXML private TableColumn<Reservation, String> colStatus;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    public void initialize() {
        colCustomer.setCellValueFactory(data -> {
            Customer c = data.getValue().getCustomer();
            return new SimpleStringProperty(c != null ? c.getUsername() : "—");
        });
        colTableNum.setCellValueFactory(data -> {
            Table t = data.getValue().getTable();
            return new SimpleIntegerProperty(t != null ? t.getTableNumber() : 0).asObject();
        });
        colDateTime.setCellValueFactory(data -> {
            var dt = data.getValue().getDate();
            return new SimpleStringProperty(dt != null ? dt.format(FORMATTER) : "—");
        });
        colPartySize.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getPartySize()).asObject());
        colStatus.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStatus() != null ? data.getValue().getStatus().name() : "—"));

        var reservations = Database.getReservations();
        reservationTable.setItems(FXCollections.observableArrayList(reservations));

        totalResLabel.setText(String.valueOf(reservations.size()));

        int active = 0;
        int cancelled = 0;
        for (Reservation r : reservations) {
            if (r.getStatus() == ReservationStatus.ACTIVE) {
                active++;
            } else if (r.getStatus() == ReservationStatus.CANCELLED) {
                cancelled++;
            }
        }
        activeResLabel.setText(String.valueOf(active));
        cancelledResLabel.setText(String.valueOf(cancelled));
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
