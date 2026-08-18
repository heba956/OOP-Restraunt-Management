
package projecttry1;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import enums.OrderStatus;

public class Order {
    private static int nextId = 1;
    private final int orderId;//made it final
    private Table table;
    private Customer customer;
    private List<OrderItem> items;
    private OrderStatus status;
    private final LocalDateTime orderTime; //madee it final

    public Order(Table table, Customer customer) {
        this.orderId = nextId++;
        this.table = table;
        this.customer = customer;
        this.items = new ArrayList<>();
        this.status = OrderStatus.PLACED;
        this.orderTime = LocalDateTime.now();
    }

    public int getOrderId() {
        return orderId;
    }
    public Table getTable() {
      return table;
    }
    public void setTable(Table table) {
       this.table = table;
    }
    public Customer getCustomer() {
        return customer;
    }
    public void setCustomer(Customer customer) {
       this.customer = customer;
    }
    public List<OrderItem> getItems() {
       return new ArrayList<>(items); // return a copy, not the live list
    }
    public OrderStatus getStatus() {
       return status;
    }
    public void setStatus(OrderStatus status) {
       this.status = status;
    }
    public LocalDateTime getOrderTime() {
       return orderTime;
    }

    public boolean addItem(MenuItem menuItem, int quantity, String notes){
        if (menuItem == null) {
        throw new IllegalArgumentException("Menu item cannot be null");}
        if (quantity <= 0){ 
        throw new IllegalArgumentException("Quantity must be positive");}
        if (!menuItem.isAvailable()) {
        throw new IllegalStateException( "Cannot add \"" + menuItem.getName() + "\" item is currently unavailable");}

        items.add(new OrderItem(menuItem, quantity, notes));
        return true; //added
    }

    public boolean removeItem(MenuItem item){
        for (int i = 0; i < items.size(); i++) {

        if (items.get(i).getMenuItem().equals(item)) {
            items.remove(i);
            return true;
        }
    }
     return false;
    }
    
    public double calculateTotalCost(){
        double total = 0;

    for (OrderItem item : items) {
        total += item.getSubtotal();
    }
    return total;
    }

    public void viewOrder(){
         if (items.isEmpty()) {
        System.out.println("Order is empty.");
        return;
    }

    for (OrderItem item : items) {

        System.out.println(
                item.getMenuItem().getName()
                + " x" + item.getQuantity()
                + " = " + item.getSubtotal()
        );
    }
    System.out.println("----------------------");
    System.out.println("Total = " + calculateTotalCost());
    }

    public boolean updateStatus(OrderStatus newStatus){
         
        if (newStatus.ordinal() < this.status.ordinal()) {
            System.out.println("Cannot change status from " + this.status + " to " + newStatus);
            return false;
        }

    this.status = newStatus;
    System.out.println("Order status updated to: " + this.status);
    return true;
    }

    public int  getItemCount(){
        return items.size();
    }
    public void clearOrder(){
        items.clear();
    }

    public boolean updateOrder(MenuItem item, int quantity, String notes) {
    if (quantity <= 0) {
        return removeItem(item);
    }
    for (OrderItem orderItem : items) {
        if (orderItem.getMenuItem().equals(item)) {
            orderItem.setQuantity(quantity);
            orderItem.setNotes(notes);
            return true;
        }
    }
    return addItem(item, quantity, notes);
}

@Override
public String toString() {
    return "Order{" +
            "orderId=" + orderId +
            ", customer=" + (customer != null ? customer.getUsername() : "Walk-in") +
            ", table=" + (table != null ? String.valueOf(table.getTableNumber()) : "—") +
            ", items=" + items.size() +
            ", total=" + calculateTotalCost() +
            ", status=" + status +
            '}';
}

}
