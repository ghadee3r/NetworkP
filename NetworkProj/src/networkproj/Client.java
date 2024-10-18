package networkproj;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private BufferedReader userInput;
    private ExecutorService executorService;

    public static void main(String[] args) {
        // Show the FirstFrame for username input
        FirstFrame mainframe = new FirstFrame();
        mainframe.setVisible(true);
        
        // The Client will be initiated after the user connects
        Client client = new Client();
        client.start();
    }

    public void start() {
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            userInput = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Connected to the server at " + SERVER_ADDRESS + ":" + SERVER_PORT);

            executorService = Executors.newSingleThreadExecutor();
            executorService.submit(new ServerMessagesHandler());

            handleUserInput();

        } catch (UnknownHostException e) {
            System.err.println("Error: Unknown server address " + SERVER_ADDRESS);
        } catch (IOException e) {
            System.err.println("Error: Unable to establish connection to the server.");
        } finally {
            stop();
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public String readMessage() throws IOException {
        return in.readLine(); // This will block until a message is received from the server
    }
    private void handleUserInput() throws IOException {
        String message;

        System.out.println("Type your messages (type 'QUIT' to exit):");

        while ((message = userInput.readLine()) != null) {
            if (message.trim().equalsIgnoreCase("QUIT")) {
                System.out.println("Disconnecting from server...");
                break;
            }
            if (!message.trim().isEmpty()) {
                out.println(message);
            }
        }
    }

    private void stop() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            if (executorService != null && !executorService.isShutdown()) {
                executorService.shutdown();
            }
            System.out.println("Connection closed. Goodbye!");
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    private class ServerMessagesHandler implements Runnable {
        @Override
        public void run() {
            try {
                String messageFromServer;
                while ((messageFromServer = in.readLine()) != null) {
                    System.out.println("Server: " + messageFromServer);
                    // You can add logic here to update the UI in ConRoom if needed
                }
            } catch (IOException e) {
                System.err.println("Connection lost. Server may have closed the connection.");
            } finally {
                stop();
            }
        }
    }
    public String getResponse() throws IOException {
    // Wait for the server's response
    return in.readLine(); // Assuming 'in' is your BufferedReader for input from the server
}

}
