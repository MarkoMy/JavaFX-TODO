import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
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
            String[] parts = clientMessage.split("\\|");
            switch (parts[0]) {
                case "logout":
                    logoutUser(parts[1], parts[2], clientSocket);
                    break;
                case "login":
                    System.out.println("Login command received");
                    loginUser(parts[1], parts[2], clientSocket);
                    break;
                case "register":
                    registerUser(parts[1], parts[2], clientSocket);
                    break;
                case "newtask":
                    addTask(parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7], parts[8], parts[9]);
                    break;
                case "gettasks":
                    sendTaskList(clientSocket, parts[1]);
                    break;
                case "newgroup":
                    System.out.println("New group command received");
                    newGroup(parts[1], parts[2]);
                    break;
                case "getusernames":
                    sendusernames(clientSocket);
                    break;
                case "getgroups":
                    sendgroups(clientSocket);
                    break;
                case "deletetask":
                    deletetask(parts[1]);

                    break;
                default:
                    // Handle unknown command
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deletetask(String title) {
        for (Task task : tasks) {
            if (task.getTitle() != null && task.getTitle().equals(title)) {
                tasks.remove(task);
                break;
            }
        }
    }

    private void sendgroups(Socket clientSocket) {
        try (PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {
            if(groups.isEmpty()){
                writer.println("No groups found");
                return;
            }else{
                for (Group group : groups) {
                    System.out.printf("Sending group %s to client%n", group.getName());
                    writer.println(group.getName());
                }
            }
            writer.println("end");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendusernames(Socket clientSocket) {
        users.clear(); // Clear the users list before reading from the file
        List<User> fileUsers = fileHandler.readFromFile("logins.txt", users);
        try (PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {
            for (User user : fileUsers) {
                writer.println(user.getUsername());
                System.out.printf("Sending username %s to client%n", user.getUsername());
            }
            writer.println("end");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void newGroup(String username, String groupName) {
        User user = findUserByName(username);
        if (user == null) {
            System.out.println("User does not exist");
            return;
        }

        Group group = new Group();
        group.setName(groupName);
        groups.add(group);

        try (PrintWriter writer = new PrintWriter(new FileOutputStream(new File("Tasks.txt"), true))) {
            writer.println("---Group:" + group.getName());
            writer.println("|");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendTaskList(Socket clientSocket, String username) {
        System.out.printf("Sending task list to user %s%n", username);
        List<Task> userTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getAuthor() != null && task.getAuthor().getUsername().equals(username) || isUserAssigned(task, username)) {
                System.out.printf("\nAdding task %s to user %s%n", task.getTitle(), username);
                userTasks.add(task);
            }
        }
        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            if (userTasks.isEmpty()) {
                System.out.println("No tasks found for user " + username);
                out.println("end");
                return;
            }
            for (Task task : userTasks) {
                StringJoiner joiner = new StringJoiner("|");
                joiner.add("title=" + task.getTitle())
                        .add("description=" + task.getDescription())
                        .add("creationdate=" + task.getCreationDate().toString())
                        .add("deadline=" + task.getDeadline().toString())
                        .add("priority=" + task.getPriority())
                        .add("status=" + task.getStatus())
                        .add("author=" + (task.getAuthor() != null ? task.getAuthor().getUsername() : "null"))
                        .add("group=" + task.getGroup().getName());
                StringBuilder members = new StringBuilder("members=");
                for (int i = 0; i < task.getAssignedUsers().size(); i++) {
                    User user = task.getAssignedUsers().get(i);
                    members.append(user.getUsername());
                    if (i < task.getAssignedUsers().size() - 1) {
                        members.append(",");
                    }
                }
                joiner.add(members.toString());
                out.println(joiner.toString());
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

    public void addTask(String author, String title, String description, String creationDate, String deadline, String priority, String status, String group, String people) {
        User aauthor = findUserByName(author);
        if (aauthor == null) {
            System.out.println("Author does not exist");
            return;
        }

        Group groupName = findGroupByName(group);
        if (groupName == null) {
            System.out.println("Group does not exist");
            return;
        }



        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setCreationDate(LocalDateTime.parse(creationDate));
        task.setDeadline(LocalDateTime.parse(deadline));
        task.setPriority(priority);
        task.setStatus(status);
        task.setAuthor(aauthor);
        task.setGroup(groupName);

        String[] userWithRoles = people.split(",");
        List<User> assignedUsers = new ArrayList<>();
        for (String userWithRole : userWithRoles) {
            String[] parts = userWithRole.split("-");
            String username = parts[0];

            User assignedUser = findUserByName(username);
            if (assignedUser == null) {
                System.out.println("Assigned user " + username + " does not exist");
                continue;
            }
            assignedUsers.add(assignedUser);
        }
        task.setAssignedUsers(assignedUsers);

        tasks.add(task);
        groupName.getTasks().add(task);

        // Save the new task to the file
        saveTask(task);
    }

    public void saveTask(Task task) {
        try {
            List<String> originalFileContent = Files.readAllLines(Paths.get("Tasks.txt"));
            List<String> modifiedFileContent = new ArrayList<>();
            boolean isGroupFound = false;

            for (String line : originalFileContent) {
                if (line.startsWith("---Group:") && line.substring(9).equals(task.getGroup().getName())) {
                    isGroupFound = true;
                }

                if (!isGroupFound || !line.equals("|")) {
                    modifiedFileContent.add(line);
                } else {
                    addTaskToContent(task, modifiedFileContent);
                    isGroupFound = false;
                }
            }

            if (isGroupFound) {
                addTaskToContent(task, modifiedFileContent);
            }

            Files.write(Paths.get("Tasks.txt"), modifiedFileContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addTaskToContent(Task task, List<String> content) {
        content.add("|");
        content.add("title=" + task.getTitle());
        content.add("description=" + task.getDescription());
        content.add("creationdate=" + task.getCreationDate().toString());
        content.add("deadline=" + task.getDeadline().toString());
        content.add("status=" + task.getStatus());
        content.add("priority=" + task.getPriority());
        content.add("author=" + task.getAuthor().getUsername());
        content.add("group=" + task.getGroup().getName());
        StringBuilder members = new StringBuilder("members=");
        for (int i = 0; i < task.getAssignedUsers().size(); i++) {
            User user = task.getAssignedUsers().get(i);
            members.append(user.getUsername());
            if (i < task.getAssignedUsers().size() - 1) {
                members.append(",");
            }
        }
        content.add(members.toString());
        content.add("|");
    }
    private boolean isGroupWritten(Group group) {
        try (BufferedReader reader = new BufferedReader(new FileReader("Tasks.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals("---Group:" + group.getName())) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
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

    private Group findGroupByName(String name) {
        for (Group group : groups) {
            if (group.getName().equals(name)) {
                return group;
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