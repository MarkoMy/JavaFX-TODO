import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.*;

public class Server {
    public List<User> users;
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
        server.loadTasks();
        //server.printTasksToConsole();
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
            switch (parts[0]) {
                case "logout":
                    logoutUser(parts[1], parts[2], clientSocket);
                    break;
                case "login":
                    loginUser(parts[1], parts[2], clientSocket);
                    break;
                case "register":
                    registerUser(parts[1], parts[2], clientSocket);
                    break;
                case "newtask":
                    addTask(parts[1], parts[2], parts[3], parts[4], parts[5], parts[6]);
                default:
                    // Handle unknown command
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addTask(String username, String title, String desc, String deadline, String priority, String status) {
        //Az egyes task-oknak legyen címe, leírása, létrehozás dátuma, határideje, prioritása, állapota, szerzője, csoportja, és a csoportból hozzárendelhető személyek (szerkesztők, dolgozók).
        Task newtask = new Task();
        newtask.setTitle(title);
        newtask.setDescription(desc);
        newtask.setCreationDate(LocalDateTime.now());
        newtask.setDeadline(LocalDateTime.parse(deadline));
        newtask.setPriority(priority);
        newtask.setStatus(status);
        tasks.add(newtask);
    }

    public void sendTaskList(Socket clientSocket, String username) {
        System.out.printf("Sending task list to user %s%n", username);
        // send task list to user which contains the username
        List<Task> userTasks = new ArrayList<>();
        for (Task task : tasks) {
            //System.out.println(task.getAuthor().getUsername() + " " + task.getAssignedUsers());
            if (task.getAuthor().getUsername().equals(username) || isUserAssigned(task, username)) {
                System.out.printf("\nAdding task %s to user %s%n", task.getTitle(), username);
                userTasks.add(task);
            }
        }
        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            //if its empty
            if (userTasks.isEmpty()) {
                System.out.println("No tasks found for user " + username);
                out.println("end");
                return;
            }
            for (Task task : userTasks) {
                out.println(task.getTitle() + "|" + task.getDescription() + "|" + task.getCreationDate() + "|"
                        + task.getDeadline() + "|" + task.getPriority() + "|" + task.getStatus());
            }
            out.println("end");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isUserAssigned(Task task, String username) {
        for (User user : task.getAssignedUsers()) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public void registerUser(String username, String password, Socket clientSocket) throws IOException {
        String s = fileHandler.writeToFile("logins.txt", users, username, password);
        if(s.equals("User registered")){
            sendClientMessage("Registration successful", clientSocket);
        }else{
            sendClientMessage("Registration failed", clientSocket);
        }
    }

    public void loginUser(String username, String password, Socket clientSocket) {
        List<User> fileUsers = fileHandler.readFromFile("logins.txt", users);
        for (User user : fileUsers) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                loggedInUsers.add(username);
                sendClientMessage("Login successful", clientSocket);
                sendTaskList(clientSocket, username);
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

    public void saveTasks() {
        try (PrintWriter writer = new PrintWriter(new File("Tasks.txt"))) {
            for (Task task : tasks) {
                StringJoiner joiner = new StringJoiner(",");
                joiner.add(task.getTitle())
                        .add(task.getDescription())
                        .add(task.getCreationDate().toString())
                        .add(task.getDeadline().toString())
                        .add(task.getPriority())
                        .add(task.getStatus());
                writer.println(joiner.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadTasks() {
        try (BufferedReader reader = new BufferedReader(new FileReader("Tasks.txt"))) {
            String line;
            Group currentGroup = null;
            Task currentTask = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("---Group:")) {
                    currentGroup = new Group();
                    currentGroup.setName(line.substring(9));
                    groups.add(currentGroup);
                } else if (line.equals("|")) {
                    if (currentTask != null) {
                        tasks.add(currentTask);
                        currentGroup.getTasks().add(currentTask);
                    }
                    currentTask = new Task();
                } else {
                    String[] parts = line.split("=");
                    switch (parts[0]) {
                        case "title":
                            currentTask.setTitle(parts[1]);
                            break;
                        case "description":
                            currentTask.setDescription(parts[1]);
                            break;
                        case "creationdate":
                            currentTask.setCreationDate(LocalDateTime.parse(parts[1]));
                            break;
                        case "deadline":
                            currentTask.setDeadline(LocalDateTime.parse(parts[1]));
                            break;
                        case "status":
                            currentTask.setStatus(parts[1]);
                            break;
                        case "priority":
                            currentTask.setPriority(parts[1]);
                            break;
                        case "author":
                            currentTask.setAuthor(findUserByName(parts[1]));
                            break;
                        case "group":
                            currentTask.setGroup(currentGroup);
                            break;
                        case "members":
                            String[] members = parts[1].split(",");
                            List<User> assignedUsers = new ArrayList<>();
                            for (String member : members) {
                                assignedUsers.add(findUserByName(member.split("-")[0]));
                            }
                            currentTask.setAssignedUsers(assignedUsers);
                            break;
                    }
                }
            }
            // Add the last task if it exists
            if (currentTask != null) {
                tasks.add(currentTask);
                currentGroup.getTasks().add(currentTask);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private User findUserByName(String name) {
        List<User> fileUsers = fileHandler.readFromFile("logins.txt", new ArrayList<>());
        for (User user : fileUsers) {
            if (user.getUsername().equals(name)) {
                return user;
            }
        }
        return null;
    }

    public void printTasksToConsole() {
        for (Task task : tasks) {
            System.out.println("Title: " + task.getTitle());
            System.out.println("Description: " + task.getDescription());
            System.out.println("Creation Date: " + task.getCreationDate());
            System.out.println("Deadline: " + task.getDeadline());
            System.out.println("Priority: " + task.getPriority());
            System.out.println("Status: " + task.getStatus());
            System.out.println("Author: " + task.getAuthor().getUsername());
            System.out.println("Group: " + task.getGroup().getName());
            System.out.print("Members: ");
            for (User user : task.getAssignedUsers()) {
                System.out.print(user.getUsername() + " ");
            }
            System.out.println("\n-------------------------");
        }
    }
}