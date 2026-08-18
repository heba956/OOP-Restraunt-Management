package projecttry1;
import enums.PaymentStatus;
import enums.PaymentMethod;
public class Invoice {
    private int invoiceId;
    private Order order;
    private static final double TAX_RATE = 0.10;
    private static final double SERVICE_RATE = 0.25;
    private double  subtotal;
    private double taxAmount;
    private double serviceCharge;
    private double totalAmount;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;

    public Invoice(int invoiceId, Order order){
        this.invoiceId = invoiceId;
        this.order = order;
        this.paymentStatus = PaymentStatus.PENDING;
        calculateInvoice();
    }

    
    public int getInvoiceId() {
      return invoiceId;
    }

    public Order getOrder() {
      return order;
    }

    public double getSubtotal() {
      return subtotal;
    }

    public double getTaxAmount() {
      return taxAmount;
    }

    public double getServiceCharge() {
      return serviceCharge;
    }

    public double getTotalAmount() {
      return totalAmount;
    }

    public PaymentStatus getPaymentStatus() {
      return paymentStatus;
    }

    public PaymentMethod getPaymentMethod() {
      return paymentMethod;
    }


    public void setInvoiceId(int invoiceId) {
      this.invoiceId = invoiceId;
    }

    public void setOrder(Order order) {
      this.order = order;
      calculateInvoice();//added
    }

    /*public void setSubtotal(double subtotal) {
      this.subtotal = subtotal;
    }*/

    /*public void setTaxAmount(double taxAmount) {
      this.taxAmount = taxAmount;
    }*/

   /* public void setServiceCharge(double serviceCharge) {
      this.serviceCharge = serviceCharge;
    }*/

   /* public void setTotalAmount(double totalAmount) {
      this.totalAmount = totalAmount;
    }*/

    public void setPaymentStatus(PaymentStatus paymentStatus) {
      this.paymentStatus = paymentStatus;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
      this.paymentMethod = paymentMethod;
    }

    public double calculateSubtotal(){
        subtotal = order.calculateTotalCost();
        return subtotal;
    }
    public double calculateTax(){
        taxAmount = subtotal * TAX_RATE;
        return taxAmount;
    }
    public double calculateServiceCharge(){
        serviceCharge = subtotal * SERVICE_RATE;
        return serviceCharge;
    }
    public double calculateTotal(){
        totalAmount = subtotal + taxAmount + serviceCharge;
        return totalAmount;
    }
    public void calculateInvoice(){
        calculateSubtotal();
        calculateTax();
        calculateServiceCharge();
        calculateTotal();
    }
    public void payInvoice(PaymentMethod method){
        paymentMethod = method;
        paymentStatus = PaymentStatus.PAID;
    }
    @Override
    public String toString() {
        return "Invoice{" +
            "invoiceId=" + invoiceId +
            ", subtotal=" + subtotal +
            ", tax=" + taxAmount +
            ", serviceCharge=" + serviceCharge +
            ", total=" + totalAmount +
            ", paymentStatus=" + paymentStatus +
            ", paymentMethod=" + paymentMethod +
            '}';
        }
}
