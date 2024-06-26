package todo.todo;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TodoScene {
    Image logoutbtn = new Image(getClass().getResource("/logoutbtnpic.png").toExternalForm());
    ImageView logoutbtnpic = new ImageView(logoutbtn);
    private Service service = new Service();
    List<Task> tasks;

    /**
     * This method creates the main scene of the application.
     * @return the created scene.
     */
    public Scene createScene() {
        BorderPane todomainPane = new BorderPane();
        todomainPane.setPadding(new Insets(10));
        tasks = service.getTasks(Page.loginScene.getLoggedInUsername());
        displayTasks(tasks, todomainPane);

        // Fejléc
        HBox header = new HBox();
        header.setAlignment(Pos.TOP_LEFT);
        Button signOutButton = new Button();
        signOutButton.getStyleClass().add("abutton");
        logoutbtnpic.setFitHeight(20);
        logoutbtnpic.setFitWidth(20);
        signOutButton.setGraphic(logoutbtnpic);
        header.getChildren().add(signOutButton);
        todomainPane.setTop(header);
        BorderPane.setMargin(header, new Insets(0, 0, 10, 0));

        //tasks
        displayTasks(tasks, todomainPane);

        //bottom buttons
        HBox bottomButtons = new HBox();
        bottomButtons.setAlignment(Pos.BOTTOM_CENTER);
        bottomButtons.setSpacing(10);

        Button newGroupButton = new Button("Új csoport");
        bottomButtons.getChildren().add(newGroupButton);
        newGroupButton.getStyleClass().add("abutton");
        BorderPane.setAlignment(newGroupButton, Pos.BOTTOM_LEFT);
        BorderPane.setMargin(newGroupButton, new Insets(0, 10, 10, 0));

        Button plusButton = new Button("Új task");
        bottomButtons.getChildren().add(plusButton);
        plusButton.getStyleClass().add("abutton");
        BorderPane.setAlignment(plusButton, Pos.BOTTOM_RIGHT);
        BorderPane.setMargin(plusButton, new Insets(0, 10, 10, 0));

        todomainPane.setBottom(bottomButtons);

        newGroupButton.setOnAction(event -> {
            bottomButtons.setVisible(false);
            // Create labels and fields
            Label groupLabel = new Label("Csoportnév:");
            TextField groupField = new TextField();
            Button sendButton = new Button("Mentés");
            sendButton.getStyleClass().add("abutton");
            Button backButton = new Button("Vissza");
            backButton.getStyleClass().add("abutton");

            // Create layout and add fields
            GridPane grid = new GridPane();
            grid.setPadding(new Insets(10));
            grid.add(groupLabel, 1, 1);
            grid.add(groupField, 2, 1);
            grid.add(sendButton, 2, 2);
            grid.add(backButton, 2, 3);

            backButton.setOnAction(event2 -> {
                grid.getChildren().clear();
                tasks = service.getTasks(LoginScene.getLoggedInUsername());
                displayTasks(tasks, todomainPane);
                // Show the bottom buttons again
                bottomButtons.setVisible(true);
            });

            sendButton.setOnAction(event2 -> {
                String username = LoginScene.getLoggedInUsername();
                String group = groupField.getText();

                //if empty than throw error
                if (group.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Hiba");
                    alert.setHeaderText(null);
                    alert.setContentText("A mező nem lehet üres!");
                    alert.showAndWait();
                } else {
                    System.out.printf("New group: %s\n", group);
                    service.newGroup(username, group);
                    grid.getChildren().clear();
                    tasks = service.getTasks(LoginScene.getLoggedInUsername());
                    displayTasks(tasks, todomainPane);
                    bottomButtons.setVisible(true);
                }
            });
            // Add grid to the main pane
            todomainPane.setCenter(grid);
        });

        plusButton.setOnAction(event -> {
            // Hide the bottom buttons
            bottomButtons.setVisible(false);

            // Create labels and fields
            Label titleLabel = new Label("Cím:");
            TextField titleField = new TextField();
            Label descriptionLabel = new Label("Leírás:");
            TextField descriptionField = new TextField();
            Label deadlineLabel = new Label("Határidő:");
            DatePicker deadlineDatePicker = new DatePicker();
            deadlineDatePicker.setValue(LocalDate.now().plusDays(1));
            TextField timeField = new TextField();
            timeField.setPromptText("HH:mm");
            Label priorityLabel = new Label("Prioritás:");
            TextField priorityField = new TextField();
            Label statusLabel = new Label("Státusz:");
            TextField statusField = new TextField();

            Label groupLabel = new Label("Csoport:");
            ComboBox<String> groupField = new ComboBox<>();
            List<String> groupNames = service.getGroupNames();
            groupField.getItems().addAll(groupNames);

            Label peopleLabel = new Label("Személyek hozzáadása:");
            ComboBox<String> peopleComboBox = new ComboBox<>();
            // Get the usernames from the server
            List<String> usernames = service.getUsernames();
            // Add the usernames to the ComboBox
            peopleComboBox.getItems().addAll(usernames);
            Label roleLabel = new Label("Role:");
            ComboBox<String> roleComboBox = new ComboBox<>();
            roleComboBox.getItems().addAll("TAG", "EDITOR", "WORKER", "ADMIN", "OWNER");
            Button addButton = new Button("Add");
            Button sendButton = new Button("Mentés");
            Button visszaButton = new Button("Vissza");

            List<String> peopleRoleList = new ArrayList<>();
            ComboBox<String> peopleRoleComboBox = new ComboBox<>();
            Button deleteButton = new Button("Delete");

            addButton.setOnAction(event1 -> {
                String selectedPerson = peopleComboBox.getValue();
                String selectedRole = roleComboBox.getValue();
                if (selectedPerson != null && selectedRole != null) {
                    // Check if the person is already in the list
                    boolean personExists = peopleRoleList.stream().anyMatch(pr -> pr.startsWith(selectedPerson + "-"));
                    if (!personExists) {
                        peopleRoleList.add(selectedPerson + "-" + selectedRole);
                        peopleRoleComboBox.getItems().add(selectedPerson + "-" + selectedRole);
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Hiba");
                        alert.setHeaderText(null);
                        alert.setContentText("Egy személyhez csak egy szerepkör rendelhető!");
                        alert.showAndWait();
                    }
                }
            });

            deleteButton.setOnAction(event1 -> {
                String selectedPersonRole = peopleRoleComboBox.getValue();
                if (selectedPersonRole != null) {
                    peopleRoleList.remove(selectedPersonRole);
                    peopleRoleComboBox.getItems().remove(selectedPersonRole);
                }
            });

            // Create layout and add fields
            GridPane grid = new GridPane();
            grid.setPadding(new Insets(10));
            grid.add(titleLabel, 1, 1);
            grid.add(titleField, 2, 1);
            grid.add(descriptionLabel, 1, 2);
            grid.add(descriptionField, 2, 2);
            grid.add(deadlineLabel, 1, 3);
            grid.add(deadlineDatePicker, 2, 3);
            grid.add(timeField, 3, 3);
            grid.add(priorityLabel, 1, 4);
            grid.add(priorityField, 2, 4);
            grid.add(statusLabel, 1, 5);
            grid.add(statusField, 2, 5);
            grid.add(groupLabel, 1, 6);
            grid.add(groupField, 2, 6);
            grid.add(peopleLabel, 1, 7);
            grid.add(peopleComboBox, 2, 7);
            grid.add(roleLabel, 1, 8);
            grid.add(roleComboBox, 2, 8);
            grid.add(addButton, 3, 8);
            grid.add(peopleRoleComboBox, 2, 9);
            grid.add(deleteButton, 3, 9);
            grid.add(sendButton, 2, 10);
            grid.add(visszaButton, 2, 11);

            visszaButton.setOnAction(event1 -> {
                grid.getChildren().clear();
                tasks = service.getTasks(LoginScene.getLoggedInUsername());
                displayTasks(tasks, todomainPane);
                // Show the bottom buttons again
                bottomButtons.setVisible(true);
            });

            sendButton.setOnAction(event1 -> {
                String username = LoginScene.getLoggedInUsername();
                String title = titleField.getText();
                String description = descriptionField.getText();
                String creationDate = LocalDate.now().toString() + "T" + timeField.getText() + ":00";
                String deadline = deadlineDatePicker.getValue().toString() + "T" + timeField.getText() + ":00";
                String priority = priorityField.getText();
                String status = statusField.getText();
                String group = groupField.getValue();
                peopleRoleList.add(username + "-OWNER");
                String people = String.join(",", peopleRoleList);

                //if empty than throw error
                if (title.isEmpty() || description.isEmpty() || creationDate.isEmpty() || deadline.isEmpty() || priority.isEmpty() || status.isEmpty() || group.isEmpty() || people.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Hiba");
                    alert.setHeaderText(null);
                    alert.setContentText("A mezők nem lehetnek üresek!");
                    alert.showAndWait();
                } else {
                    service.sendTaskMessage(username, title, description, creationDate, deadline, priority, status, group, people);
                    grid.getChildren().clear();
                    tasks = service.getTasks(LoginScene.getLoggedInUsername());
                    displayTasks(tasks, todomainPane);
                    // Show the bottom buttons again
                    bottomButtons.setVisible(true);
                    System.out.println("Task sent");
                }
            });
    // Add grid to the main pane
    todomainPane.setCenter(grid);
});

        signOutButton.setOnAction(event -> {
            String username = Page.loginScene.getLoggedInUsername();
            String password = Page.loginScene.getLoggedInPassword();
            service.sendLogoutMessage(username, password);
            Scene todoScene = Page.loginScene.createScene();
            Stage stage = (Stage) todomainPane.getScene().getWindow();
            stage.setScene(todoScene);
        });

        Scene scene = new Scene(todomainPane, 450, 800);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());

        return scene;
    }
    /**
     * This method displays the tasks in the main pane.
     * @param tasks the list of tasks to display.
     * @param todomainPane the main pane where tasks are displayed.
     */
