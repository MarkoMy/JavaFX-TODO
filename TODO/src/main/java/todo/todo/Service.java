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
            String loginMessage = "login|" + username + "|" + password;
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
        String logoutMessage = "logout|" + username + "|" + password;
        out.println(logoutMessage);
        close();
    }

    public String sendRegisterData(String username, String password) {
        try{
            Service();
            String registerMessage = "register|" + username + "|" + password;
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

    //username, title, description, creationDate, deadline, priority, status, group, people
    public void sendTaskMessage(String username, String title, String description,String creationDate, String deadline, String priority, String status, String group, String people) {
        //send task message to server
        Service();
        String taskMessage = "newtask|" + username + "|" + title + "|" + description + "|" + creationDate + "|"
                                        + deadline + "|" + priority + "|" + status + "|" + group + "|" + people;
        out.println(taskMessage);
        out.flush();
    }

    public List<Task> getTasks(String username) {
        Service();
        List<Task> tasks = new ArrayList<>();
        try {
            String getTasksMessage = "gettasks|" + username;
            System.out.println(getTasksMessage);
            out.println(getTasksMessage);
            String response;
            System.out.println("Getting tasks from server");
            //title=tesztcím|description=tesztleírás|creationdate=2019-01-01T12:00:00|deadline=2019-01-02T12:00:00
            // |status=incomplete|priority=high|author=Mark|group=IT|members=Mark-OWNER,Scolwerz-WORKER

            while (!(response = in.readLine()).equals("end")) {
                System.out.printf("Server response: %s\n", response);
                String[] parts = response.split("\\|");
                String title = null, description = null, creationDate = null, deadline = null,
                        priority = null, status = null, author = null, group = null, members = null;
                for (String part : parts) {
                    String[] keyValue = part.split("=");
                    switch (keyValue[0]) {
                        case "title":
                            title = keyValue[1];
                            break;
                        case "description":
                            description = keyValue[1];
                            break;
                        case "creationdate":
                            creationDate = keyValue[1];
                            break;
                        case "deadline":
                            deadline = keyValue[1];
                            break;
                        case "priority":
                            priority = keyValue[1];
                            break;
                        case "status":
                            status = keyValue[1];
                            break;
                        case "author":
                            author = keyValue[1];
                            break;
                        case "group":
                            group = keyValue[1];
                            break;
                        case "members":
                            members = keyValue[1];
                            break;
                    }
                }
                Task task = new Task(title, author, description, creationDate, deadline, priority, status, group, members);
                tasks.add(task);
            }
            if (response.equals("end")) {
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
        String groupMessage = "newgroup|" + username + "|" + group;
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

    public List<String> getUsernames() {
        List<String> usernames = new ArrayList<>();
        try {
            Service();
            out.println("getusernames");

            String response;
            while (!(response = in.readLine()).equals("end")) {
                usernames.add(response);
                System.out.println(response);
            }
        } catch (IOException e) {
            System.out.println("Server connection error: " + e.getMessage());
        } finally {
            close();
        }
        return usernames;
    }

    public List<String> getGroupNames() {
        List<String> groupNames = new ArrayList<>();
        try {
            Service();
            out.println("getgroups");

            String response;
            while (!(response = in.readLine()).equals("end")) {
                groupNames.add(response);
                System.out.println(response);
            }
        } catch (IOException e) {
            System.out.println("Server connection error: " + e.getMessage());
        } finally {
            close();
        }
        return groupNames;
    }

    public void deleteTask(String title) {
        //sent delete message to server
        try {
            Service();
            String deleteMessage = "deletetask|" + title;
            out.println(deleteMessage);
            out.flush();
        } catch (Exception e) {
            System.out.println("Server connection error: " + e.getMessage());
        }

    }
}