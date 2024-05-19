package todo.todo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Service {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 8080;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public void Service() {
        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.out.println("Server connection error: " + e.getMessage());
        }
    }

    public String sendLoginData(String username, String password) {
        try{
            Service();
            String loginMessage = "login " + username + " " + password;
            System.out.println(loginMessage);
            out.println(loginMessage);

            // Read the server's response
            String response = in.readLine();
            return response;

        } catch (IOException e) {
            System.out.println("Server connection error: " + e.getMessage());
            return null;
        }
    }
    public void sendLogoutMessage(String username, String password) {
        Service();
        String logoutMessage = "logout " + username + " " + password;
        out.println(logoutMessage);
        close();
    }

    public String sendRegisterData(String username, String password) {
        try{
            Service();
            String registerMessage = "register " + username + " " + password;
            out.println(registerMessage);
            out.flush();

            // Read the server's response
            String response = in.readLine();
            return response;

        } catch (IOException e) {
            System.out.println("Server connection error: " + e.getMessage());
            return null;
        }
    }

    public void sendTaskMessage(String username, String title, String description, String deadline, String priority, String status) {
        //send task message to server
        Service();
        String taskMessage = "newtask " + username + " "  + title + " " + description + " " + deadline + " " + priority + " " + status;
        out.println(taskMessage);
        out.flush();
    }

    public List<Task> getTasks(String username) {
        Service();
        List<Task> tasks = new ArrayList<>();
        try {
            String getTasksMessage = "gettasks "+ username;
            System.out.println(getTasksMessage);
            out.println(getTasksMessage);
            String response;
            System.out.println("Getting tasks from server");
            while (!(response = in.readLine()).equals("end")) {
                System.out.printf("Server response: %s\n", response);
                String[] parts = response.split("\\|");
                String title = parts[0];
                String description = parts[1];
                String creationDate = parts[2];
                String deadline = parts[3];
                String priority = parts[4];
                String status = parts[5];
                Task task = new Task(title, description, creationDate, deadline, priority, status);
                tasks.add(task);
            }
            if(response.equals("end")){
                System.out.println("End of tasks");
            }
        } catch (IOException e) {
            System.out.println("Server connection error: " + e.getMessage());
        }
        return tasks;
    }

    public void newGroup(String username, String group) {
        Service();
        //send group message to server
        String groupMessage = "newgroup " + username + " " + group;
        out.println(groupMessage);
        out.flush();
    }

    public void close() {
        try {
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("Error when closing the socket: " + e.getMessage());
        }
    }
}