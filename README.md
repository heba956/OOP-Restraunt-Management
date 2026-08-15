# Restaurant Management & Order System

A comprehensive JavaFX-based restaurant management application demonstrating core object-oriented programming principles, multi-tier architecture, and real-time order status tracking via TCP sockets.

---

## Overview

This system provides a complete solution for restaurant operations, enabling customers to register, browse menus, make reservations, and place orders—while administrators manage tables, staff, and invoices. The application features a responsive GUI built with JavaFX, an in-memory database with seed data, and real-time order status updates through a background TCP server.

---

## Key Features

- **User Authentication**  
  Secure login and registration for customers and staff with validation (min. 6-character passwords, 11-digit phone numbers)

- **Menu Management**  
  Organized menu system with categories (Appetizers, Main Course, Beverages, Desserts), item availability tracking, and detailed descriptions

- **Table Reservation System**  
  Reserve tables by availability, date, and party size with conflict detection and status management

- **Order Management**  
  Place orders, modify item quantities, add special instructions, track order status (PLACED → PREPARING → READY → SERVED), and calculate totals

- **Invoicing & Payment**  
  Generate invoices, process multiple payment methods (Cash, Credit Card, Mobile Payment), and manage payment status

- **Real-Time Order Status Updates**  
  TCP socket server broadcasts order status changes to all connected clients for live dashboard updates

- **Loyalty Program**  
  Customers earn loyalty points based on spending (1 point per 10 currency units)

- **Role-Based Access**  
  Separate dashboards and permissions for customers, waiters, and administrators

---

## Technology Stack

| Component | Technology |
|-----------|-----------|
| **Language** | Java (97.2%) |
| **GUI Framework** | JavaFX with FXML |
| **Styling** | CSS (2.8%) |
| **Database** | In-Memory (ArrayList-based) |
| **Networking** | TCP Sockets (java.net) |
| **Concurrency** | Virtual Threads (Java 21+) |
| **Build Tool** | Ant (build.xml) |

---

## Architecture

### Model Layer (Business Logic)
- **Customer** — User accounts with balance, loyalty points, dietary preferences
- **Order** — Order management with item tracking, status updates, total calculation
- **Invoice** — Invoice generation and payment processing
- **Reservation** — Table booking with datetime and party size
- **MenuItem** & **MenuCategory** — Menu structure and availability management
- **Table** — Table information (capacity, type, status)
- **Staff** — Base class for **Admin** and **Waiter** roles

### Data Layer
- **Database** — Static in-memory repository holding customers, orders, tables, menu items, reservations, and invoices with synchronized initialization

### Networking Layer
- **Server** — TCP server (port 8080) using virtual threads to handle concurrent clients and broadcast order status updates
- **OrderStatusClient** — Client-side connection to receive real-time order updates
- **OrderStatusListener** — Event listener interface for status change notifications

### Presentation Layer (JavaFX GUI)
- **App** — Application entry point; manages scene switching and global CSS stylesheet
- **LoginController** / **Login.fxml** — Authentication screen
- **RegisterController** / **Register.fxml** — Customer registration
- **CustDashboardController** / **CustDashboard.fxml** — Customer homepage
- **MenuController** / **MenuView.fxml** — Browse and filter menu items by category
- **CheckoutController** / **Checkout.fxml** — Order review and payment
- **MakeReservationController** / **MakeReservation.fxml** — Book tables
- **TableController** / **TableView.fxml** — View and manage restaurant tables

---

## Project Structure

```
projecttry1/
├── Main.java                          # Legacy CLI entry point
├── App.java                           # JavaFX application launcher
├── Database.java                      # In-memory data repository
├── Server.java                        # TCP server for order status broadcast
│
├── Models/
│   ├── Customer.java
│   ├── Order.java
│   ├── OrderItem.java
│   ├── Invoice.java
│   ├── Reservation.java
│   ├── MenuItem.java
│   ├── MenuCategory.java
│   ├── Table.java
│   ├── Staff.java
│   ├── Admin.java
│   └── Waiter.java
│
├── Networking/
│   ├── OrderStatusClient.java
│   ├── OrderStatusListener.java
│   └── OrderStatusSocketPublisher.java
│
├── GUI Controllers (FXML MVC)/
│   ├── LoginController.java
│   ├── RegisterController.java
│   ├── CustDashboardController.java
│   ├── MenuController.java
│   ├── CheckoutController.java
│   ├── MakeReservationController.java
│   └── TableController.java
│
├── FXML Layouts/
│   ├── Login.fxml
│   ├── Register.fxml
│   ├── CustDashboard.fxml
│   ├── MenuView.fxml
│   ├── Checkout.fxml
│   ├── MakeReservation.fxml
│   ├── TableView.fxml
│   └── cust.fxml
│
├── Resources/
│   ├── style.css                      # Global stylesheet
│   ├── logo.jpeg
│   └── img_*.png                      # Category images (appetizers, mains, salads, soups, beverages, desserts)
│
└── Build/
    ├── build.xml                      # Ant build configuration
    └── manifest.mf                    # JAR manifest
```

