package org.kobbigal.sisenselogreader.views.filters;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.kobbigal.sisenselogreader.model.Log;

import java.util.function.Predicate;

class DetailsSearchboxContainer extends VBox {

    TextField searchField;
    private ObjectProperty<Predicate<Log>> filter;

    DetailsSearchboxContainer(){
        this.setSpacing(5);

        filter = new SimpleObjectProperty<>();

        Label label = new Label("Details");
        label.setFont(Font.font("Agency FB", FontWeight.BOLD, 16));

        searchField = new TextField();
        searchField.setPromptText("e.g. finished initializing");
        filter.bind(Bindings.createObjectBinding(() ->
                log -> log.getDetails().toLowerCase().contains(searchField.getText().toLowerCase()),
                searchField.textProperty()
        ));

        this.getChildren().addAll(label, searchField);
    }

    Predicate<Log> getFilter() {
        return filter.get();
    }

    public String getText() {
        return searchField.getText();
    }
}
