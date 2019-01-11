package org.kobbigal.sisenselogreader.views.table;

import javafx.collections.transformation.FilteredList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.kobbigal.sisenselogreader.model.Log;

import java.util.Date;

public class LogTableContainer extends VBox {

    private TableView<Log> table = new TableView<>();

    public LogTableContainer(){
        this.setSpacing(0);
        table.setPrefHeight(400);

        TableColumn<Log, String> sourceColumn = new TableColumn<>("Source");
        sourceColumn.setSortable(false);
        sourceColumn.setMinWidth(80);

        TableColumn<Log, Date> timeColumn = new TableColumn<>("Time");
        timeColumn.setMinWidth(180);
        timeColumn.setSortable(true);

        TableColumn<Log, String> verbosityColumn = new TableColumn<>("Log Level");
        verbosityColumn.setSortable(false);
        verbosityColumn.setMinWidth(60);

        TableColumn<Log, String> componentColumn = new TableColumn<>("Class");
        componentColumn.setSortable(false);
        componentColumn.setMinWidth(200);

        TableColumn<Log, String> detailsColumn = new TableColumn<>("Details");
        detailsColumn.setSortable(false);
        detailsColumn.setMinWidth(575);

        sourceColumn.setCellValueFactory(new PropertyValueFactory<>("source"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        verbosityColumn.setCellValueFactory(new PropertyValueFactory<>("verbosity"));
        componentColumn.setCellValueFactory(new PropertyValueFactory<>("component"));
        detailsColumn.setCellValueFactory(new PropertyValueFactory<>("details"));

        table.getColumns().addAll(sourceColumn, timeColumn, verbosityColumn, componentColumn, detailsColumn);
        this.getChildren().add(table);

    }

    public void setTableItems(FilteredList<Log> logs){
        this.table.getItems().addAll(logs);
    }
}
