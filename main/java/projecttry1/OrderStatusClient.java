package projecttry1;

import java.net.*;
import java.io.*;
import javafx.application.Platform;

public class OrderStatusClient implements Runnable {
    private final String host;
    private final int port;
    private final String username;
    private final OrderStatusListener listener;
    private volatile boolean running = true;
    private volatile Socket socket;

    public OrderStatusClient(String host, int port, String username, OrderStatusListener listener) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.listener = listener;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(host, port);
            try (var out = new PrintWriter(socket.getOutputStream(), true);
                 var in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                out.println("REGISTER|" + username);
                out.flush();

                String line;
                while (running && (line = in.readLine()) != null) {
                    String[] parts = line.split("\\|");
                    if (parts.length == 3 && parts[0].equals("ORDER_STATUS")) {
                        String orderId = parts[1];
                        String status = parts[2];
                        Platform.runLater(() -> listener.onStatusUpdate(orderId, status));
                    }
                }
            }
        } catch (IOException ignored) {
        }
    }

    public void stop() {
        running = false;
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ignored) {
        }
    }
}