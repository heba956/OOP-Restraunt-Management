package projecttry1;

import java.io.PrintWriter;
import java.net.Socket;

public class OrderStatusSocketPublisher {
    public static void sendStatusUpdate(String host, int port, int orderId, String status) {
        new Thread(() -> {
            try {
                Socket socket = new Socket(host, port);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println("ORDER_STATUS|" + orderId + "|" + status);
                out.flush();
                Thread.sleep(80);
                socket.close();
            } catch (Exception e) {
                System.out.println("Failed to send TCP order status update: " + e.getMessage());
            }
        }, "SocketPublisherThread").start();
    }
}
