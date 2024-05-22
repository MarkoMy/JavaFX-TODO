import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * The FileHandler class provides methods to read from and write to a file
 * that contains user data.
 */
public class FileHandler {

    /**
     * Reads user data from a specified file and populates a list of users.
     *
     * @param filename the name of the file to read from
     * @param users    the list to populate with user data
     * @return the list of users read from the file
     */
    public synchronized List<User> readFromFile(String filename, List<User> users) {
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

    /**
     * Writes a new user's data to a specified file if the user does not already exist.
     *
     * @param filename the name of the file to write to
     * @param users    the list of existing users
     * @param username the username of the new user
     * @param password the password of the new user
     * @return a message indicating whether the user was registered successfully or if the user already exists
     * @throws IOException if an I/O error occurs
     */
    public synchronized String writeToFile(String filename, List<User> users,  String username, String password) throws IOException {
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
