package dk.dtu.menu.local.computer_opponent;

import dk.dtu.menu.Help;
import dk.dtu.menu.MenuManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ComputerSetupPane extends BorderPane {

    private CheckBox player1CheckBox;
    private CheckBox player2CheckBox;
    private CheckBox sizeSmallCheckBox;
    private CheckBox sizeMediumCheckBox;
    private CheckBox sizeLargeCheckBox;
    private CheckBox difficultyEasyCheckBox;
    private CheckBox difficultyMediumCheckBox;
    private CheckBox difficultyHardCheckBox;

    public ComputerSetupPane(MenuManager manager) {
        setPrefSize(600, 600);
        setPadding(new Insets(40));

        Label titleLabel = Help.createTitleLabel("HEX GAME", 60);
        VBox titleBox = new VBox(titleLabel);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(0, 0, -120, 0));
        setTop(titleBox);

        VBox centerContent = new VBox(30);
        centerContent.setAlignment(Pos.CENTER);

        HBox playerBox = new HBox(50);
        playerBox.setAlignment(Pos.CENTER);
        Label playerLabel = Help.createLabel("Player", 18, true);

        player1CheckBox = Help.creatCheckBox("1", true);
        player2CheckBox = Help.creatCheckBox("2", false);

        player1CheckBox.setOnAction(_ -> {
            player2CheckBox.setSelected(!player1CheckBox.isSelected());
        });

        player2CheckBox.setOnAction(_ -> {
            player1CheckBox.setSelected(!player2CheckBox.isSelected());
        });

        playerBox.getChildren().addAll(playerLabel, player1CheckBox, player2CheckBox);

        HBox sizeBox = new HBox(25);
        sizeBox.setAlignment(Pos.CENTER);
        Label sizeLabel = Help.createLabel("Board Size", 18, true);

        sizeSmallCheckBox = Help.creatCheckBox("Small", false);
        sizeMediumCheckBox = Help.creatCheckBox("Medium", true);
        sizeLargeCheckBox = Help.creatCheckBox("Large", false);

        CheckBox[] checkBoxes = { sizeSmallCheckBox, sizeMediumCheckBox, sizeLargeCheckBox };
        for (CheckBox cb : checkBoxes) {
            cb.setOnAction(_ -> {
                for (CheckBox other : checkBoxes) {
                    if (other != cb) other.setSelected(false);
                }
                if (!cb.isSelected()) cb.setSelected(true);
            });
        }

        sizeBox.getChildren().addAll(sizeLabel, sizeSmallCheckBox, sizeMediumCheckBox, sizeLargeCheckBox);
        HBox difficultyBox = new HBox(25);
        difficultyBox.setAlignment(Pos.CENTER);
        Label difficultyLabel = Help.createLabel("Difficulty", 18, true);

        difficultyEasyCheckBox = Help.creatCheckBox("Easy", false);
        difficultyMediumCheckBox = Help.creatCheckBox("Medium", true);
        difficultyHardCheckBox = Help.creatCheckBox("Hard", false);

        CheckBox[] difficultyCheckBoxes = { difficultyEasyCheckBox, difficultyMediumCheckBox, difficultyHardCheckBox };
        for (CheckBox cb : difficultyCheckBoxes) {
            cb.setOnAction(e -> {
                for (CheckBox other : difficultyCheckBoxes) {
                    if (other != cb) other.setSelected(false);
                }
                if (!cb.isSelected()) cb.setSelected(true);
            });
        }
        difficultyBox.getChildren().addAll(difficultyLabel, difficultyEasyCheckBox, difficultyMediumCheckBox, difficultyHardCheckBox);

        centerContent.getChildren().addAll(playerBox, sizeBox,difficultyBox);
        setCenter(centerContent);

        HBox buttonBox = new HBox(30);
        buttonBox.setAlignment(Pos.CENTER);
        Button startBtn = Help.createButton("Start Game", 150, 40, false);
        Button backBtn = Help.createButton("Back", 150, 40, false);

        startBtn.setOnAction(_ -> {
            int player = player1CheckBox.isSelected() ? 2 : 1;
            int size = sizeSmallCheckBox.isSelected() ? 3 : sizeLargeCheckBox.isSelected() ? 11 : 7;
            int number = player1CheckBox.isSelected() ? 1 : 2;
            int difficulty = difficultyEasyCheckBox.isSelected() ? 500 : difficultyHardCheckBox.isSelected() ? 10000 : 5000;
            manager.startGame(size, player, number,difficulty);
        });

        backBtn.setOnAction(_ -> manager.showMainMenu());

        buttonBox.getChildren().addAll(backBtn, startBtn);
        setBottom(buttonBox);
        BorderPane.setAlignment(buttonBox, Pos.CENTER);
    }
}