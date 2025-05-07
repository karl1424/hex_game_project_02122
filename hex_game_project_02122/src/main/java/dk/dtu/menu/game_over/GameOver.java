package dk.dtu.menu.game_over;

import dk.dtu.main.GamePanel;
import dk.dtu.menu.MenuManager;
import dk.dtu.menu.MenuPanel;

public class GameOver extends MenuPanel {

    public GameOver(MenuManager manager, GamePanel gamePanel) {
        super(manager, gamePanel);
    }

    @Override
    protected void createUI() {
        GameOverPane pane = new GameOverPane(manager, gamePanel);
        getChildren().add(pane);
    }

    public void setWinner(int winner) {
        ((GameOverPane) getChildren().get(0)).setWinner(winner);
    }
}
