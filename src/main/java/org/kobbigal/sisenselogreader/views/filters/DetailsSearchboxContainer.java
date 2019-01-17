package org.kobbigal.sisenselogreader.views.filters;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

class DetailsSearchboxContainer extends VBox {

    private TextField searchField;

    DetailsSearchboxContainer(){
        this.setSpacing(5);

        Label label = new Label("Details");
        label.setFont(Font.font("Agency FB", FontWeight.BOLD, 16));

        searchField = new TextField();
        searchField.setPromptText("e.g. finished initializing");

        this.getChildren().addAll(label, searchField);
    }

    void clearSearchFieldText(){
        searchField.clear();
    }

    public String getText() {
        return searchField.getText();
    }
}
