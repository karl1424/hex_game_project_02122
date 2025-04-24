package dk.dtu.Menu;

import dk.dtu.main.GamePanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class MainMenu extends MenuPanel{
    private VBox mainMenuPane;
    
    public MainMenu(MenuManager manager, GamePanel gamePanel) {
        super(manager, gamePanel);
    }

    @Override
    protected void createUI() {
        mainMenuPane = new VBox(20);
        mainMenuPane.setAlignment(Pos.CENTER);
        mainMenuPane.setPadding(new Insets(50));
        mainMenuPane.setPrefSize(600, 600);

        Label titleLabel = Help.createTitleLabel("HEX GAME", 60);

        Button playVsComputerBtn = Help.createButton("Play vs Computer", 200, 40, true);
        Button playLocalBtn = Help.createButton("Play Local", 200, 40, true);
        Button playOnlineBtn = Help.createButton("Play Online", 200, 40, true);
        Button exitBtn = Help.createButton("Exit", 200, 40, true);

        mainMenuPane.getChildren().addAll(titleLabel, playVsComputerBtn, playLocalBtn, playOnlineBtn, exitBtn);

        // Center the VBox in the Pane
        mainMenuPane.layoutXProperty().bind(widthProperty().subtract(mainMenuPane.widthProperty()).divide(2));
        mainMenuPane.layoutYProperty().bind(heightProperty().subtract(mainMenuPane.heightProperty()).divide(2));

        playVsComputerBtn.setOnAction(_ -> manager.showComputerSetup());
        playLocalBtn.setOnAction(_ -> manager.showLocalSetup());
        playOnlineBtn.setOnAction(_ -> manager.showOnlineSetup());
        exitBtn.setOnAction(_ -> System.exit(0));

        getChildren().add(mainMenuPane);
    }

    
}
