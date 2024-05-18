package todo.todo;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    private LoginService loginService = new LoginService();

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

        signOutButton.setOnAction(event -> {
            String username = Page.loginScene.getLoggedInUsername();
            String password = Page.loginScene.getLoggedInPassword();
            loginService.sendLogoutMessage(username, password);
            Scene todoScene = Page.loginScene.createScene();
            Stage stage = (Stage) todomainPane.getScene().getWindow();
            stage.setScene(todoScene);
        });

        Scene scene = new Scene(todomainPane, 450, 800);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        return scene;
    }
}