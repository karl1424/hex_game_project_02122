package dk.dtu.menu.main_menu;

import dk.dtu.menu.Help;
import dk.dtu.menu.MenuManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class MainMenuPane extends VBox {

    public MainMenuPane(MenuManager manager) {
        setPrefSize(600, 600);
        setAlignment(Pos.CENTER);
        setSpacing(20);
        setPadding(new Insets(40));

        Label titleLabel = Help.createTitleLabel("HEX GAME", 60);
        titleLabel.setPadding(new Insets(0, 0, 40, 0));

        Button localGameBtn = Help.createButton("Player vs Player", 180, 40, false);
        Button computerBtn = Help.createButton("Computer Opponent", 180, 40, false);
        Button onlineBtn = Help.createButton("Play Online", 180, 40, false);
        Button exitBtn = Help.createButton("Quit", 180, 40, false);

        localGameBtn.setOnAction(_ -> manager.showLocalSetup());
        computerBtn.setOnAction(_ -> manager.showComputerSetup());
        onlineBtn.setOnAction(_ -> manager.showOnlineSetup());
        exitBtn.setOnAction(_ -> System.exit(0));

        getChildren().addAll(titleLabel, localGameBtn, computerBtn, onlineBtn, exitBtn);
    }
}
