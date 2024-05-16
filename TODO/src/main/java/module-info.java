module todo.todo {
    requires javafx.controls;
    requires javafx.fxml;


    opens todo.todo to javafx.fxml;
    exports todo.todo;
}