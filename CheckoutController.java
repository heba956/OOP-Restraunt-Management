package com.restaurant.restaurantsystem;

import enums.PaymentMethod;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

import projecttry1.Invoice;

public class CheckoutController {

    // =========================
    // FXML ELEMENTS
    // =========================

    @FXML
    private Label invoiceIdLabel;

    @FXML
    private Label subtotalLabel;

    @FXML
    private Label taxLabel;

    @FXML
    private Label serviceChargeLabel;

    @FXML
    private Label totalLabel;

    @FXML
    private Label paymentStatusLabel;

    @FXML
    private RadioButton cashRadio;

    @FXML
    private RadioButton creditCardRadio;

    @FXML
    private RadioButton onlineRadio;

    // =========================
    // PAYMENT GROUP
    // =========================

    private ToggleGroup paymentGroup;

    // =========================
    // CURRENT INVOICE
    // =========================

    private Invoice invoice;

    // =========================
    // INITIALIZE
    // =========================

    @FXML
    public void initialize() {

        // Put all payment options in the same group
        paymentGroup = new ToggleGroup();

        cashRadio.setToggleGroup(paymentGroup);
        creditCardRadio.setToggleGroup(paymentGroup);
        onlineRadio.setToggleGroup(paymentGroup);
    }

    // =========================
    // RECEIVE INVOICE
    // =========================

    public void setInvoice(Invoice invoice) {

        this.invoice = invoice;

        if (invoice != null) {

            invoiceIdLabel.setText(
                    "Invoice #" + invoice.getInvoiceId()
            );

            subtotalLabel.setText(
                    String.format("%.2f", invoice.getSubtotal())
            );

            taxLabel.setText(
                    String.format("%.2f", invoice.getTaxAmount())
            );

            serviceChargeLabel.setText(
                    String.format("%.2f", invoice.getServiceCharge())
            );

            totalLabel.setText(
                    String.format("%.2f", invoice.getTotalAmount())
            );

            paymentStatusLabel.setText(
                    "Payment Status: " + invoice.getPaymentStatus()
            );
        }
    }

    // =========================
    // CONFIRM PAYMENT
    // =========================

    @FXML
    private void confirmPayment() {

        // Make sure an invoice was provided
        if (invoice == null) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Payment Error");
            alert.setHeaderText(null);
            alert.setContentText(
                    "No invoice has been loaded."
            );

            alert.showAndWait();
            return;
        }

        // Determine selected payment method
        PaymentMethod selectedMethod = null;

        if (cashRadio.isSelected()) {

            selectedMethod = PaymentMethod.CASH;

        } else if (creditCardRadio.isSelected()) {

            selectedMethod = PaymentMethod.CREDIT_CARD;

        } else if (onlineRadio.isSelected()) {

            selectedMethod = PaymentMethod.ONLINE;
        }

        // Make sure a payment method was selected
        if (selectedMethod == null) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Payment Required");
            alert.setHeaderText(null);
            alert.setContentText(
                    "Please select a payment method."
            );

            alert.showAndWait();
            return;
        }

        // Pay the invoice
        invoice.payInvoice(selectedMethod);

        // Update payment status
        paymentStatusLabel.setText(
                "Payment Status: " + invoice.getPaymentStatus()
        );

        // Show success message
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Payment Successful");
        alert.setHeaderText("Payment Confirmed");

        alert.setContentText(
                "Invoice #" + invoice.getInvoiceId()
                + " has been paid successfully.\n"
                + "Payment Method: " + selectedMethod
                + "\nTotal: "
                + String.format("%.2f", invoice.getTotalAmount())
        );

        alert.showAndWait();
    }
}