import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public String getPriority() {
        return priority;
    }

    public String getStatus() {
        return status;
    }

    public User getAuthor() {
        return author;
    }

    public Group getGroup() {
        return group;
    }

    public List<User> getAssignedUsers() {
        return assignedUsers;
    }

    public List<EditHistory> getEditHistories() {
        return editHistories;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public void setAssignedUsers(List<User> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }

    public void setEditHistories(List<EditHistory> editHistories) {
        this.editHistories = editHistories;
    }

}