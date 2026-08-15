/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projecttry1;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import enums.ReservationStatus;
import enums.TableStatus;
/**
 *
 * @author malak
 */
public class Customer {
     // Attributes
    private String username;
    private String password;
    private LocalDate dateOfBirth;
    private double balance;
    private String phoneNumber;
    private int loyaltyPoints;
    private String dietaryPreferences;

    // Constructors
    public Customer() {
        this.balance = 0.0;
        this.loyaltyPoints = 0;
    }

    public Customer(String username, String password, LocalDate dateOfBirth,
                    double balance, String phoneNumber,
                    String dietaryPreferences) {

        this.username = username;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.balance = balance;
        this.phoneNumber = phoneNumber;
        this.dietaryPreferences = dietaryPreferences;
        this.loyaltyPoints = 0;
    }

    // Getters

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public double getBalance() {
        return balance;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public String getDietaryPreferences() {
        return dietaryPreferences;
    }

    // Setters

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        if (validatePassword(password)) {
            this.password = password;
        } else {
            System.out.println("Password must be at least 6 characters.");
        }
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setPhoneNumber(String phoneNumber) {
        if (validatePhoneNumber(phoneNumber)) {
            this.phoneNumber = phoneNumber;
        } else {
            System.out.println("Invalid phone number.");
        }
    }

    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public void setDietaryPreferences(String dietaryPreferences) {
        this.dietaryPreferences = dietaryPreferences;
    }

    // Validation

    public boolean validatePassword(String password) {
        return password != null && password.length() >= 6;
    }

    public boolean validatePhoneNumber(String phoneNumber) {
        return phoneNumber != null && phoneNumber.length() == 11;
    }

    // Account Methods

    public boolean register(String username, String password,
                            LocalDate dateOfBirth,
                            String phoneNumber,
                            String dietaryPreferences) {

        if (!validatePassword(password) || !validatePhoneNumber(phoneNumber)) {
            System.out.println("Registration failed.");
            return false;
        }

        this.username = username;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.dietaryPreferences = dietaryPreferences;

        System.out.println("Registration successful.");
        return true;
    }

    public boolean login(String username, String password) {

        if (this.username.equals(username) && this.password.equals(password)) {
            System.out.println("Login successful.");
            return true;
        }

        System.out.println("Incorrect username or password.");
        return false;
    }

    // Customer Functions

    public void viewAvailableTables(ArrayList<Table> tables) {

        for (Table table : tables) {
            System.out.println(table);
        }
    }

    public void viewMenu(ArrayList<MenuItem> menuItems) {

        for (MenuItem item : menuItems) {
            System.out.println(item);
        }
    }

    public Reservation makeReservation(Table table,
                                       LocalDateTime dateTime,
                                       int partySize) {
for (Reservation reservation : Database.reservations) {

    if (reservation.getTable().equals(table)
            && reservation.getDate().equals(dateTime)
            && reservation.getStatus() == ReservationStatus.ACTIVE) {

        throw new IllegalArgumentException("Table is already reserved.");
    }
}
        Reservation reservation = new Reservation(this, table, dateTime, partySize);
Database.reservations.add(reservation);
        System.out.println("Reservation created.");

        return reservation;
    }

    public void viewReservations(ArrayList<Reservation> reservations) {

        for (Reservation reservation : reservations) {
            System.out.println(reservation);
        }
    }

    public void cancelReservation(Reservation reservation) {

        System.out.println("Reservation cancelled.");
         reservation.cancelReservation();
       
        // Database.reservations.remove(reservation); do we need to be removed from database
    }

    public Order placeOrder(Table table ,ArrayList<OrderItem> items) {

        Order order = new Order(table, this);
        for (OrderItem item : items) {
        order.addItem(item.getMenuItem(), item.getQuantity(), item.getNotes());
    }
        Database.orders.add(order);
        System.out.println("Order placed.");

        return order;
    }

    public boolean checkoutAndPay(double amount) {

        if (balance >= amount) {

            balance -= amount;
            loyaltyPoints += (int) (amount / 10);

            System.out.println("Payment successful.");
            return true;
        }

        System.out.println("Insufficient balance.");
        return false;
    }
@Override
public String toString() {
    return "Customer{" +
            "username='" + username + '\'' +
            ", balance=" + balance +
            ", phoneNumber='" + phoneNumber + '\'' +
            ", loyaltyPoints=" + loyaltyPoints +
            ", dietaryPreferences='" + dietaryPreferences + '\'' +
            '}';
}
}
