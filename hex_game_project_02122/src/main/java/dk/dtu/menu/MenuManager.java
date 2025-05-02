package dk.dtu.menu;

import dk.dtu.connection.Client;
import dk.dtu.main.GamePanel;
import dk.dtu.menu.online.OnlineGameMenu;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MenuManager extends Pane {
    private GamePanel gamePanel;
    private Client client;
    private Stage primaryStage;
    
    private MainMenu mainMenuPanel;
    private ComputerSetUpMenu computerSetupPanel;
    private LocalGameMenu localGamePanel;
    public OnlineGameMenu onlinePanel;
    private GameOver gameOverPanel;
    
    public MenuManager(GamePanel gamePanel, Client client, Stage primaryStage) {
        this.gamePanel = gamePanel;
        this.client = client;
        this.primaryStage = primaryStage;
        
        setPrefSize(600, 600);
        initializePanels();
        showMainMenu();
    }
    
    private void initializePanels() {
        mainMenuPanel = new MainMenu(this, gamePanel);
        computerSetupPanel = new ComputerSetUpMenu(this, gamePanel);
        localGamePanel = new LocalGameMenu(this, gamePanel);
        onlinePanel = new OnlineGameMenu(this, gamePanel, client);
        gameOverPanel = new GameOver(this, gamePanel);
    }
    
    public void showMainMenu() {
        showPanel(mainMenuPanel, false);
    }
    
    public void showComputerSetup() {
        showPanel(computerSetupPanel, false);
    }
    
    public void showLocalSetup() {
        showPanel(localGamePanel, false);
    }
    
    public void showOnlineSetup() {
        showPanel(onlinePanel, true);
    }
    
    
    public void showGameOver(int winner) {
        gameOverPanel.setWinner(winner);
        if (!gamePanel.getChildren().contains(gameOverPanel)) {
            gamePanel.getChildren().add(gameOverPanel);
        }
    }
    
    private void showPanel(MenuPanel panel, boolean flag) {
        getChildren().clear();
        if(flag){
            onlinePanel = new OnlineGameMenu(this, gamePanel, client);
            getChildren().add(onlinePanel);
        } else {
            getChildren().add(panel);
        }
    }
    
    public void startGame(int gridSize, int computerPlayer, int playerNumber) {
        gamePanel.gameInit(gridSize, computerPlayer, playerNumber);
        primaryStage.getScene().setRoot(gamePanel);
    }
    
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
    public GamePanel getGamePanel() {
        return gamePanel;
    }
    
    public Client getClient() {
        return client;
    }
}