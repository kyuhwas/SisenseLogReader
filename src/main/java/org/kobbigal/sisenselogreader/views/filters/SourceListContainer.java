package org.kobbigal.sisenselogreader.views.filters;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.kobbigal.sisenselogreader.model.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class SourceListContainer extends VBox {

    private ListView<String> valueList;
    private ObjectProperty<Predicate<Log>> filter;

    SourceListContainer() {
        this.setSpacing(5);

        filter = new SimpleObjectProperty<>();

        Label label = new Label("Sources");
        label.setFont(Font.font("Agency FB", FontWeight.BOLD, 16));

        valueList = new ListView<>();
        valueList.setPrefHeight(valueList.getItems().size() * 24);

        this.getChildren().addAll(label, valueList);
    }

    Predicate<Log> getFilter() {
        return filter.get();
    }

    void setList(FilteredList<Log> logList) {
        List<String> l = new ArrayList<>();

        for (Log log : logList){
            l.add(log.getSource());
        }

        System.out.println("Sources values: " + new HashSet<>(l));

        valueList.getItems().addAll(new HashSet<>(l));
        valueList.setPrefHeight(valueList.getItems().size() * 24);
        valueList.setTooltip(new Tooltip("Hold CMD/CTRL to select multiple values"));

        ObjectBinding<Predicate<Log>> binding = new ObjectBinding<Predicate<Log>>() {

            private final Set<String> sources = new HashSet<>();
            {
                valueList.getSelectionModel().getSelectedItems().addListener((ListChangeListener<String>) c -> {
                    boolean changed = false;

                    while (c.next()){

                        if (c.wasRemoved()){
                            changed = true;
                            c.getRemoved().stream().map(String::toLowerCase).forEach(sources::remove);
                        }

                        if (c.wasAdded()){
                            changed = true;
                            try {
                                c.getAddedSubList().stream().map(String::toLowerCase).forEach(sources::add);
                            }
                            catch (IndexOutOfBoundsException e){
                                e.printStackTrace();
                            }
                        }
                    }
                    if (changed){
                        invalidate();
                    }
                });
            }
            @Override
            protected Predicate<Log> computeValue() {
                return log -> sources.contains(log.getSource().toLowerCase());
            }
        };
        filter.bind(binding);

    }

    public void clearList() {
        valueList.getItems().clear();
    }
}
