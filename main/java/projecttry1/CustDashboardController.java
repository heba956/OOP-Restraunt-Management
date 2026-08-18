package projecttry1;

import java.io.IOException;
import enums.OrderStatus;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class CustDashboardController {

    @FXML private Label avatarLabel;
    @FXML private Label usernameLabel;
    @FXML private Label loyaltyLabel;
    @FXML private Label balanceLabel;

    @FXML private Label orderIdLabel;
    @FXML private Label stepPlaced;
    @FXML private Label stepPreparing;
    @FXML private Label stepServed;
    @FXML private Label statusDetailLabel;


    private Customer customer;
    private Order activeOrder;
    private OrderStatusClient socketClient;

    @FXML
    public void initialize() {
        customer = Session.getCurrentCustomer();

        if (customer == null && !Database.customers.isEmpty()) {
            customer = Database.customers.get(0);
            Session.setCurrentCustomer(customer);
        }

        if (customer != null) {
            String username = customer.getUsername();
            String initial = (username != null && !username.isEmpty()) ? username.substring(0, 1).toUpperCase() : "U";
            avatarLabel.setText(initial);
            usernameLabel.setText(username);
            loyaltyLabel.setText(customer.getLoyaltyPoints() + " pts");
            balanceLabel.setText(String.format("$%.2f", customer.getBalance()));

            loadActiveOrder();                 refreshStatusFromDB();  
            initSocketClient();     
        }
    }

    private void loadActiveOrder() {
        activeOrder = null;
        if (customer != null) {
            // First search for an active UNPAID order
            for (int i = Database.orders.size() - 1; i >= 0; i--) {
                Order o = Database.orders.get(i);
                if (o.getCustomer() != null && o.getCustomer().getUsername().equals(customer.getUsername()) && o.getStatus() != OrderStatus.PAID) {
                    activeOrder = o;
                    break;
                }
            }
            // If no unpaid order exists, fallback to the most recent order (e.g. recently paid)
            if (activeOrder == null) {
                for (int i = Database.orders.size() - 1; i >= 0; i--) {
                    Order o = Database.orders.get(i);
                    if (o.getCustomer() != null && o.getCustomer().getUsername().equals(customer.getUsername())) {
                        activeOrder = o;
                        break;
                    }
                }
            }
        }

        if (activeOrder != null) {
            if (orderIdLabel != null) orderIdLabel.setText("Order #" + activeOrder.getOrderId());
            updateStatusUI(activeOrder.getStatus());
        } else {
            if (orderIdLabel != null) orderIdLabel.setText("No Active Order");
            if (statusDetailLabel != null) statusDetailLabel.setText("Status: No active order placed yet.");
        }
    }

    
    private void refreshStatusFromDB() {
        if (activeOrder == null) return;
        
        for (Order o : Database.orders) {
            if (o.getOrderId() == activeOrder.getOrderId()) {
                activeOrder = o;
                updateStatusUI(o.getStatus());
               
                if (o.getStatus() == OrderStatus.PAID && customer != null && balanceLabel != null) {
                    balanceLabel.setText(String.format("$%.2f", customer.getBalance()));
                }
                return;
            }
        }
    }


    private void initSocketClient() {
        String username = Session.getCurrentCustomer().getUsername();

        socketClient = new OrderStatusClient("localhost", 8080, username, (orderIdStr, newStatusStr) -> {

            loadActiveOrder();
            if (activeOrder != null && String.valueOf(activeOrder.getOrderId()).equals(orderIdStr)) {
                try {
                    OrderStatus newStatus = OrderStatus.valueOf(newStatusStr);
                    activeOrder.setStatus(newStatus);
                    updateStatusUI(newStatus);
                } catch (IllegalArgumentException ignored) {}
            }
        });
        new Thread(socketClient, "OrderStatusClientThread").start();
    }

   
   //there was a func here

    private void updateStatusUI(OrderStatus status) {
        if (stepPlaced == null || stepPreparing == null || stepServed == null || statusDetailLabel == null) return;

        String inactiveStyle = "-fx-padding: 5 10; -fx-background-radius: 12; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-color: #ecf0f1; -fx-text-fill: #7f8c8d;";
        String activeBlue = "-fx-padding: 5 10; -fx-background-radius: 12; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-color: #3498db; -fx-text-fill: white;";
        String activeOrange = "-fx-padding: 5 10; -fx-background-radius: 12; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-color: #e67e22; -fx-text-fill: white;";
        String activeGreen = "-fx-padding: 5 10; -fx-background-radius: 12; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-color: #27ae60; -fx-text-fill: white;";

        switch (status) {
            case PLACED:
                stepPlaced.setStyle(activeBlue);
                stepPreparing.setStyle(inactiveStyle);
                stepServed.setStyle(inactiveStyle);
                statusDetailLabel.setText("Status: PLACED - Kitchen has received your order.");
                break;
            case PREPARING:
                stepPlaced.setStyle(inactiveStyle);
                stepPreparing.setStyle(activeOrange);
                stepServed.setStyle(inactiveStyle);
                statusDetailLabel.setText("Status: PREPARING - Chef is cooking your meal!");
                break;
            case SERVED:
                stepPlaced.setStyle(inactiveStyle);
                stepPreparing.setStyle(inactiveStyle);
                stepServed.setStyle(activeGreen);
                statusDetailLabel.setText("Status: SERVED - Enjoy your meal! Bon appétit! 🍽️");
                break;
            case PAID:
                stepPlaced.setStyle(inactiveStyle);
                stepPreparing.setStyle(inactiveStyle);
                stepServed.setStyle(activeGreen);
                statusDetailLabel.setText("Status: PAID - Invoice settled. Thank you! 💳");
                if (customer != null && balanceLabel != null) {
                    balanceLabel.setText(String.format("$%.2f", customer.getBalance()));
                }
                break;
        }
    }

    private void stopSocketClient() {
        if (socketClient != null) {
            socketClient.stop();
        }
    }

    @FXML
    private void handleViewMenu(ActionEvent event) {
        stopSocketClient();
        try {
            App.switchScene("MenuView");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewTables(ActionEvent event) {
        stopSocketClient();
        try {
            App.switchScene("TableView");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleMakeReservation(ActionEvent event) {
        stopSocketClient();
        try {
            App.switchScene("MakeReservation");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCheckout(ActionEvent event) {
        stopSocketClient();
        try {
            App.switchScene("Checkout");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        stopSocketClient();
        Session.setCurrentCustomer(null);
        try {
            App.switchScene("Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
