package dk.dtu.menu;

import dk.dtu.main.GamePanel;
import javafx.scene.layout.Pane;

public abstract class MenuPanel extends Pane {
    protected MenuManager manager;
    protected GamePanel gamePanel;

    public MenuPanel(MenuManager manager, GamePanel gamePanel) {
        this.manager = manager;
        this.gamePanel = gamePanel;
        setPrefSize(600, 600);
        createUI();
    }

    protected abstract void createUI();
}
