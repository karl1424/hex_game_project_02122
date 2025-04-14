package dk.dtu.main;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Menu extends Pane {
    private GamePanel gamePanel;
    private Stage primaryStage;

    private BorderPane computerSetupPane;
    private BorderPane localSetUpPane;
    private VBox mainMenuPane;
    private StackPane gameOverPane;
    private CheckBox player1CheckBox;
    private CheckBox player2CheckBox;

    private CheckBox compSizeSmallCheckBox;
    private CheckBox compSizeMediumCheckBox;
    private CheckBox compSizeLargeCheckBox;

    private CheckBox localSizeSmallCheckBox;
    private CheckBox localSizeMediumCheckBox;
    private CheckBox localSizeLargeCheckBox;

    public Menu(GamePanel gamePanel, Stage primaryStage) {
        this.gamePanel = gamePanel;
        this.primaryStage = primaryStage;

        setPrefSize(600, 600);
        setupMainMenu();
        computerOpponentSetup();
        localGameSetup();
        setupGameOver();

        showMainMenu();
    }

    private void setupMainMenu() {
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

        playVsComputerBtn.setOnAction(e -> showComputerSetup());
        playLocalBtn.setOnAction(e -> showLocalSetUp());
        playOnlineBtn.setOnAction(e -> {
            System.out.println("Not implemented yet");
        });
        exitBtn.setOnAction(e -> System.exit(0));
    }

    private void computerOpponentSetup() {
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

        compSizeSmallCheckBox = Help.creatCheckBox("Small", false);
        compSizeMediumCheckBox = Help.creatCheckBox("Medium", true);
        compSizeLargeCheckBox = Help.creatCheckBox("Large", false);

        CheckBox[] compSizeCheckBoxes = { compSizeSmallCheckBox, compSizeMediumCheckBox, compSizeLargeCheckBox };
        for (CheckBox cb : compSizeCheckBoxes) {
            cb.setOnAction(e -> {
                for (CheckBox otherCb : compSizeCheckBoxes) {
                    if (otherCb != cb)
                        otherCb.setSelected(false);
                }
                if (!cb.isSelected())
                    cb.setSelected(true);
            });
        }

        sizeBox.getChildren().addAll(sizeLabel, compSizeSmallCheckBox, compSizeMediumCheckBox, compSizeLargeCheckBox);

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
        backBtn.setOnAction(e -> showMainMenu());
    }

    private void localGameSetup() {
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

        localSizeSmallCheckBox = Help.creatCheckBox("Small", false);
        localSizeMediumCheckBox = Help.creatCheckBox("Medium", true);
        localSizeLargeCheckBox = Help.creatCheckBox("Large", false);

        CheckBox[] localSizeCheckBoxes = { localSizeSmallCheckBox, localSizeMediumCheckBox, localSizeLargeCheckBox };
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

        sizeBox.getChildren().addAll(sizeLabel, localSizeSmallCheckBox, localSizeMediumCheckBox,
                localSizeLargeCheckBox);

        centerContent.getChildren().add(sizeBox);
        localSetUpPane.setCenter(centerContent);

        HBox buttonBox = new HBox(30);
        buttonBox.setAlignment(Pos.CENTER);

        Button startGameBtn = Help.createButton("Start Game", 150, 40, false);
        Button backBtn = Help.createButton("Back", 150, 40, false);

        buttonBox.getChildren().addAll(backBtn, startGameBtn);
        localSetUpPane.setBottom(buttonBox);
        BorderPane.setAlignment(buttonBox, Pos.CENTER);

        startGameBtn.setOnAction(e -> startGameLocal());
        backBtn.setOnAction(e -> showMainMenu());
    }

    private void setupGameOver() {
        gameOverPane = new StackPane();
        gameOverPane.setPrefSize(600, 600);

        Rectangle overlay = new Rectangle(600, 600);
        overlay.setFill(Color.rgb(0, 0, 0, 0.5));

        VBox popupContent = new VBox(20);
        popupContent.setAlignment(Pos.CENTER);
        popupContent.setMaxWidth(300);
        popupContent.setMaxHeight(250);
        popupContent.setStyle(
                "-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 10, 0, 0, 5);");
        popupContent.setPadding(new Insets(25));

        Label titleLabel = Help.createLabel("Game Over", 36, true);
        Label resultLabel = Help.createLabel("", 24, true);

        Button playAgainBtn = Help.createButton("Play Again", 140, 35, false);
        Button mainMenuBtn = Help.createButton("Main Menu", 140, 35, false);

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(playAgainBtn, mainMenuBtn);

        popupContent.getChildren().addAll(titleLabel, resultLabel, buttonBox);
        gameOverPane.getChildren().addAll(overlay, popupContent);
    }

    public void showGameOver(int winner, GamePanel gamePanel) {
        Label resultLabel = (Label) ((VBox) gameOverPane.getChildren().get(1)).getChildren().get(1);

        if (winner == 1) {
            resultLabel.setText("Player 1 wins!");
            resultLabel.setTextFill(Color.RED);
        } else {
            resultLabel.setText("Player 2 wins!");
            resultLabel.setTextFill(Color.BLUE);
        }

        Button playAgainBtn = (Button) ((HBox) ((VBox) gameOverPane.getChildren().get(1)).getChildren().get(2))
                .getChildren().get(0);
        Button mainMenuBtn = (Button) ((HBox) ((VBox) gameOverPane.getChildren().get(1)).getChildren().get(2))
                .getChildren().get(1);

        playAgainBtn.setOnAction(e -> {
            gamePanel.getChildren().remove(gameOverPane);
            gamePanel.resetGame();
        });

        mainMenuBtn.setOnAction(e -> {
            primaryStage.getScene().setRoot(this);
            showMainMenu();
        });

        if (!gamePanel.getChildren().contains(gameOverPane)) {
            gamePanel.getChildren().add(gameOverPane);
        }
    }

    private void showMainMenu() {
        getChildren().clear();
        getChildren().add(mainMenuPane);
    }

    private void showComputerSetup() {
        getChildren().clear();
        getChildren().add(computerSetupPane);
    }

    private void showLocalSetUp() {
        getChildren().clear();
        getChildren().add(localSetUpPane);
    }

    private void startGame() {
        int computerPlayer = player1CheckBox.isSelected() ? 2 : 1;

        int gridSize = 7;
        if (compSizeSmallCheckBox.isSelected()) {
            gridSize = 3;
        } else if (compSizeMediumCheckBox.isSelected()) {
            gridSize = 7;
        } else if (compSizeLargeCheckBox.isSelected()) {
            gridSize = 11;
        }

        gamePanel.gameInit(gridSize, computerPlayer);
        primaryStage.getScene().setRoot(gamePanel);
    }

    private void startGameLocal() {
        int gridSize = 7;

        if (localSizeSmallCheckBox.isSelected()) {
            gridSize = 3;
        } else if (localSizeMediumCheckBox.isSelected()) {
            gridSize = 7;
        } else if (localSizeLargeCheckBox.isSelected()) {
            gridSize = 11;
        }
        gamePanel.gameInit(gridSize, 0);
        primaryStage.getScene().setRoot(gamePanel);
    }
}
