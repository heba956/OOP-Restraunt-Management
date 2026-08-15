package projecttry1;

public class Session {
    private static Customer currentCustomer;

    public static void setCurrentCustomer(Customer customer) {
        currentCustomer = customer;
    }

    public static Customer getCurrentCustomer() {
        return currentCustomer;
    }
}
