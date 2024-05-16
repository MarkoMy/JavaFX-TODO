package todo.todo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Page extends Application {
    public static LoginScene loginScene = new LoginScene();
    public static TodoScene todoScene = new TodoScene();
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Login");
        primaryStage.setWidth(450);
        primaryStage.setHeight(800);

        primaryStage.setScene(loginScene.createScene());
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}