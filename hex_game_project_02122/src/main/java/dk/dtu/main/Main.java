package dk.dtu.main;

import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.Space;

import javafx.application.Application;
import javafx.event.*;
import javafx.scene.control.Button;
import javafx.scene.input.*;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        GamePanel gamePanel = new GamePanel();
        Scene scene = new Scene(gamePanel);

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
 