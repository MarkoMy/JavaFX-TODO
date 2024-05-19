package todo.todo;
/**
 * This class represents a Task in the application.
 */
public class Task {
    private String title;
    private String author;
    private String description;
    private String creationDate;
    private String deadline;
    private String priority;
    private String status;
    private String group;
    private String members;

    /**
     * Constructs a new Task with the given parameters.
     *
     * @param title the title of the task.
     * @param author the author of the task.
     * @param description the description of the task.
     * @param creationDate the creation date of the task.
     * @param deadline the deadline of the task.
     * @param priority the priority of the task.
     * @param status the status of the task.
     * @param group the group of the task.
     * @param members the members of the task.
     */
    public Task(String title, String author, String description,String creationDate, String deadline,
                String priority, String status, String group, String members) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.creationDate = creationDate;
        this.deadline = deadline;
        this.priority = priority;
        this.status = status;
        this.group = group;
        this.members = members;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public String getDeadline() {
        return deadline;
    }

    public String getPriority() {
        return priority;
    }

    public String getStatus() {
        return status;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getMembers() {
        return members;
    }

    public void setMembers(String members) {
        this.members = members;
    }
}