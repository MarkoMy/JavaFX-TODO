import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Group {
    private String name;
    public List<User> users = Collections.synchronizedList(new ArrayList<>());
    private List<Task> tasks = Collections.synchronizedList(new ArrayList<>());

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
