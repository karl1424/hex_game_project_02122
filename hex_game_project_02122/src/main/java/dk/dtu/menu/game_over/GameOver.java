package dk.dtu.menu.game_over;

import dk.dtu.main.GamePanel;
import dk.dtu.menu.MenuManager;
import dk.dtu.menu.MenuPanel;

public class GameOver extends MenuPanel {

    private GameOverPane pane;

    public GameOver(MenuManager manager, GamePanel gamePanel) {
        super(manager, gamePanel);
    }

    @Override
    protected void createUI() {
        pane = new GameOverPane(manager, gamePanel);
        getChildren().add(pane);
    }

    public void setWinner(int winner) {
        pane.setWinner(winner);
    }

    public void setLocal() {
        pane.setLocal();
    }

    public void setOnline() {
        pane.setOnline();
    }
}
