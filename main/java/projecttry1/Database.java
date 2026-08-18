package projecttry1;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import enums.OrderStatus;
import enums.PaymentMethod;
import enums.PaymentStatus;
import enums.ReservationStatus;
import enums.TableStatus;
import enums.TableType;


public class Database {
    
    public static ArrayList<Customer> customers = new ArrayList<>();

   
    public static ArrayList<Staff> staffMembers = new ArrayList<>();
    public static ArrayList<Table> tables = new ArrayList<>();
    public static ArrayList<MenuCategory> menuCategories = new ArrayList<>();
    public static ArrayList<MenuItem> menuItems = new ArrayList<>();

    
    public static ArrayList<Reservation> reservations = new ArrayList<>();

  
    public static ArrayList<Order> orders = new ArrayList<>();
    public static ArrayList<OrderItem> orderItems = new ArrayList<>();

    
    public static ArrayList<Invoice> invoices = new ArrayList<>();

    private static boolean initialized = false;

    
    public static synchronized List<Table> getTables() { return new ArrayList<>(tables); }
    public static synchronized List<MenuItem> getMenuItems() { return new ArrayList<>(menuItems); }
    public static synchronized List<MenuCategory> getMenuCategories() { return new ArrayList<>(menuCategories); }
    public static synchronized List<Customer> getCustomers() { return new ArrayList<>(customers); }
    public static synchronized List<Staff> getStaffMembers() { return new ArrayList<>(staffMembers); }
    public static synchronized List<Reservation> getReservations() { return new ArrayList<>(reservations); }
    public static synchronized List<Order> getOrders() { return new ArrayList<>(orders); }
    public static synchronized List<Invoice> getInvoices() { return new ArrayList<>(invoices); }

    
    public static synchronized void initializeDatabase() {
        if (initialized) return;
        
        customers.clear();
        staffMembers.clear();
        tables.clear();
        menuCategories.clear();
        menuItems.clear();
        reservations.clear();
        orders.clear();
        orderItems.clear();
        invoices.clear();

        initializeMenu();
        initializeTables();
        initializeCustomers();
        initializeStaff();
        initializeReservations();
        initializeOrders();
        initializeInvoices();

        initialized = true;
    }
    
    private static void initializeMenu() {
        MenuCategory appetizers = new MenuCategory("Appetizers", "Delicious starters");
        MenuCategory mainCourse = new MenuCategory("Main Course", "Hearty main dishes");
        MenuCategory beverages = new MenuCategory("Beverages", "Refreshing hot and cold drinks");
        MenuCategory desserts = new MenuCategory("Desserts", "Sweet treats");

        menuCategories.add(appetizers);
        menuCategories.add(mainCourse);
        menuCategories.add(beverages);
        menuCategories.add(desserts);

        menuItems.add(new MenuItem("Caesar Salad", 80.0, "Fresh salad with Caesar dressing", appetizers, true));
        menuItems.add(new MenuItem("Garlic Bread", 45.0, "Toasted bread with garlic butter", appetizers, true));
        menuItems.add(new MenuItem("Chicken Alfredo", 220.0, "Creamy fettuccine pasta with chicken", mainCourse, true));
        menuItems.add(new MenuItem("Grilled Ribeye Steak", 350.0, "Prime beef steak served with sides", mainCourse, true));
        menuItems.add(new MenuItem("Cola", 35.0, "Chilled soft drink", beverages, true));
        menuItems.add(new MenuItem("Fresh Orange Juice", 60.0, "Freshly squeezed juice", beverages, true));
        menuItems.add(new MenuItem("Chocolate Lava Cake", 90.0, "Rich chocolate cake with warm center", desserts, true));
        menuItems.add(new MenuItem("Tiramisu", 95.0, "Classic Italian coffee-flavored dessert", desserts, true));
    }

    private static void initializeStaff() {
        staffMembers.add(new Admin("admin", "admin123", 40, new Date()));
        staffMembers.add(new Waiter("ahmed", "waiter123", 48, new Date()));
    }

    private static void initializeCustomers() {
        customers.add(new Customer("malak", "123456", LocalDate.of(2006, 10, 26), 1000.0, "01012345678", "Vegetarian"));
        customers.add(new Customer("alex", "abcdef", LocalDate.of(2005, 5, 14), 600.0, "01198765432", "No allergies"));
        customers.add(new Customer("Heba", "123456", LocalDate.of(2000, 1, 1), 1200.0, "01234567890", "None"));
    }

    private static void initializeTables() {
        tables.add(new Table(1, 2, TableType.INDOOR, TableStatus.AVAILABLE, "12:00 PM"));
        tables.add(new Table(2, 4, TableType.OUTDOOR, TableStatus.AVAILABLE, "2:00 PM"));
        tables.add(new Table(3, 6, TableType.VIP, TableStatus.RESERVED, "4:00 PM"));
        tables.add(new Table(4, 8, TableType.INDOOR, TableStatus.AVAILABLE, "6:00 PM"));
        tables.add(new Table(5, 4, TableType.VIP, TableStatus.OCCUPIED, "8:00 PM"));
    }

    private static void initializeReservations() {
        if (!customers.isEmpty() && tables.size() > 2) {
            Reservation reservation = new Reservation(customers.get(0), tables.get(2), LocalDateTime.now().plusDays(1), 4);
            reservation.setStatus(ReservationStatus.ACTIVE);
            reservations.add(reservation);
        }
    }

    private static void initializeOrders() {
        if (!customers.isEmpty() && !tables.isEmpty() && menuItems.size() > 2) {
            Order order = new Order(tables.get(0), customers.get(0));
            order.addItem(menuItems.get(2), 1, "Extra sauce");
            order.addItem(menuItems.get(4), 2, "Cold");
            orders.add(order);
            orderItems.addAll(order.getItems());
        }
    }

    private static void initializeInvoices() {
        if (!orders.isEmpty()) {
            Invoice invoice = new Invoice(1, orders.get(0));
            invoices.add(invoice);
        }
    }
}
