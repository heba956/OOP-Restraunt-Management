package projecttry1;

import java.io.IOException;
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

        // Try to find an existing invoice for the current customer's order
        Invoice customerInvoice = null;
        if (currentCustomer != null) {
            // Walk invoices in reverse to get the most recent one
            for (int i = Database.invoices.size() - 1; i >= 0; i--) {
                Invoice inv = Database.invoices.get(i);
                if (inv.getOrder().getCustomer() != null &&
                        inv.getOrder().getCustomer().getUsername()
                                .equals(currentCustomer.getUsername())) {
                    customerInvoice = inv;
                    break;
                }
            }
            // No invoice yet — find the customer's order and create one
            if (customerInvoice == null) {
                for (int i = Database.orders.size() - 1; i >= 0; i--) {
                    Order o = Database.orders.get(i);
                    if (o.getCustomer() != null &&
                            o.getCustomer().getUsername().equals(currentCustomer.getUsername())) {
                        customerInvoice = new Invoice(Database.invoices.size() + 1, o);
                        Database.invoices.add(customerInvoice);
                        break;
                    }
                }
            }
        }

        // Fall back: use any existing invoice, or create a demo one
        if (customerInvoice == null && !Database.invoices.isEmpty()) {
            customerInvoice = Database.invoices.get(Database.invoices.size() - 1);
        }

        if (customerInvoice == null && !Database.orders.isEmpty()) {
            customerInvoice = new Invoice(1, Database.orders.get(0));
            Database.invoices.add(customerInvoice);
        }

        if (customerInvoice == null) {
            Customer c = currentCustomer != null ? currentCustomer :
                    (!Database.customers.isEmpty() ? Database.customers.get(0) : null);
            Table t = !Database.tables.isEmpty() ? Database.tables.get(0) :
                    new Table(1, 4, enums.TableType.INDOOR, enums.TableStatus.OCCUPIED);
            Order defaultOrder = new Order(t, c);
            if (!Database.menuItems.isEmpty()) {
                defaultOrder.addItem(Database.menuItems.get(0), 2, "Standard");
            }
            customerInvoice = new Invoice(1, defaultOrder);
            Database.invoices.add(customerInvoice);
        }

        setInvoice(customerInvoice);
    }


    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;

        if (invoice != null) {
            invoiceIdLabel.setText("Invoice #" + invoice.getInvoiceId());
            subtotalLabel.setText(String.format("$%.2f", invoice.getSubtotal()));
            taxLabel.setText(String.format("$%.2f", invoice.getTaxAmount()));
            serviceChargeLabel.setText(String.format("$%.2f", invoice.getServiceCharge()));
            totalLabel.setText(String.format("$%.2f", invoice.getTotalAmount()));
            paymentStatusLabel.setText("Payment Status: " + invoice.getPaymentStatus());
        }
    }

    @FXML
    private void confirmPayment() {
        if (invoice == null) {
            showAlert(Alert.AlertType.ERROR, "Payment Error", "No invoice loaded.");
            return;
        }

        if (invoice.getPaymentStatus() == PaymentStatus.PAID) {
            showAlert(Alert.AlertType.INFORMATION, "Invoice Paid", "This invoice is already paid!");
            handleBack();
            return;
        }

        PaymentMethod selectedMethod = PaymentMethod.CASH;
        if (creditCardRadio.isSelected()) {
            selectedMethod = PaymentMethod.CREDIT_CARD;
        } else if (onlineRadio.isSelected()) {
            selectedMethod = PaymentMethod.ONLINE;
        }

        invoice.payInvoice(selectedMethod);
        paymentStatusLabel.setText("Payment Status: " + invoice.getPaymentStatus());

        // Directly update the shared in-memory Order object so the dashboard
        // can read it on reload (no socket needed — dashboard client is stopped during checkout).
        if (invoice.getOrder() != null) {
            invoice.getOrder().setStatus(enums.OrderStatus.PAID);
        }

        Customer customer = Session.getCurrentCustomer();
        if (customer != null) {
            customer.checkoutAndPay(invoice.getTotalAmount());
        }

        showAlert(Alert.AlertType.INFORMATION, "Payment Successful",
                "Invoice #" + invoice.getInvoiceId() + " paid successfully!\n" +
                "Method: " + selectedMethod + "\nTotal Paid: " + String.format("$%.2f", invoice.getTotalAmount()));

        handleBack();
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

