package todo.todo;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class TaskPane extends VBox {
    private Task task;

    public TaskPane(Task task) {
        this.task = task;
        //this.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        Label titleLabel = new Label(task.getTitle());
        Label authorLabel = new Label(task.getAuthor());
        this.getChildren().addAll(titleLabel, authorLabel);
        this.setOnMouseClicked(event -> showDetails());
    }

    private void showDetails() {
        // Clear the pane and show all details
        this.getChildren().clear();
        Label titleLabel = new Label("Title: " + task.getTitle());
        Label authorLabel = new Label("Author: " + task.getAuthor());
        Label descriptionLabel = new Label("Description: " + task.getDescription());
        Label deadlineLabel = new Label("Deadline: " + task.getDeadline());
        Label priorityLabel = new Label("Priority: " + task.getPriority());
        Label statusLabel = new Label("Status: " + task.getStatus());
        this.getChildren().addAll(titleLabel, authorLabel, descriptionLabel, deadlineLabel, priorityLabel, statusLabel);
    }
}