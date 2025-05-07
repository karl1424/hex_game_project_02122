package dk.dtu.menu.local.computer_opponent;

import dk.dtu.main.GamePanel;
import dk.dtu.menu.MenuManager;
import dk.dtu.menu.MenuPanel;

public class ComputerSetUpMenu extends MenuPanel {

    public ComputerSetUpMenu(MenuManager manager, GamePanel gamePanel) {
        super(manager, gamePanel);
    }

    @Override
    protected void createUI() {
        ComputerSetupPane pane = new ComputerSetupPane(manager);
        getChildren().add(pane);
    }
}