private void displayTasks(List<Task> tasks, BorderPane todomainPane) {
    VBox taskList = new VBox();
    if (tasks.isEmpty()) {
        taskList.getChildren().add(new Label("itt nincs semmi látnivaló..."));
    } else {
        for (Task task : tasks) {
            Button taskButton = new Button(task.getTitle());
            taskButton.getStyleClass().add("taskbutton");
            taskButton.setOnAction(event -> showDetails(task, todomainPane));
            taskList.getChildren().add(taskButton);
        }
    }
    todomainPane.setCenter(taskList);
}
    /**
     * This method shows the details of a task.
     * @param task the task to show details of.
     * @param todomainPane the main pane where task details are displayed.
     */
    private void showDetails(Task task, BorderPane todomainPane) {
        VBox taskDetails = new VBox();
        Label titleLabel = new Label("Title: " + task.getTitle());
        Label authorLabel = new Label("Author: " + task.getAuthor());
        Label descriptionLabel = new Label("Description: " + task.getDescription());
        Label deadlineLabel = new Label("Deadline: " + task.getDeadline());
        Label priorityLabel = new Label("Priority: " + task.getPriority());
        Label statusLabel = new Label("Status: " + task.getStatus());
        Label groupLabel = new Label("Group: " + task.getGroup());
        Label membersLabel = new Label("Members: " + task.getMembers());
        Button backButton = new Button("Vissza");
        backButton.getStyleClass().add("abutton");
        Button deleteButton = new Button("Törlés");
        deleteButton.getStyleClass().add("abutton");
        deleteButton.setOnAction(event -> {
            service.deleteTask(task.getTitle());
            tasks = service.getTasks(LoginScene.getLoggedInUsername());
            displayTasks(tasks, todomainPane);
        });
        backButton.setOnAction(event -> displayTasks(tasks, todomainPane));
        taskDetails.getChildren().addAll(titleLabel, authorLabel, descriptionLabel, deadlineLabel, priorityLabel, statusLabel, groupLabel, membersLabel, backButton, deleteButton);
        todomainPane.setCenter(taskDetails);
    }
}