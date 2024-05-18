import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {
    public List<User> readFromFile(String filename, List<User> users) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length >= 2) {
                    User user = new User();
                    user.setUsername(parts[0]);
                    user.setPassword(parts[1]);
                    users.add(user);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    public String writeToFile(String filename, List<User> users,  String username, String password) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(" ");
            if(parts[1].equals(username)){
                return "User already exists";
            }
        }

        // Ha a felhasználó nem létezik, hozzáadjuk a fájl végéhez
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(new File(filename), true))) {
            writer.println(username + " " + password);
            return "User registered";
        } catch (IOException e) {
            e.printStackTrace();
            return "Error registering user";
        }

    }
}
