package org.kobbigal.sisenselogreader.controllers;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import org.kobbigal.sisenselogreader.model.Log;
import org.kobbigal.sisenselogreader.views.count.LogCountContainer;
import org.kobbigal.sisenselogreader.views.filters.FiltersContainer;

public class FiltersSelectionController {

    private FiltersContainer filtersContainer;
    private FilteredList<Log> logFilteredList;

    public FiltersSelectionController(FiltersContainer filtersContainer){
        this.filtersContainer = filtersContainer;
    }

    public void filter(){
        ObservableList<String> sourceListContainerList = filtersContainer.getSourceListContainerList();
        ObservableList<String> verbosityListContainerList = filtersContainer.getVerbosityListContainerList();
        String componentSearchboxContainerText = filtersContainer.getComponentSearchboxContainerText();
        String detailsSearchboxContainerText = filtersContainer.getDetailsSearchboxContainerText();

        System.out.println("Source list values: " + sourceListContainerList);
        System.out.println("Verbosity list values: " + verbosityListContainerList);
        System.out.println("Component search value: " + componentSearchboxContainerText);
        System.out.println("Details search value: " + detailsSearchboxContainerText);

        logFilteredList.setPredicate(log ->
                log.getComponent().toLowerCase().contains(componentSearchboxContainerText.toLowerCase()) &&
                log.getDetails().toLowerCase().contains(detailsSearchboxContainerText.toLowerCase())
        );


        LogCountContainer.getInstance().setNumberOfLogs(logFilteredList.size());

    }

    public void setLogFilteredList(FilteredList<Log> logFilteredList) {
        this.logFilteredList = logFilteredList;
    }
}
