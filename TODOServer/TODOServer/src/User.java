import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private String password;
    private List<Task> tasks;

    public User() {
        this.tasks = new ArrayList<>();
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String toString() {
        return "User [username=" + username + ", password=" + password + "]";
    }

    public List<Task> getTasks() {
        return tasks;
    }
    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}