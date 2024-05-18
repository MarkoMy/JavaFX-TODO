import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Task {
    private String title;
    private String description;
    private LocalDateTime creationDate;
    private LocalDateTime deadline;
    private String priority;
    private String status;
    private User author;
    private Group group;
    private List<User> assignedUsers;
    private List<EditHistory> editHistories;

    public Task() {
        this.creationDate = LocalDateTime.now();
        this.assignedUsers = new ArrayList<>();
        this.editHistories = new ArrayList<>();
    }

    // getters and setters

    public void editTask(User user, String title, String description, LocalDateTime deadline, String priority, String status) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.priority = priority;
        this.status = status;
        this.editHistories.add(new EditHistory(user, LocalDateTime.now()));
    }

    public void shareTask(Group group) {
        this.group = group;
        group.getTasks().add(this);
    }

    public void deleteTask() {
        this.author.getTasks().remove(this);
        this.group.getTasks().remove(this);
    }
}