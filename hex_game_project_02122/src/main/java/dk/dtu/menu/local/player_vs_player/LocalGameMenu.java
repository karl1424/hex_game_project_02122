package dk.dtu.menu.local.player_vs_player;

import dk.dtu.main.GamePanel;
import dk.dtu.menu.MenuManager;
import dk.dtu.menu.MenuPanel;

public class LocalGameMenu extends MenuPanel {

    public LocalGameMenu(MenuManager manager, GamePanel gamePanel) {
        super(manager, gamePanel);
    }

    @Override
    protected void createUI() {
        LocalGamePane pane = new LocalGamePane(manager);
        getChildren().add(pane);
    }
}