---

## OOP Concepts Demonstrated

- **Encapsulation** — Private attributes with public getters/setters in all model classes
- **Inheritance** — Staff hierarchy (Admin and Waiter extend Staff base class)
- **Polymorphism** — Virtual threads and listener patterns for order status updates
- **Abstraction** — OrderStatusListener interface for decoupling event handling
- **Single Responsibility Principle** — Database, Server, and UI controllers each have focused duties
- **Composition** — Order contains OrderItems; Invoice references Orders; Reservations reference Tables and Customers

---

## Networking & Concurrency

- **TCP Socket Server** — Listens on port 8080 for client connections
- **Virtual Threads** — Handles multiple concurrent clients efficiently using Java 21+ virtual thread executors
- **Broadcast Mechanism** — Order status updates (e.g., "ORDER_STATUS order_1 READY") are pushed to all connected clients
- **Synchronized Collections** — Thread-safe client list using `Collections.synchronizedList()`

---

## How to Run

### Prerequisites
- **Java 21+** (for virtual threads support)
- **JavaFX SDK** (version 21 or compatible)
- **Ant** build tool

### Compilation & Execution

1. **Build the project:**
   ```bash
   ant compile
   ```

2. **Run the application:**
   ```bash
   ant run
   ```
   Or directly:
   ```bash
   java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -cp build/classes projecttry1.App
   ```

3. **Test via CLI** (legacy mode):
   ```bash
   java -cp build/classes projecttry1.Main
   ```

---

## GUI Workflow

1. **Login/Register** — Create an account or log in with existing credentials
2. **Customer Dashboard** — View available options: browse menu, make reservations, view orders
3. **Menu** — Filter items by category (Appetizers, Mains, Beverages, Desserts), view details
4. **Checkout** — Review items in cart, apply special instructions, proceed to payment
5. **Reservations** — Select date, time, and party size to book a table
6. **Admin/Waiter Views** — Manage tables, view live orders, update status

---

## Main Classes & Responsibilities

| Class | Responsibility |
|-------|---------------|
| **Customer** | Authentication, account management, order/reservation lifecycle |
| **Order** | Item tracking, status transitions, cost calculations |
| **Invoice** | Payment processing, amount reconciliation, status management |
| **Reservation** | Booking details, conflict detection, status tracking |
| **Table** | Capacity, type (Indoor/VIP/Outdoor), current status |
| **Database** | Centralized data storage and seed initialization |
| **Server** | TCP listener, client connection handling, broadcast logic |
| **App** | JavaFX lifecycle, scene management, resource loading |

---

## Validation & Error Handling

- **Password Validation** — Minimum 6 characters
- **Phone Number Validation** — Exactly 11 digits
- **Order Constraints** — Prevents negative quantities, null menu items, and unavailable items
- **Reservation Conflict Detection** — Prevents double-booking tables
- **Order Status Flow** — Enforces unidirectional status progression (cannot revert states)
- **Server Port Binding** — Gracefully handles "address already in use" errors

---

## Database Initialization

On startup, the Database populates with:
- **3 Customers** (malak, alex, Heba) with balances and dietary preferences
- **2 Staff Members** (admin, waiter) with roles
- **5 Tables** with varying capacities and types (Indoor, Outdoor, VIP)
- **8 Menu Items** across 4 categories with prices and availability
- **1 Sample Reservation** and **1 Sample Order** for demonstration

---

## Future Enhancements

- **Persistent Storage** — Replace in-memory ArrayList with relational database (MySQL, PostgreSQL)
- **Authentication** — Implement JWT or OAuth2 for secure token-based sessions
- **Order Tracking** — Add real-time GPS tracking for delivery orders
- **Analytics Dashboard** — Sales reports, peak hours, customer analytics
- **Payment Gateway Integration** — Stripe, PayPal, or local payment APIs
- **Multi-Language Support** — Internationalization (i18n) for menus and UI
- **Mobile App** — React Native or Flutter client for on-the-go ordering
- **Notification System** — Push notifications for order status via WebSockets
- **Unit & Integration Tests** — JUnit 5 test suite for all core classes
- **Docker Containerization** — Containerize for cloud deployment

---

## License

This project is an educational assignment and is provided as-is for learning purposes. Modify and distribute freely with appropriate attribution.

---

## Contributors

Developed by the OOP course team at the university.

---

## Support & Questions

For issues, feature requests, or questions, please open an issue on this repository.
