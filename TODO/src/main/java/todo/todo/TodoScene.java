package todo.todo;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class TodoScene {
    private LoginService loginService = new LoginService();

    public Scene createScene() {
        GridPane todoPane = new GridPane();
        todoPane.setAlignment(Pos.CENTER);
        todoPane.setHgap(10);
        todoPane.setVgap(10);

        Button signOutButton = new Button("Kijelentkezés");
        signOutButton.setOnAction(event -> {
            String username = Page.loginScene.getLoggedInUsername();
            String password = Page.loginScene.getLoggedInPassword();
            loginService.sendLogoutMessage(username, password);
            Scene todoScene = Page.loginScene.createScene();
            Stage stage = (Stage) todoPane.getScene().getWindow();
            stage.setScene(todoScene);
        });

        todoPane.add(signOutButton, 0, 0);

        Scene scene = new Scene(todoPane, 450, 800);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        return scene;
    }
}