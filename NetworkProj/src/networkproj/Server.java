package networkproj;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Server {
 private static final int PORT = 54321;
    private static final int ROOM_CAPACITY = 3; // Maximum of 3 players in the waiting room
    private static Set<String> connectedUsers = ConcurrentHashMap.newKeySet();
    private static List<ServerThread> serverThreads = Collections.synchronizedList(new ArrayList<>());
    private static Room waitingRoom = new Room();  // Single waiting room
    private static Queue<ServerThread> waitingQueue = new LinkedList<>();  // Queue for extra players if the room is full

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ServerThread newClient = new ServerThread(clientSocket);
                serverThreads.add(newClient);
                new Thread(newClient).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class Room {
        private List<ServerThread> players = new ArrayList<>();

        public boolean addPlayer(ServerThread player) {
            if (players.size() < ROOM_CAPACITY) {
                players.add(player);
                broadcastRoomUpdate();
                return true;
            }
            return false;
        }

        public boolean isFull() {
            return players.size() == ROOM_CAPACITY;
        }
/*
        private void broadcastRoomUpdate() {
            StringBuilder roomStatus = new StringBuilder("Waiting room players: ");
            for (ServerThread player : players) {
                roomStatus.append(player.username).append(", ");
            }
            broadcastToRoom(roomStatus.toString());
        }
*/
        private void broadcastToRoom(String message) {
            for (ServerThread player : players) {
                player.out.println(message);
            }
        }
        private void broadcastRoomUpdate() {
    StringBuilder roomStatus = new StringBuilder("WAITING_ROOM_LIST:");
    for (ServerThread player : players) {
        roomStatus.append(player.getUsername()).append(",");
    }
    if (roomStatus.length() > 0) {
        roomStatus.setLength(roomStatus.length() - 1); // Remove the last comma
    }
    broadcastToRoom(roomStatus.toString());
}

    }

  static class ServerThread implements Runnable {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;

    public ServerThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            // Initialize the PrintWriter and BufferedReader
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            // Handle error (you might want to close the socket or throw an exception)
        }
    }
 public Socket getSocket() {
        return clientSocket;
    }
    @Override

public void run() {
    try {
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream(), true);

        // Ask for username
        out.println("Enter your username: ");
        username = in.readLine();

        if (username != null && !username.isEmpty() && connectedUsers.add(username)) {
            out.println("You are connected.");
            broadcast("Player " + username + " has joined the game.");
            sendConnectedUsersList();
        } else {
            out.println("Failed to connect. Username may already be taken.");
            return; // Exit if connection fails
        }

        // Listen for further events
        String clientMessage;
        while ((clientMessage = in.readLine()) != null) {
            if (clientMessage.equalsIgnoreCase("play")) {
                pairRequest();
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    } finally {
        disconnect();
    }
}

    // Your connect method remains the same
    public boolean connect(String un) throws IOException {
        if (un != null && !un.isEmpty() && connectedUsers.add(un)) {
            this.username = un; // Set the username
            out.println("You are connected.");
            broadcast("Player " + username + " has joined the game.");
         
            sendConnectedUsersList();
            return true; // Indicate success
        } else {
            out.println("Failed to connect.");
            return false; // Indicate failure
        }
    }

    
    // Other methods...


     
       void pairRequest() {
    if (!waitingRoom.isFull()) {
        waitingRoom.addPlayer(this);
        out.println("You have been added to the waiting room."); // Success message
        sendWaitingRoomUsersList(); // Send the updated list of players in the waiting room
    } else {
        out.println("Waiting room is full. You have been added to the queue."); // Full message
        waitingQueue.offer(this);
    }


        }
  public PrintWriter getOut() {
        return out;
    }
        private void disconnect() {
            if (username != null) {
                connectedUsers.remove(username);
                serverThreads.remove(this);
                broadcast("Player " + username + " has left the game.");
            }
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
private void broadcast(String message) {
    synchronized (serverThreads) {
        for (ServerThread serverThread : serverThreads) {
            serverThread.out.println(message);
        }
    }
}

// Add this in the connect method after successfully adding a user


       private void sendConnectedUsersList() {
    synchronized (serverThreads) {
        StringBuilder userList = new StringBuilder("Connected players: ");
        for (String user : connectedUsers) {
            userList.append(user).append(", ");
        }
        // Remove the last comma and space
        if (userList.length() > 0) {
            userList.setLength(userList.length() - 2);
        }
        out.println(userList.toString()); // Send to the current client only
    }
}
       private void sendWaitingRoomUsersList() {
    // Use synchronized block to ensure thread safety when accessing the waiting room players
    synchronized (waitingRoom.players) {
        StringBuilder userList = new StringBuilder("WAITING_ROOM_USERS:");
        for (ServerThread player : waitingRoom.players) {
            userList.append(player.getUsername()).append(",");
        }
        // Remove the last comma if there's at least one player
        if (userList.length() > 0) {
            userList.setLength(userList.length() - 1); // Remove the last comma
        }
        out.println(userList.toString()); // Send to the current client only
    }
}

      public List<String> getConnectedUsers() {
    return new ArrayList<>(connectedUsers); // Convert Set to List
}



        public void sendMessage(String message) {
            if (out != null) {
                out.println(message);
            }
        }

        public String getUsername() {
            return username;
        }
public String getResponse() throws IOException {
    // Wait for the server's response
    return in.readLine(); // Assuming 'in' is your BufferedReader for input from the server
}

  }
}
