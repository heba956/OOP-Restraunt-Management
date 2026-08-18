package projecttry1;

import java.io.IOException;
import enums.OrderStatus;
import enums.PaymentMethod;
import enums.PaymentStatus;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

public class CheckoutController {

    @FXML private Label invoiceIdLabel;
    @FXML private Label subtotalLabel;
    @FXML private Label taxLabel;
    @FXML private Label serviceChargeLabel;
    @FXML private Label totalLabel;
    @FXML private Label paymentStatusLabel;
    @FXML private RadioButton cashRadio;
    @FXML private RadioButton creditCardRadio;
    @FXML private RadioButton onlineRadio;

    private ToggleGroup paymentGroup;
    private Invoice invoice;

    @FXML
    public void initialize() {
        paymentGroup = new ToggleGroup();
        cashRadio.setToggleGroup(paymentGroup);
        creditCardRadio.setToggleGroup(paymentGroup);
        onlineRadio.setToggleGroup(paymentGroup);
        cashRadio.setSelected(true);

        Customer currentCustomer = Session.getCurrentCustomer();

        Invoice customerInvoice = null;
        if (currentCustomer != null) {
            // 1. Find the latest active UNPAID order for this customer
            Order activeOrder = null;
            for (int i = Database.orders.size() - 1; i >= 0; i--) {
                Order o = Database.orders.get(i);
                if (o.getCustomer() != null
                        && o.getCustomer().getUsername().equals(currentCustomer.getUsername())
                        && o.getStatus() != OrderStatus.PAID) {
                    activeOrder = o;
                    break;
                }
            }

            if (activeOrder != null) {
                // 2. Find any pending invoice already referencing this exact active order
                for (int i = Database.invoices.size() - 1; i >= 0; i--) {
                    Invoice inv = Database.invoices.get(i);
                    if (inv.getOrder() == activeOrder && inv.getPaymentStatus() == PaymentStatus.PENDING) {
                        customerInvoice = inv;
                        break;
                    }
                }

                // 3. If no pending invoice exists for this active order, create one
                if (customerInvoice == null) {
                    customerInvoice = new Invoice(Database.invoices.size() + 1, activeOrder);
                    Database.invoices.add(customerInvoice);
                }

                // 4. Always re-calculate the invoice to ensure single source of truth directly from activeOrder
                customerInvoice.calculateInvoice();
            }
        }

        setInvoice(customerInvoice);
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;

        if (invoice != null && invoice.getOrder() != null) {
            invoice.calculateInvoice();
            invoiceIdLabel.setText("Invoice #" + invoice.getInvoiceId() + " (Order #" + invoice.getOrder().getOrderId() + ")");
            subtotalLabel.setText(String.format("$%.2f", invoice.getSubtotal()));
            taxLabel.setText(String.format("$%.2f", invoice.getTaxAmount()));
            serviceChargeLabel.setText(String.format("$%.2f", invoice.getServiceCharge()));
            totalLabel.setText(String.format("$%.2f", invoice.getTotalAmount()));
            paymentStatusLabel.setText("Payment Status: " + invoice.getPaymentStatus());
        } else {
            invoiceIdLabel.setText("No Invoice");
            subtotalLabel.setText("-");
            taxLabel.setText("-");
            serviceChargeLabel.setText("-");
            totalLabel.setText("-");
            paymentStatusLabel.setText("No active order to check out.");
        }
    }

    @FXML
    private void confirmPayment() {
        if (invoice == null || invoice.getOrder() == null) {
            showAlert(Alert.AlertType.ERROR, "No Order",
                    "You have no active order to check out. Please add items from the menu first.");
            return;
        }

        // Fresh recalculation ensures 100% synchronization with order items
        invoice.calculateInvoice();
        double totalToCharge = invoice.getTotalAmount();

        if (invoice.getPaymentStatus() == PaymentStatus.PAID) {
            showAlert(Alert.AlertType.INFORMATION, "Already Paid",
                    "This invoice has already been paid.");
            handleBack();
            return;
        }

        Customer customer = Session.getCurrentCustomer();
        if (customer != null && customer.getBalance() < totalToCharge) {
            showAlert(Alert.AlertType.ERROR, "Insufficient Balance",
                    String.format("Your balance is $%.2f but the total is $%.2f.%n"
                            + "Please top up your account before checking out.",
                            customer.getBalance(), totalToCharge));
            return;
        }

        PaymentMethod selectedMethod = PaymentMethod.CASH;
        if (creditCardRadio.isSelected()) {
            selectedMethod = PaymentMethod.CREDIT_CARD;
        } else if (onlineRadio.isSelected()) {
            selectedMethod = PaymentMethod.ONLINE;
        }

        boolean paid = true;
        if (customer != null) {
            paid = customer.checkoutAndPay(totalToCharge);
        }

        if (paid) {
            invoice.payInvoice(selectedMethod);
            paymentStatusLabel.setText("Payment Status: " + invoice.getPaymentStatus());

            if (invoice.getOrder() != null) {
                invoice.getOrder().setStatus(OrderStatus.PAID);
                if (customer != null) {
                    OrderStatusSocketPublisher.sendStatusUpdate(
                            "localhost", 8080, customer.getUsername(), invoice.getOrder().getOrderId(), "PAID");
                }
            }

            showAlert(Alert.AlertType.INFORMATION, "Payment Successful",
                    "Invoice #" + invoice.getInvoiceId() + " paid!\n"
                    + "Method: " + selectedMethod
                    + "\nTotal Charged: " + String.format("$%.2f", totalToCharge)
                    + "\nRemaining Balance: " + String.format("$%.2f", (customer != null ? customer.getBalance() : 0.0))
                    + "\nLoyalty points earned: " + (int)(totalToCharge / 10));

            handleBack();
        } else {
            showAlert(Alert.AlertType.ERROR, "Payment Failed", "Could not process payment.");
        }
    }

    @FXML
    private void handleBack() {
        try {
            App.switchScene("CustDashboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
