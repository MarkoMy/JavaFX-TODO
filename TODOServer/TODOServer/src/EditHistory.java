import java.time.LocalDateTime;

public class EditHistory {
    private User user;
    private LocalDateTime timestamp;

    public EditHistory(User user, LocalDateTime timestamp) {
        this.user = user;
        this.timestamp = timestamp;
    }

    public User getUser() {
        return user;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}