package projecttry1;

public class Session {
    private static Customer currentCustomer;
    private static Staff currentStaff;

    public static void setCurrentCustomer(Customer customer) {
        currentCustomer = customer;
    }

    public static Customer getCurrentCustomer() {
        return currentCustomer;
    }

    public static void setCurrentStaff(Staff staff) {
        currentStaff = staff;
    }

    public static Staff getCurrentStaff() {
        return currentStaff;
    }
}
