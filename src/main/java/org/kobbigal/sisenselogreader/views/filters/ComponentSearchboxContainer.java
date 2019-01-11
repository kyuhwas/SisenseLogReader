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

class ComponentSearchboxContainer extends VBox {

    private ObjectProperty<Predicate<Log>> filter;

    ComponentSearchboxContainer(){
        this.setSpacing(5);

        filter = new SimpleObjectProperty<>();

        Label label = new Label("Component");
        label.setFont(Font.font("Agency FB", FontWeight.BOLD, 16));

        TextField searchField = new TextField();
        searchField.setPromptText("e.g. finished initializing");
        filter.bind(Bindings.createObjectBinding(() ->
                        log -> log.getComponent().toLowerCase().contains(searchField.getText().toLowerCase()),
                searchField.textProperty()
        ));

        this.getChildren().addAll(label, searchField);
    }

    Predicate<Log> getFilter() {
        return filter.get();
    }
}
