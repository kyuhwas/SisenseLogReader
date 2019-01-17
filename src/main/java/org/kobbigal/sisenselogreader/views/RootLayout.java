package org.kobbigal.sisenselogreader.views;

import javafx.collections.transformation.FilteredList;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.kobbigal.sisenselogreader.controllers.DateSelectionController;
import org.kobbigal.sisenselogreader.model.Log;
import org.kobbigal.sisenselogreader.views.count.LogCountContainer;
import org.kobbigal.sisenselogreader.views.filters.FiltersContainer;
import org.kobbigal.sisenselogreader.views.menu.AppMenuBar;
import org.kobbigal.sisenselogreader.views.center.DateSelectionContainer;
import org.kobbigal.sisenselogreader.views.center.LogTableContainer;
import org.kobbigal.sisenselogreader.views.status.AppStatusContainer;

public class RootLayout extends BorderPane {

    private static RootLayout instance;
    private DateSelectionContainer dateSelectionContainer;
    private LogTableContainer logTableContainer;
    private LogCountContainer logCountContainer;
    private FiltersContainer filtersContainer;
    private AppStatusContainer appStatusContainer;
    private DateSelectionController controller;

    public static RootLayout getInstance() {

        if (instance == null){
            instance = new RootLayout();
        }

        return instance;
    }

    private RootLayout(){
        AppMenuBar appMenuBar = new AppMenuBar();
        this.dateSelectionContainer = new DateSelectionContainer();
        this.logTableContainer = new LogTableContainer();
        this.filtersContainer = FiltersContainer.getInstance();
        this.logCountContainer = LogCountContainer.getInstance();
        this.controller = new DateSelectionController(this);
        this.appStatusContainer = AppStatusContainer.getInstance();

        this.setTop(appMenuBar);
        this.setCenter(centerLayoutDateSelectionAndTable(dateSelectionContainer, logTableContainer));
        dateSelectionContainer.getSetDatesBtn().setOnAction(event -> controller.handleSubmit());
        this.setLeft(filtersContainer);
    }

    public AppStatusContainer getAppStatusContainer() {
        return appStatusContainer;
    }

    public DateSelectionContainer getDateSelectionContainer() {
        return dateSelectionContainer;
    }

    public void setLogFilteredList(FilteredList<Log> logFilteredList) {
        logTableContainer.setTableItems(logFilteredList);
        setLogCount(logFilteredList.size());
        enableLogCount();
        filtersContainer.setFilteredList(logFilteredList);
    }

    public void clearList(){
        logTableContainer.clearTable();
        filtersContainer.clearFilters();
    }

    private void setLogCount(int numberOfLogs) {
        this.logCountContainer.setNumberOfLogs(numberOfLogs);
    }

    public void setNumberOfFiles(int numberOfFiles){
        this.logCountContainer.setNumberOfFilesRead(numberOfFiles);
    }

    private void enableLogCount(){
        this.setBottom(logCountContainer);
    }

    private VBox centerLayoutDateSelectionAndTable(GridPane dateContainer, VBox table){

        VBox container = new VBox(5);
        container.getChildren().addAll(dateContainer, table);
        return container;

    }
}
