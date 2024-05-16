import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Server {
    private List<User> users;
    private List<Task> tasks;
    private List<Group> groups;
    private FileHandler fileHandler;
    private ServerSocket serverSocket;
    private Set<String> loggedInUsers;

    public Server() {
        loggedInUsers = new HashSet<>();
        users = new ArrayList<>();
        tasks = new ArrayList<>();
        groups = new ArrayList<>();
        fileHandler = new FileHandler();
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.startServer();
    }

    public void startServer() {
        try {
            serverSocket = new ServerSocket(8080);
            System.out.println("Server started.");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                handleClientMessage(clientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleClientMessage(Socket clientSocket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String clientMessage = in.readLine();
            System.out.println("Received message: " + clientMessage);
            String[] parts = clientMessage.split(" ");
            if (parts.length >= 3 && parts[0].equals("logout")) {
                logoutUser(parts[1], parts[2], clientSocket);
            } else if (parts.length >= 3 && parts[0].equals("login")) {
                loginUser(parts[1], parts[2], clientSocket);
            }else if(parts.length >= 3 && parts[0].equals("register")){
                registerUser(parts[1], parts[2]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendTaskList(User user) {
        // send task list to user
    }

    public void registerUser(String username, String password) {
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        users.add(newUser);
        fileHandler.writeToFile("logins.txt", users);
    }

    public void loginUser(String username, String password, Socket clientSocket) {
        List<User> fileUsers = fileHandler.readFromFile("logins.txt");
        for (User user : fileUsers) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                loggedInUsers.add(username);
                sendClientMessage("Login successful", clientSocket);
                return;
            }
        }
        sendClientMessage("Login failed", clientSocket);
    }

    public void logoutUser(String username, String password, Socket clientSocket) {
        if (loggedInUsers.contains(username)) {
            loggedInUsers.remove(username);
            sendClientMessage("Logout successful", clientSocket);
        } else {
            sendClientMessage("Logout failed: user is not logged in", clientSocket);
        }
    }
    public void sendClientMessage(String message, Socket clientSocket) {
        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}