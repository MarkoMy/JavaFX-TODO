import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {
    public List<User> readFromFile(String filename) {
        List<User> users = new ArrayList<>();
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

    public void writeToFile(String filename, List<User> users) {
        try {
            PrintWriter writer = new PrintWriter(filename);
            for (User user : users) {
                writer.println(user.getUsername() + " " + user.getPassword());
            }
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
