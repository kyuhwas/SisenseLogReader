package org.kobbigal.sisenselogreader.views.filters;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

class ComponentSearchboxContainer extends VBox {

    private TextField searchField;

    ComponentSearchboxContainer(){
        this.setSpacing(5);

        Label label = new Label("Component");
        label.setFont(Font.font("Agency FB", FontWeight.BOLD, 16));

        searchField = new TextField();
        searchField.setPromptText("e.g. Prism.Shared.ETL");

        this.getChildren().addAll(label, searchField);
    }

    String getSearchText() {
        return searchField.getText();
    }
}
