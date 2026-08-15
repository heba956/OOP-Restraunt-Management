/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projecttry1;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import enums.Role;
import enums.OrderStatus;
import enums.TableStatus;
/**
 *
 * @author malak
 */
public class Waiter extends Staff{
    private final List<Table>assignedTables;
    
    public Waiter(String username, String password, int workingHours, Date DateOfBirth){
        super(username, password, Role.WAITER, workingHours, DateOfBirth);
        this.assignedTables=new ArrayList<>();}
    
    public List<Table> getAssignedTables() {
        return new ArrayList<>(assignedTables);//return assignedTables; was changed so that it cant be clearedd easily
    }

    public void assignTable(Table table) {
        //assignedTables.add(table);
        
    if (!assignedTables.contains(table)) {
        assignedTables.add(table);
    } //changed to avoid duplicates
    }
    public void takeOrder(Order order){
        Database.orders.add(order);
    }
    public void updateOrderStatus(Order order, OrderStatus newStatus) {
        if (order.updateStatus(newStatus)) {
            OrderStatusSocketPublisher.sendStatusUpdate("localhost", 8080, order.getOrderId(), newStatus.name());
        }
    }

    public void manageSeating(Table table) {
        if (assignedTables.contains(table)) { //table.getStatus() == TableStatus.RESERVED) add if problem occurs 
            table.setStatus(TableStatus.OCCUPIED);
        }
    }

    public void checkOutTable(Table table) {
        if (assignedTables.contains(table)) {
            table.setStatus(TableStatus.AVAILABLE);
            assignedTables.remove(table); //added to make sure waiter will not own it after checkout
        }
    }
}
