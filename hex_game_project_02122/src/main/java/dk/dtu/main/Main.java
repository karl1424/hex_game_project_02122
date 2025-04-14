package dk.dtu.main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        GamePanel gamePanel = new GamePanel(primaryStage);
        Menu menu = gamePanel.getMenu();
        Scene scene = new Scene(menu);

        primaryStage.setTitle("HEX Game");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.setOnCloseRequest((WindowEvent _) -> {
            System.exit(0);
        });
        primaryStage.show();
        gamePanel.requestFocus();
    }

}
