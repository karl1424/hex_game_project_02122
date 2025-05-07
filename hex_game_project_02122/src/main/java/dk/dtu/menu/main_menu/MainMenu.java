package dk.dtu.menu.main_menu;

import dk.dtu.main.GamePanel;
import dk.dtu.menu.MenuManager;
import dk.dtu.menu.MenuPanel;

public class MainMenu extends MenuPanel {

    public MainMenu(MenuManager manager, GamePanel gamePanel) {
        super(manager, gamePanel);
    }

    @Override
    protected void createUI() {
        MainMenuPane pane = new MainMenuPane(manager);
        getChildren().add(pane);
    }
}
