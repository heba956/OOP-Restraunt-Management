package projecttry1;
public class Session{
    private static Customer currentCustomer;
    public static void setCurrentCustomer(Customer c){currentCustomer=c;}
    public static projecttry1.Customer getCurrentCustomer() {return currentCustomer;}
}
