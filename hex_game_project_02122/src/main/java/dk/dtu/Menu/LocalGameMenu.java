package dk.dtu.Menu;

import dk.dtu.main.GamePanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class LocalGameMenu extends MenuPanel{
    private BorderPane localSetUpPane;

    private CheckBox sizeSmallCheckBox;
    private CheckBox sizeMediumCheckBox;
    private CheckBox sizeLargeCheckBox;

    public LocalGameMenu(MenuManager manager, GamePanel gamePanel) {
        super(manager, gamePanel);
    }

    @Override
    protected void createUI() {
        localSetUpPane = new BorderPane();
        localSetUpPane.setPrefSize(600, 600);
        localSetUpPane.setPadding(new Insets(40));

        Label titleLabel = Help.createTitleLabel("HEX GAME", 60);
        VBox titleBox = new VBox(titleLabel);
        titleBox.setAlignment(Pos.CENTER);
        localSetUpPane.setTop(titleBox);

        VBox centerContent = new VBox(30);
        centerContent.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(0, 0, -180, 0));

        HBox sizeBox = new HBox(25);
        sizeBox.setAlignment(Pos.CENTER);

        Label sizeLabel = Help.createLabel("Board Size", 18, true);

        sizeSmallCheckBox = Help.creatCheckBox("Small", false);
        sizeMediumCheckBox = Help.creatCheckBox("Medium", true);
        sizeLargeCheckBox = Help.creatCheckBox("Large", false);

        CheckBox[] localSizeCheckBoxes = { sizeSmallCheckBox, sizeMediumCheckBox, sizeLargeCheckBox };
        // We can ensure that only one checkbox is selected at a time
        for (CheckBox cb : localSizeCheckBoxes) {
            cb.setOnAction(e -> {
                for (CheckBox otherCb : localSizeCheckBoxes) {
                    if (otherCb != cb)
                        otherCb.setSelected(false);
                }
                if (!cb.isSelected())
                    cb.setSelected(true);
            });
        }

        sizeBox.getChildren().addAll(sizeLabel, sizeSmallCheckBox, sizeMediumCheckBox, sizeLargeCheckBox);

        centerContent.getChildren().add(sizeBox);
        localSetUpPane.setCenter(centerContent);

        HBox buttonBox = new HBox(30);
        buttonBox.setAlignment(Pos.CENTER);

        Button startGameBtn = Help.createButton("Start Game", 150, 40, false);
        Button backBtn = Help.createButton("Back", 150, 40, false);

        buttonBox.getChildren().addAll(backBtn, startGameBtn);
        localSetUpPane.setBottom(buttonBox);
        BorderPane.setAlignment(buttonBox, Pos.CENTER);

        startGameBtn.setOnAction(e -> startLocalGame());
        backBtn.setOnAction(e -> manager.showMainMenu());
        
        getChildren().add(localSetUpPane);
    }
    
    private void startLocalGame() {
        int gridSize = 7;

        if (sizeSmallCheckBox.isSelected()) {
            gridSize = 3;
        } else if (sizeMediumCheckBox.isSelected()) {
            gridSize = 7;
        } else if (sizeLargeCheckBox.isSelected()) {
            gridSize = 11;
        }
        
        manager.startGame(gridSize, 0);
    }
}
