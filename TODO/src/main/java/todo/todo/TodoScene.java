package todo.todo;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TodoScene {
    Image logoutbtn = new Image(getClass().getResource("/logoutbtnpic.png").toExternalForm());
    ImageView logoutbtnpic = new ImageView(logoutbtn);
    private Service service = new Service();

    public Scene createScene() {
        BorderPane todomainPane = new BorderPane();
        todomainPane.setPadding(new Insets(10));

        // Fejléc
        HBox header = new HBox();
        header.setAlignment(Pos.TOP_LEFT);
        Button signOutButton = new Button();
        logoutbtnpic.setFitHeight(20);
        logoutbtnpic.setFitWidth(20);
        signOutButton.setGraphic(logoutbtnpic);
        header.getChildren().add(signOutButton);
        todomainPane.setTop(header);

        VBox center = new VBox();
        Label emptyLabel = new Label("itt nincs semmi látnivaló...");
        center.getChildren().add(emptyLabel);
        todomainPane.setCenter(center);

        Button plusButton = new Button("+");
        todomainPane.setBottom(plusButton);
        BorderPane.setAlignment(plusButton, Pos.BOTTOM_RIGHT);
        BorderPane.setMargin(plusButton, new Insets(0, 10, 10, 0));

        Button newGroupButton = new Button("Új csoport");
        todomainPane.setBottom(newGroupButton);
        BorderPane.setAlignment(newGroupButton, Pos.BOTTOM_LEFT);
        BorderPane.setMargin(newGroupButton, new Insets(0, 10, 10, 0));

        newGroupButton.setOnAction(event -> {
            // Create labels and fields
            Label groupLabel = new Label("Csoportnév:");
            TextField groupField = new TextField();
            Button sendButton = new Button("Mentés");

            // Create layout and add fields
            GridPane grid = new GridPane();
            grid.setPadding(new Insets(10));
            grid.add(groupLabel, 1, 1);
            grid.add(groupField, 2, 1);
            grid.add(sendButton, 2, 2);

            sendButton.setOnAction(event1 -> {
                String username = Page.loginScene.getLoggedInUsername();
                String group = groupField.getText();

                //if empty than throw error
                if (group.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Hiba");
                    alert.setHeaderText(null);
                    alert.setContentText("A mező nem lehet üres!");
                    alert.showAndWait();
                } else {
                    service.newGroup(username, group);
                    grid.getChildren().clear();
                }
            });
            // Add grid to the main pane
            todomainPane.setCenter(grid);
        });


        plusButton.setOnAction(event -> {
            // Create labels and fields
            Label titleLabel = new Label("Cím:");
            TextField titleField = new TextField();
            Label descriptionLabel = new Label("Leírás:");
            TextField descriptionField = new TextField();
            Label deadlineLabel = new Label("Határidő:");
            TextField deadlineField = new TextField();
            deadlineField.setText("yyyy-MM-ddTHH:mm:ss");
            Label priorityLabel = new Label("Prioritás:");
            TextField priorityField = new TextField();
            Label statusLabel = new Label("Státusz:");
            TextField statusField = new TextField();
            Button sendButton = new Button("Mentés");

            // Create layout and add fields
            GridPane grid = new GridPane();
            grid.setPadding(new Insets(10));
            grid.add(titleLabel, 1, 1);
            grid.add(titleField, 2, 1);
            grid.add(descriptionLabel, 1, 2);
            grid.add(descriptionField, 2, 2);
            grid.add(deadlineLabel, 1, 3);
            grid.add(deadlineField, 2, 3);
            grid.add(priorityLabel, 1, 4);
            grid.add(priorityField, 2, 4);
            grid.add(statusLabel, 1, 5);
            grid.add(statusField, 2, 5);
            grid.add(sendButton, 2, 6);

            sendButton.setOnAction(event1 -> {
                String username = Page.loginScene.getLoggedInUsername();
                String title = titleField.getText();
                String description = descriptionField.getText();
                String deadline = deadlineField.getText();
                String priority = priorityField.getText();
                String status = statusField.getText();

                //if empty than throw error
                if (title.isEmpty() || description.isEmpty() || deadline.isEmpty() || priority.isEmpty() || status.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Hiba");
                    alert.setHeaderText(null);
                    alert.setContentText("A mezők nem lehetnek üresek!");
                    alert.showAndWait();
                } else {
                    //if the deadline is not in the correct format, throw an error yyyy-MM-ddTHH:mm:ss
                    if (!deadline.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}")) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Hiba");
                        alert.setHeaderText(null);
                        alert.setContentText("A határidő formátuma nem megfelelő! (yyyy-MM-ddTHH:mm:ss)");
                        alert.showAndWait();
                    } else {
                        service.sendTaskMessage(username, title, description, deadline, priority, status);
                        grid.getChildren().clear();
                    }
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
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        return scene;
    }
}