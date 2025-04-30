package dk.dtu.menu1;

import dk.dtu.main.GamePanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ComputerSetUpMenu extends MenuPanel{
    private BorderPane computerSetupPane;

    private CheckBox player1CheckBox;
    private CheckBox player2CheckBox;
    private CheckBox sizeSmallCheckBox;
    private CheckBox sizeMediumCheckBox;
    private CheckBox sizeLargeCheckBox;

    public ComputerSetUpMenu(MenuManager manager, GamePanel gamePanel) {
        super(manager, gamePanel);
    }

    @Override
    protected void createUI() {
        computerSetupPane = new BorderPane();
        computerSetupPane.setPrefSize(600, 600);
        computerSetupPane.setPadding(new Insets(40));

        Label titleLabel = Help.createTitleLabel("HEX GAME", 60);
        VBox titleBox = new VBox(titleLabel);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(0, 0, -120, 0));
        computerSetupPane.setTop(titleBox);

        VBox centerContent = new VBox(30);
        centerContent.setAlignment(Pos.CENTER);

        HBox playerBox = new HBox(50);
        playerBox.setAlignment(Pos.CENTER);

        Label playerLabel = Help.createLabel("Player", 18, true);

        player1CheckBox = Help.creatCheckBox("1", true);
        player2CheckBox = Help.creatCheckBox("2", false);

        player1CheckBox.setOnAction(e -> {
            if (player1CheckBox.isSelected())
                player2CheckBox.setSelected(false);
            else
                player2CheckBox.setSelected(true);
        });

        player2CheckBox.setOnAction(e -> {
            if (player2CheckBox.isSelected())
                player1CheckBox.setSelected(false);
            else
                player1CheckBox.setSelected(true);
        });

        playerBox.getChildren().addAll(playerLabel, player1CheckBox, player2CheckBox);

        HBox sizeBox = new HBox(25);
        sizeBox.setAlignment(Pos.CENTER);

        Label sizeLabel = Help.createLabel("Board Size", 18, true);

        sizeSmallCheckBox = Help.creatCheckBox("Small", false);
        sizeMediumCheckBox = Help.creatCheckBox("Medium", true);
        sizeLargeCheckBox = Help.creatCheckBox("Large", false);

        CheckBox[] sizeCheckBoxes = { sizeSmallCheckBox, sizeMediumCheckBox, sizeLargeCheckBox };
        for (CheckBox cb : sizeCheckBoxes) {
            cb.setOnAction(e -> {
                for (CheckBox otherCb : sizeCheckBoxes) {
                    if (otherCb != cb)
                        otherCb.setSelected(false);
                }
                if (!cb.isSelected())
                    cb.setSelected(true);
            });
        }

        sizeBox.getChildren().addAll(sizeLabel, sizeSmallCheckBox, sizeMediumCheckBox, sizeLargeCheckBox);

        centerContent.getChildren().addAll(playerBox, sizeBox);
        computerSetupPane.setCenter(centerContent);

        HBox buttonBox = new HBox(30);
        buttonBox.setAlignment(Pos.CENTER);

        Button startGameBtn = Help.createButton("Start Game", 150, 40, false);
        Button backBtn = Help.createButton("Back", 150, 40, false);

        buttonBox.getChildren().addAll(backBtn, startGameBtn);
        computerSetupPane.setBottom(buttonBox);
        BorderPane.setAlignment(buttonBox, Pos.CENTER);

        startGameBtn.setOnAction(e -> startGame());
        backBtn.setOnAction(e -> manager.showMainMenu());
        
        getChildren().add(computerSetupPane);
    }
    
    private void startGame() {
        int computerPlayer = player1CheckBox.isSelected() ? 2 : 1;

        int gridSize = 7;
        if (sizeSmallCheckBox.isSelected()) {
            gridSize = 3;
        } else if (sizeMediumCheckBox.isSelected()) {
            gridSize = 7;
        } else if (sizeLargeCheckBox.isSelected()) {
            gridSize = 11;
        }

        manager.startGame(gridSize, computerPlayer);
    }
}
