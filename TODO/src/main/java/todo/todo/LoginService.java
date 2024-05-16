package todo.todo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class LoginService {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 8080;

    public String sendLoginData(String username, String password) {
        try (
             Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

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
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String logoutMessage = "logout " + username + " " + password;
            out.println(logoutMessage);

        } catch (IOException e) {
            System.out.println("Server connection error: " + e.getMessage());
        }
    }
}