package projecttry1;

@FunctionalInterface
public interface OrderStatusListener {
    void onStatusUpdate(String orderId, String status);
}
