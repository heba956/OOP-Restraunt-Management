/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package projecttry1;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import enums.OrderStatus;
import enums.PaymentMethod;
import enums.PaymentStatus;
import enums.ReservationStatus;
import enums.TableStatus;
import enums.TableType;
/**
 *
 * @author malak
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
         Database.initializeDatabase();
Customer customer1 = Database.customers.get(0);
Customer customer2 = Database.customers.get(1);

Order order =
new Order(
Database.tables.get(0),
customer1);
order.addItem(
Database.menuItems.get(0),
2,
"No onions");
order.removeItem(
Database.menuItems.get(0));
order.updateOrder(
Database.menuItems.get(0),
3,
"Extra cheese");
order.viewOrder();
Invoice invoice =
new Invoice(
5,
order);
System.out.println(invoice);
invoice.payInvoice(
PaymentMethod.CREDIT_CARD);

System.out.println(invoice);
System.out.println(Database.customers);

System.out.println(Database.staffMembers);

System.out.println(Database.tables);

System.out.println(Database.menuCategories);

System.out.println(Database.menuItems);

System.out.println(Database.reservations);

System.out.println(Database.orders);

System.out.println(Database.invoices);
}}
