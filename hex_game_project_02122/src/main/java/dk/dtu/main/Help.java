package dk.dtu.main;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Help {

    public static Button createButton(String text,double width, double height, boolean bold) {
        Button button = new Button(text);
        button.setPrefWidth(width);
        button.setPrefHeight(height);
        FontWeight weight = bold ? FontWeight.BOLD : FontWeight.NORMAL;
        button.setFont(Font.font("Arial", weight, 16));
        return button;
    }

    public static Label createLabel(String text, double fontSize, boolean bold) {
        Label label = new Label(text);
        FontWeight weight = bold ? FontWeight.BOLD : FontWeight.NORMAL;
        label.setFont(Font.font("Arial", weight, fontSize));
        return label;
    }

    public static Label createTitleLabel(String text, double fontSize) {
        Label titleLabel = new Label(text);
        titleLabel.setFont(Font.font("Consolas", FontWeight.BOLD, fontSize > 0 ? fontSize : 60));
        titleLabel.setStyle(
                "-fx-text-fill: linear-gradient(from 0% 0% to 100% 0%, red 10%, blue 60%);" +
                "-fx-effect: dropshadow(gaussian, black, 4, 0.7, 0, 0);" +
                "-fx-padding: 20 0 40 0;"
        );
        
        return titleLabel;
    }

    public static CheckBox creatCheckBox(String text, boolean selected) {
        CheckBox checkBox = new CheckBox(text);
        checkBox.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        checkBox.setSelected(selected);
        return checkBox;
    }

}
