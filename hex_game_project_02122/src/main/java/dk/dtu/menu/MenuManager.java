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
    private OnlineGameMenu onlinePanel;
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
        showPanel(mainMenuPanel);
    }
    
    public void showComputerSetup() {
        showPanel(computerSetupPanel);
    }
    
    public void showLocalSetup() {
        showPanel(localGamePanel);
    }
    
    public void showOnlineSetup() {
        showPanel(onlinePanel);
    }
    
    
    public void showGameOver(int winner) {
        gameOverPanel.setWinner(winner);
        if (!gamePanel.getChildren().contains(gameOverPanel)) {
            gamePanel.getChildren().add(gameOverPanel);
        }
    }
    
    private void showPanel(MenuPanel panel) {
        getChildren().clear();
        getChildren().add(panel);
    }
    
    public void startGame(int gridSize, int computerPlayer) {
        gamePanel.gameInit(gridSize, computerPlayer);
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