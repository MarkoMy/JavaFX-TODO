import java.util.ArrayList;
import java.util.List;

public class Group {
    private String name;
    private List<User> users;
    private List<Task> tasks;

    public Group() {
        this.tasks = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Task> getTasks() {
        return tasks;
    }
}
