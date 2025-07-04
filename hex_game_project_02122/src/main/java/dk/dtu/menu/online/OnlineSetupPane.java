package dk.dtu.menu.online;

import dk.dtu.menu.Help;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

public class OnlineSetupPane extends VBox {
    Label serverIsDownLabel;

    public OnlineSetupPane(OnlineGameMenu parent) {
        super(20);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(50));
        setPrefSize(600, 600);

        Label titleLabel = Help.createTitleLabel("HEX GAME", 60);
        Button hostBtn = Help.createButton("Host", 200, 40, true);
        Button joinBtn = Help.createButton("Join", 200, 40, true);
        Button backBtn = Help.createButton("Back", 200, 40, true);

        serverIsDownLabel = Help.createLabel("Server is down", 20, false);
        serverIsDownLabel.setVisible(false);

        getChildren().addAll(titleLabel, hostBtn, joinBtn, backBtn, serverIsDownLabel);

        hostBtn.setOnAction(_ -> parent.onHost(0));
        joinBtn.setOnAction(_ -> parent.onJoin());
        backBtn.setOnAction(_ -> parent.onBack());
    }

    public void serverIsDown() {
        serverIsDownLabel.setVisible(true);
    }
}
