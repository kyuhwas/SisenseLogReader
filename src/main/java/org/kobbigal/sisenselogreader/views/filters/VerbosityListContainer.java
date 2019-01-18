package org.kobbigal.sisenselogreader.views.filters;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.kobbigal.sisenselogreader.model.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class VerbosityListContainer extends VBox {

    private ListView<String> valueList;

    VerbosityListContainer() {
        this.setSpacing(5);

        Label label = new Label("Verbosity");
        label.setFont(Font.font("Agency FB", FontWeight.BOLD, 16));

        valueList = new ListView<>();
        valueList.setPrefHeight(valueList.getItems().size() * 24);

        this.getChildren().addAll(label, valueList);
    }

    void setList(FilteredList<Log> logList) {

        List<String> l = new ArrayList<>();

        for (Log log : logList){
            l.add(log.getVerbosity());
        }

        System.out.println("Verbosity values: " + new HashSet<>(l));

        valueList.getItems().addAll(new HashSet<>(l));
        valueList.setPrefHeight(valueList.getItems().size() * 24);
        valueList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        valueList.getSelectionModel().selectAll();
        valueList.setTooltip(new Tooltip("Hold CMD/CTRL to select multiple values"));

    }

    void selectAll(){
        valueList.getSelectionModel().selectAll();
    }

    void clearList() {
        valueList.getItems().clear();
    }

    public ObservableList<String> getList() {
        return valueList.getSelectionModel().getSelectedItems();
    }
}
