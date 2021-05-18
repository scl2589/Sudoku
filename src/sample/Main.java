package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello Sudoku");
        primaryStage.setScene(new Scene(root, 500, 550));
        primaryStage.show();
        SQLiteManager manager = new SQLiteManager();
        manager.createConnection();     // 연결
        manager.closeConnection();      // 연결 해제
        manager.ensureConnection();     // 재연결
    }

    public static void main(String[] args) {
        launch(args);
    }

}
