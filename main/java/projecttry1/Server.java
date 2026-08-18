package projecttry1;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Server {
    // Maps a logged-in customer's username to their live connection's writer
    private final Map<String, PrintWriter> clientWriters = new ConcurrentHashMap<>();

    public void start(final int portNumber) {
        try (var serverSocket = new ServerSocket(portNumber)) {
            try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
                while (true) {
                    var client = serverSocket.accept();
                    executor.submit(() -> handleClient(client));
                }
            }
        } catch (BindException e) {
            System.out.println("Server port " + portNumber + " is already active.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket client) {
        String registeredUsername = null;
        try {
            var in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            var out = new PrintWriter(client.getOutputStream(), true);

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received: " + inputLine);
                String[] parts = inputLine.split("\\|", -1);

                if (parts.length == 2 && parts[0].equals("REGISTER")) {
                    registeredUsername = parts[1];
                    clientWriters.put(registeredUsername, out);

                } else if (parts.length == 4 && parts[0].equals("PUBLISH")) {
                    String targetUsername = parts[1];
                    String orderId = parts[2];
                    String status = parts[3];
                    sendToUser(targetUsername, "ORDER_STATUS|" + orderId + "|" + status);
                }
            }
        } catch (SocketException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (registeredUsername != null) {
                clientWriters.remove(registeredUsername, /* only remove if still this connection */
                        clientWriters.get(registeredUsername));
            }
            try { client.close(); } catch (IOException ignored) {}
        }
    }

    private void sendToUser(String username, String message) {
        PrintWriter out = clientWriters.get(username);
        if (out == null) {

            return;
        }
        out.println(message);
        if (out.checkError()) {

            clientWriters.remove(username, out);
        }
    }
}