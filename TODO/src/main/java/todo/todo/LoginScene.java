package todo.todo;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class LoginScene{
    private String loggedInUsername;
    private String loggedInPassword;
    LoginService loginService = new LoginService();


    public Scene createScene() {
        //main pane

        GridPane mainPane = new GridPane();
        mainPane.setAlignment(Pos.CENTER);
        mainPane.setHgap(10);
        mainPane.setVgap(10);

        // Create elements
        TextField usernameField = new TextField();
        usernameField.setPromptText("Felhasználónév");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Jelszó");

        Button loginButton = new Button("Bejelentkezés");

        // Set button actions
        loginButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            Task<String> loginTask = new Task<>() {
                @Override
                protected String call() throws Exception {
                    System.out.println("Sending login data");
                    return loginService.sendLoginData(username, password);
                }
            };

            loginTask.setOnSucceeded(workerStateEvent -> {
                String response = loginTask.getValue();
                System.out.println("Valasz: " + response);
                if (response == null || !response.equals("Login successful")) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Login Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Login failed. Please check your username and password.");
                    alert.showAndWait();
                } else {
                    loggedInUsername = username;
                    loggedInPassword = password;
                    System.out.println("Login successful");
                    Scene todoScene = Page.todoScene.createScene();
                    Stage stage = (Stage) mainPane.getScene().getWindow();
                    stage.setScene(todoScene);
                }
            });

            loginTask.setOnFailed(workerStateEvent -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login Error");
                alert.setHeaderText(null);
                alert.setContentText("An error occurred while trying to login.");
                alert.showAndWait();
            });

            new Thread(loginTask).start();
        });

        // Add elements to the main pane
        mainPane.add(usernameField, 0, 0);
        mainPane.add(passwordField, 0, 1);
        mainPane.add(loginButton, 0, 2);

        // Set alignment to center
        GridPane.setHalignment(usernameField, HPos.CENTER);
        GridPane.setHalignment(passwordField, HPos.CENTER);
        GridPane.setHalignment(loginButton, HPos.CENTER);

        GridPane.setValignment(usernameField, VPos.CENTER);
        GridPane.setValignment(passwordField, VPos.CENTER);
        GridPane.setValignment(loginButton, VPos.CENTER);


        Scene scene = new Scene(mainPane, 450, 800);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        return scene;
    }

    public String getLoggedInUsername() {
        return loggedInUsername;
    }

    public String getLoggedInPassword() {
        return loggedInPassword;
    }

}