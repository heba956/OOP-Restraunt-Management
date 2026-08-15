package projecttry1;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Server {
    private final List<Socket> clients = Collections.synchronizedList(new ArrayList<>());

    public void start(final int portNumber) {
        try (var serverSocket = new ServerSocket(portNumber)) {
            try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
                while (true) {
                    var client = serverSocket.accept();
                    clients.add(client);
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
        try {
            var in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received: " + inputLine);

                if (inputLine.startsWith("ORDER_STATUS")) {
                    broadcast(inputLine); // push the status update to every connected client
                }
            }
        } catch (SocketException ignored) {
            // Client closed connection normally
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            clients.remove(client);
            try { client.close(); } catch (IOException ignored) {}
        }
    }

    private void broadcast(String message) {
        synchronized (clients) {
            Iterator<Socket> it = clients.iterator();
            while (it.hasNext()) {
                Socket s = it.next();
                if (s.isClosed()) {
                    it.remove();
                    continue;
                }
                try {
                    var out = new PrintWriter(s.getOutputStream(), true);
                    out.println(message);
                    out.flush();
                } catch (IOException e) {
                    it.remove();
                    try { s.close(); } catch (IOException ignored) {}
                }
            }
        }
    }
}
