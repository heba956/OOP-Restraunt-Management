package projecttry1;

import java.net.*;
import java.io.*;
import javafx.application.Platform;

public class OrderStatusClient implements Runnable {
    private final String host;
    private final int port;
    private final OrderStatusListener listener;
    private volatile boolean running = true;

    public OrderStatusClient(String host, int port, OrderStatusListener listener) {
        this.host = host;
        this.port = port;
        this.listener = listener;
    }

    @Override
    public void run() {
        try (var socket = new Socket(host, port);
             var in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String line;
            while (running && (line = in.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 3 && parts[0].equals("ORDER_STATUS")) {
                    String orderId = parts[1];
                    String status = parts[2];
                    Platform.runLater(() -> listener.onStatusUpdate(orderId, status));
                }
            }
        } catch (IOException ignored) {
            // Connection closed or socket server offline
        }
    }

    public void stop() { running = false; }
}
