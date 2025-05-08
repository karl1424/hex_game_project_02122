package dk.dtu.menu.local.player_vs_player;

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

public class LocalGamePane extends BorderPane {

    private CheckBox sizeSmallCheckBox;
    private CheckBox sizeMediumCheckBox;
    private CheckBox sizeLargeCheckBox;

    public LocalGamePane(MenuManager manager) {
        setPrefSize(600, 600);
        setPadding(new Insets(40));

        Label titleLabel = Help.createTitleLabel("HEX GAME", 60);
        VBox titleBox = new VBox(titleLabel);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(0, 0, -180, 0));
        setTop(titleBox);

        VBox center = new VBox(30);
        center.setAlignment(Pos.CENTER);

        HBox sizeBox = new HBox(25);
        sizeBox.setAlignment(Pos.CENTER);
        Label sizeLabel = Help.createLabel("Board Size", 18, true);

        sizeSmallCheckBox = Help.creatCheckBox("Small", false);
        sizeMediumCheckBox = Help.creatCheckBox("Medium", true);
        sizeLargeCheckBox = Help.creatCheckBox("Large", false);

        CheckBox[] boxes = { sizeSmallCheckBox, sizeMediumCheckBox, sizeLargeCheckBox };
        for (CheckBox cb : boxes) {
            cb.setOnAction(_ -> {
                for (CheckBox other : boxes)
                    if (cb != other) other.setSelected(false);
                if (!cb.isSelected()) cb.setSelected(true);
            });
        }

        sizeBox.getChildren().addAll(sizeLabel, sizeSmallCheckBox, sizeMediumCheckBox, sizeLargeCheckBox);
        center.getChildren().add(sizeBox);
        setCenter(center);

        HBox buttonBox = new HBox(30);
        buttonBox.setAlignment(Pos.CENTER);

        Button startBtn = Help.createButton("Start Game", 150, 40, false);
        Button backBtn = Help.createButton("Back", 150, 40, false);

        startBtn.setOnAction(_ -> {
            int size = sizeSmallCheckBox.isSelected() ? 3 : sizeLargeCheckBox.isSelected() ? 11 : 7;
            manager.startGame(size, 0, 1);
        });

        backBtn.setOnAction(_ -> manager.showMainMenu());

        buttonBox.getChildren().addAll(backBtn, startBtn);
        setBottom(buttonBox);
        BorderPane.setAlignment(buttonBox, Pos.CENTER);
    }
}
